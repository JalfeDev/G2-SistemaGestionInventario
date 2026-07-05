import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { API_URL, authService, getApiError } from '../services/api'
import { defaultRouteForRole, isReleaseRole, normalizeRole } from '../utils/roles'
import { Icon } from '../components/ui'

export default function Login() {
  const [form, setForm] = useState({ usuario: '', contrasena: '' })
  const [error, setError] = useState(() => {
    const message = localStorage.getItem('hotel_auth_message') || ''
    localStorage.removeItem('hotel_auth_message')
    return message
  })
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  async function submit(event) {
    event.preventDefault()
    if (!form.usuario || !form.contrasena) return setError('Ingresa tu usuario y contrasena.')
    setLoading(true)
    setError('')
    try {
      const { data } = await authService.login(form.usuario, form.contrasena)
      const role = normalizeRole(data.rol)
      if (!isReleaseRole(role)) {
        return setError('Tu rol no tiene una vista habilitada en Release 1.')
      }
      localStorage.setItem('hotel_token', data.token)
      localStorage.setItem('hotel_user', JSON.stringify({ nombre: data.nombre, rol: role }))
      navigate(defaultRouteForRole(role))
    } catch (requestError) {
      setError(getApiError(requestError, 'Credenciales incorrectas. Revisa los datos e intenta nuevamente.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <div className="login-visual">
        <div className="login-brand"><span>HP</span> Hotel Piramide</div>
        <div className="login-copy">
          <p className="eyebrow light">Gestion hotelera inteligente</p>
          <h1>Control preciso para una operacion impecable.</h1>
          <p>Administra insumos, housekeeping y abastecimiento desde un solo lugar.</p>
        </div>
        <div className="login-stats">
          <div><strong>360</strong><span>Vision integral</span></div>
          <div><strong>24/7</strong><span>Operacion continua</span></div>
          <div><strong>1</strong><span>Panel central</span></div>
        </div>
      </div>
      <div className="login-panel">
        <form className="login-form" onSubmit={submit}>
          <div className="login-mobile-brand"><span>HP</span> Hotel Piramide</div>
          <p className="eyebrow">Bienvenido</p>
          <h2>Inicia sesion</h2>
          <p className="muted">Accede al sistema de inventario del hotel.</p>
          <label className="field"><span>Usuario</span><input value={form.usuario} onChange={(event) => setForm({ ...form, usuario: event.target.value })} placeholder="Ingresa tu usuario" autoComplete="username" /></label>
          <label className="field"><span>Contrasena</span><input type="password" value={form.contrasena} onChange={(event) => setForm({ ...form, contrasena: event.target.value })} placeholder="Ingresa tu contrasena" autoComplete="current-password" /></label>
          {error && <div className="notice notice-danger">{error}</div>}
          <button className="button primary wide" disabled={loading}>{loading ? 'Validando acceso...' : 'Ingresar al sistema'}</button>
          <p className="login-endpoint"><Icon name="check" size={15} /> Conexion configurada: {API_URL}</p>
        </form>
      </div>
    </div>
  )
}
