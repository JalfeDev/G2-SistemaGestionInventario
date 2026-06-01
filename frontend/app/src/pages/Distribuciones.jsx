import { useState } from 'react'

const productosMock = [
  { id: 1, nombre: 'Shampoo', stockActual: 5 },
  { id: 2, nombre: 'Jabón de manos', stockActual: 20 },
  { id: 3, nombre: 'Papel higiénico', stockActual: 3 },
  { id: 4, nombre: 'Desinfectante', stockActual: 8 },
  { id: 5, nombre: 'Toallas', stockActual: 2 },
]

const habitacionesMock = [
  { id: 1, numero: '101' }, { id: 2, numero: '102' },
  { id: 3, numero: '201' }, { id: 4, numero: '202' },
  { id: 5, numero: '301' },
]

const historialMock = [
  { id: 1, producto: 'Shampoo', habitacion: '101', cantidad: 2, fecha: '2026-05-30' },
  { id: 2, producto: 'Toallas', habitacion: '202', cantidad: 3, fecha: '2026-05-29' },
]

function Distribuciones() {
  const [form, setForm] = useState({ productoId: '', habitacionId: '', cantidad: '' })
  const [historial, setHistorial] = useState(historialMock)
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')

  const inputStyle = {
    width: '100%', padding: '10px', borderRadius: '4px',
    border: '1px solid #ccc', boxSizing: 'border-box', fontSize: '14px'
  }

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setMensaje('')
    setError('')
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!form.productoId || !form.habitacionId || !form.cantidad) {
      setError('Completa todos los campos')
      return
    }
    const cantidad = parseInt(form.cantidad)
    if (cantidad <= 0) {
      setError('La cantidad debe ser mayor a cero')
      return
    }
    const producto = productosMock.find(p => p.id === parseInt(form.productoId))
    if (cantidad > producto.stockActual) {
      setError(`Stock insuficiente. Disponible: ${producto.stockActual}`)
      return
    }
    const habitacion = habitacionesMock.find(h => h.id === parseInt(form.habitacionId))
    const nuevaDistribucion = {
      id: historial.length + 1,
      producto: producto.nombre,
      habitacion: habitacion.numero,
      cantidad,
      fecha: new Date().toISOString().split('T')[0]
    }
    setHistorial([nuevaDistribucion, ...historial])
    setMensaje(`✓ Distribución registrada. Stock de ${producto.nombre} actualizado.`)
    setForm({ productoId: '', habitacionId: '', cantidad: '' })
  }

  return (
    <div style={{ padding: '24px', fontFamily: 'Arial' }}>
      <h2 style={{ color: '#1F3864' }}>Registro de Distribución — Housekeeping</h2>

      <div style={{ maxWidth: '600px', marginBottom: '32px' }}>
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '16px' }}>
            <label style={{ display: 'block', marginBottom: '6px' }}>Producto</label>
            <select name="productoId" value={form.productoId} onChange={handleChange} style={inputStyle}>
              <option value="">Selecciona un producto</option>
              {productosMock.map(p => (
                <option key={p.id} value={p.id}>{p.nombre} (Stock: {p.stockActual})</option>
              ))}
            </select>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={{ display: 'block', marginBottom: '6px' }}>Habitación</label>
            <select name="habitacionId" value={form.habitacionId} onChange={handleChange} style={inputStyle}>
              <option value="">Selecciona una habitación</option>
              {habitacionesMock.map(h => (
                <option key={h.id} value={h.id}>Habitación {h.numero}</option>
              ))}
            </select>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={{ display: 'block', marginBottom: '6px' }}>Cantidad</label>
            <input
                type="number" name="cantidad" value={form.cantidad}
                onChange={handleChange} placeholder="Ej: 2" 
                step="1"
                min="1"
                style={inputStyle}
            />
          </div>

          {error && <p style={{ color: '#cc0000', marginBottom: '12px' }}>⚠ {error}</p>}
          {mensaje && <p style={{ color: '#2e7d32', marginBottom: '12px' }}>{mensaje}</p>}

          <button type="submit" style={{
            padding: '12px 24px', backgroundColor: '#1F3864',
            color: 'white', border: 'none', borderRadius: '4px',
            fontSize: '16px', cursor: 'pointer'
          }}>
            Registrar distribución
          </button>
        </form>
      </div>

      <h3 style={{ color: '#1F3864' }}>Historial de distribuciones</h3>
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ backgroundColor: '#1F3864', color: 'white' }}>
            <th style={{ padding: '10px', textAlign: 'left' }}>Producto</th>
            <th style={{ padding: '10px', textAlign: 'left' }}>Habitación</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Cantidad</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Fecha</th>
          </tr>
        </thead>
        <tbody>
          {historial.map((h, i) => (
            <tr key={h.id} style={{
              backgroundColor: i % 2 === 0 ? '#f9f9f9' : 'white',
              borderBottom: '1px solid #ddd'
            }}>
              <td style={{ padding: '10px' }}>{h.producto}</td>
              <td style={{ padding: '10px' }}>Hab. {h.habitacion}</td>
              <td style={{ padding: '10px', textAlign: 'center' }}>{h.cantidad}</td>
              <td style={{ padding: '10px', textAlign: 'center' }}>{h.fecha}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default Distribuciones