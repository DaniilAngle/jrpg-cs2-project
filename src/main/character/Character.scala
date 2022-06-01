package character

import scala.collection.mutable.ListBuffer

class Character(base_hp: Int = 100, base_mp: Int = 100, base_attack: Int = 10,
                base_m_attack: Int = 20, base_def: Int = 3, base_m_def: Int = 9) {

  var charType: String = ""
  var name: String = ""
  var hp: Int = base_hp
  var current_hp: Int = this.hp
  var magic: Int = base_mp
  var current_magic: Int = this.magic
  var attack_power: Int = base_attack
  var armor: Int = base_def
  var magic_def: Int = base_m_def
  var magic_power: Int = base_m_attack
  var alive: Boolean = true
  var exp: Int = 0
  var lvl: Int = 1
  var lvl_up_exp: Int = 100
  var action_list: ListBuffer[String] = ListBuffer("Physical Attack")

  def magic_attack(opponent: Character, consumption: Int, extra: Int = 0): Unit = {
    if (use_magic(consumption)) {
      opponent.take_magical_damage(this.magic_power + (consumption / 5) + extra)
    }
  }

  def take_magical_damage(dmg: Int): Unit = {
    if (dmg > this.magic_def) {
      this.current_hp -= (dmg - this.magic_def)
      if (this.current_hp <= 0) {
        this.current_hp = 0
        this.alive = false
      }
    }
  }

  def use_magic(consumption: Int): Boolean = {
    if (this.current_magic >= consumption) {
      this.current_magic -= consumption
      true
    } else {
      false
    }
  }

  def gained_exp(opponent: Character): Int = {
    var exp: Int = 0
    exp += opponent.lvl * 20 + 23
    exp
  }

  def gain_exp(experience: Int): Unit = {
    this.exp += experience
    if (this.exp >= this.lvl_up_exp) {
      this.lvl_up()
    }
  }

  def lvl_up(): Unit = {
    while (this.lvl_up_exp <= this.exp) {
      this.exp -= this.lvl_up_exp
      this.lvl_up_exp += 40 * this.lvl
      this.hp += 5 * this.lvl
      this.magic += 2 * this.lvl
      this.current_hp = this.hp
      this.current_magic = this.magic
      this.armor += this.lvl
      this.attack_power += this.lvl
      this.magic_power += this.lvl
      this.magic_def += this.lvl
      this.lvl += 1
    }
  }

  def curValues(): Unit = {
    val stats = "LVL:" + this.lvl.toString + " exp:" + this.exp.toString + "/" + this.lvl_up_exp.toString + " HP:" + this.current_hp.toString + "/" + this.hp.toString +
      " MP:" + this.current_magic + "/" + this.magic + " Def:" + this.armor + " Mag def:" + this.magic_def + " Power:" + this.attack_power + " M power:" + this.magic_power
    println(stats)
  }

  def add_action(action: String): Unit = {
    if (action_list.length < 4) {
      action_list += action
    }
  }

  def battleOptions(): List[String] = {
    action_list.clear()
    if (this.alive) {
      action_list += "Physical Attack"
    }
    action_list.toList
  }

  def takeAction(option: String, creature: Character, party: Party): Unit = {
    if (option == "Physical Attack") {
      this.physical_attack(creature)
    }
  }

  def physical_attack(opponent: Character): Unit = {
    opponent.take_physical_damage(this.attack_power)
  }

  def take_physical_damage(dmg: Int): Unit = {
    if (dmg > this.armor) {
      this.current_hp -= (dmg - this.armor)
      if (this.current_hp <= 0) {
        this.current_hp = 0
        this.alive = false
      }
    }
  }
}
