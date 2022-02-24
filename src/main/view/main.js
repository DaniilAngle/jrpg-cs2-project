
const jsonTest = '{"playerParty":{"characters":[{"name":"Mango", "type": "Mage","mp": 100,"max_mp": 100, "hp":50, "maxHP":70,' +
    ' "battleOptions":["Physical Attack", "Fireblast", "Heal"]},{"name":"Sagusa","type": "Warrior","mp": 100,"max_mp": 100, "hp":10, "maxHP":70, ' +
    '"battleOptions":["Physical Attack", "Fireblast"]},{"name":"SukaBlj", "type": "Healer","mp": 100,"max_mp": 100, "hp":50, "maxHP":70,' +
    ' "battleOptions":["Physical Attack", "Fireblast"]}]}, "enemyParty": {"characters":[{"name":"Sasugo", "type": "Warrior","mp": 100,"max_mp": 100, ' +
    '"hp":50, "maxHP":70, "battleOptions":["Physical Attack", "Fireblast"]},{"name":"Kusy", "type": "Warrior","mp": 100,"max_mp": 100, ' +
    '"hp":50, "maxHP":70, "battleOptions":["Physical Attack", "Fireblast"]},{"name":"Fusy", "type": "Warrior","mp": 100,"max_mp": 100, ' +
    '"hp":50, "maxHP":70, "battleOptions":["Physical Attack", "Fireblast"]}]}}'

let battleOptions = {}
let enemyCharImgID = {}
let charSpecs = {}
let allyCharImgID = {}

function setImg(type, div, hp) {
    if (hp === 0) {
        div.src = "/dead.png"
    } else {
        switch (type) {
            case "Mage":
                div.src = "/Mage.png"
                break;
            case "Warrior":
                div.src = "/Warrior.png"
                break;
            case "Healer":
                div.src = "/Healer.png"
        }
    }
}

function setName(name, div) {
    div.innerText = name
}


function setStats(character, div) {
    div.innerText = "HP: " + character.hp.toString() + "/" + character.maxHP.toString() + " MP: " + character.mp.toString() +
        "/" + character.max_mp.toString()
}
function update(json_data) {
    let battle_specs = JSON.parse(json_data)
    let i = 1
    //player party
    for (let character of battle_specs.playerParty.characters) {
        let nameID = "name"+i.toString()
        let nameDiv = document.getElementById(nameID)
        setName(character.name, nameDiv)
        let statDiv = document.getElementById("stats"+i.toString())
        setStats(character, statDiv)
        let imgID = "char" + i.toString()
        let typeDiv = document.getElementById(imgID)
        setImg(character.type, typeDiv, character.hp)
        battleOptions[character.name] = character.battleOptions
        allyCharImgID[imgID] = [character.hp, character.name]
        charSpecs[character.name] = [character.hp, nameID, imgID]
        i++
    }
    i = 1
    //enemy party
    for (let character of battle_specs.enemyParty.characters) {
        let nameID = "enemy-name"+i.toString()
        let nameDiv = document.getElementById(nameID)
        setName(character.name, nameDiv)
        let statDiv = document.getElementById("enemy-stats" + i.toString())
        setStats(character, statDiv)
        let imgID = "enemy-char" + i.toString()
        let typeDiv = document.getElementById(imgID)
        setImg(character.type, typeDiv, character.hp)
        enemyCharImgID[imgID] = [character.hp, character.name]
        charSpecs[character.name] = [character.hp, nameID, imgID]
        i++
    }
}

function getOffset(el) {
    const rect = el.getBoundingClientRect();
    return {
        left: rect.left + window.scrollX,
        top: rect.top + window.scrollY
    };
}

function animate_(heroName, enemyName, value) {
    let charDiv = document.getElementById(charSpecs[heroName][1])
    charDiv.setAttribute('style', 'color: #f7f6cd')

    for (let character in charSpecs) {
        let imgDiv = document.getElementById(charSpecs[character][2])
        imgDiv.setAttribute('onclick', '')
        imgDiv.setAttribute('style', 'cursor: auto; width: 50px')
    }

    let enemyImg = document.getElementById(charSpecs[enemyName][2])
    let infoDiv = document.getElementById('battle-state-info')
    if (value < 0) {
        infoDiv.innerText = `${heroName} has dealt ${value*-1} damage to ${enemyName}`
        enemyImg.className = 'fighting'
        enemyImg.onanimationend = () => {
            enemyImg.className = 'char'
        }
    } else if (value === 0) {
        infoDiv.innerText = `${heroName} dealt no damage to the ${enemyName}`
        enemyImg.className = 'no-damage'
        enemyImg.onanimationend = () => {
            enemyImg.className = 'char'
        }
    } else {
        infoDiv.innerText = `${heroName} restored ${value} health to ${enemyName}`
        enemyImg.className = 'healing'
        enemyImg.onanimationend = () => {
            enemyImg.className = 'char'
        }
    }
}

function animateHelper(heroName, enemyName, value) {
    animate_(heroName,enemyName,value)
    let div = document.getElementById("battle-option-buttons")
    while (div.hasChildNodes()) {
        div.removeChild(div.firstChild)
    }
}


// Controller

function enemySelection(name) {
    for (let character in enemyCharImgID) {
        if (enemyCharImgID[character][0] > 0) {
            let char = document.getElementById(character)
            char.setAttribute('onclick', `animateHelper("${name}", "${enemyCharImgID[character][1]}", ${0})`)
            char.setAttribute('style', 'cursor: pointer; width: 50px')
        }
    }
}

function allySelection(name) {
    for (let character in allyCharImgID) {
        if (allyCharImgID[character][0] > 0) {
            let char = document.getElementById(character)
            char.setAttribute('onclick', `animateHelper("${name}", "${allyCharImgID[character][1]}", ${15})`)
            char.setAttribute('style', 'cursor: pointer; width: 50px')
        }
    }
}

function takeTurn(name) {
    let charName = document.getElementById(charSpecs[name][1])
    charName.setAttribute('style', 'color: red')
    let infoDiv = document.getElementById('battle-state-info')
    infoDiv.innerText = ""
    let div = document.getElementById("battle-option-buttons")
    while (div.hasChildNodes()) {
        div.removeChild(div.firstChild)
    }
    for (let option of battleOptions[name]) {
        let optionButton = document.createElement("button")
        optionButton.className = "btn btn-primary"
        optionButton.innerText = option
        if (option === "Heal" || option === "Heal Party") {
            optionButton.setAttribute('onclick', `allySelection("${name}")`)
            div.appendChild(optionButton)
        } else {
            optionButton.setAttribute('onclick', `enemySelection("${name}")`)
            div.appendChild(optionButton)
        }
    }
}

function loseBattle() {
    const loseBattleTest = '{"playerParty":{"characters":[{"name":"Masago", "type": "Mage","mp": 100,"max_mp": 100, "hp":0, "maxHP":70,' +
        ' "battleOptions":["Physical Attack", "Fireblast", "Heal"]},{"name":"Sagusa","type": "Warrior","mp": 100,"max_mp": 100, "hp":0, "maxHP":70, ' +
        '"battleOptions":["Physical Attack", "Fireblast"]},{"name":"SukaBlyatj", "type": "Healer","mp": 100,"max_mp": 100, "hp":0, "maxHP":70,' +
        ' "battleOptions":["Physical Attack", "Fireblast"]}]}, "enemyParty": {"characters":[{"name":"Sasugo", "type": "Warrior","mp": 100,"max_mp": 100, ' +
        '"hp":50, "maxHP":70, "battleOptions":["Physical Attack", "Fireblast"]},{"name":"Kusy", "type": "Warrior","mp": 100,"max_mp": 100, ' +
        '"hp":50, "maxHP":70, "battleOptions":["Physical Attack", "Fireblast"]},{"name":"Fusy", "type": "Warrior","mp": 100,"max_mp": 100, ' +
        '"hp":50, "maxHP":70, "battleOptions":["Physical Attack", "Fireblast"]}]}}'
    update(loseBattleTest)
}

function  winBattle() {
    const winBattleTest =  '{"playerParty":{"characters":[{"name":"Masago", "type": "Mage","mp": 100,"max_mp": 100, "hp":50, "maxHP":70,' +
        ' "battleOptions":["Physical Attack", "Fireblast", "Heal"]},{"name":"Sagusa","type": "Warrior","mp": 100,"max_mp": 100, "hp":10, "maxHP":70, ' +
        '"battleOptions":["Physical Attack", "Fireblast"]},{"name":"SukaBlyatj", "type": "Healer","mp": 100,"max_mp": 100, "hp":50, "maxHP":70,' +
        ' "battleOptions":["Physical Attack", "Fireblast"]}]}, "enemyParty": {"characters":[{"name":"Sasugo", "type": "Warrior","mp": 100,"max_mp": 100, ' +
        '"hp":0, "maxHP":70, "battleOptions":["Physical Attack", "Fireblast"]},{"name":"Kusy", "type": "Warrior","mp": 100,"max_mp": 100, ' +
        '"hp":0, "maxHP":70, "battleOptions":["Physical Attack", "Fireblast"]},{"name":"Fusy", "type": "Warrior","mp": 100,"max_mp": 100, ' +
        '"hp":0, "maxHP":70, "battleOptions":["Physical Attack", "Fireblast"]}]}}'
    update(winBattleTest)
}


function restore() {
    update(jsonTest)
}