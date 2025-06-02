import api from '../services/axiosService';

export const loginUsuario = async (credenciales) => {
  return api.post('/api/auth/login', credenciales);
};
