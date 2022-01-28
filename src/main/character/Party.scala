package character

import scala.collection.mutable.ListBuffer

class Party(main_char: Character){
  var char_list: ListBuffer[Character] = ListBuffer(
    main_char
  )


  def add_party_member(character: Character): Unit = {
    if (char_list.length < 4) {
      char_list += character
    }
  }

  def battle_end(opponent: Party): Unit = {
    var dead_counter: Int = 0
    for (opponent_member <- opponent.char_list.indices) {
      if (!opponent.char_list(opponent_member).alive) {
        dead_counter += 1
      }
    }
    if (dead_counter == opponent.char_list.length) {
      fight_win(opponent)
    }
  }

  def fight_win(opponent: Party): Unit = {
    var exp_from_battle: Int = 0
    var distributed_exp: Int = 0
    for (enemy_member <- opponent.char_list.indices) {
      exp_from_battle += this.char_list(enemy_member).gained_exp(opponent.char_list(enemy_member))
    }
    var alive_members: Int = 0
    for (member <- this.char_list.indices) {
      if (this.char_list(member).alive) {
        alive_members += 1
      }
    }
    distributed_exp = exp_from_battle/alive_members
    for (member <- this.char_list.indices) {
      if (this.char_list(member).alive) {
        this.char_list(member).gain_exp(distributed_exp)
        if (this.char_list(member).lvl_up_exp < this.char_list(member).exp) {
          this.char_list(member).lvl_up()
        }
      }
    }
  }
}
