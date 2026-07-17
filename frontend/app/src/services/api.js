import axios from 'axios'
import { getStoredUser } from '../utils/roles'

export const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_URL,
  timeout: 9000,
  headers: { Accept: 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('hotel_token')
  if (token && !config.url?.includes('/auth/login')) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    if (status === 401 && !error.config?.url?.includes('/auth/login')) {
      localStorage.setItem('hotel_auth_message', 'Sesión expirada o no autorizada.')
      localStorage.removeItem('hotel_token')
      localStorage.removeItem('hotel_user')
      if (window.location.pathname !== '/login') window.location.assign('/login')
    }
    if (status === 403) {
      console.warn('No tienes permisos para esta acción.', {
        endpoint: `${error.config?.method?.toUpperCase() || 'REQUEST'} ${error.config?.url || ''}`,
        rol: getStoredUser().rol || 'SIN_ROL',
      })
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
  logout: () => api.post('/auth/logout'),
}

export const productoService = {
  ...crud('/productos'),
  alertas: () => api.get('/productos/alertas'),
  importarCsv: (archivo) => {
    const formData = new FormData()
    formData.append('archivo', archivo)
    return api.post('/productos/importar-csv', formData)
  },
}
export const importacionCsvService = {
  importar: (archivo) => productoService.importarCsv(archivo),
}

export const proveedorService = crud('/proveedores')
export const proveedorProductoService = crud('/proveedor-producto')
export const ingresoService = {
  listar: () => api.get('/ingresos-inventario'),
  registrar: (data) => api.post('/ingresos-inventario', data),
}
export const detalleIngresoService = crud('/detalles-ingreso')
export const distribucionService = {
  listar: (params = {}) => api.get('/distribuciones-insumos', { params }),
  registrar: (data) => api.post('/distribuciones-insumos', data),
}
export const detalleDistribucionService = crud('/detalles-distribucion')
export const solicitudService = {
  ...crud('/solicitudes-compra'),
  listarPorUsuario: (usuarioId) => api.get(`/solicitudes-compra/usuario/${usuarioId}`),
  revisar: (id, payload) => api.put(`/solicitudes-compra/${id}/revisar`, payload),
}
export const solicitudReabastecimientoService = solicitudService
export const detalleSolicitudService = crud('/detalles-solicitud')
export const notificacionService = crud('/notificaciones')
export const notificacionStockService = {
  listar: () => api.get('/notificaciones-stock'),
  ultimas: () => api.get('/notificaciones-stock/ultimas'),
}
export const movimientoService = crud('/movimientos-inventario')
export const usuarioService = {
  ...crud('/usuarios'),
  cambiarRol: (id, rolId) => api.patch(`/usuarios/${id}/rol`, { rolId }),
  cambiarActivo: (id, activo) => api.patch(`/usuarios/${id}/activo`, { activo }),
}
export const categoriaService = crud('/categorias')
export const unidadService = crud('/unidades-medida')
export const rolService = crud('/roles')
export const habitacionService = crud('/habitaciones')
export const reporteService = {
  consultar: (fechaInicio, fechaFin) => api.get('/reportes/consumo', { params: { fechaInicio, fechaFin } }),
  descargarPdf: (fechaInicio, fechaFin) => api.get('/reportes/consumo/pdf', { params: { fechaInicio, fechaFin }, responseType: 'blob' }),
}
export const reporteCostoProveedorService = {
  consultar: (proveedorId, fechaInicio, fechaFin) =>
    api.get('/reportes/costos-proveedor', { params: { proveedorId, fechaInicio, fechaFin } }),
  descargarPdf: (proveedorId, fechaInicio, fechaFin) =>
    api.get('/reportes/costos-proveedor/pdf', { params: { proveedorId, fechaInicio, fechaFin }, responseType: 'blob' }),
}
export const reporteCostosProveedorService = reporteCostoProveedorService
export const dashboardService = {
  consultar: () => api.get('/dashboard'),
}
export const historialPreciosService = {
  consultar: (productoId, proveedorId) =>
    api.get('/ingresos-inventario/historial-precios', { params: { productoId, proveedorId } }),
}

export function isCanceledRequest(error) {
  return axios.isCancel(error) || error?.code === 'ERR_CANCELED' || error?.name === 'AbortError'
}

export function getApiError(error, fallback = 'No se pudo completar la operacion.') {
  if (isCanceledRequest(error)) return ''
  if (error?.code === 'ECONNABORTED') return 'La operacion tardo demasiado. Revisa si el backend esta enviando correos y vuelve a intentar.'
  const status = error?.response?.status
  const detail = error?.response?.data?.detail || error?.response?.data?.message || error?.response?.data?.error
  if (status === 401) {
    return error.config?.url?.includes('/auth/login')
      ? detail || fallback
      : 'Sesión expirada o no autorizada.'
  }
  if (status === 403) return 'No tienes permisos para esta acción.'
  if (status === 404) return 'Endpoint no encontrado.'
  if (status >= 500) return 'Error interno del servidor.'
  if (!error?.response) return 'Backend no disponible. Verifica que el servicio este iniciado.'
  return detail || fallback
}

export default api
