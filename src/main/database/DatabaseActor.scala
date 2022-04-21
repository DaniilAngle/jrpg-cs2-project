package database

import akka.actor.Actor
import messages._

class DatabaseActor extends Actor{

  val database: Database = new SQLDatabase()

  override def receive: Receive = {
    case CheckCredentials(username, password, server) =>
      println("success login " + username)
      if (database.playerExists(username, password)) {
        sender() ! LoginResult(username,result = true, server)
      } else {
        println("failed to login " + username)
        sender() ! LoginResult(username,result = false, server)
      }
    case RegisterCheck(username, password, server) =>
      if (database.usernameExists(username))  {
        println("reg unsuccessful " + username)
        sender() ! RegisterResult(username, registered = false, server)
      } else {
        println("reg successful " + username)
        database.createPlayer(username, password)
        sender() ! RegisterResult(username, registered = true, server)
      }
    case GetPartyData(username) => sender() ! AddParty(username, database.loadGameState(username))
    case SaveGame(username, gameState) => database.saveGameState(username, gameState)
  }
}
