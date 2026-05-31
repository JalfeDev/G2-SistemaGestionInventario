import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

function Login() {
  const [usuario, setUsuario] = useState('')
  const [contrasena, setContrasena] = useState('')
  const [error, setError] = useState('')

  const navigate = useNavigate()

  const handleLogin = (e) => {
    e.preventDefault()
    if (!usuario || !contrasena) {
      setError('Ingresa tu usuario y contraseña')
      return
    }
    if (usuario === 'admin' && contrasena === '123') {
      navigate('/stock')
    } else {
      setError('Usuario o contraseña incorrectos')
    }
  }

  return (
    <div style={{
      display: 'flex', justifyContent: 'center', alignItems: 'center',
      height: '100vh', backgroundColor: '#f0f2f5', fontFamily: 'Arial'
    }}>
      <div style={{
        backgroundColor: 'white', padding: '40px', borderRadius: '8px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)', width: '360px'
      }}>
        <h2 style={{ textAlign: 'center', color: '#1F3864', marginBottom: '8px' }}>
          Hotel Pirámide
        </h2>
        <p style={{ textAlign: 'center', color: '#666', marginBottom: '24px' }}>
          Sistema de Gestión de Inventario
        </p>
        <form onSubmit={handleLogin}>
          <div style={{ marginBottom: '16px' }}>
            <label style={{ display: 'block', marginBottom: '6px' }}>Usuario</label>
            <input
              type="text"
              value={usuario}
              onChange={e => setUsuario(e.target.value)}
              placeholder="Ingresa tu usuario"
              style={{ width: '100%', padding: '10px', borderRadius: '4px',
                border: '1px solid #ccc', boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: '16px' }}>
            <label style={{ display: 'block', marginBottom: '6px' }}>Contraseña</label>
            <input
              type="password"
              value={contrasena}
              onChange={e => setContrasena(e.target.value)}
              placeholder="Ingresa tu contraseña"
              style={{ width: '100%', padding: '10px', borderRadius: '4px',
                border: '1px solid #ccc', boxSizing: 'border-box' }}
            />
          </div>
          {error && <p style={{ color: '#cc0000', marginBottom: '12px' }}>⚠ {error}</p>}
          <button type="submit" style={{
            width: '100%', padding: '12px', backgroundColor: '#1F3864',
            color: 'white', border: 'none', borderRadius: '4px', fontSize: '16px', cursor: 'pointer'
          }}>
            Iniciar sesión
          </button>
        </form>
      </div>
    </div>
  )
}

export default Login