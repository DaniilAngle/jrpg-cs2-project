package character

import scala.collection.mutable.ListBuffer

class Warrior (base_hp: Int =  130, base_mp: Int = 30, base_attack: Int = 18,
               base_m_attack: Int = 11, base_def: Int = 13,
               base_m_def: Int = 7) extends Character(base_hp, base_mp, base_attack, base_m_attack, base_def, base_m_def) {

  var can_use_moves: Boolean = true

  override def lvl_up(): Unit = {
    this.magic += this.lvl
    this.hp += this.lvl * 5
    this.magic_power += this.lvl
    this.magic_def += this.lvl
    this.armor += this.lvl * 2
    this.attack_power += this.lvl + 5
  }

  override def physical_attack(opponent: Character): Unit = {
    can_use_moves = true
    super.physical_attack(opponent)
  }

  def smash(opponent: Character): Unit = {
    if (this.current_hp > 15) {
      opponent.take_physical_damage(35 + this.lvl * 2)
      this.current_hp -= 15
    }
  }

  def dark_slash(opponent: Character): Unit = {
    if (this.lvl >= 5) {
      magic_attack(opponent, 15, 40)
      physical_attack(opponent)
    }
  }

  override def battleOptions(): List[String] = {
    super.battleOptions()
    if (this.current_hp > 15) {
      action_list += "Smash"
    }
    if (this.lvl >= 5) {
      action_list += "Dark Slash"
    }
    action_list.toList
  }

  override def takeAction(option: String, creature: Character, party: Party): Unit = {
    super.takeAction(option, creature, party)
    if (option == "Smash") {
      this.smash(creature)
    }
    if (option == "Dark Slash") {
      this.dark_slash(creature)
    }
  }
}
