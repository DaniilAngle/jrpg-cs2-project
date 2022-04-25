package messages

import akka.actor.ActorRef


case class Register(username: String, password: String, server: ActorRef)
case class Login(username: String, password: String, server: ActorRef)
case class CheckCredentials(username: String, password: String, server: ActorRef)
case class RegisterCheck(username: String, password: String, server: ActorRef)
case class RegisterResult(username: String, registered: Boolean, server: ActorRef)
case class RegSuccess(username: String)
case class RegFail(username: String)
case class LoginSuccess(username: String)
case class LoginFail(username: String)
case class LoginResult(username: String, result: Boolean, server: ActorRef)

case class CreateNewParty(username: String, charTypes: List[String], charNames: List[String])
case class AddParty(username: String, partyJSON: String)
case class RemoveParty(username: String)
case class BattleStarted(username1: String, username2: String)
case class UpdateGameState(username: String, gameState: String)
case class TurnAction(username: String, enemyUsername: String, heroName: String, enemyName: String, option: String)
case class GetPartyData(username: String)
case class SaveGame(username: String, gameState: String)
case class TurnResult(username: String, enemyUsername: String, heroName: String, enemyName: String, value: Int)
case class BattleEnded(username: String, enemyUsername: String)
case class TurnTakes(username1: String, charName: String)
case class RecreateParty(username: String)