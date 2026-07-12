import { useState } from 'react'
import { Card, Empty, Icon, Loader, Notice, PageHeader } from '../components/ui'
import { categoriaService, productoService, unidadService } from '../services/api'
import { useApiResource } from '../hooks/useApiResource'
import { fallbackCategorias, fallbackUnidades } from '../data/fallbackData'

const COLUMNAS = ['nombre', 'stockMinimo']

export default function ImportacionCsv() {
  const categorias = useApiResource(categoriaService.listar, fallbackCategorias)
  const unidades   = useApiResource(unidadService.listar,   fallbackUnidades)

  const [rows,      setRows]      = useState([])
  const [file,      setFile]      = useState(null)
  const [filename,  setFilename]  = useState('')
  const [csvError,  setCsvError]  = useState('')
  const [importing, setImporting] = useState(false)
  const [resultado, setResultado] = useState(null)

  function readFile(event) {
    const selected = event.target.files?.[0]
    if (!selected) return
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

  async function importar() {
    if (!file) return
    setImporting(true)
    setResultado(null)

    try {
      const { data } = await productoService.importarCsv(file)
      setResultado({ exitosos: data.exitosos, errores: data.errores })
      if (data.exitosos > 0) {
        setRows([])
        setFile(null)
        setFilename('')
      }
    } catch (error) {
      setCsvError(error?.response?.data?.message || 'No se pudo importar el archivo.')
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

      {/* Referencia de IDs disponibles */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '16px' }}>
        <Card>
          <div className="card-title"><div><span>Referencia</span><h3>Categorías disponibles</h3></div></div>
          {categorias.loading ? <Loader rows={2} /> : (
            <div className="table-wrap">
              <table>
                <thead><tr><th>ID</th><th>Nombre</th></tr></thead>
                <tbody>
                  {categorias.data.map((c) => (
                    <tr key={c.id}><td><strong>{c.id}</strong></td><td>{c.nombre}</td></tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
        <Card>
          <div className="card-title"><div><span>Referencia</span><h3>Unidades disponibles</h3></div></div>
          {unidades.loading ? <Loader rows={2} /> : (
            <div className="table-wrap">
              <table>
                <thead><tr><th>ID</th><th>Nombre</th><th>Abrev.</th></tr></thead>
                <tbody>
                  {unidades.data.map((u) => (
                    <tr key={u.id}><td><strong>{u.id}</strong></td><td>{u.nombre}</td><td>{u.abreviatura}</td></tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </div>

      {/* Upload */}
      <Card className="upload-card">
        <div className="upload-icon"><Icon name="upload" size={28} /></div>
        <h3>Selecciona un archivo CSV</h3>
        <p>Columnas: <strong>nombre</strong>, stockActual, <strong>stockMinimo</strong>, categoriaId, unidadId</p>
        <p style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Obligatorias en negrita. Usa los IDs de las tablas de referencia.</p>
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
                  <th>Categoría ID</th>
                  <th>Unidad ID</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr key={row.linea}>
                    <td>{row.linea}</td>
                    <td><strong>{row.nombre}</strong></td>
                    <td>{row.stockActual || '0'}</td>
                    <td>{row.stockMinimo}</td>
                    <td>{row.categoriaId || '-'}</td>
                    <td>{row.unidadId    || '-'}</td>
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