import { useState } from 'react'
import { Card, Empty, Icon, Loader, Notice, PageHeader } from '../components/ui'
import { getApiError, importacionCsvService } from '../services/api'

const COLUMNAS = ['nombre', 'stockMinimo']

export default function ImportacionCsv() {
  const [rows,      setRows]      = useState([])
  const [file,      setFile]      = useState(null)
  const [filename,  setFilename]  = useState('')
  const [csvError,  setCsvError]  = useState('')
  const [importing, setImporting] = useState(false)
  const [resultado, setResultado] = useState(null)

  function readFile(event) {
    const selected = event.target.files?.[0]
    if (!selected) return
    if (!selected.name.toLowerCase().endsWith('.csv')) {
      setFile(null)
      setFilename('')
      setRows([])
      setResultado(null)
      setCsvError('Selecciona un archivo con extension .csv.')
      event.target.value = ''
      return
    }
    setFile(selected)
    setFilename(selected.name)
    setCsvError('')
    setResultado(null)
    setRows([])

    const reader = new FileReader()
    reader.onload = () => {
      const lines   = String(reader.result).trim().split(/\r?\n/).filter(Boolean)
      const headers = lines.shift()?.split(',').map((h) => h.trim()) || []
      const missing = COLUMNAS.filter((col) => !headers.includes(col))

      if (missing.length) {
        setCsvError(`Faltan columnas obligatorias: ${missing.join(', ')}.`)
        return
      }

      const parsed = lines.map((line, index) => {
        const values = line.split(',').map((v) => v.trim())
        return headers.reduce(
          (row, header, pos) => ({ ...row, [header]: values[pos], linea: index + 2 }),
          {}
        )
      })
      setRows(parsed)
    }
    reader.readAsText(selected)
  }

  function descargarPlantilla() {
    const contenido = 'nombre,stockActual,stockMinimo,categoria,unidad\nShampoo,20,5,Amenidades,Unidad\nToallas,50,10,Lenceria,Unidad\n'
    const blob = new Blob([contenido], { type: 'text/csv' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'plantilla-productos.csv'
    document.body.appendChild(link)
    link.click()
    link.remove()
    URL.revokeObjectURL(url)
  }

  async function importar() {
    if (!file) {
      setCsvError('Selecciona un archivo CSV antes de importar.')
      return
    }
    setImporting(true)
    setResultado(null)

    try {
      const { data } = await importacionCsvService.importar(file)
      setResultado({ totalFilas: data.totalFilas || 0, exitosos: data.exitosos || 0, errores: data.errores || [] })
      if ((data.exitosos || 0) > 0 && (data.errores || []).length === 0) {
        setRows([])
        setFile(null)
        setFilename('')
      }
    } catch (error) {
      setCsvError(getApiError(error, 'No se pudo importar el archivo.'))
    } finally {
      setImporting(false)
    }
  }

  return (
    <>
      <PageHeader
        title="Importación CSV"
        description="Carga lotes de productos desde un archivo CSV al catálogo del sistema."
      />

      {/* Upload */}
      <Card className="upload-card">
        <div className="upload-icon"><Icon name="upload" size={28} /></div>
        <h3>Selecciona un archivo CSV</h3>
        <p>Columnas: <strong>nombre</strong>, stockActual, <strong>stockMinimo</strong>, categoria, unidad</p>
        <p style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Obligatorias en negrita. Usa el nombre real de la categoria y la unidad de medida (ej. Amenidades, Unidad).</p>
        <button className="button subtle" onClick={descargarPlantilla}>Descargar plantilla</button>
        <label className="button primary file-button">
          Elegir archivo
          <input type="file" accept=".csv,text/csv" onChange={readFile} />
        </label>
        {filename && <span className="filename">{filename}</span>}
      </Card>

      {csvError && <Notice tone="danger">{csvError}</Notice>}

      {/* Resultado de importación */}
      {resultado && (
        <Notice tone={resultado.errores.length === 0 ? 'success' : 'warning'}>
          <strong>{resultado.exitosos} producto{resultado.exitosos !== 1 ? 's' : ''} importado{resultado.exitosos !== 1 ? 's' : ''} correctamente.</strong>
          <p>{resultado.totalFilas} fila{resultado.totalFilas !== 1 ? 's' : ''} procesada{resultado.totalFilas !== 1 ? 's' : ''}; {resultado.errores.length} rechazada{resultado.errores.length !== 1 ? 's' : ''}.</p>
          {resultado.errores.length > 0 && (
            <ul style={{ marginTop: '8px' }}>
              {resultado.errores.map((e) => (
                <li key={e.linea}>Línea {e.linea} — <strong>{e.nombre}</strong>: {e.mensaje}</li>
              ))}
            </ul>
          )}
        </Notice>
      )}

      {/* Vista previa */}
      {rows.length > 0 && (
        <Card>
          <div className="card-title">
            <div><span>Vista previa</span><h3>Contenido del archivo</h3></div>
            <strong>{rows.length} filas listas para importar</strong>
          </div>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Línea</th>
                  <th>Nombre</th>
                  <th>Stock actual</th>
                  <th>Stock mínimo</th>
                  <th>Categoría</th>
                  <th>Unidad</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr key={row.linea}>
                    <td>{row.linea}</td>
                    <td><strong>{row.nombre}</strong></td>
                    <td>{row.stockActual || '0'}</td>
                    <td>{row.stockMinimo}</td>
                    <td>{row.categoria || '-'}</td>
                    <td>{row.unidad    || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div style={{ marginTop: '16px' }}>
            {importing ? (
              <Loader rows={1} />
            ) : (
              <button className="button primary" onClick={importar}>
                <Icon name="upload" size={16} /> Importar {rows.length} productos al sistema
              </button>
            )}
          </div>
        </Card>
      )}

      {rows.length === 0 && !csvError && !resultado && (
        <Card><Empty text="Selecciona un CSV válido para ver su contenido." /></Card>
      )}
    </>
  )
}
