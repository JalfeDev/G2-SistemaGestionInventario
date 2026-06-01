import { Navigate, Route, BrowserRouter, Routes } from 'react-router-dom'
import AppLayout from './components/AppLayout'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Productos from './pages/Productos'
import Entradas from './pages/Entradas'
import Distribuciones from './pages/Distribuciones'
import Proveedores from './pages/Proveedores'
import Usuarios from './pages/Usuarios'
import ImportacionCsv from './pages/ImportacionCsv'
import HistorialPrecios from './pages/HistorialPrecios'
import Solicitudes from './pages/Solicitudes'
import Alertas from './pages/Alertas'

function ProtectedLayout() {
  const hasSession = localStorage.getItem('hotel_token') || localStorage.getItem('hotel_demo')
  return hasSession ? <AppLayout /> : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route element={<ProtectedLayout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/productos" element={<Productos />} />
          <Route path="/entradas" element={<Entradas />} />
          <Route path="/distribuciones" element={<Distribuciones />} />
          <Route path="/proveedores" element={<Proveedores />} />
          <Route path="/usuarios" element={<Usuarios />} />
          <Route path="/importacion" element={<ImportacionCsv />} />
          <Route path="/historial-precios" element={<HistorialPrecios />} />
          <Route path="/solicitudes" element={<Solicitudes />} />
          <Route path="/alertas" element={<Alertas />} />
        </Route>
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
