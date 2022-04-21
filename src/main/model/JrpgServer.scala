package model

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import character._
import com.corundumstudio.socketio.listener.{DataListener, DisconnectListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import database.DatabaseActor
import messages._
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.ListBuffer




class JrpgServer(val database: ActorRef, val authenticationSys: ActorRef) extends Actor{

  val battleSystem: ActorRef = this.context.actorOf(Props(classOf[BattleSystem], self, database))

  var socketToUsername: Map[SocketIOClient, String] = Map()
  var usernameToSocket: Map[String, SocketIOClient] = Map()
  var usernameToParty: Map[String, Party] = Map()
  var playersInLobby: ListBuffer[String] = ListBuffer.empty

  val config: Configuration = new Configuration {
    setHostname("localhost")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)

  server.addDisconnectListener(new DisconnectionListener(this))
  server.addEventListener("regClicked", classOf[String], new RegisterListener(this))
  server.addEventListener("logClicked", classOf[String], new LoginListener(this))
  server.addEventListener("battleStarted", classOf[String], new BattleListener(this))
  server.addEventListener("lobbyEntered", classOf[Nothing], new LobbyEnterListener(this))
  server.addEventListener("lobbyExited", classOf[Nothing], new LobbyExitListener(this))
  server.addEventListener("turnDecision", classOf[String], new TurnDecisionListener(this))

  server.start()

  //reg/log connect listeners
  class LoginListener(server: JrpgServer) extends DataListener[String] {
    override def onData(socket: SocketIOClient, userPass: String, ackRequest: AckRequest): Unit = {
      println("User tried to login with " + userPass)
      val jsVal: JsValue = Json.parse(userPass)
      val username: String = (jsVal \ "username").as[String]
      val password: String = (jsVal \ "password").as[String]
      server.socketToUsername += (socket -> username)
      server.usernameToSocket += (username -> socket)
      socket.sendEvent("lobbyUpdate")
    }
  }

  class RegisterListener(server: JrpgServer) extends DataListener[String]{
    override def onData(socket: SocketIOClient, userPass: String, ackRequest: AckRequest): Unit = {
      println(userPass + " registered to the game with socket " + socket)
      val jsVal: JsValue = Json.parse(userPass)
      val username: String = (jsVal \ "username").as[String]
      val password: String = (jsVal \ "password").as[String]
      authenticationSys ! Register(username, password, server.self)
      server.socketToUsername += (socket -> username)
      server.usernameToSocket += (username -> socket)
    }
  }

  class DisconnectionListener(server: JrpgServer) extends DisconnectListener {
    override def onDisconnect(socket: SocketIOClient): Unit = {
      if(server.socketToUsername.contains(socket)){
        val username = server.socketToUsername(socket)
        server.socketToUsername -= socket
        server.usernameToSocket -= username
        println(username + " Disconnected")
        battleSystem ! RemoveParty(username)
      }
    }
  }

  // lobby listeners

  def lobbySpam(): Unit = {
    playersInLobby.foreach( {player => usernameToSocket(player).sendEvent("lobbyUpdate", Json.stringify(Json.toJson(playersInLobby)))})
  }

  class LobbyEnterListener(server: JrpgServer) extends DataListener[Nothing] {
    override def onData(socketIOClient: SocketIOClient, t: Nothing, ackRequest: AckRequest): Unit = {
      playersInLobby += socketToUsername(socketIOClient)
      lobbySpam()
    }
  }

  class LobbyExitListener(server: JrpgServer) extends DataListener[Nothing] {
    override def onData(socketIOClient: SocketIOClient, t: Nothing, ackRequest: AckRequest): Unit = {
      playersInLobby -= socketToUsername(socketIOClient)
      lobbySpam()
    }
  }

  //battle sockets

  class BattleListener(server: JrpgServer) extends DataListener[String] {
    override def onData(socketIOClient: SocketIOClient, username2: String, ackRequest: AckRequest): Unit = {
      val party1username: String = socketToUsername(socketIOClient)
      battleSystem ! BattleStarted(party1username, username2)
      usernameToSocket(username2).sendEvent("battleCall")
    }
  }

  class TurnDecisionListener(server: JrpgServer) extends DataListener[String] {
    override def onData(socketIOClient: SocketIOClient, heroEnemyData: String, ackRequest: AckRequest): Unit = {
      val parsedData: JsValue = Json.parse(heroEnemyData)
      val heroName: String = (parsedData \ "hero").as[String]
      val enemyName: String = (parsedData \ "enemy").as[String]
      val battleOption: String = (parsedData \ "option").as[String]
      val enemyPartyID: String = (parsedData \ "enemyPartyID").as[String]
      val userPartyID: String = (parsedData \ "userPartyID").as[String]
      battleSystem ! TurnAction(userPartyID, enemyPartyID, heroName, enemyName, battleOption)
    }
  }


  override def receive: Receive = {
        //login
    case RegFail(username) => usernameToSocket(username).sendEvent("regFailure")
    case RegSuccess(username) => usernameToSocket(username).sendEvent("credentialsCorrect")
      battleSystem ! GetPartyData(username)
    case LoginFail(username) => usernameToSocket(username).sendEvent("loginFailure")
    case LoginSuccess(username) => usernameToSocket(username).sendEvent("credentialsCorrect")
      battleSystem ! GetPartyData(username)
        //battle
    case turnResult: TurnResult => //TODO create JSON with data of turn and send back to the js
  }


}

object JrpgServer {
  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem()

    import actorSystem.dispatcher
    import scala.concurrent.duration._

    val db = actorSystem.actorOf(Props(classOf[DatabaseActor]))
    val authSys = actorSystem.actorOf(Props(classOf[AuthenticationSys], db))
    val server = actorSystem.actorOf(Props(classOf[JrpgServer], db, authSys))

  }
}