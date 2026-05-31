const productosMock = [
  { id: 1, nombre: 'Shampoo', categoria: 'Limpieza', stockActual: 5, stockMinimo: 10, unidad: 'unidad' },
  { id: 2, nombre: 'Jabón de manos', categoria: 'Limpieza', stockActual: 20, stockMinimo: 15, unidad: 'unidad' },
  { id: 3, nombre: 'Papel higiénico', categoria: 'Higiene', stockActual: 3, stockMinimo: 20, unidad: 'paquete' },
  { id: 4, nombre: 'Desinfectante', categoria: 'Limpieza', stockActual: 8, stockMinimo: 5, unidad: 'litro' },
  { id: 5, nombre: 'Toallas', categoria: 'Habitación', stockActual: 2, stockMinimo: 10, unidad: 'unidad' },
]

function Alertas() {
  const criticos = productosMock
    .filter(p => p.stockActual <= p.stockMinimo)
    .map(p => ({ ...p, falta: p.stockMinimo - p.stockActual }))
    .sort((a, b) => b.falta - a.falta)

  return (
    <div style={{ padding: '24px', fontFamily: 'Arial' }}>
      <h2>⚠ Alertas de Stock Mínimo</h2>
      <p style={{ color: '#666' }}>
        {criticos.length} producto(s) con stock crítico
      </p>

      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ backgroundColor: '#1F3864', color: 'white' }}>
            <th style={{ padding: '10px', textAlign: 'left' }}>Producto</th>
            <th style={{ padding: '10px', textAlign: 'left' }}>Categoría</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Stock Actual</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Stock Mínimo</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Unidad</th>
            <th style={{ padding: '10px', textAlign: 'center' }}>Falta</th>
          </tr>
        </thead>
        <tbody>
          {criticos.map((p, i) => (
            <tr key={p.id} style={{
              backgroundColor: i % 2 === 0 ? '#FFF3F3' : '#FFE5E5',
              borderBottom: '1px solid #ddd'
            }}>
              <td style={{ padding: '10px', fontWeight: 'bold' }}>{p.nombre}</td>
              <td style={{ padding: '10px' }}>{p.categoria}</td>
              <td style={{ padding: '10px', textAlign: 'center', color: '#cc0000', fontWeight: 'bold' }}>
                {p.stockActual}
              </td>
              <td style={{ padding: '10px', textAlign: 'center' }}>{p.stockMinimo}</td>
              <td style={{ padding: '10px', textAlign: 'center' }}>{p.unidad}</td>
              <td style={{ padding: '10px', textAlign: 'center', color: '#cc0000', fontWeight: 'bold' }}>
                {p.falta}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default Alertas