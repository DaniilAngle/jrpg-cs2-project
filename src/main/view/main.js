const socket = io.connect("http://localhost:8080/", {transports: ['websocket']});

let battleLayoutHTML = '<head>\n' +
    '    <meta charset="UTF-8">\n' +
    '    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">\n' +
    '    <link rel="stylesheet" href="styles.css">\n' +
    '    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/socket.io/2.2.0/socket.io.js"></script>\n' +
    '    <script src="main.js"></script>\n' +
    '    <title>...</title>\n' +
    '</head>\n' +
    '<body>\n' +
    '    <h1 id="battlePlayers" style="color: brown; text-align: center; padding-top: 1vh">admin VS black</h1>\n' +
    '    <div id="frame" class="main-frame">\n' +
    '        <img src="" id="sword">\n' +
    '        <div class="vstack gap-4">\n' +
    '            <div class="container">\n' +
    '                <div class="row justify-content-between">\n' +
    '                    <div class="col">\n' +
    '                        <div id="name1" class="char-name"></div>\n' +
    '                        <div id="stats1" class="char-stats"></div>\n' +
    '                        <img id="char1" class="char" style=" width: 50px">\n' +
    '                    </div>\n' +
    '                    <div class="col float-end">\n' +
    '                        <div id="enemy-name1" class="enemy-char-name"></div>\n' +
    '                        <div id="enemy-stats1" class="enemy-char-stats"></div>\n' +
    '                        <img id="enemy-char1" class="char float-end" style=" width: 50px">\n' +
    '                    </div>\n' +
    '                </div>\n' +
    '                <div class="row justify-content-between">\n' +
    '                    <div id="second-char" class="col">\n' +
    '                        <div id="name2" class="char-name"></div>\n' +
    '                        <div id="stats2" class="char-stats"></div>\n' +
    '                        <img id="char2" class="char" style=" width: 50px">\n' +
    '                    </div>\n' +
    '                    <div id="second-enemy-char" class="col float-end">\n' +
    '                        <div id="enemy-name2" class="enemy-char-name"></div>\n' +
    '                        <div id="enemy-stats2" class="enemy-char-stats"></div>\n' +
    '                        <img id="enemy-char2" class="char float-end" style=" width: 50px">\n' +
    '                    </div>\n' +
    '                </div>\n' +
    '                <div class="row justify-content-between">\n' +
    '                    <div class="col">\n' +
    '                        <div id="name3" class="char-name"></div>\n' +
    '                        <div id="stats3" class="char-stats"></div>\n' +
    '                        <img id="char3" class="char" style=" width: 50px">\n' +
    '                    </div>\n' +
    '                    <div class="col float-end">\n' +
    '                        <div id="enemy-name3" class="enemy-char-name"></div>\n' +
    '                        <div id="enemy-stats3" class="enemy-char-stats"></div>\n' +
    '                        <img id="enemy-char3" class="char float-end" style=" width: 50px">\n' +
    '                    </div>\n' +
    '                </div>\n' +
    '            </div>\n' +
    '        </div>\n' +
    '    </div>\n' +
    '    <div class="container-fluid text-center">\n' +
    '        <div class="row">\n' +
    '            </div>\n' +
    '            <div id="battle-state-info" class="col">\n' +
    '            </div>\n' +
    '            <div id="battle-option-list" class="col">\n' +
    '                <div id="battle-option-buttons" class="vstack">\n' +
    '                </div>\n' +
    '            </div>\n' +
    '        </div>\n' +
    '    </div>\n' +
    '<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p" crossorigin="anonymous"></script>\n' +
    '</body>'

let lobbyHTML = '<head>\n' +
    '    <meta charset="UTF-8">\n' +
    '    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">\n' +
    '    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/socket.io/2.2.0/socket.io.js"></script>\n' +
    '    <script src="main.js"></script>\n' +
    '    <title>...</title>\n' +
    '</head>\n' +
    '<body style="background: blanchedalmond">\n' +
    '<h1 style="text-align: center" id="lobbyMain">Lobby</h1>\n' +
    '<div style="text-align: center; padding-top: 2vh" id="lobbyPlayers"></div>\n' +
    '</body>'

let charSelector = "<head>\n" +
    "  <meta charset=\"UTF-8\">\n" +
    "  <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3\" crossorigin=\"anonymous\">\n" +
    "  <script type=\"text/javascript\" src=\"https://cdnjs.cloudflare.com/ajax/libs/socket.io/2.2.0/socket.io.js\"></script>\n" +
    "  <script src=\"main.js\"></script>\n" +
    "  <title>...</title>\n" +
    "</head>\n" +
    "<body style=\"padding: 3vh; background: blanchedalmond\">\n" +
    "<h1>Select your heroes</h1>\n" +
    "<p>First Hero</p>\n" +
    "<label for=\"char1Name\">Name</label><input id=\"char1Name\">\n" +
    "<button id=\"char1Mage\" onclick=\"addChar('mage', 0);\">Mage</button>\n" +
    "<button id=\"char1Warrior\" onclick=\"addChar('warrior', 0)\">Warrior</button>\n" +
    "<button id=\"char1Healer\" onclick=\"addChar('healer', 0)\">Healer</button>\n" +
    "<p>Second Hero</p>\n" +
    "<label for=\"char2Name\">Name</label><input id=\"char2Name\">\n" +
    "<button id=\"char2Mage\" onclick=\"addChar('mage', 1);\">Mage</button>\n" +
    "<button id=\"char2Warrior\" onclick=\"addChar('warrior', 1)\">Warrior</button>\n" +
    "<button id=\"char2Healer\" onclick=\"addChar('healer', 1)\">Healer</button>\n" +
    "<p>Third Hero</p>\n" +
    "<label for=\"char3Name\">Name</label><input id=\"char3Name\">\n" +
    "<button id=\"char3Mage\" onclick=\"addChar('mage', 2);\">Mage</button>\n" +
    "<button id=\"char3Warrior\" onclick=\"addChar('warrior', 2)\">Warrior</button>\n" +
    "<button id=\"char3Healer\" onclick=\"addChar('healer', 2)\">Healer</button>\n" +
    "<hr>\n" +
    "<button id=\"submitDecision\" onclick=\"sendDecision()\">Submit</button>\n" +
    "</body>"

loginFail();
regFail();
logSuccess();
lobbyChange();
moveToBattle();
battleGameState();
battleResult();
turnReceiver();
battleGameState();
decisionResultUpdate();

//registration

let user = ""

function regClicked() {
    user = document.getElementById('username').value
    document.cookie = `username=${user}; path=/jrpg-cs2-project/view`
    let password = document.getElementById('password').value
    if (user.length !== 0 && password.length >= 4) {
        let data = {'username' : user, 'password' : password}
        socket.emit('regClicked', JSON.stringify(data))
    } else if (user.length === 0 && password.length === 0) {
        document.getElementById('info').innerText = "Please fill in the spaces"
    } else {
        document.getElementById('info').innerText = "Password is too short"
    }
}

function loginClicked() {
    document.getElementById('info').innerText = ""
    user = document.getElementById('username').value
    document.cookie = `username=${user}; path=/jrpg-cs2-project/view`
    let password = document.getElementById('password').value
    if (user.length !== 0 && password.length !==0) {
        let data = {'username': user, 'password': password}
        socket.emit('logClicked', JSON.stringify(data))
    } else {
        document.getElementById('info').innerText = "Please fill in the spaces"
    }
}

function logSuccess() {
    socket.on('credentialsCorrect', function () {
        document.querySelector('html').innerHTML = lobbyHTML
        document.getElementById('lobbyMain').innerText = `Lobby: ${user}`
        socket.emit('lobbyEntered')
    });
}

function regSuccess() {
    socket.on('regSuccess', function () {
        document.querySelector('html').innerHTML = charSelector
    })
}

regSuccess();

function loginFail() {
    socket.on('loginFailure', function () {
        document.getElementById('info').innerText = "Username or password is incorrect"
    });
}
function regFail() {
    socket.on('regFailure', function () {
        document.getElementById('info').innerText = "Username already exists"
    });
}

//charSelection

let charTypeList = ["", "", ""]

function addChar(type, idx) {
    charTypeList[idx] = type
}

function sendDecision() {
    let name1 = document.getElementById('char1Name').value
    let name2 = document.getElementById('char2Name').value
    let name3 = document.getElementById('char3Name').value
    let charNameList = [name1, name2, name3]
    if (checkValidity(charNameList) && checkValidity(charTypeList)) {
        socket.emit('charsSelected', JSON.stringify({"characterNames": charNameList, "characterTypes": charTypeList}))
        document.querySelector('html').innerHTML = lobbyHTML
        document.getElementById('lobbyMain').innerText = `Lobby: ${user}`
        socket.emit('lobbyEntered')
    }

}

function checkValidity(list) {
    let check = true
    for (let element of list) {
        if (element === "") {
            check = false
        }
    }
    return check
}


//lobby

let selectedEnemy = ""

function lobbyChange() {
    socket.on('lobbyUpdate', function (lobbyJSON) {
        while (document.getElementById('lobbyPlayers').hasChildNodes()) {
            document.getElementById('lobbyPlayers').removeChild(document.getElementById('lobbyPlayers').firstChild)
        }
        let lobbyData = JSON.parse(lobbyJSON)
        let lobby = document.getElementById("lobbyPlayers")
        for (let element of lobbyData) {
            let button = document.createElement("button")
            button.innerText = element
            button.className = "btn btn-primary"
            button.setAttribute('onclick', `battleEnemySelect("${element}")`)
            lobby.appendChild(button)
        }
    })
}

function battleEnemySelect(enemy) {
    user = document.cookie.replace("username=", "")
    if (enemy !== user) {
        console.log(enemy, user)
        selectedEnemy = enemy
        socket.emit('battleStarted', enemy.toString())
        document.querySelector('html').innerHTML = battleLayoutHTML
        document.getElementById('battlePlayers').innerText = `${user} vs ${selectedEnemy}`
    }
}

//battle

function moveToBattle() {
    socket.on('battleCall', function (enemyUsername) {
        selectedEnemy = enemyUsername
        document.querySelector('html').innerHTML = battleLayoutHTML
        document.getElementById('battlePlayers').innerText = `${user} vs ${selectedEnemy}`
    })
}

function battleGameState() {
    socket.on('updateGameState', function (gameStateFromServer) {
        console.log(gameStateFromServer)
        update(gameStateFromServer)
    })
}

function decisionSender(heroName, enemyName, option) {
    console.log(heroName + " attacked " + enemyName + " with " + option, "208")
    if (option === "Heal" || option === "Heal Party") {
        socket.emit('turnDecision', JSON.stringify({"userPartyID": user, "enemyPartyID": user,"hero": heroName, "enemy": enemyName, "option": option}))
    } else {
        socket.emit('turnDecision', JSON.stringify({"userPartyID": user, "enemyPartyID": selectedEnemy,"hero": heroName, "enemy": enemyName, "option": option}))
    }
}

function decisionResultUpdate() {
    socket.on('turnResult', function (turnResultJson) {
        let turnResult = JSON.parse(turnResultJson)
        let heroName = turnResult["hero"]
        let enemyName = turnResult["enemy"]
        let value = turnResult["valueOfMove"]
        animateHelper(heroName, enemyName, value)
    })
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function  battleResult() {
    socket.on('battleEnded', async function (winner) {
        document.getElementById('battle-state-info').innerText = `The winner of the battle is ${winner}`
        selectedEnemy = ""
        await sleep(4000)
        document.querySelector('html').innerHTML = lobbyHTML
        if (user === winner) {
            socket.emit('lobbyEntered')
            document.getElementById('lobbyMain').innerText = `Lobby: ${user}`
        } else {
            document.querySelector('html').innerHTML = charSelector
        }
    })
}

function turnReceiver() {
    socket.on('takeTurn', function (charName) {
        takeTurn(charName)
    })
}
//GUI

let battleOptions = {}
let enemyCharImgID = {}
let charSpecs = {}
let allyCharImgID = {}
let userCharsNamesToNameID = {}

function setImg(type, div, hp) {
    if (hp === 0) {
        div.src = "/dead.png"
    } else {
        switch (type) {
            case "mage":
                div.src = "/Mage.png"
                break;
            case "warrior":
                div.src = "/Warrior.png"
                break;
            case "healer":
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
        userCharsNamesToNameID[character.name] = nameID
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
    if (value > 0) {
        infoDiv.innerText = `${heroName} has dealt ${value} damage to ${enemyName}`
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
        infoDiv.innerText = `${heroName} restored ${value*(-1)} health to ${enemyName}`
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


// Control

function enemySelection(name, option) {
    for (let character in enemyCharImgID) {
        if (enemyCharImgID[character][0] > 0) {
            let char = document.getElementById(character)
            char.setAttribute('onclick', `decisionSender('${name}', "${enemyCharImgID[character][1]}", '${option}')`)
            char.setAttribute('style', 'cursor: pointer; width: 50px')
        }
    }
}

function allySelection(name, option) {
    for (let character in allyCharImgID) {
        if (allyCharImgID[character][0] > 0) {
            let char = document.getElementById(character)
            char.setAttribute('onclick', `decisionSender("${name}", "${allyCharImgID[character][1]}", "${option}")`)
            char.setAttribute('style', 'cursor: pointer; width: 50px')
        }
    }
}

function takeTurn(name) {
    let charName = document.getElementById(userCharsNamesToNameID[name])
    charName.setAttribute('style', 'color: red')
    let infoDiv = document.getElementById('battle-state-info')
    let div = document.getElementById("battle-option-buttons")
    while (div.hasChildNodes()) {
        div.removeChild(div.firstChild)
    }
    for (let option of battleOptions[name]) {
        let optionButton = document.createElement("button")
        optionButton.className = "btn btn-primary"
        optionButton.innerText = option
        if (option === "Heal" || option === "Heal Party") {
            optionButton.setAttribute('onclick', `allySelection("${name}", "${option}")`)
            div.appendChild(optionButton)
        } else {
            optionButton.setAttribute('onclick', `enemySelection("${name}", "${option}")`)
            div.appendChild(optionButton)
        }
    }
}