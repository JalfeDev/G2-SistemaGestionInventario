import { useState } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { Icon } from './ui'

const navigation = [
  ['dashboard', 'dashboard', 'Dashboard'],
  ['productos', 'box', 'Productos'],
  ['entradas', 'arrowDown', 'Entradas'],
  ['distribuciones', 'rooms', 'Housekeeping'],
  ['proveedores', 'truck', 'Proveedores'],
  ['usuarios', 'users', 'Usuarios'],
  ['importacion', 'upload', 'Importacion CSV'],
  ['historial-precios', 'chart', 'Historial de precios'],
  ['solicitudes', 'clipboard', 'Reabastecimiento'],
  ['alertas', 'bell', 'Alertas de stock'],
]

export default function AppLayout() {
  const [open, setOpen] = useState(false)
  const navigate = useNavigate()
  const user = JSON.parse(localStorage.getItem('hotel_user') || '{}')

  function logout() {
    localStorage.removeItem('hotel_token')
    localStorage.removeItem('hotel_user')
    localStorage.removeItem('hotel_demo')
    navigate('/login')
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
          {navigation.map(([path, icon, label]) => (
            <NavLink key={path} to={`/${path}`} onClick={() => setOpen(false)} className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              <Icon name={icon} size={18} /><span>{label}</span>
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
            <div className="avatar">{(user.nombre || 'D').slice(0, 1)}</div>
            <div><strong>{user.nombre || 'Modo demostracion'}</strong><span>{user.rol || 'Vista temporal'}</span></div>
            <button className="icon-button" onClick={logout} title="Cerrar sesion"><Icon name="logout" size={18} /></button>
          </div>
        </header>
        <main className="content"><Outlet /></main>
      </div>
      {open && <button className="sidebar-overlay" onClick={() => setOpen(false)} aria-label="Cerrar menu" />}
    </div>
  )
}
