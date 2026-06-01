import { useState } from 'react'
import { Card, Empty, Icon, Notice, PageHeader } from '../components/ui'

const expected = ['nombre', 'stockActual', 'stockMinimo', 'categoriaId', 'unidadId']

export default function ImportacionCsv() {
  const [rows, setRows] = useState([])
  const [error, setError] = useState('')
  const [filename, setFilename] = useState('')

  function readFile(event) {
    const file = event.target.files?.[0]
    if (!file) return
    setFilename(file.name)
    setError('')
    const reader = new FileReader()
    reader.onload = () => {
      const lines = String(reader.result).trim().split(/\r?\n/).filter(Boolean)
      const headers = lines.shift()?.split(',').map((item) => item.trim()) || []
      const missing = expected.filter((field) => !headers.includes(field))
      if (missing.length) {
        setRows([])
        setError(`Faltan columnas obligatorias: ${missing.join(', ')}.`)
        return
      }
      setRows(lines.map((line, index) => {
        const values = line.split(',').map((item) => item.trim())
        return headers.reduce((row, header, position) => ({ ...row, [header]: values[position], line: index + 2 }), {})
      }))
    }
    reader.readAsText(file)
  }

  return (
    <>
      <PageHeader title="Importacion CSV" description="Previsualiza lotes de productos antes de incorporarlos al catalogo." />
      <Notice tone="warning">El backend actual no expone un endpoint de importacion CSV. Esta pantalla valida y previsualiza el archivo localmente sin enviar cambios.</Notice>
      <Card className="upload-card">
        <div className="upload-icon"><Icon name="upload" size={28} /></div>
        <h3>Selecciona un archivo CSV</h3>
        <p>Columnas requeridas: {expected.join(', ')}</p>
        <label className="button primary file-button">Elegir archivo<input type="file" accept=".csv,text/csv" onChange={readFile} /></label>
        {filename && <span className="filename">{filename}</span>}
      </Card>
      {error && <Notice tone="danger">{error}</Notice>}
      <Card><div className="card-title"><div><span>Revision local</span><h3>Vista previa del contenido</h3></div><strong>{rows.length} filas</strong></div>{rows.length === 0 ? <Empty text="Selecciona un CSV valido para ver su contenido." /> : <div className="table-wrap"><table><thead><tr><th>Linea</th><th>Producto</th><th>Stock actual</th><th>Stock minimo</th><th>Categoria ID</th><th>Unidad ID</th></tr></thead><tbody>{rows.map((row) => <tr key={row.line}><td>{row.line}</td><td><strong>{row.nombre}</strong></td><td>{row.stockActual}</td><td>{row.stockMinimo}</td><td>{row.categoriaId}</td><td>{row.unidadId}</td></tr>)}</tbody></table></div>}</Card>
    </>
  )
}
