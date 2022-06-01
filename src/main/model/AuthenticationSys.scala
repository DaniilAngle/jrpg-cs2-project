package model

import akka.actor.{Actor, ActorRef}
import messages._

class AuthenticationSys(database: ActorRef) extends Actor {
  override def receive: Receive = {
    case Login(username, password, server) => database ! CheckCredentials(username, password, server)
    case Register(username, password, server) => database ! RegisterCheck(username, password, server)
    case RegisterResult(username, registered, server) =>
      if (registered) {
        server ! RegSuccess(username)
      } else {
        server ! RegFail(username)
      }
    case LoginResult(username, result, server) =>
      if (result) {
        server ! LoginSuccess(username)
      } else {
        server ! LoginFail(username)
      }
  }
}
