fetch("http://localhost:8080/api/auth/login", {
    method: "POST",
    headers: {
        "Content-Type": "application/json"
    },
    body: JSON.stringify({
        email: "usuario@ejemplo.com",
        password: "123456"
    })
})
.then(response => {
    if (response.ok) {
        window.location.href = "/siguiente-pagina.html";
    } else {
        alert("Credenciales incorrectas");
    }
});
