async function filtrar() {
  const dni = document.getElementById('filtroDni').value;
  const apellidos = document.getElementById('filtroApellidos').value;
  const desde = document.getElementById('filtroDesde').value;
  const hasta = document.getElementById('filtroHasta').value;

  let url = "/api/contratos/filtrar?";
  if (dni) url += `dni=${encodeURIComponent(dni)}&`;
  if (apellidos) url += `apellidos=${encodeURIComponent(apellidos)}&`;
  if (desde && hasta) url += `desde=${desde}&hasta=${hasta}&`;

  try {
    const resp = await fetch(url);
    const datos = await resp.json();
    mostrarResultados(datos);
  } catch (error) {
    alert("Error al cargar contratos: " + error);
  }
}

function mostrarResultados(contratos) {
  const tabla = document.getElementById("tablaContratos");
  tabla.innerHTML = "";

  if (!contratos || contratos.length === 0) {
    tabla.innerHTML = "<tr><td colspan='6'>No se encontraron contratos</td></tr>";
    return;
  }

  contratos.forEach(c => {
    const fila = `
      <tr>
        <td>${c.id}</td>
        <td>${c.dni || ''}</td>
        <td>${c.apellidos || ''}</td>
        <td>${c.email}</td>
        <td>${c.fechaFirma || '-'}</td>
        <td>${c.estado || '-'}</td>
      </tr>`;
    tabla.innerHTML += fila;
  });
}

function limpiar() {
  document.getElementById('filtroDni').value = '';
  document.getElementById('filtroApellidos').value = '';
  document.getElementById('filtroDesde').value = '';
  document.getElementById('filtroHasta').value = '';
  filtrar();
}

// Cargar todos al entrar
filtrar();
