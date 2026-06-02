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
import { defaultRouteForRole, getStoredUser, ROLES } from './utils/roles'

const ADMINISTRADOR = [ROLES.ADMINISTRADOR]
const GERENTE = [ROLES.GERENTE]
const ENCARGADO_ALMACEN = [ROLES.ENCARGADO_ALMACEN]
const HOUSEKEEPER = [ROLES.HOUSEKEEPER]

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
        </Route>
        <Route path="*" element={<Navigate to={localStorage.getItem('hotel_token') ? defaultRouteForRole(getStoredUser().rol) : '/login'} replace />} />
      </Routes>
    </BrowserRouter>
  )
}
