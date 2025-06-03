async function guardarFirma() {
  const contratoId = document.getElementById("contratoId").value;
  if (!contratoId) {
    alert("Por favor ingresa un ID de contrato.");
    return;
  }

  const canvas = document.getElementById("canvasFirma");
  const firmaBase64 = canvas.toDataURL("image/png");

  const response = await fetch("/api/firmas-temporales", {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      contratoId: parseInt(contratoId),
      imagenBase64: firmaBase64
    })
  });

  const data = await response.text();
  alert("Respuesta del backend: " + data);
}

function verPreview() {
  const contratoId = document.getElementById("contratoId").value;
  if (!contratoId) {
    alert("Por favor ingresa un ID de contrato.");
    return;
  }
  window.open("/api/firmas-temporales/preview/" + contratoId, "_blank");
}

function limpiarCanvas() {
  const canvas = document.getElementById("canvasFirma");
  const ctx = canvas.getContext("2d");
  ctx.clearRect(0, 0, canvas.width, canvas.height);
}

// Código para dibujar en el canvas
const canvas = document.getElementById('canvasFirma');
const ctx = canvas.getContext('2d');
let dibujando = false;

canvas.addEventListener('mousedown', () => dibujando = true);
canvas.addEventListener('mouseup', () => dibujando = false);
canvas.addEventListener('mouseout', () => dibujando = false);
canvas.addEventListener('mousemove', dibujar);

function dibujar(event) {
  if (!dibujando) return;
  ctx.lineWidth = 2;
  ctx.lineCap = 'round';
  ctx.strokeStyle = '#000';

  const rect = canvas.getBoundingClientRect();
  ctx.lineTo(event.clientX - rect.left, event.clientY - rect.top);
  ctx.stroke();
  ctx.beginPath();
  ctx.moveTo(event.clientX - rect.left, event.clientY - rect.top);
}
