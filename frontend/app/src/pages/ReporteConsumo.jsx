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

  function downloadPdf() {
    if (!report || report.categorias.length === 0) return setError('No hay movimientos en este rango para exportar')
    setError('')
    try {
      const pdf = generarPdfConsumo(report, filters)
      const url = URL.createObjectURL(new Blob([pdf], { type: 'application/pdf' }))
      const link = document.createElement('a')
      link.href = url
      link.download = `reporte-consumo-${filters.fechaInicio}-${filters.fechaFin}.pdf`
      document.body.appendChild(link)
      link.click()
      link.remove()
      setTimeout(() => URL.revokeObjectURL(url), 1000)
    } catch (pdfError) {
      console.error('No se pudo generar el PDF de consumo.', pdfError)
      setError('No se pudo generar el PDF.')
    }
  }

  return (
    <>
      <PageHeader title="Reporte de consumo de inventario" description="Consumo real agrupado por categoria a partir de distribuciones registradas." />
      {error && <Notice tone="danger">{error}</Notice>}
      <Card className="form-card"><form className="form-grid" onSubmit={consult}><Field label="Fecha de inicio"><input type="date" value={filters.fechaInicio} onChange={(event) => setFilters({ ...filters, fechaInicio: event.target.value })} /></Field><Field label="Fecha de fin"><input type="date" value={filters.fechaFin} onChange={(event) => setFilters({ ...filters, fechaFin: event.target.value })} /></Field><div className="form-actions"><button className="button primary">Consultar reporte</button>{report?.categorias.length > 0 && <button type="button" className="button subtle" onClick={downloadPdf}>Descargar PDF</button>}</div></form></Card>
      {loading ? <Loader /> : report && (report.categorias.length === 0 ? <Card><Empty text="No hay movimientos en este rango para exportar" /></Card> : <>{report.categorias.map((category) => <Card key={category.categoria}><div className="card-title"><div><span>Categoria</span><h3>{category.categoria}</h3></div><strong>Subtotal: {category.subtotal}</strong></div><div className="table-wrap"><table><thead><tr><th>Producto</th><th>Categoria</th><th>Cantidad consumida</th></tr></thead><tbody>{category.productos.map((item) => <tr key={item.productoId}><td><strong>{item.producto}</strong></td><td>{item.categoria}</td><td>{item.cantidadConsumida}</td></tr>)}</tbody></table></div></Card>)}<Card><div className="card-title"><h3>Total general consumido</h3><strong>{report.totalGeneral}</strong></div></Card></>)}
    </>
  )
}

function generarPdfConsumo(report, filters) {
  const lineas = [
    'Reporte de consumo de inventario',
    `Fecha de inicio: ${report?.fechaInicio || filters.fechaInicio}`,
    `Fecha de fin: ${report?.fechaFin || filters.fechaFin}`,
    `Fecha de generacion: ${new Date().toLocaleString('es-PE')}`,
    '',
  ]

  for (const categoria of report?.categorias || []) {
    lineas.push(`Categoria: ${categoria.categoria || '-'}`)
    for (const item of categoria.productos || []) {
      lineas.push(`  Producto: ${item.producto || '-'}`)
      lineas.push(`  Categoria: ${item.categoria || categoria.categoria || '-'}`)
      lineas.push(`  Cantidad consumida: ${item.cantidadConsumida ?? '-'}`)
    }
    lineas.push(`Subtotal por categoria: ${categoria.subtotal ?? 0}`)
    lineas.push('')
  }

  lineas.push(`Total general consumido: ${report?.totalGeneral ?? 0}`)
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
