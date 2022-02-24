package character

import scala.collection.mutable.ListBuffer

class Mage(base_hp: Int =  80, base_mp: Int = 200, base_attack: Int = 3,
           base_m_attack: Int = 30, base_def: Int = 2,
           base_m_def: Int = 25) extends Character(base_hp, base_mp, base_attack, base_m_attack, base_def, base_m_def) {

  override def lvl_up(): Unit = {
    while (this.lvl_up_exp <= this.exp) {
      this.exp -= this.lvl_up_exp
      this.lvl_up_exp += 40 * this.lvl
      this.hp += this.lvl * 2
      this.magic += this.lvl * 2 + 30
      this.current_hp = this.hp
      this.current_magic = this.magic
      this.armor += this.lvl / 2
      this.attack_power += this.lvl / 3
      this.magic_power += this.lvl + 5
      this.magic_def += this.lvl + 3
      this.lvl += 1
    }
  }

  def fireball(opponent: Character): Unit = {
    magic_attack(opponent,40, 5)
  }

  def dark_energy(opponent: Character): Unit = {
    if (this.lvl >= 5) {
      magic_attack(opponent, 60, 20)
    }
  }

  def firewall(opponent_party: Party): Unit ={
    if (this.lvl >= 10) {
      if (use_magic(250)) {
        for (member <- opponent_party.char_list) {
          if (member.alive) {
            magic_attack(member, 0, 50)
          }
        }
      }
    }
  }

  override def battleOptions(): List[String] = {
    action_list.clear()
    if (this.alive) {
      action_list += "Physical Attack"
    }
    if (this.current_magic >= 40 && !action_list.contains("Fireball")) {
      add_action("Fireball" )
    }
    if (this.lvl >= 5 && this.current_magic >= 60 && !action_list.contains("Dark Energy")) {
      add_action("Dark Energy")
    }
    if (this.lvl >= 10 && this.current_magic >= 250 && !action_list.contains("Firewall")) {
      add_action("Firewall")
    }
    action_list.toList
  }

  override def takeAction(option: String, creature: Character, party: Party): Unit = {
    super.takeAction(option, creature, party)
    if (option == "Fireball") {
      this.fireball(creature)
    }
    if (option == "Dark Energy") {
      this.dark_energy(creature)
    }
    if (option == "Firewall") {
      this.firewall(party)
    }
  }
}
