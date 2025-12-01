/**
 * Fetches the login form and listens to the login button
 */
document.querySelector(".form-login").addEventListener("submit", function (event) {
    event.preventDefault();

    const username = document.getElementById("login-username").value.trim();
    const password = document.getElementById("login-password").value.trim();

    if (!username || !password) {
        alert("Username and password are required!");
        return;
    }

    fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    })
        .then(response => response.json())
        .then(data => {
            localStorage.setItem("token", data.token);
            window.location.href = "/userprofile.html";
        })
        .catch(err => alert("Login failed: " + err));
});

/**
 * Fetches the signup form and listens to the signup button
 */
document.querySelector(".form-signup").addEventListener("submit", function (event) {
    event.preventDefault();

    const username = document.getElementById("signup-username").value.trim();
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

    fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, password })
    })
        .then(response => response.json())
        .then(data => alert("Registered successfully!"))
        .catch(err => alert("Registration failed: " + err));
});

/**
 * Switches between login and signup on click
 */
const switchers = [...document.querySelectorAll('.switcher')]
switchers.forEach(item => {
    item.addEventListener('click', function() {
        switchers.forEach(item => item.parentElement.classList.remove('is-active'))
        this.parentElement.classList.add('is-active')
    })
});

/**
 * Clears input
 */
const clearInput = () => {
    const input = document.getElementsByTagName("input")[0];
    input.value = "";
}
