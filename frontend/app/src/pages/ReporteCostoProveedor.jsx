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
  const [downloading, setDownloading] = useState(false)
  const [error, setError] = useState('')
  const proveedoresReporte = report?.proveedores || []

  async function consult(event) {
    event.preventDefault()
    if (loading) return
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
    if (downloading) return
    if (!filters.fechaInicio || !filters.fechaFin) return setError('Selecciona fecha de inicio y fecha de fin.')
    if (filters.fechaInicio > filters.fechaFin) return setError('La fecha de inicio no puede ser posterior a la fecha de fin.')
    if (proveedoresReporte.length === 0) return setError('No hay datos para exportar.')
    setError('')
    setDownloading(true)
    try {
      const pdf = generarPdfCostosProveedor(report, filters)
      const url = URL.createObjectURL(new Blob([pdf], { type: 'application/pdf' }))
      const link = document.createElement('a')
      link.href = url
      link.download = `reporte-costos-proveedor-${filters.fechaInicio}-${filters.fechaFin}.pdf`
      document.body.appendChild(link)
      link.click()
      link.remove()
      setTimeout(() => URL.revokeObjectURL(url), 1000)
    } catch (pdfError) {
      console.error('No se pudo generar el PDF de costos por proveedor.', pdfError)
      setError('No se pudo generar el PDF.')
    } finally {
      setDownloading(false)
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
            <button className="button primary" disabled={loading}>{loading ? 'Consultando...' : 'Consultar'}</button>
            {proveedoresReporte.length > 0 && <button type="button" className="button subtle" onClick={downloadPdf} disabled={downloading}>{downloading ? 'Generando...' : 'Descargar PDF'}</button>}
          </div>
        </form>
      </Card>
      {loading ? <Loader /> : report && (proveedoresReporte.length === 0 ? <Card><Empty text="No hay compras registradas en este rango." /></Card> : (
        <>
          {proveedoresReporte.map((proveedor) => (
            <Card key={proveedor.proveedor}>
              <div className="card-title"><div><span>Proveedor</span><h3>{proveedor.proveedor || '-'}</h3></div><strong>Subtotal: {money(proveedor.subtotal)}</strong></div>
              <div className="table-wrap">
                <table>
                  <thead><tr><th>Fecha</th><th>Producto</th><th>Cantidad</th><th>Precio unitario</th><th>Costo total</th></tr></thead>
                  <tbody>
                    {(proveedor.items || []).map((item, index) => (
                      <tr key={`${item.fecha || 'fecha'}-${item.producto || 'producto'}-${index}`}><td>{formatDate(item.fecha)}</td><td><strong>{item.producto || '-'}</strong></td><td>{item.cantidad ?? '-'}</td><td>{money(item.precioUnitario)}</td><td>{money(item.costoTotal)}</td></tr>
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

function generarPdfCostosProveedor(report, filters) {
  const lineas = [
    'Hotel Piramide - Reporte de costos por proveedor',
    `Fecha de inicio: ${report?.fechaInicio || filters.fechaInicio}`,
    `Fecha de fin: ${report?.fechaFin || filters.fechaFin}`,
    `Fecha de generacion: ${new Date().toLocaleString('es-PE')}`,
    '',
  ]

  for (const proveedor of report?.proveedores || []) {
    lineas.push(`Proveedor: ${proveedor.proveedor || '-'}`)
    for (const item of proveedor.items || []) {
      lineas.push(`  Fecha: ${formatDate(item.fecha)}`)
      lineas.push(`  Producto: ${item.producto || '-'} | Cantidad: ${item.cantidad ?? '-'} | Precio unitario: ${money(item.precioUnitario)} | Costo total: ${money(item.costoTotal)}`)
    }
    lineas.push(`Subtotal: ${money(proveedor.subtotal)}`)
    lineas.push('')
  }

  lineas.push(`Total general: ${money(report?.totalGeneral)}`)
  return construirPdf(lineas)
}

function construirPdf(lineas) {
  const paginas = paginar(lineas)
  const objetos = [
    '<< /Type /Catalog /Pages 2 0 R >>',
    construirIndicePaginas(paginas.length),
    '<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>',
  ]

  paginas.forEach((pagina, index) => {
    const contenidoId = 5 + index * 2
    objetos.push(`<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 842] /Resources << /Font << /F1 3 0 R >> >> /Contents ${contenidoId} 0 R >>`)
    const contenido = construirContenidoPagina(pagina)
    objetos.push(`<< /Length ${contenido.length} >>\nstream\n${contenido}\nendstream`)
  })

  let salida = '%PDF-1.4\n'
  const offsets = [0]
  objetos.forEach((objeto, index) => {
    offsets.push(salida.length)
    salida += `${index + 1} 0 obj\n${objeto}\nendobj\n`
  })

  const inicioXref = salida.length
  salida += `xref\n0 ${objetos.length + 1}\n0000000000 65535 f \n`
  for (let index = 1; index < offsets.length; index++) {
    salida += `${String(offsets[index]).padStart(10, '0')} 00000 n \n`
  }
  salida += `trailer\n<< /Size ${objetos.length + 1} /Root 1 0 R >>\nstartxref\n${inicioXref}\n%%EOF`
  return salida
}

function paginar(lineas) {
  const lineasPorPagina = 42
  const paginas = []
  for (let inicio = 0; inicio < lineas.length; inicio += lineasPorPagina) {
    paginas.push(lineas.slice(inicio, inicio + lineasPorPagina))
  }
  return paginas.length > 0 ? paginas : [['']]
}

function construirIndicePaginas(cantidad) {
  let kids = ''
  for (let index = 0; index < cantidad; index++) {
    kids += `${4 + index * 2} 0 R `
  }
  return `<< /Type /Pages /Kids [${kids}] /Count ${cantidad} >>`
}

function construirContenidoPagina(lineas) {
  let contenido = 'BT /F1 10 Tf 50 795 Td 14 TL\n'
  for (const linea of lineas) {
    contenido += `(${escaparPdf(normalizarPdf(linea))}) Tj T*\n`
  }
  return `${contenido}ET`
}

function normalizarPdf(texto) {
  return String(texto ?? '')
    .replace(/\u00A0/g, ' ')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^\x20-\x7E]/g, '?')
}

function escaparPdf(texto) {
  return texto.replace(/\\/g, '\\\\').replace(/\(/g, '\\(').replace(/\)/g, '\\)')
}
