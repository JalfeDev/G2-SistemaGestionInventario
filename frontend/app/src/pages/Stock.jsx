import { useState } from 'react'

const productosMock = [
  { id: 1, nombre: 'Shampoo', categoria: 'Limpieza', stockActual: 5, stockMinimo: 10, unidad: 'unidad' },
  { id: 2, nombre: 'Jabón de manos', categoria: 'Limpieza', stockActual: 20, stockMinimo: 15, unidad: 'unidad' },
  { id: 3, nombre: 'Papel higiénico', categoria: 'Higiene', stockActual: 3, stockMinimo: 20, unidad: 'paquete' },
  { id: 4, nombre: 'Desinfectante', categoria: 'Limpieza', stockActual: 8, stockMinimo: 5, unidad: 'litro' },
  { id: 5, nombre: 'Toallas', categoria: 'Habitación', stockActual: 2, stockMinimo: 10, unidad: 'unidad' },
]

function Stock() {
  const [busqueda, setBusqueda] = useState('')
  const [categoriaFiltro, setCategoriaFiltro] = useState('')

  const categorias = [...new Set(productosMock.map(p => p.categoria))]

  const productosFiltrados = productosMock.filter(p => {
    const coincideNombre = p.nombre.toLowerCase().includes(busqueda.toLowerCase())
    const coincideCategoria = categoriaFiltro === '' || p.categoria === categoriaFiltro
    return coincideNombre && coincideCategoria
  })

  return (
    <div style={{ padding: '24px', fontFamily: 'Arial' }}>
      <h2>Consulta de Stock Actual</h2>

      <div style={{ display: 'flex', gap: '12px', marginBottom: '16px' }}>
        <input
          type="text"
          placeholder="Buscar producto..."
          value={busqueda}
          onChange={e => setBusqueda(e.target.value)}
          style={{ padding: '8px', width: '250px', borderRadius: '4px', border: '1px solid #ccc' }}
        />
        <select
          value={categoriaFiltro}
          onChange={e => setCategoriaFiltro(e.target.value)}
          style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
        >
          <option value="">Todas las categorías</option>
          {categorias.map(c => <option key={c} value={c}>{c}</option>)}
        </select>
      </div>

      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ backgroundColor: '#1F3864', color: 'white' }}>
            <th style={{ padding: '10px', textAlign: 'left' }}>Producto</th>
            <th style={{ padding: '10px', textAlign: 'left' }}>Categoría</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Stock Actual</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Stock Mínimo</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Unidad</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Estado</th>
          </tr>
        </thead>
        <tbody>
          {productosFiltrados.map((p, i) => {
            const enAlerta = p.stockActual <= p.stockMinimo
            return (
              <tr
                key={p.id}
                style={{
                  backgroundColor: enAlerta ? '#FFE5E5' : i % 2 === 0 ? '#f9f9f9' : 'white',
                  borderBottom: '1px solid #ddd'
                }}
              >
                <td style={{ padding: '10px' }}>{p.nombre}</td>
                <td style={{ padding: '10px' }}>{p.categoria}</td>
                <td style={{ padding: '10px', textAlign: 'center', fontWeight: 'bold',
                  color: enAlerta ? '#cc0000' : '#2e7d32' }}>
                  {p.stockActual}
                </td>
                <td style={{ padding: '10px', textAlign: 'center' }}>{p.stockMinimo}</td>
                <td style={{ padding: '10px', textAlign: 'center' }}>{p.unidad}</td>
                <td style={{ padding: '10px', textAlign: 'center' }}>
                  {enAlerta
                    ? <span style={{ color: '#cc0000', fontWeight: 'bold' }}>⚠ Stock crítico</span>
                    : <span style={{ color: '#2e7d32' }}>✓ Normal</span>
                  }
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}

export default Stock