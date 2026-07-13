import { useState } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useApiResource } from '../hooks/useApiResource'
import { authService, productoService } from '../services/api'
import { getStoredUser, roleLabel, ROLES } from '../utils/roles'
import { Icon } from './ui'

const navigation = [
  { path: 'usuarios', icon: 'users', label: 'Usuarios', roles: [ROLES.ADMINISTRADOR] },
  { path: 'productos', icon: 'box', label: 'Productos', roles: [ROLES.ADMINISTRADOR] },
  { path: 'configuracion', icon: 'clipboard', label: 'Configuracion', roles: [ROLES.ADMINISTRADOR] },
  { path: 'importacion-csv', icon: 'upload', label: 'Importar CSV', roles: [ROLES.ADMINISTRADOR] },
  { path: 'reporte-consumo', icon: 'chart', label: 'Reportes', roles: [ROLES.GERENTE] },
  { path: 'reporte-costos-proveedor', icon: 'chart', label: 'Costos por proveedor', roles: [ROLES.GERENTE] },
  { path: 'alertas', icon: 'bell', label: 'Alertas', roles: [ROLES.GERENTE, ROLES.ADMINISTRADOR] },
  { path: 'dashboard', icon: 'chart', label: 'Dashboard', roles: [ROLES.GERENTE] },
  { path: 'entradas', icon: 'arrowDown', label: 'Entradas', roles: [ROLES.ENCARGADO_ALMACEN] },
  { path: 'stock', icon: 'box', label: 'Stock', roles: [ROLES.ENCARGADO_ALMACEN] },
  { path: 'solicitudes', icon: 'clipboard', label: 'Solicitudes', roles: [ROLES.ENCARGADO_ALMACEN, ROLES.GERENTE] },
  { path: 'historial-precios', icon: 'chart', label: 'Historial de precios', roles: [ROLES.ENCARGADO_ALMACEN, ROLES.GERENTE] },
  { path: 'distribuciones', icon: 'rooms', label: 'Distribuciones', roles: [ROLES.HOUSEKEEPER] },
]

export default function AppLayout() {
  const [open, setOpen] = useState(false)
  const navigate = useNavigate()
  const user = getStoredUser()
  const alertasActivas = useApiResource(productoService.alertas)

  async function logout() {
    try {
      await authService.logout()
    } finally {
      localStorage.removeItem('hotel_token')
      localStorage.removeItem('hotel_user')
      navigate('/login')
    }
  }

  return (
    <div className="app-shell">
      <aside className={`sidebar ${open ? 'sidebar-open' : ''}`}>
        <div className="brand">
          <div className="brand-mark">HP</div>
          <div><strong>Hotel Piramide</strong><span>Inventory suite</span></div>
        </div>
        <div className="sidebar-label">Menu principal</div>
        <nav>
          {navigation.filter((item) => item.roles.includes(user.rol)).map((item) => (
            <NavLink key={item.path} to={`/${item.path}`} onClick={() => setOpen(false)} className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              <Icon name={item.icon} size={18} /><span>{item.label}</span>
              {item.path === 'alertas' && alertasActivas.data.length > 0 && <span className="badge badge-danger">{alertasActivas.data.length}</span>}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <span>Conexion API</span>
          <strong><i /> Backend Java</strong>
        </div>
      </aside>
      <div className="main-panel">
        <header className="topbar">
          <button className="icon-button mobile-menu" onClick={() => setOpen(!open)} aria-label="Abrir menu"><Icon name="menu" /></button>
          <div className="topbar-title"><span>Sistema de inventario</span><strong>Operacion hotelera</strong></div>
          <div className="topbar-user">
            <div className="avatar">{(user.nombre || 'U').slice(0, 1)}</div>
            <div><strong>{user.nombre || 'Usuario'}</strong><span>{roleLabel(user.rol)}</span></div>
            <button className="icon-button" onClick={logout} title="Cerrar sesion"><Icon name="logout" size={18} /></button>
          </div>
        </header>
        <main className="content"><Outlet /></main>
      </div>
      {open && <button className="sidebar-overlay" onClick={() => setOpen(false)} aria-label="Cerrar menu" />}
    </div>
  )
}
