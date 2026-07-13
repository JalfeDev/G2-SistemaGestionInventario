import { useCallback, useEffect, useState } from 'react'
import { Card, Empty, Field, Loader, Notice, PageHeader } from '../components/ui'
import { fallbackProductos, fallbackProveedores } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { getApiError, historialPreciosService, productoService, proveedorService } from '../services/api'
import { formatDate, money } from '../utils/formatters'

export default function HistorialPrecios() {
  const productos = useApiResource(productoService.listar, fallbackProductos)
  const proveedores = useApiResource(proveedorService.listar, fallbackProveedores)
  const [filtros, setFiltros] = useState({ productoId: '', proveedorId: '' })
  const [historial, setHistorial] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const items = historial?.historial || []

  const consultarHistorial = useCallback((cancelado = () => false) => {
    setLoading(true)
    setError('')
    return historialPreciosService.consultar(filtros.productoId || undefined, filtros.proveedorId || undefined)
      .then(({ data }) => { if (!cancelado()) setHistorial(data) })
      .catch((requestError) => { if (!cancelado()) setError(getApiError(requestError)) })
      .finally(() => { if (!cancelado()) setLoading(false) })
  }, [filtros.productoId, filtros.proveedorId])

  useEffect(() => {
    let cancelado = false
    const timer = window.setTimeout(() => consultarHistorial(() => cancelado), 0)
    return () => {
      cancelado = true
      window.clearTimeout(timer)
    }
  }, [consultarHistorial])

  return (
    <>
      <PageHeader title="Historial de precios" description="Consulta costos registrados por producto y proveedor en cada ingreso." />
      {error && <Notice tone="danger">{error}</Notice>}
      <Card className="form-card">
        <div className="form-grid">
          <Field label="Producto">
            <select value={filtros.productoId} onChange={(event) => setFiltros({ ...filtros, productoId: event.target.value })}>
              <option value="">Todos los productos</option>
              {productos.data.map((item) => <option key={item.id} value={item.id}>{item.nombre}</option>)}
            </select>
          </Field>
          <Field label="Proveedor">
            <select value={filtros.proveedorId} onChange={(event) => setFiltros({ ...filtros, proveedorId: event.target.value })}>
              <option value="">Todos los proveedores</option>
              {proveedores.data.map((item) => <option key={item.id} value={item.id}>{item.nombre}</option>)}
            </select>
          </Field>
        </div>
      </Card>
      {loading ? <Loader /> : historial && (
        <>
          <Card><div className="card-title"><h3>Precio promedio</h3><strong>{money(historial.precioPromedio)}</strong></div></Card>
          <Card>{items.length === 0 ? <Empty text="No hay precios registrados para los filtros seleccionados." /> : <div className="table-wrap"><table><thead><tr><th>Fecha de ingreso</th><th>Producto</th><th>Proveedor</th><th>Cantidad</th><th>Costo unitario</th><th>Total</th></tr></thead><tbody>{items.map((item, index) => <tr key={`${item.fecha || 'fecha'}-${item.producto || 'producto'}-${index}`}><td>{formatDate(item.fecha)}</td><td><strong>{item.producto || '-'}</strong></td><td>{item.proveedor || '-'}</td><td>{item.cantidad ?? '-'}</td><td>{money(item.costoUnitario)}</td><td><strong>{money(item.costoTotal)}</strong></td></tr>)}</tbody></table></div>}</Card>
        </>
      )}
    </>
  )
}
