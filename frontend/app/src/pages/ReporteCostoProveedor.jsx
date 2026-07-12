import { useState } from 'react'
import { Card, Empty, Field, Loader, Notice, PageHeader } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { fallbackProveedores } from '../data/fallbackData'
import { getApiError, proveedorService, reporteCostoProveedorService } from '../services/api'
import { formatDate, money } from '../utils/formatters'

export default function ReporteCostoProveedor() {
  const proveedores = useApiResource(proveedorService.listar, fallbackProveedores)
  const [filters, setFilters] = useState({ proveedorId: '', fechaInicio: '', fechaFin: '' })
  const [report, setReport] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  async function consult(event) {
    event.preventDefault()
    if (!filters.fechaInicio || !filters.fechaFin) return setError('Selecciona fecha de inicio y fecha de fin.')
    if (filters.fechaInicio > filters.fechaFin) return setError('La fecha de inicio no puede ser posterior a la fecha de fin.')
    setLoading(true)
    setError('')
    try {
      const { data } = await reporteCostoProveedorService.consultar(filters.proveedorId || undefined, filters.fechaInicio, filters.fechaFin)
      setReport(data)
    } catch (requestError) {
      setError(getApiError(requestError))
    } finally {
      setLoading(false)
    }
  }

  async function downloadPdf() {
    setError('')
    try {
      const { data } = await reporteCostoProveedorService.descargarPdf(filters.proveedorId || undefined, filters.fechaInicio, filters.fechaFin)
      const url = URL.createObjectURL(new Blob([data], { type: 'application/pdf' }))
      const link = document.createElement('a')
      link.href = url
      link.download = `reporte-costos-proveedor-${filters.fechaInicio}-${filters.fechaFin}.pdf`
      document.body.appendChild(link)
      link.click()
      link.remove()
      setTimeout(() => URL.revokeObjectURL(url), 1000)
    } catch (requestError) {
      setError(getApiError(requestError))
    }
  }

  return (
    <>
      <PageHeader title="Reporte de costos por proveedor" description="Consulta el costo de las compras registradas a cada proveedor en un rango de fechas." />
      {error && <Notice tone="danger">{error}</Notice>}
      <Card className="form-card">
        <form className="form-grid" onSubmit={consult}>
          <Field label="Proveedor">
            <select value={filters.proveedorId} onChange={(event) => setFilters({ ...filters, proveedorId: event.target.value })}>
              <option value="">Todos los proveedores</option>
              {proveedores.data.map((item) => <option key={item.id} value={item.id}>{item.nombre}</option>)}
            </select>
          </Field>
          <Field label="Fecha de inicio"><input type="date" value={filters.fechaInicio} onChange={(event) => setFilters({ ...filters, fechaInicio: event.target.value })} /></Field>
          <Field label="Fecha de fin"><input type="date" value={filters.fechaFin} onChange={(event) => setFilters({ ...filters, fechaFin: event.target.value })} /></Field>
          <div className="form-actions">
            <button className="button primary">Consultar</button>
            {report?.proveedores.length > 0 && <button type="button" className="button subtle" onClick={downloadPdf}>Descargar PDF</button>}
          </div>
        </form>
      </Card>
      {loading ? <Loader /> : report && (report.proveedores.length === 0 ? <Card><Empty text="No hay compras registradas en este rango." /></Card> : (
        <>
          {report.proveedores.map((proveedor) => (
            <Card key={proveedor.proveedor}>
              <div className="card-title"><div><span>Proveedor</span><h3>{proveedor.proveedor}</h3></div><strong>Subtotal: {money(proveedor.subtotal)}</strong></div>
              <div className="table-wrap">
                <table>
                  <thead><tr><th>Fecha</th><th>Producto</th><th>Cantidad</th><th>Precio unitario</th><th>Costo total</th></tr></thead>
                  <tbody>
                    {proveedor.items.map((item, index) => (
                      <tr key={index}><td>{formatDate(item.fecha)}</td><td><strong>{item.producto}</strong></td><td>{item.cantidad}</td><td>{money(item.precioUnitario)}</td><td>{money(item.costoTotal)}</td></tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </Card>
          ))}
          <Card><div className="card-title"><h3>Total general</h3><strong>{money(report.totalGeneral)}</strong></div></Card>
        </>
      ))}
    </>
  )
}
