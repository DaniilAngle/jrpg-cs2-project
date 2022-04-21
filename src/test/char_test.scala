import org.scalatest.funsuite.AnyFunSuite
import character.{Character, Healer, Mage, Party, Warrior}

class char_test extends AnyFunSuite {
  test("Testing initial val") {
    val char1: Character = new Character()
    assert(char1.current_hp == 100)
  }

  test("Take dmg") {
    val dmg1: Character = new Character()
    assert(dmg1.current_hp == 100)
    dmg1.take_physical_damage(40)
    assert(dmg1.current_hp == 63)
    assert(dmg1.alive)
    dmg1.take_physical_damage(90)
    assert(dmg1.current_hp == 0)
    assert(!dmg1.alive)

  }

  test("dmg lower than defence") {
    val char1: Character = new Character()
    val char2: Character = new Character()
    char2.armor = 100
    char2.magic_def = 100
    assert(char2.current_hp == 100)
    char1.physical_attack(char2)
    assert(char2.current_hp == 100)
    char1.magic_attack(char2, 10)
    assert(char2.current_hp == 100)
  }

  test("attack other character"){
    val char1: Character = new Character()
    val char2: Character = new Character()
    assert(char1.current_hp == 100)
    char1.physical_attack(char2)
    assert(char2.current_hp == 93)
    char1.magic_attack(char2, 10)
    assert(char1.current_magic == 90)
    assert(char2.current_hp == 80)
    char1.magic_attack(char2, 100)
    assert(char2.current_hp == 80)
    assert(char1.current_magic == 90)
  }

  test("gain exp character"){
    val char1: Character = new Character()
    val char2: Character = new Character()
    assert(char1.lvl == 1)
    assert(char1.exp == 0)
    assert(char1.lvl_up_exp == 100)
    char1.gain_exp(char1.gained_exp(char2))
    assert(char1.exp == 43)
    char2.lvl = 3
    char1.take_physical_damage(20)
    assert(char1.current_hp == 83)
    char1.gain_exp(char1.gained_exp(char2))
    assert(char1.exp == 26)
    assert(char1.lvl == 2)
    assert(char1.armor == 4)
    assert(char1.current_hp == 105)
    assert(char1.hp == 105)
    assert(char1.lvl_up_exp == 140)
    char2.lvl = 20
    char1.gain_exp(char1.gained_exp(char2))
    assert(char1.lvl == 4)
    assert(char1.lvl_up_exp == 340)
    assert(char1.current_hp == 130)
  }

  test("party test"){
    val char1: Character = new Character()
    val char2: Character = new Character()
    val char3: Character = new Character()
    val win_party: Party = new Party()
    win_party.add_party_member(char2)
    win_party.add_party_member(char3)
    val char4: Character = new Character()
    val char5: Character = new Character()
    val char6: Character = new Character()
    val defeat_party: Party = new Party()
    defeat_party.add_party_member(char5)
    defeat_party.add_party_member(char6)
    win_party.battle_end(defeat_party)
    assert(win_party.char_list(0).exp == 0)
    defeat_party.char_list(0).alive = false
    defeat_party.char_list(1).alive = false
    defeat_party.char_list(2).alive = false
    win_party.battle_end(defeat_party)
    assert(win_party.char_list(0).exp == 43)
    win_party.char_list(0).alive = false
    win_party.battle_end(defeat_party)
    assert(win_party.char_list(0).exp == 43)
    assert(win_party.char_list(1).lvl == 2)
    assert(win_party.char_list(1).exp == 7)
  }

  test("party size test") {
    val char1: Character = new Character()
    val char2: Character = new Character()
    val char3: Character = new Character()
    val char4: Character = new Character()
    val char5: Character = new Character()
    val party: Party = new Party()
    party.add_party_member(char2)
    party.add_party_member(char3)
    party.add_party_member(char4)
    party.add_party_member(char5)
    assert(party.char_list.length == 4)
  }
  test("initial stats") {
    val warrior: Warrior = new Warrior()
    val healer: Healer = new  Healer()
    val mage: Mage = new Mage()
    assert(warrior.current_hp == 130)
    assert(warrior.armor == 13)
    assert(warrior.attack_power == 18)
    assert(warrior.current_hp == 130)
    assert(healer.hp == 110)
    assert(healer.armor == 2)
    assert(healer.magic == 150)
    assert(healer.current_magic == 150)
    assert(mage.magic == 200)
    assert(mage.alive)
    assert(mage.magic_power == 30)
  }

  test("lvl up stats") {
    val warrior: Warrior = new Warrior()
    val healer: Healer = new  Healer()
    val mage: Mage = new Mage()
    warrior.gain_exp(100)
    assert(warrior.lvl == 2)
    assert(warrior.armor == 15)
    assert(warrior.hp == 135)
    healer.gain_exp(300)
    assert(healer.lvl == 3)
    assert(healer.magic == 196)
    mage.gain_exp(500)
    assert(mage.lvl == 4)
    assert(mage.magic_power == 51)
  }

  test("battle options") {
    val warrior: Warrior = new Warrior()
    val healer: Healer = new  Healer()
    val mage: Mage = new Mage()
    assert(warrior.battleOptions() == List("Physical Attack", "Smash"))
    warrior.lvl = 20
    assert(warrior.battleOptions() == List("Physical Attack", "Smash", "Dark Slash"))
    warrior.current_hp = 10
    assert(warrior.battleOptions() == List("Physical Attack", "Dark Slash"))
    assert(healer.battleOptions() == List("Physical Attack", "Heal", "Holy Ray"))
    healer.lvl = 15
    assert(healer.battleOptions() == List("Physical Attack", "Heal", "Holy Ray"))
    healer.current_magic = 200
    assert(healer.battleOptions() == List("Physical Attack", "Heal", "Holy Ray", "Heal Party"))
    assert(mage.battleOptions() == List("Physical Attack", "Fireball"))
    mage.lvl = 5
    assert(mage.battleOptions() == List("Physical Attack", "Fireball", "Dark Energy"))
    mage.lvl = 10
    assert(mage.battleOptions() == List("Physical Attack", "Fireball", "Dark Energy"))
    mage.current_magic = 300
    assert(mage.battleOptions() == List("Physical Attack", "Fireball", "Dark Energy", "Firewall"))
    mage.current_magic = 0
    assert(mage.battleOptions() == List("Physical Attack"))
  }

  test("attacks, aoe attacks, heals") {
    val warrior: Warrior = new Warrior()
    val healer: Healer = new  Healer()
    val mage: Mage = new Mage()
    val party1: Party = new Party()
    val party2: Party = new Party()
    party1.add_party_member(healer)
    party1.char_list.head.takeAction("Smash", party2.char_list.head, party2)
    assert(party2.char_list.head.current_hp == 45)
    assert(party1.char_list.head.current_hp == 115)
    party1.char_list.head.takeAction("Bash", party2.char_list.head, party2)
    assert(party2.char_list.head.current_hp == 45)
    party1.char_list.head.takeAction("Dark Slash", party2.char_list.head, party2)
    party1.char_list.head.lvl = 5
    party2.char_list.head.current_hp = 150
    party1.char_list.head.takeAction("Dark Slash", party2.char_list.head, party2)
    assert(party2.char_list.head.current_hp == 105)
    party1.char_list.head.takeAction("Physical Attack", party2.char_list.head, party2)
    assert(party2.char_list.head.current_hp == 89)
    party2.char_list.head.takeAction("Firewall", party1.char_list.head, party1)
    assert(party1.char_list.head.current_hp == 115)
    party2.char_list.head.lvl = 10
    party2.char_list.head.current_magic = 300
    party2.char_list.head.hp = 120
    party2.char_list.head.takeAction("Firewall", party1.char_list.head, party1)
    party1.char_list.head.curValues()
    assert(party1.char_list.head.current_hp == 42)
    party2.char_list.head.curValues()
    party1.char_list(1).takeAction("Heal", party1.char_list.head, party1)
    assert(party1.char_list.head.current_hp == 62)
    assert(party1.char_list(1).current_hp == 50)
    party1.char_list(1).curValues()
    party1.char_list(1).gain_exp(300)
    party1.char_list(1).curValues()
    party2.char_list.head.takeAction("Fireball", party1.char_list(1), party1)
    assert(party1.char_list(1).current_hp == 103)
    party1.char_list(1).takeAction("Heal", party1.char_list.head, party1)
    assert(party1.char_list.head.current_hp == 92)
    party1.char_list(1).takeAction("Heal", party1.char_list(1), party1)
    assert(party1.char_list(1).current_hp == 119)
    party2.char_list.head.current_magic = 300
    party2.char_list.head.takeAction("Fireball", party1.char_list(1), party1)
    assert(party1.char_list(1).current_hp == 103)
    party1.char_list(1).current_magic = 300
    party1.char_list(1).lvl = 15
    assert(party2.char_list.head.current_hp == 89)
    party1.char_list(1).takeAction("Heal Party", party1.char_list.head, party1)
    assert(party1.char_list(1).current_hp == 119)
    assert(party1.char_list.head.current_hp == 130)
    assert(party2.char_list.head.current_hp == 89)
    party1.char_list.head.takeAction("Smash", party2.char_list.head, party2)
    assert(party2.char_list.head.current_hp == 46)
    assert(party1.char_list.head.current_hp == 115)
  }
}
