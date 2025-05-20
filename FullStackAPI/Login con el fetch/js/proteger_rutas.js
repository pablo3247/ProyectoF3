const response = await fetch("https://tu-api.com/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password })
});

const data = await response.json();

if (response.ok) {
    // data.token, data.user, etc.
}
