package character

class Character {
  var hp: Int = 100
  var current_hp: Int = this.hp
  var magic: Int = 100
  var current_magic: Int = this.magic
  var attack_power: Int = 10
  var armor: Int = 3
  var magic_def: Int = 9
  var magic_power: Int = 20
  var alive = true

  def take_physical_damage(dmg: Int): Unit = {
    if (dmg > this.armor) {
      this.current_hp -=  (dmg - this.armor)
      if (this.current_hp <= 0) {
        this.current_hp = 0
        this.alive = false
      }
    }
  }

  def take_magical_damage(dmg: Int): Unit = {
    if (dmg > this.magic_def) {
      this.current_hp -=  (dmg - this.magic_def)
      if (this.current_hp <= 0) {
        this.current_hp = 0
        this.alive = false
      }
    }
  }

  def physical_attack(opponent: Character): Unit = {
    opponent.take_physical_damage(this.attack_power)
  }

  def use_magic(consumption: Int): Boolean = {
    if (this.current_magic >= consumption) {
      this.current_magic -= consumption
      true
    } else {
      false
    }
  }

  def magic_attack(opponent: Character, consumption: Int): Unit = {
    if (use_magic(consumption)) {
      opponent.take_magical_damage(this.magic_power)
    }
  }
}