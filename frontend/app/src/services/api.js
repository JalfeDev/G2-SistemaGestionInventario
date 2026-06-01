import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: BASE_URL,
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const productoService = {
  listar: () => api.get('/productos'),
  crear: (data) => api.post('/productos', data),
  actualizar: (id, data) => api.put(`/productos/${id}`, data),
  eliminar: (id) => api.delete(`/productos/${id}`),
  alertas: () => api.get('/productos/alertas'),
}

export const authService = {
  login: (usuario, contrasena) =>
    api.post('/auth/login', { usuario, contrasena }),
}

export const entradaService = {
  registrar: (data) => api.post('/entradas', data),
}

export const usuarioService = {
  listar: () => api.get('/usuarios'),
  crear: (data) => api.post('/usuarios', data),
}

export default api