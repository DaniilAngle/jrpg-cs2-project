package character

class Healer(base_hp: Int =  110, base_mp: Int = 150, base_attack: Int = 1,
             base_m_attack: Int = 15, base_def: Int = 2,
             base_m_def: Int = 20) extends Character(base_hp, base_mp, base_attack, base_m_attack, base_def, base_m_def) {

  override def lvl_up(): Unit = {
    this.magic += this.lvl * 2 + 20
    this.hp += this.lvl * 3
    this.magic_power += this.lvl + 3
    this.magic_def += this.lvl + 2
    this.armor += this.lvl / 2
    this.attack_power += this.lvl / 3
  }

  def healing(creature: Character, healing: Int): Unit = {
    if (creature.current_hp + healing <= creature.hp) {
      creature.current_hp += healing
    } else {
      creature.current_hp = creature.hp
    }
  }
  def heal(creature: Character): Unit = {
    var heal: Int = 15 + this.lvl * 5
    if (this.use_magic(30)) {
      healing(creature, heal)
    }
  }
  def holy_ray(opponent: Character): Unit = {
    this.magic_attack(opponent, 40)
  }

  def all_heal(party: Party): Unit = {
    if (this.lvl >= 15) {
      var heal: Int = 100 + this.lvl * 2
      if (this.use_magic(200))
      for (member <- party.char_list) {
        if (member.alive) {
          healing(member, heal)
        }
      }
    }
  }

  override def battleOptions(): List[String] = {
    if (this.current_magic >= 30) {
      action_list += "Heal"
    }
    if (this.current_magic >= 40) {
      action_list += "Holy Ray"
    }
    if (this.lvl >= 15 && this.current_magic >= 200) {
      action_list += "Heal Party"
    }
    action_list.toList
  }

  override def takeAction(option: String, creature: Character, party: Party): Unit = {
    super.takeAction(option, creature, party)
    if (option == "Heal") {
      this.heal(creature)
    }
    if (option == "Holy Ray") {
      this.holy_ray(creature)
    }
    if (option == "Heal Party") {
      this.all_heal(party)
    }
  }
}
