import { useState } from 'react'
import { Card, Empty, Field, Loader, Notice, PageHeader } from '../components/ui'
import { getApiError, reporteService } from '../services/api'

const LINES_PER_PAGE = 42

function formatValue(value) {
  return value ?? ''
}

function normalizePdfText(text) {
  return String(text)
    .normalize('NFD')
    .replace(/\p{M}/gu, '')
    .replace(/[^\x20-\x7E]/g, '')
}

function escapePdfText(text) {
  return normalizePdfText(text).replace(/\\/g, '\\\\').replace(/\(/g, '\\(').replace(/\)/g, '\\)')
}

function splitLine(text, maxLength = 95) {
  const words = normalizePdfText(text).split(' ')
  const lines = []
  let current = ''

  for (const word of words) {
    const next = current ? `${current} ${word}` : word
    if (next.length <= maxLength) {
      current = next
    } else {
      if (current) lines.push(current)
      current = word
    }
  }

  if (current) lines.push(current)
  return lines.length ? lines : ['']
}

function buildReportLines(report, filters) {
  const lines = [
    'Reporte de consumo de inventario',
    `Fecha inicio: ${formatValue(report.fechaInicio || filters.fechaInicio)}`,
    `Fecha fin: ${formatValue(report.fechaFin || filters.fechaFin)}`,
    `Fecha de generacion: ${new Date().toLocaleString('es-PE')}`,
    '',
  ]

  for (const category of report.categorias) {
    lines.push(`Categoria: ${formatValue(category.categoria)}`)
    lines.push('Producto | Categoria | Cantidad consumida')

    for (const item of category.productos) {
      lines.push(...splitLine(`${formatValue(item.producto)} | ${formatValue(item.categoria || category.categoria)} | ${formatValue(item.cantidadConsumida)}`))
    }

    lines.push(`Subtotal por categoria: ${formatValue(category.subtotal)}`)
    lines.push('')
  }

  if (report.totalGeneral !== undefined) {
    lines.push(`Total general consumido: ${formatValue(report.totalGeneral)}`)
  }

  return lines
}

function byteLength(text) {
  return new TextEncoder().encode(text).length
}

function paginate(lines) {
  const pages = []
  for (let start = 0; start < lines.length; start += LINES_PER_PAGE) {
    pages.push(lines.slice(start, start + LINES_PER_PAGE))
  }
  return pages.length ? pages : [['']]
}

function buildPagesObject(pageCount) {
  const kids = Array.from({ length: pageCount }, (_, index) => `${4 + index * 2} 0 R`).join(' ')
  return `<< /Type /Pages /Kids [${kids}] /Count ${pageCount} >>`
}

function buildPageContent(lines) {
  const content = ['BT /F1 10 Tf 50 795 Td 14 TL']
  for (const line of lines) {
    content.push(`(${escapePdfText(line)}) Tj T*`)
  }
  content.push('ET')
  return content.join('\n')
}

function buildPdfBlob(report, filters) {
  const pages = paginate(buildReportLines(report, filters))
  const objects = [
    '<< /Type /Catalog /Pages 2 0 R >>',
    buildPagesObject(pages.length),
    '<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>',
  ]

  pages.forEach((pageLines, index) => {
    const contentId = 5 + index * 2
    const content = buildPageContent(pageLines)
    objects.push(`<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 842] /Resources << /Font << /F1 3 0 R >> >> /Contents ${contentId} 0 R >>`)
    objects.push(`<< /Length ${byteLength(content)} >>\nstream\n${content}\nendstream`)
  })

  let pdf = '%PDF-1.4\n'
  const offsets = [0]
  objects.forEach((object, index) => {
    offsets.push(byteLength(pdf))
    pdf += `${index + 1} 0 obj\n${object}\nendobj\n`
  })

  const xrefStart = byteLength(pdf)
  pdf += `xref\n0 ${objects.length + 1}\n0000000000 65535 f \n`
  offsets.slice(1).forEach((offset) => {
    pdf += `${String(offset).padStart(10, '0')} 00000 n \n`
  })
  pdf += `trailer\n<< /Size ${objects.length + 1} /Root 1 0 R >>\nstartxref\n${xrefStart}\n%%EOF`

  return new Blob([pdf], { type: 'application/pdf' })
}

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
      return setError('No hay movimientos en este rango para exportar')
    }
    setError('')
    try {
      const url = URL.createObjectURL(buildPdfBlob(report, filters))
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
      {loading ? <Loader /> : report && (report.categorias.length === 0 ? <Card><Empty text="No hay movimientos en este rango para exportar" /></Card> : <>{report.categorias.map((category) => <Card key={category.categoria}><div className="card-title"><div><span>Categoria</span><h3>{category.categoria}</h3></div><strong>Subtotal: {category.subtotal}</strong></div><div className="table-wrap"><table><thead><tr><th>Producto</th><th>Categoria</th><th>Cantidad consumida</th></tr></thead><tbody>{category.productos.map((item) => <tr key={item.productoId}><td><strong>{item.producto}</strong></td><td>{item.categoria}</td><td>{item.cantidadConsumida}</td></tr>)}</tbody></table></div></Card>)}<Card><div className="card-title"><h3>Total general consumido</h3><strong>{report.totalGeneral}</strong></div></Card></>)}
    </>
  )
}
