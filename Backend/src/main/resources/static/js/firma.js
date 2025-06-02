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



const canvas = document.getElementById('canvasFirma');
const ctx = canvas.getContext('2d');
let dibujando = false;

canvas.addEventListener('mousedown', () => dibujando = true);
canvas.addEventListener('mouseup', () => dibujando = false);
canvas.addEventListener('mouseout', () => dibujando = false);
canvas.addEventListener('mousemove', dibujar);

function dibujar(evento) {
  if (!dibujando) return;
  const rect = canvas.getBoundingClientRect();
  const x = evento.clientX - rect.left;
  const y = evento.clientY - rect.top;
  ctx.lineWidth = 2;
  ctx.lineCap = 'round';
  ctx.strokeStyle = '#000';
  ctx.lineTo(x, y);
  ctx.stroke();
  ctx.beginPath();
  ctx.moveTo(x, y);
}

function limpiarCanvas() {
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  ctx.beginPath();
}

function guardarCanvasComoImagen() {
  const dataURL = canvas.toDataURL('image/png');
  document.getElementById('firmaDatos').value = dataURL;
}
