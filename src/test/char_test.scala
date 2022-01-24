import org.scalatest.funsuite.AnyFunSuite
import character.{Character, Party}

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
    assert(char2.current_hp == 72)
    char1.magic_attack(char2, 100)
    assert(char2.current_hp == 72)
    assert(char1.current_magic == 90)
  }

  test("gain exp character"){
    val char1: Character = new Character()
    val char2: Character = new Character()
    assert(char1.lvl == 1)
    assert(char1.exp == 0)
    assert(char1.lvl_up_exp == 100)
    char1.gain_exp(char1.gained_exp(char2))
    assert(char1.exp == 10)
    char2.lvl = 4
    char1.take_physical_damage(20)
    assert(char1.current_hp == 83)
    char1.gain_exp(char1.gained_exp(char2))
    char1.lvl_up()
    assert(char1.exp == 70)
    assert(char1.lvl == 2)
    assert(char1.armor == 4)
    assert(char1.current_hp == 110)
    assert(char1.hp == 110)
    assert(char1.lvl_up_exp == 150)
    char2.lvl = 12
    char1.gain_exp(char1.gained_exp(char2))
    char1.lvl_up()
    assert(char1.lvl == 6)
    assert(char1.lvl_up_exp == 757)
    assert(char1.current_hp == 130)
  }

  test("party test"){
    val win_party: Party = new Party()
    val char1: Character = new Character()
    val char2: Character = new Character()
    val char3: Character = new Character()
    win_party.add_party_member(char1)
    win_party.add_party_member(char2)
    win_party.add_party_member(char3)
    val char4: Character = new Character()
    val char5: Character = new Character()
    val char6: Character = new Character()
    val defeat_party: Party = new Party()
    defeat_party.add_party_member(char4)
    defeat_party.add_party_member(char5)
    defeat_party.add_party_member(char6)
    win_party.fight_win(defeat_party)
    assert(win_party.char_list(0).exp == 10)
    win_party.char_list(0).alive = false
    win_party.fight_win(defeat_party)
    assert(win_party.char_list(0).exp == 10)
    assert(win_party.char_list(1).exp == 25)
    defeat_party.char_list(0).lvl = 4
    assert(defeat_party.char_list(0).lvl == 4)
    win_party.fight_win(defeat_party)
    assert(win_party.char_list(1).lvl == 2)
    assert(win_party.char_list(1).exp == 15)
  }

  test("party size test") {
    val party: Party = new Party()
    val char1: Character = new Character()
    val char2: Character = new Character()
    val char3: Character = new Character()
    val char4: Character = new Character()
    val char5: Character = new Character()
    party.add_party_member(char1)
    party.add_party_member(char2)
    party.add_party_member(char3)
    party.add_party_member(char4)
    party.add_party_member(char5)
    assert(party.char_list.length == 4)
  }

  test("party death test") {
    val win_party: Party = new Party()
    val defeat_party: Party = new Party()
    val char1: Character = new Character()
    val char2: Character = new Character()
    val char3: Character = new Character()
    val char4: Character = new Character()
    val char5: Character = new Character()
    val char6: Character = new Character()
    win_party.add_party_member(char1)
    win_party.add_party_member(char2)
    win_party.add_party_member(char3)
    defeat_party.add_party_member(char4)
    defeat_party.add_party_member(char5)
    defeat_party.add_party_member(char6)
    defeat_party.char_list(0).alive = false
    defeat_party.char_list(1).alive = false
    win_party.battle_end(defeat_party)
    assert(win_party.char_list(0).exp == 0)
    defeat_party.char_list(2).alive = false
    win_party.battle_end(defeat_party)
    assert(win_party.char_list(0).exp == 10)
  }
}
