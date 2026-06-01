import axios from 'axios'

export const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_URL,
  timeout: 9000,
  headers: { Accept: 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('hotel_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config?.url?.includes('/auth/login')) {
      localStorage.removeItem('hotel_token')
      localStorage.removeItem('hotel_user')
    }
    return Promise.reject(error)
  },
)

const crud = (path) => ({
  listar: () => api.get(path),
  buscar: (id) => api.get(`${path}/${id}`),
  crear: (data) => api.post(path, data),
  actualizar: (id, data) => api.put(`${path}/${id}`, data),
  eliminar: (id) => api.delete(`${path}/${id}`),
})

export const authService = {
  login: (usuario, contrasena) => api.post('/auth/login', { usuario, contrasena }),
  me: () => api.get('/auth/me'),
}

export const productoService = {
  ...crud('/productos'),
  alertas: () => api.get('/productos/alertas'),
}

export const proveedorService = crud('/proveedores')
export const proveedorProductoService = crud('/proveedor-producto')
export const ingresoService = crud('/ingresos-inventario')
export const detalleIngresoService = crud('/detalles-ingreso')
export const distribucionService = crud('/distribuciones-insumos')
export const detalleDistribucionService = crud('/detalles-distribucion')
export const solicitudService = crud('/solicitudes-compra')
export const detalleSolicitudService = crud('/detalles-solicitud')
export const notificacionService = crud('/notificaciones')
export const movimientoService = crud('/movimientos-inventario')
export const usuarioService = {
  ...crud('/usuarios'),
  cambiarRol: (id, rolId) => api.patch(`/usuarios/${id}/rol`, { rolId }),
}
export const categoriaService = crud('/categorias')
export const unidadService = crud('/unidades-medida')
export const rolService = crud('/roles')
export const habitacionService = crud('/habitaciones')

export function getApiError(error, fallback = 'No se pudo completar la operacion.') {
  if (!error?.response) return 'El backend no esta disponible. Se muestran datos temporales.'
  return error.response.data?.message || error.response.data?.error || fallback
}

export default api
