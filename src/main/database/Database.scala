package database

trait Database {
  def playerExists(username: String, password: String): Boolean
  def usernameExists(username: String): Boolean
  def createPlayer(username: String, password: String): Unit
  def saveGameState(username: String, gameState: String): Unit
  def loadGameState(username: String): String
  def recreateParty(username: String): Unit
}
