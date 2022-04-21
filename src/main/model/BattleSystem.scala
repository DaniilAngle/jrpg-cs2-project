package model

import akka.actor.{Actor, ActorRef}
import character.{Character, Healer, Mage, Party, Warrior}
import messages._
import play.api.libs.json.Json.{toJson, using}
import play.api.libs.json.{JsBoolean, JsNumber, JsObject, JsString, JsValue, Json}

class BattleSystem(server: ActorRef, database: ActorRef) extends Actor{

  var usernameToParty: Map[String, Party] = Map()
  var playersInBattle: Map[String, List[Party]] = Map.empty
  var usernameToBattleID: Map[String, String] = Map.empty
  var usernameToCharNamesToChar: Map[String, Map[String, Character]] = Map.empty

  override def receive: Receive = {
    case BattleStarted(username1, username2) =>
      val battleID: String = username1 + username2
      usernameToBattleID += (username1 -> battleID)
      usernameToBattleID += (username2 -> battleID)
    case turnAction: TurnAction =>
      var enemyHpChange: Int = usernameToCharNamesToChar(turnAction.enemyUsername)(turnAction.enemyName).current_hp
      usernameToCharNamesToChar(turnAction.username)(turnAction.heroName).takeAction(turnAction.option,
        usernameToCharNamesToChar(turnAction.enemyUsername)(turnAction.enemyName),usernameToParty(turnAction.enemyUsername))
      enemyHpChange -= usernameToCharNamesToChar(turnAction.enemyUsername)(turnAction.enemyName).current_hp
      server ! TurnResult(turnAction.username, turnAction.enemyUsername, turnAction.heroName, turnAction.enemyName, enemyHpChange)
    case GetPartyData(username) => database ! GetPartyData(username)
    case AddParty(username, partyJSON) =>
      usernameToParty += (username -> partyRecreator(partyJSON, username))
    case RemoveParty(username) =>
      usernameToParty -= username
      usernameToCharNamesToChar -= username
      database ! SaveGame(username, gameStateParser(username))
  }

  def partyRecreator(partyJSON: String, username: String): Party = {
    val party: Party = new Party()
    var CharNameToChar: Map[String, Character] = Map.empty
    for (i <- 0 to 2) {
      val parsedParty: JsValue = Json.parse(partyJSON)
      val charName: String = (parsedParty \ "characters" \ i \"name" ).as[String]
      val charType: String = (parsedParty \ "characters" \ i \"type" ).as[String]
      val charLvl: Int = (parsedParty \ "characters" \ i \"lvl" ).as[Int]
      val charCurrentMP: Int = (parsedParty \ "characters"\ i \ "currentHP" ).as[Int]
      val charCurrentHP: Int = (parsedParty \ "characters"\ i \ "currentMP" ).as[Int]
      val charExp: Int = (parsedParty \ "characters" \ i \"currentMP" ).as[Int]
      val charAlive: Boolean = (parsedParty \ "characters"\ i \ "alive").as[Boolean]
      val char: Character = classMatcher(charType)
      char.name = charName
      char.lvl = charLvl
      char.exp = charExp
      char.alive = charAlive
      char.current_hp = charCurrentMP
      char.current_magic = charCurrentHP
      party.add_party_member(char)
      CharNameToChar += (charName -> char)
    }
    usernameToCharNamesToChar += (username -> CharNameToChar)
    party
  }

  def classMatcher(charType: String): Character = {
    charType match {
      case "mage" => new Mage()
      case "warrior" => new Warrior()
      case "healer" => new Healer()
    }
  }

  def gameStateParser(username: String): String = {
    val party: Party = usernameToParty(username)
    var listOfChars: List[JsValue] = List.empty
    for (char <- party.char_list) {
      var name: String = char.name
      var charType: String = char.charType
      var charExp: Int = char.exp
      var charCurHP: Int = char.current_hp
      var charCurMP: Int = char.current_magic
      var charLvl: Int = char.lvl
      var charAlive: Boolean = char.alive
      val charInJson: JsValue = JsObject(Map(
        "name" -> JsString(name),
        "type" -> JsString(charType),
        "exp" -> JsNumber(charExp),
        "lvl" -> JsNumber(charLvl),
        "currentHP" -> JsNumber(charCurHP),
        "currentMP" -> JsNumber(charCurMP),
        "alive" -> JsBoolean(charAlive)
      ))
      listOfChars ::= charInJson
    }
    val jsonBack: String = Json.stringify(Json.toJson(JsObject(
      Map(
        "characters" -> toJson(listOfChars)
      )
    )))
    jsonBack
  }
}
