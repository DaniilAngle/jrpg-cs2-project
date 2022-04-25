package model

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import character._
import com.corundumstudio.socketio.listener.{DataListener, DisconnectListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import database.DatabaseActor
import messages._
import play.api.libs.json.{JsNumber, JsObject, JsString, JsValue, Json}

import scala.collection.mutable.ListBuffer




class JrpgServer(val database: ActorRef, val authenticationSys: ActorRef) extends Actor{

  val battleSystem: ActorRef = this.context.actorOf(Props(classOf[BattleSystem], self, database))

  var socketToUsername: Map[SocketIOClient, String] = Map()
  var usernameToSocket: Map[String, SocketIOClient] = Map()
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
  server.addEventListener("turnDecision", classOf[String], new TurnDecisionListener(this))
  server.addEventListener("charsSelected", classOf[String], new CharSelectedListener(this))
  server.start()

  //reg/log connect listeners
  class LoginListener(server: JrpgServer) extends DataListener[String] {
    override def onData(socket: SocketIOClient, userPass: String, ackRequest: AckRequest): Unit = {
      println("User tried to login with " + userPass)
      val jsVal: JsValue = Json.parse(userPass)
      val username: String = (jsVal \ "username").as[String]
      val password: String = (jsVal \ "password").as[String]
      authenticationSys ! Login(username, password, server.self)
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
      val username = server.socketToUsername(socket)
      if (playersInLobby.contains(username)) {
        playersInLobby -= username
        lobbySpam()
      }
        server.socketToUsername -= socket
        server.usernameToSocket -= username
        println(username + " Disconnected")
        battleSystem ! RemoveParty(username)
    }
  }
  //chars select listener

  class CharSelectedListener(server: JrpgServer) extends DataListener[String]{
    override def onData(socketIOClient: SocketIOClient, t: String, ackRequest: AckRequest): Unit = {
      val jsonParsed: JsValue = Json.parse(t)
      val charNames: List[String] = (jsonParsed \ "characterNames").as[List[String]]
      val charTypes: List[String] = (jsonParsed \ "characterTypes").as[List[String]]
      println(charTypes, charNames, "lol")
      battleSystem ! CreateNewParty(socketToUsername(socketIOClient), charTypes, charNames)
    }
  }

  // lobby listeners

  def lobbySpam(): Unit = {
    if (playersInLobby.nonEmpty) {
      playersInLobby.foreach( {player => usernameToSocket(player).sendEvent("lobbyUpdate", Json.stringify(Json.toJson(playersInLobby)))})
    }
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
      playersInLobby -= username2
      playersInLobby -= party1username
      battleSystem ! BattleStarted(party1username, username2)
      lobbySpam()
      usernameToSocket(username2).sendEvent("battleCall", party1username)
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
      println("turn action received", "heroName: " + heroName, "enemyName: " + enemyName,
        "battleOption: " + battleOption, "enemyPartyId: " + enemyPartyID, "userPartyID: " + userPartyID)
      battleSystem ! TurnAction(userPartyID, enemyPartyID, heroName, enemyName, battleOption)
    }
  }


  override def receive: Receive = {
        //login
    case RegFail(username) => usernameToSocket(username).sendEvent("regFailure")
    socketToUsername -= usernameToSocket(username)
    usernameToSocket -= username
    case RegSuccess(username) => usernameToSocket(username).sendEvent("regSuccess")
    case LoginFail(username) => usernameToSocket(username).sendEvent("loginFailure")
      socketToUsername -= usernameToSocket(username)
      usernameToSocket -= username
    case LoginSuccess(username) => usernameToSocket(username).sendEvent("credentialsCorrect")
      battleSystem ! GetPartyData(username)
        //battle
    case turnTakes: TurnTakes => usernameToSocket(turnTakes.username1).sendEvent("takeTurn", turnTakes.charName)
    case turnResult: TurnResult =>
      usernameToSocket(turnResult.username).sendEvent("turnResult", makeTurnJson(turnResult))
      usernameToSocket(turnResult.enemyUsername).sendEvent("turnResult", makeTurnJson(turnResult))
    case updateGameState: UpdateGameState =>
      usernameToSocket(updateGameState.username).sendEvent("updateGameState", updateGameState.gameState)
    case battleEnded: BattleEnded =>
      usernameToSocket(battleEnded.username).sendEvent("battleEnded", battleEnded.username)
      usernameToSocket(battleEnded.enemyUsername).sendEvent("battleEnded", battleEnded.username)
  }

  def makeTurnJson(turnResult: TurnResult): String = {
    val jsValue: JsValue = JsObject(
      Seq(
        "hero" -> JsString(turnResult.heroName),
        "enemy" -> JsString(turnResult.enemyName),
        "valueOfMove" -> JsNumber(turnResult.value)
      )
    )
    Json.stringify(jsValue)
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