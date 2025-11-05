document.getElementById("register").addEventListener("click", function (event) {
    event.preventDefault();
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const email = document.getElementById("email").value;

    const newUser = {
        username,
        password,
        email,
    }

    fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(newUser)
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            alert("Registered! Redirecting to login...");
            window.location.href = "/login.html";
        })
        .catch(err => console.log(err));
})