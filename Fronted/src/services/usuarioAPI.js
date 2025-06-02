import api from '../services/axiosService';

// POST: crear nuevo usuario
export const crearUsuario = async (datos) => {
  const res = await api.post('/api/usuarios/crear', datos);
  return res.data;
};

// POST: login
export const loginUsuario = async (credenciales) => {
  const res = await api.post('/api/auth/login', credenciales);
  return res.data;
};

// GET: obtener usuarios
export const obtenerUsuarios = async () => {
  const res = await api.get('/api/usuarios');
  return res.data;
};
