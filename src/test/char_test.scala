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
    val win_party: Party = new Party(char1)
    win_party.add_party_member(char2)
    win_party.add_party_member(char3)
    val char4: Character = new Character()
    val char5: Character = new Character()
    val char6: Character = new Character()
    val defeat_party: Party = new Party(char4)
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
    val party: Party = new Party(char1)
    party.add_party_member(char2)
    party.add_party_member(char3)
    party.add_party_member(char4)
    party.add_party_member(char5)
    assert(party.char_list.length == 4)
  }
  test("type test, attacks, aoe attacks") {
    val warrior: Warrior = new Warrior()
    val healer: Healer = new  Healer()
    val mage: Mage = new Mage()
    val party1: Party = new Party(warrior)
    val party2: Party = new Party(mage)
    party1.add_party_member(healer)
    party1.char_list(0).curValues()
    party1.char_list(1).curValues()
    party2.char_list(0).curValues()
  }
}
