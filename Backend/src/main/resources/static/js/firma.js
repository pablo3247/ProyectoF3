async function guardarFirma() {
  const contratoId = document.getElementById("contratoId").value;
  const datosFirma = document.getElementById("firmaDatos").value;

  const response = await fetch("/api/contratos/firma-temporal", {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      contratoId: parseInt(contratoId),
      datosFirma: datosFirma
    })
  });

  const data = await response.text();
  alert("Respuesta del backend: " + data);
}

function verPreview() {
  const contratoId = document.getElementById("contratoId").value;
  window.open("/api/contratos/preview/" + contratoId, "_blank");
}
