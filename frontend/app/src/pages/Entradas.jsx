import { useState } from 'react'

const productosMock = [
  { id: 1, nombre: 'Shampoo' },
  { id: 2, nombre: 'Jabón de manos' },
  { id: 3, nombre: 'Papel higiénico' },
  { id: 4, nombre: 'Desinfectante' },
  { id: 5, nombre: 'Toallas' },
]

const proveedoresMock = [
  { id: 1, nombre: 'Proveedor Lima S.A.' },
  { id: 2, nombre: 'Distribuidora Norte' },
  { id: 3, nombre: 'Insumos Perú' },
]

function Entradas() {
  const [form, setForm] = useState({
    productoId: '', proveedorId: '', cantidad: '', precioUnitario: ''
  })
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')

  const costoTotal = form.cantidad && form.precioUnitario
    ? (parseFloat(form.cantidad) * parseFloat(form.precioUnitario)).toFixed(2)
    : '0.00'

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setMensaje('')
    setError('')
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!form.productoId || !form.proveedorId || !form.cantidad || !form.precioUnitario) {
      setError('Completa todos los campos')
      return
    }
    if (parseInt(form.cantidad) <= 0) {
      setError('La cantidad debe ser mayor a cero')
      return
    }
    setMensaje(`✓ Entrada registrada. Stock actualizado. Costo total: S/ ${costoTotal}`)
    setForm({ productoId: '', proveedorId: '', cantidad: '', precioUnitario: '' })
  }

  const inputStyle = {
    width: '100%', padding: '10px', borderRadius: '4px',
    border: '1px solid #ccc', boxSizing: 'border-box', fontSize: '14px'
  }

  return (
    <div style={{ padding: '24px', fontFamily: 'Arial', maxWidth: '600px' }}>
      <h2 style={{ color: '#1F3864' }}>Registro de Entrada de Insumos</h2>

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '16px' }}>
          <label style={{ display: 'block', marginBottom: '6px' }}>Producto</label>
          <select name="productoId" value={form.productoId} onChange={handleChange} style={inputStyle}>
            <option value="">Selecciona un producto</option>
            {productosMock.map(p => <option key={p.id} value={p.id}>{p.nombre}</option>)}
          </select>
        </div>

        <div style={{ marginBottom: '16px' }}>
          <label style={{ display: 'block', marginBottom: '6px' }}>Proveedor</label>
          <select name="proveedorId" value={form.proveedorId} onChange={handleChange} style={inputStyle}>
            <option value="">Selecciona un proveedor</option>
            {proveedoresMock.map(p => <option key={p.id} value={p.id}>{p.nombre}</option>)}
          </select>
        </div>

        <div style={{ marginBottom: '16px' }}>
          <label style={{ display: 'block', marginBottom: '6px' }}>Cantidad</label>
          <input
            type="number" name="cantidad" value={form.cantidad}
            onChange={handleChange} placeholder="Ej: 50" style={inputStyle}
          />
        </div>

        <div style={{ marginBottom: '16px' }}>
          <label style={{ display: 'block', marginBottom: '6px' }}>Precio por unidad (S/)</label>
          <input
            type="number" name="precioUnitario" value={form.precioUnitario}
            onChange={handleChange} placeholder="Ej: 5.50" step="0.01" style={inputStyle}
          />
        </div>

        <div style={{ marginBottom: '20px', padding: '12px', backgroundColor: '#f0f4f8',
          borderRadius: '4px', border: '1px solid #ddd' }}>
          <strong>Costo total calculado: S/ {costoTotal}</strong>
        </div>

        {error && <p style={{ color: '#cc0000', marginBottom: '12px' }}>⚠ {error}</p>}
        {mensaje && <p style={{ color: '#2e7d32', marginBottom: '12px' }}>{mensaje}</p>}

        <button type="submit" style={{
          padding: '12px 24px', backgroundColor: '#1F3864',
          color: 'white', border: 'none', borderRadius: '4px',
          fontSize: '16px', cursor: 'pointer'
        }}>
          Registrar entrada
        </button>
      </form>
    </div>
  )
}

export default Entradas