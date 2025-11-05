
document.getElementById("login").addEventListener("click", function (event) {
    event.preventDefault();
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const user = {
        username,
        password,
    }

    fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user)
    })
    .then(response => response.json())
    .then(data => {console.log(data)})
    .catch(err => console.log(err));
})

