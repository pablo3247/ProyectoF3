import React, { useState } from "react";

function FormularioUsuario() {
  const [formData, setFormData] = useState({
    nombre: "",
    apellidos: "",
    dnnni: "",
    correo: "",
    telefono: ""
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    fetch("http://localhost:8080/api/usuarios", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formData)
    })
      .then((res) => {
        if (!res.ok) throw new Error("Error al guardar");
        return res.json();
      })
      .then((data) => {
        alert("Datos enviados correctamente");
        console.log(data);
      })
      .catch((err) => {
        console.error(err);
        alert("Error al enviar los datos");
      });
  };

  return (
    <div style={{ maxWidth: "400px", margin: "auto" }}>
      <h2>Datos del Cliente</h2>
      <form onSubmit={handleSubmit}>
        <input name="nombre" placeholder="Nombre" value={formData.nombre} onChange={handleChange} required />
        <input name="apellidos" placeholder="Apellidos" value={formData.apellidos} onChange={handleChange} required />
        <input name="dnnni" placeholder="DNI" value={formData.dnnni} onChange={handleChange} required />
        <input name="correo" placeholder="Correo electrónico" type="email" value={formData.correo} onChange={handleChange} required />
        <input name="telefono" placeholder="Teléfono" value={formData.telefono} onChange={handleChange} required />
        <button type="submit">Continuar</button>
      </form>
    </div>
  );
}

export default FormularioUsuario;
