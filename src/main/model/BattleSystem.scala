package model

import akka.actor.{Actor, ActorRef}
import character.{Character, Healer, Mage, Party, Warrior}
import messages._
import play.api.libs.json.Json.toJson
import play.api.libs.json._

import scala.collection.mutable.ListBuffer
import scala.util.Random

class BattleSystem(server: ActorRef, database: ActorRef) extends Actor {

  var usernameToParty: Map[String, Party] = Map()
  var usernameToEnemyUsername: Map[String, String] = Map.empty
  var usernameToCharNamesToChar: Map[String, Map[String, Character]] = Map.empty
  var battleIDToUsernames: Map[String, List[String]] = Map.empty
  var usernameToBattleID: Map[String, String] = Map.empty

  override def receive: Receive = {
    case BattleStarted(username1, username2) =>
      usernameToEnemyUsername += (username1 -> username2)
      usernameToEnemyUsername += (username2 -> username1)
      val battleID: String = username1 + username2
      battleIDToUsernames += (battleID -> List(username1, username2))
      usernameToBattleID += (username1 -> battleID)
      usernameToBattleID += (username2 -> battleID)
      server ! UpdateGameState(username1, battlePartyJSONCreator(username1, username2))
      server ! UpdateGameState(username2, battlePartyJSONCreator(username2, username1))
      if (Random.nextDouble() < 0.5) {
        server ! TurnTakes(username1, firstMoveSelection(username1))
      } else {
        server ! TurnTakes(username2, firstMoveSelection(username2))
      }

    case turnAction: TurnAction =>
      var enemyHpChange: Int = usernameToCharNamesToChar(turnAction.enemyUsername)(turnAction.enemyName).current_hp
      usernameToCharNamesToChar(turnAction.username)(turnAction.heroName).takeAction(turnAction.option,
        usernameToCharNamesToChar(turnAction.enemyUsername)(turnAction.enemyName), usernameToParty(turnAction.enemyUsername))
      enemyHpChange -= usernameToCharNamesToChar(turnAction.enemyUsername)(turnAction.enemyName).current_hp
      server ! TurnResult(turnAction.username, usernameToEnemyUsername(turnAction.username), turnAction.heroName, turnAction.enemyName, enemyHpChange)

      // nextTurn
      val nextPlayerUsername: String = usernameToEnemyUsername(turnAction.username)
      server ! UpdateGameState(turnAction.username, battlePartyJSONCreator(turnAction.username, nextPlayerUsername))
      server ! UpdateGameState(nextPlayerUsername, battlePartyJSONCreator(nextPlayerUsername, turnAction.username))

      val battleID: String = usernameToBattleID(turnAction.username)
      val nextChar: List[String] = turnSelect(battleIDToUsernames(battleID).head, battleIDToUsernames(battleID).last, usernameToCharNamesToChar(turnAction.username)(turnAction.heroName))
      if (nextChar.isEmpty) {
        usernameToParty(turnAction.username).battle_end(usernameToParty(turnAction.enemyUsername))
        server ! BattleEnded(turnAction.username, turnAction.enemyUsername)
        database ! SaveGame(turnAction.username, gameStateForDBParser(turnAction.username))
        usernameToEnemyUsername -= turnAction.username
        usernameToEnemyUsername -= turnAction.enemyUsername
        usernameToParty -= turnAction.enemyUsername
        database ! GetPartyData(turnAction.enemyUsername)
      } else {
        server ! TurnTakes(nextChar.head, nextChar.last)
      }

    case GetPartyData(username) => database ! GetPartyData(username)

    case AddParty(username, partyJSON) =>
      usernameToParty += (username -> partyRecreator(partyJSON, username))

    case RemoveParty(username) =>
      database ! SaveGame(username, gameStateForDBParser(username))
      usernameToParty -= username
      usernameToCharNamesToChar -= username

    case createNewParty: CreateNewParty =>
      newPartyCreatorForDB(createNewParty.username, createNewParty.charTypes, createNewParty.charNames)
      database ! SaveGame(createNewParty.username, gameStateForDBParser(createNewParty.username))

  }

  def newPartyCreatorForDB(username: String, types: List[String], names: List[String]): Party = {
    val party: Party = new Party()
    var charNameToChar: Map[String, Character] = Map.empty
    for (i <- types.indices) {
      val char: Character = classMatcher(types(i))
      char.name = names(i)
      party.add_party_member(char)
      charNameToChar += (char.name -> char)
    }
    usernameToParty += (username -> party)
    usernameToCharNamesToChar += (username -> charNameToChar)
    party
  }

  def firstMoveSelection(username: String): String = {
    val listOfChars: ListBuffer[Character] = usernameToParty(username).char_list
    turnSelectHelp(0, listOfChars).name
  }


  def partyRecreator(partyJSON: String, username: String): Party = {
    val party: Party = new Party()
    var charNameToChar: Map[String, Character] = Map.empty
    var charNameToIdx: Map[String, Int] = Map.empty
    for (i <- 0 to 2) {
      val parsedParty: JsValue = Json.parse(partyJSON)
      val charName: String = (parsedParty \ "characters" \ i \ "name").as[String]
      val charType: String = (parsedParty \ "characters" \ i \ "type").as[String]
      val charLvl: Int = (parsedParty \ "characters" \ i \ "lvl").as[Int]
      val charCurrentMP: Int = (parsedParty \ "characters" \ i \ "currentHP").as[Int]
      val charCurrentHP: Int = (parsedParty \ "characters" \ i \ "currentMP").as[Int]
      val charExp: Int = (parsedParty \ "characters" \ i \ "currentMP").as[Int]
      val charAlive: Boolean = (parsedParty \ "characters" \ i \ "alive").as[Boolean]
      val charMaxHP: Int = (parsedParty \ "characters" \ i \ "maxHP").as[Int]
      val charMaxMP: Int = (parsedParty \ "characters" \ i \ "maxMP").as[Int]
      val charDef: Int = (parsedParty \ "characters" \ i \ "defense").as[Int]
      val charMagDef: Int = (parsedParty \ "characters" \ i \ "magDefense").as[Int]
      val charLvlUpExp: Int = (parsedParty \ "characters" \ i \ "charLvlUpExp").as[Int]
      val charAttackPower: Int = (parsedParty \ "characters" \ i \ "attackPower").as[Int]
      val charMagAttackPower: Int = (parsedParty \ "characters" \ i \ "magAttackPower").as[Int]

      val char: Character = classMatcher(charType)

      char.name = charName
      char.lvl = charLvl
      char.exp = charExp
      char.alive = charAlive
      char.current_hp = charCurrentMP
      char.current_magic = charCurrentHP
      char.hp = charMaxHP
      char.magic = charMaxMP
      char.armor = charDef
      char.magic_def = charMagDef
      char.lvl_up_exp = charLvlUpExp
      char.attack_power = charAttackPower
      char.magic_power = charMagAttackPower
      party.add_party_member(char)
      charNameToChar += (charName -> char)
      charNameToIdx += (charName -> i)
    }
    usernameToCharNamesToChar += (username -> charNameToChar)
    party
  }

  def classMatcher(charType: String): Character = {
    charType match {
      case "mage" => new Mage()
      case "warrior" => new Warrior()
      case "healer" => new Healer()
    }
  }

  def gameStateForDBParser(username: String): String = {
    val party: Party = usernameToParty(username)
    var listOfChars: List[JsValue] = List.empty
    for (char <- party.char_list) {
      val name: String = char.name
      val charType: String = char.charType
      val charExp: Int = char.exp
      val charMaxHP: Int = char.hp
      val charMaxMP: Int = char.magic
      val charCurHP: Int = char.current_hp
      val charCurMP: Int = char.current_magic
      val charLvl: Int = char.lvl
      val charAlive: Boolean = char.alive
      val charArmor: Int = char.armor
      val charMag_Def: Int = char.magic_def
      val charMagPower: Int = char.magic_power
      val charLvlUpExp: Int = char.lvl_up_exp
      val charAttackPower: Int = char.attack_power

      val charInJson: JsValue = JsObject(Map(
        "name" -> JsString(name),
        "type" -> JsString(charType),
        "exp" -> JsNumber(charExp),
        "lvl" -> JsNumber(charLvl),
        "currentHP" -> JsNumber(charCurHP),
        "currentMP" -> JsNumber(charCurMP),
        "alive" -> JsBoolean(charAlive),
        "maxHP" -> JsNumber(charMaxHP),
        "maxMP" -> JsNumber(charMaxMP),
        "defense" -> JsNumber(charArmor),
        "magDefense" -> JsNumber(charMag_Def),
        "attackPower" -> JsNumber(charAttackPower),
        "magAttackPower" -> JsNumber(charMagPower),
        "charLvlUpExp" -> JsNumber(charLvlUpExp)
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

  def deadCheck(listOfChars: ListBuffer[Character], index: Int = 0, deadCount: Int = 0): String = {
    if (!listOfChars(index).alive) {
      if (deadCount == 3) {
        "none"
      } else {
        if (index == 2) {
          deadCheck(listOfChars, 0, deadCount + 1)
        } else {
          deadCheck(listOfChars, index + 1, deadCount + 1)
        }
      }
    } else {
      ""
    }
  }

  def turnSelect(username1: String, username2: String, lastMoved: Character): List[String] = {

    val party1char: ListBuffer[Character] = usernameToParty(username1).char_list
    val party2char: ListBuffer[Character] = usernameToParty(username2).char_list

    if (deadCheck(party1char) == "none" || deadCheck(party2char) == "none") {
      return List()
    }

    val moveOrderList: ListBuffer[Character] = ListBuffer.empty
    for (i <- 0 to 2) {
      moveOrderList += party1char(i)
      moveOrderList += party2char(i)
    }
    val activeChar: Character = turnSelectHelp(moveOrderList.indexOf(lastMoved), moveOrderList)
    if (party1char.contains(activeChar)) {
      List(username1, activeChar.name)
    } else {
      List(username2, activeChar.name)
    }
  }

  def turnSelectHelp(idx: Int, moveOrderList: ListBuffer[Character]): Character = {
    var nextIdx: Int = idx + 1
    if (nextIdx == moveOrderList.length) {
      nextIdx = 0
    }
    if (moveOrderList(nextIdx).alive) {
      moveOrderList(nextIdx)
    } else {
      turnSelectHelp(nextIdx, moveOrderList)
    }
  }

  def battlePartyJSONCreator(user: String, enemyUser: String): String = {
    val jsonUser: List[JsValue] = JSONPartyClient(user)
    val jsonEnemy: List[JsValue] = JSONPartyClient(enemyUser)
    val battlePartyDataJSON: JsValue = JsObject(
      Map(
        "playerParty" -> JsObject(Map(
          "characters" -> toJson(jsonUser)
        )),
        "enemyParty" -> JsObject(Map(
          "characters" -> toJson(jsonEnemy)
        ))
      ))
    Json.stringify(battlePartyDataJSON)
  }

  def JSONPartyClient(user: String): List[JsValue] = {
    val party: Party = usernameToParty(user)
    var listOfChars: List[JsValue] = List.empty
    for (creature <- party.char_list) {
      val name: String = creature.name
      val charType: String = creature.charType
      val charCurHP: Int = creature.current_hp
      val charCurMP: Int = creature.current_magic
      val charMaxHP: Int = creature.hp
      val charMaxMP: Int = creature.magic
      val battleOptions: List[String] = creature.battleOptions()
      val charInJson: JsValue = JsObject(Map(
        "name" -> JsString(name),
        "type" -> JsString(charType),
        "mp" -> JsNumber(charCurMP),
        "max_mp" -> JsNumber(charMaxMP),
        "hp" -> JsNumber(charCurHP),
        "maxHP" -> JsNumber(charMaxHP),
        "battleOptions" -> Json.toJson(battleOptions)
      ))
      listOfChars ::= charInJson
    }
    listOfChars
  }
}
