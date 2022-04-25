package database

import java.sql.{Connection, DriverManager, ResultSet}
import scala.io.Source

class SQLDatabase extends Database {
  val url = "jdbc:mysql://localhost:3306/users"
  val username = "root"
  val password = "1234"
  var connection: Connection = DriverManager.getConnection(url, username, password)

  setupTable()

  def setupTable(): Unit = {
    val statement = connection.createStatement()
    statement.execute("CREATE TABLE IF NOT EXISTS players (username TEXT, password TEXT, gameState TEXT)")
  }


  def playerExists(username: String, password: String): Boolean = {
    val statement = connection.prepareStatement("SELECT * FROM players WHERE username=? AND password=?")
    statement.setString(1, username)
    statement.setString(2, password)
    val result: ResultSet = statement.executeQuery()
    result.next()
  }


  def createPlayer(username: String, password: String): Unit = {
    val statement = connection.prepareStatement("INSERT INTO players VALUE (?, ?, ?)")
    statement.setString(1, username)
    statement.setString(2, password)
    statement.setString(3, "")
    statement.execute()
  }


  def saveGameState(username: String, gameState: String): Unit = {
    val statement = connection.prepareStatement("UPDATE players SET gameState = ? WHERE username = ?")
    statement.setString(1, gameState)
    statement.setString(2, username)
    statement.execute()
  }


  def loadGameState(username: String): String = {
    val statement = connection.prepareStatement("SELECT * FROM players WHERE username=?")
    statement.setString(1, username)
    val result: ResultSet = statement.executeQuery()
    result.next()
    result.getString("gameState")
  }

  def usernameExists(username: String): Boolean = {
    val statement = connection.prepareStatement("SELECT * FROM players WHERE username=?")
    statement.setString(1, username)
    val result: ResultSet = statement.executeQuery()
    result.next()
  }

  def recreateParty(username: String): Unit = {
    val statement = connection.prepareStatement("UPDATE players SET gameState = ? WHERE username = ?")
    statement.setString(1, "")
    statement.setString(2, username)
  }
}
