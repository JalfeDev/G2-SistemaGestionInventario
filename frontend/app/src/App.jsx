import { Navigate, Route, BrowserRouter, Routes } from 'react-router-dom'
import AppLayout from './components/AppLayout'
import Login from './pages/Login'
import Productos from './pages/Productos'
import Entradas from './pages/Entradas'
import Distribuciones from './pages/Distribuciones'
import Usuarios from './pages/Usuarios'
import Stock from './pages/Stock'
import Alertas from './pages/Alertas'
import ReporteConsumo from './pages/ReporteConsumo'
import Configuracion from './pages/Configuracion'
import ImportacionCsv from './pages/ImportacionCsv'
import Dashboard from './pages/Dashboard'
import Solicitudes from './pages/Solicitudes'
import HistorialPrecios from './pages/HistorialPrecios'
import ReporteCostoProveedor from './pages/ReporteCostoProveedor'
import { defaultRouteForRole, getStoredUser, ROLES } from './utils/roles'

const ADMINISTRADOR = [ROLES.ADMINISTRADOR]
const GERENTE = [ROLES.GERENTE]
const ENCARGADO_ALMACEN = [ROLES.ENCARGADO_ALMACEN]
const HOUSEKEEPER = [ROLES.HOUSEKEEPER]
const ALMACEN_O_GERENTE = [ROLES.ENCARGADO_ALMACEN, ROLES.GERENTE]

function ProtectedLayout() {
  return localStorage.getItem('hotel_token') ? <AppLayout /> : <Navigate to="/login" replace />
}

function AllowedRoute({ roles, children }) {
  const user = getStoredUser()
  return roles.includes(user.rol) ? children : <Navigate to={defaultRouteForRole(user.rol)} replace />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route element={<ProtectedLayout />}>
          <Route path="/stock" element={<AllowedRoute roles={ENCARGADO_ALMACEN}><Stock /></AllowedRoute>} />
          <Route path="/alertas" element={<AllowedRoute roles={GERENTE}><Alertas /></AllowedRoute>} />
          <Route path="/productos" element={<AllowedRoute roles={ADMINISTRADOR}><Productos /></AllowedRoute>} />
          <Route path="/entradas" element={<AllowedRoute roles={ENCARGADO_ALMACEN}><Entradas /></AllowedRoute>} />
          <Route path="/distribuciones" element={<AllowedRoute roles={HOUSEKEEPER}><Distribuciones /></AllowedRoute>} />
          <Route path="/usuarios" element={<AllowedRoute roles={ADMINISTRADOR}><Usuarios /></AllowedRoute>} />
          <Route path="/reporte-consumo" element={<AllowedRoute roles={GERENTE}><ReporteConsumo /></AllowedRoute>} />
          <Route path="/configuracion" element={<AllowedRoute roles={ADMINISTRADOR}><Configuracion /></AllowedRoute>} />
          <Route path="/importacion-csv" element={<AllowedRoute roles={ADMINISTRADOR}><ImportacionCsv /></AllowedRoute>} />
          <Route path="/dashboard" element={<AllowedRoute roles={GERENTE}><Dashboard /></AllowedRoute>} />
          <Route path="/solicitudes" element={<AllowedRoute roles={ALMACEN_O_GERENTE}><Solicitudes /></AllowedRoute>} />
          <Route path="/historial-precios" element={<AllowedRoute roles={ALMACEN_O_GERENTE}><HistorialPrecios /></AllowedRoute>} />
          <Route path="/reporte-costos-proveedor" element={<AllowedRoute roles={GERENTE}><ReporteCostoProveedor /></AllowedRoute>} />
        </Route>
        <Route path="*" element={<Navigate to={localStorage.getItem('hotel_token') ? defaultRouteForRole(getStoredUser().rol) : '/login'} replace />} />
      </Routes>
    </BrowserRouter>
  )
}