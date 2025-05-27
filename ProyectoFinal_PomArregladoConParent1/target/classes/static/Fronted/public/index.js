fetch("http://localhost:8080/login", {
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
        window.location.href = "formulario.html";
    } else {
        alert("Credenciales incorrectas");
    }
});
