import org.scalatest.funsuite.AnyFunSuite
import character.Character

class char_test extends AnyFunSuite {
  test("Testing initial val") {
    val char1: Character = new Character()
    assert(char1.current_hp == 100)
  }

  test("Take dmg") {
    val dmg1: Character = new Character()
    assert(dmg1.current_hp == 100)
    dmg1.take_physical_damage(40)
    assert(dmg1.current_hp == 60)
    assert(dmg1.alive == true)
    dmg1.take_physical_damage(90)
    assert(dmg1.current_hp == 0)
    assert(dmg1.alive == false)

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
    assert(char2.current_hp == 82)
    char1.magic_attack(char2, 100)
    assert(char2.current_hp == 82)
    assert(char1.current_magic == 90)
  }
}
