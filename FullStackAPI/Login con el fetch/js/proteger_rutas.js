const token = localStorage.getItem("token");

fetch("http://localhost:8080/api/usuarios", {
    headers: {
        "Authorization": "Bearer " + token
    }
});
