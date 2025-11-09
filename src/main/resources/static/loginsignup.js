/**
 * Pobiera formularz logowania i nasluchuje bttn logowania
 */
document.querySelector(".form-login").addEventListener("submit", function (event) {
    event.preventDefault(); //blokuje domyslne wyslanie formualrza

    //pobiera wartoscdi wpisane trim usuwa biale znaki
    const username = document.getElementById("login-username").value.trim();
    const password = document.getElementById("login-password").value.trim();

    if (!username || !password) {
        alert("Username and password are required!");
        return;
    }

    fetch("http://localhost:8080/api/auth/login", { //endpoint POST logowania
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }) //przekazuje w request zmienne
    })
        .then(response => response.json())
        .then(data => {
            localStorage.setItem("token", data.token); //zapisuje token w localStorage
            window.location.href = "/userprofile.html"; //przenosi do profilu usera
        })
        .catch(err => alert("Login failed: " + err));
})

/**
 * Pobiera formularz rejstarcji i nasluchuje bttn logowania
 */
document.querySelector(".form-signup").addEventListener("submit", function (event) {
    event.preventDefault(); //blokuje domyslne wyslanie formualrza

    const username = document.getElementById("signup-username").value.trim(); //pobiera wartoscdi wpisane trim usuwa biale znaki
    const email = document.getElementById("signup-email").value.trim();
    const password = document.getElementById("signup-password").value.trim();
    const passwordConfirm = document.getElementById("signup-password-confirm").value.trim();


    if (!username || !email || !password || !passwordConfirm) {
        alert("All fields are required!");
        return;
    }

    if (password !== passwordConfirm) {
        alert("Passwords do not match!");
        return;
    }

    if (password.length < 6) {
        alert("Password must be at least 6 characters!");
        return;
    }

    fetch("http://localhost:8080/api/auth/register", { //endpoint POST rejstracji
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, password }) //przekazuje w request zmienne
    })
        .then(response => response.json())
        .then(data => alert("Registered successfully!"))
        .catch(err => alert("Registration failed: " + err));
})

/**
 * Po kliku przełacza login na signup i odwrotnie
 */
const switchers = [...document.querySelectorAll('.switcher')]
switchers.forEach(item => {
    item.addEventListener('click', function() {
        switchers.forEach(item => item.parentElement.classList.remove('is-active'))
        this.parentElement.classList.add('is-active')
    })
})

/**
 * czysci input
 */
const clearInput = () => {
    const input = document.getElementsByTagName("input")[0];
    input.value = "";
}




