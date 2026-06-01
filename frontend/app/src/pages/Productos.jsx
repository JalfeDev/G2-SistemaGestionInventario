import { useState } from 'react'

const productosMock = [
  { id: 1, nombre: 'Shampoo', categoria: 'Limpieza', stockActual: 5, stockMinimo: 10 },
  { id: 2, nombre: 'Jabón de manos', categoria: 'Limpieza', stockActual: 20, stockMinimo: 15 },
  { id: 3, nombre: 'Papel higiénico', categoria: 'Higiene', stockActual: 3, stockMinimo: 20 },
  { id: 4, nombre: 'Desinfectante', categoria: 'Limpieza', stockActual: 8, stockMinimo: 5 },
  { id: 5, nombre: 'Toallas', categoria: 'Habitación', stockActual: 2, stockMinimo: 10 },
]

const categoriasMock = ['Limpieza', 'Higiene', 'Habitación', 'Mantenimiento']

function Productos() {
  const [productos, setProductos] = useState(productosMock)
  const [mostrarForm, setMostrarForm] = useState(false)
  const [form, setForm] = useState({ nombre: '', categoria: '', stockMinimo: '' })
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')

  const inputStyle = {
    width: '100%', padding: '10px', borderRadius: '4px',
    border: '1px solid #ccc', boxSizing: 'border-box', fontSize: '14px'
  }

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setError('')
  }

  const handleCrear = (e) => {
    e.preventDefault()
    if (!form.nombre || !form.categoria || !form.stockMinimo) {
      setError('Completa todos los campos')
      return
    }
    const duplicado = productos.find(
      p => p.nombre.toLowerCase() === form.nombre.toLowerCase() &&
           p.categoria === form.categoria
    )
    if (duplicado) {
      setError('Ya existe un producto con ese nombre en esa categoría')
      return
    }
    const nuevo = {
      id: productos.length + 1,
      nombre: form.nombre,
      categoria: form.categoria,
      stockActual: 0,
      stockMinimo: parseInt(form.stockMinimo)
    }
    setProductos([...productos, nuevo])
    setMensaje(`✓ Producto "${form.nombre}" registrado correctamente`)
    setForm({ nombre: '', categoria: '', stockMinimo: '' })
    setMostrarForm(false)
  }

  const handleEliminar = (id) => {
    setProductos(productos.filter(p => p.id !== id))
    setMensaje('✓ Producto eliminado')
  }

  return (
    <div style={{ padding: '24px', fontFamily: 'Arial' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2 style={{ color: '#1F3864', margin: 0 }}>Gestión de Productos</h2>
        <button
          onClick={() => { setMostrarForm(!mostrarForm); setError(''); setMensaje('') }}
          style={{
            padding: '10px 20px', backgroundColor: '#1F3864',
            color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer'
          }}
        >
          {mostrarForm ? 'Cancelar' : '+ Nuevo producto'}
        </button>
      </div>

      {mensaje && <p style={{ color: '#2e7d32', marginBottom: '12px' }}>{mensaje}</p>}

      {mostrarForm && (
        <div style={{ backgroundColor: '#f0f4f8', padding: '20px', borderRadius: '8px', marginBottom: '24px', maxWidth: '500px' }}>
          <h3 style={{ marginTop: 0, color: '#1F3864' }}>Nuevo producto</h3>
          <form onSubmit={handleCrear}>
            <div style={{ marginBottom: '12px' }}>
              <label style={{ display: 'block', marginBottom: '6px' }}>Nombre</label>
              <input name="nombre" value={form.nombre} onChange={handleChange}
                placeholder="Ej: Shampoo" style={inputStyle} />
            </div>
            <div style={{ marginBottom: '12px' }}>
              <label style={{ display: 'block', marginBottom: '6px' }}>Categoría</label>
              <select name="categoria" value={form.categoria} onChange={handleChange} style={inputStyle}>
                <option value="">Selecciona una categoría</option>
                {categoriasMock.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div style={{ marginBottom: '12px' }}>
              <label style={{ display: 'block', marginBottom: '6px' }}>Stock mínimo</label>
              <input name="stockMinimo" type="number" min="0" value={form.stockMinimo}
                onChange={handleChange} placeholder="Ej: 10" style={inputStyle} />
            </div>
            {error && <p style={{ color: '#cc0000', marginBottom: '12px' }}>⚠ {error}</p>}
            <button type="submit" style={{
              padding: '10px 20px', backgroundColor: '#2e7d32',
              color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer'
            }}>
              Guardar producto
            </button>
          </form>
        </div>
      )}

      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ backgroundColor: '#1F3864', color: 'white' }}>
            <th style={{ padding: '10px', textAlign: 'left' }}>Nombre</th>
            <th style={{ padding: '10px', textAlign: 'left' }}>Categoría</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Stock actual</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Stock mínimo</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {productos.map((p, i) => (
            <tr key={p.id} style={{
              backgroundColor: i % 2 === 0 ? '#f9f9f9' : 'white',
              borderBottom: '1px solid #ddd'
            }}>
              <td style={{ padding: '10px' }}>{p.nombre}</td>
              <td style={{ padding: '10px' }}>{p.categoria}</td>
              <td style={{ padding: '10px', textAlign: 'center' }}>{p.stockActual}</td>
              <td style={{ padding: '10px', textAlign: 'center' }}>{p.stockMinimo}</td>
              <td style={{ padding: '10px', textAlign: 'center' }}>
                <button
                  onClick={() => handleEliminar(p.id)}
                  style={{
                    padding: '6px 12px', backgroundColor: '#cc0000',
                    color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer'
                  }}
                >
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default Productos