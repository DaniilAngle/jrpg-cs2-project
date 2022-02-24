package character

import scala.collection.mutable.ListBuffer

class Warrior (base_hp: Int =  130, base_mp: Int = 30, base_attack: Int = 18,
               base_m_attack: Int = 11, base_def: Int = 13,
               base_m_def: Int = 7) extends Character(base_hp, base_mp, base_attack, base_m_attack, base_def, base_m_def) {


  override def lvl_up(): Unit = {
    while (this.lvl_up_exp <= this.exp) {
      this.exp -= this.lvl_up_exp
      this.lvl_up_exp += 40 * this.lvl
      this.hp += this.lvl * 5
      this.magic += this.lvl
      this.current_hp = this.hp
      this.current_magic = this.magic
      this.armor += this.lvl * 2
      this.attack_power += this.lvl + 5
      this.magic_power += this.lvl
      this.magic_def += this.lvl + 1
      this.lvl += 1
    }
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
    action_list.clear()
    if (this.alive) {
      action_list += "Physical Attack"
    }
    if (this.current_hp > 15 && !action_list.contains("Smash")) {
      action_list += "Smash"
    }
    if (this.lvl >= 5 && this.current_magic >= 15 && !action_list.contains("Dark Slash")) {
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
