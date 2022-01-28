package character

import scala.collection.mutable.ListBuffer

class Mage(base_hp: Int =  80, base_mp: Int = 200, base_attack: Int = 3,
           base_m_attack: Int = 30, base_def: Int = 2,
           base_m_def: Int = 25) extends Character(base_hp, base_mp, base_attack, base_m_attack, base_def, base_m_def) {

  override def lvl_up(): Unit = {
    this.magic += this.lvl * 2 + 30
    this.hp += this.lvl * 2
    this.magic_power += this.lvl + 5
    this.magic_def += this.lvl + 5
    this.armor += this.lvl / 2
    this.attack_power += this.lvl / 3
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
    if (this.current_magic >= 40) {
      add_action("Fireball")
    }
    if (this.lvl >= 5 && this.current_magic >= 60) {
      add_action("Dark Energy")
    }
    if (this.lvl >= 10 && this.current_magic >= 250) {
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
