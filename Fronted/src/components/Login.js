import React, { useState } from 'react';
import { loginUsuario } from '../api/usuarioAPI';

export default function Login() {
  const [credenciales, setCredenciales] = useState({ usuario: '', clave: '' });
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const respuesta = await loginUsuario(credenciales);
      console.log('Login correcto:', respuesta);
      // 🔐 Guarda token o redirige según tu lógica
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error al iniciar sesión');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Usuario"
        value={credenciales.usuario}
        onChange={e => setCredenciales({ ...credenciales, usuario: e.target.value })}
        required
      />
      <input
        type="password"
        placeholder="Contraseña"
        value={credenciales.clave}
        onChange={e => setCredenciales({ ...credenciales, clave: e.target.value })}
        required
      />
      <button type="submit">Iniciar sesión</button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </form>
  );
}
