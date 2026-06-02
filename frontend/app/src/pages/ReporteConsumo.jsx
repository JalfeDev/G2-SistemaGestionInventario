import { useState } from 'react'
import { Card, Empty, Field, Loader, Notice, PageHeader } from '../components/ui'
import { getApiError, reporteService } from '../services/api'

export default function ReporteConsumo() {
  const [filters, setFilters] = useState({ fechaInicio: '', fechaFin: '' })
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
      setReport((await reporteService.consultar(filters.fechaInicio, filters.fechaFin)).data)
    } catch (requestError) {
      setError(getApiError(requestError))
    } finally {
      setLoading(false)
    }
  }

  async function downloadPdf() {
    if (!report?.categorias.length) {
      return setError('No hay movimientos en este rango para exportar.')
    }
    setError('')
    try {
      const response = await reporteService.descargarPdf(filters.fechaInicio, filters.fechaFin)
      const url = URL.createObjectURL(response.data)
      const link = document.createElement('a')
      link.href = url
      link.download = `reporte-consumo-${filters.fechaInicio}-${filters.fechaFin}.pdf`
      document.body.appendChild(link)
      link.click()
      link.remove()
      setTimeout(() => URL.revokeObjectURL(url), 1000)
    } catch (requestError) {
      console.error('No se pudo generar el PDF.', requestError)
      setError('No se pudo generar el PDF')
    }
  }

  return (
    <>
      <PageHeader title="Reporte de consumo de inventario" description="Consumo real agrupado por categoria a partir de distribuciones registradas." />
      {error && <Notice tone="danger">{error}</Notice>}
      <Card className="form-card"><form className="form-grid" onSubmit={consult}><Field label="Fecha de inicio"><input type="date" value={filters.fechaInicio} onChange={(event) => setFilters({ ...filters, fechaInicio: event.target.value })} /></Field><Field label="Fecha de fin"><input type="date" value={filters.fechaFin} onChange={(event) => setFilters({ ...filters, fechaFin: event.target.value })} /></Field><div className="form-actions"><button className="button primary">Consultar reporte</button>{report?.categorias.length > 0 && <button type="button" className="button subtle" onClick={downloadPdf}>Descargar PDF</button>}</div></form></Card>
      {loading ? <Loader /> : report && (report.categorias.length === 0 ? <Card><Empty text="No hay movimientos en este rango para exportar." /></Card> : <>{report.categorias.map((category) => <Card key={category.categoria}><div className="card-title"><div><span>Categoria</span><h3>{category.categoria}</h3></div><strong>Subtotal: {category.subtotal}</strong></div><div className="table-wrap"><table><thead><tr><th>Producto</th><th>Categoria</th><th>Cantidad consumida</th></tr></thead><tbody>{category.productos.map((item) => <tr key={item.productoId}><td><strong>{item.producto}</strong></td><td>{item.categoria}</td><td>{item.cantidadConsumida}</td></tr>)}</tbody></table></div></Card>)}<Card><div className="card-title"><h3>Total general consumido</h3><strong>{report.totalGeneral}</strong></div></Card></>)}
    </>
  )
}
