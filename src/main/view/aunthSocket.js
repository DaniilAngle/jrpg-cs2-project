//
// const socket = io.connect("http://localhost:8080/", {transports: ['websocket']});
// loginFail();
// regFail();
// logRegSuccess();
//
// function regClicked() {
//     let user = document.getElementById('username').value
//     let password = document.getElementById('password').value
//     if (user.length !== 0 && password.length >= 4) {
//         let data = {'username' : user, 'password' : password}
//         socket.emit('regClicked', JSON.stringify(data))
//     } else if (user.length === 0 && password.length === 0) {
//         document.getElementById('info').innerText = "Please fill in the spaces"
//     } else {
//         document.getElementById('info').innerText = "Password is too short"
//     }
// }
//
// function loginClicked() {
//     let user = document.getElementById('username').value
//     let password = document.getElementById('password').value
//     if (user.length !== 0 && password.length !==0) {
//         let data = {'username': user, 'password': password}
//         socket.emit('logClicked', JSON.stringify(data))
//     } else {
//         document.getElementById('info').innerText = "Please fill in the spaces"
//     }
// }

function logRegSuccess() {
    socket.on('credentialsCorrect', function () {
        document.location.href = './index.html'
    });
}

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