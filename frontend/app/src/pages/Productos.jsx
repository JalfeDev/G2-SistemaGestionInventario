import { useMemo, useState } from 'react'
import { Badge, Card, Empty, Field, Icon, Loader, Notice, PageHeader, ResourceNotice, SearchBox } from '../components/ui'
import { fallbackCategorias, fallbackProductos, fallbackUnidades } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { categoriaService, getApiError, productoService, unidadService } from '../services/api'

const initialForm = { nombre: '', stockActual: '0', stockMinimo: '', categoriaId: '', unidadId: '' }

export default function Productos() {
  const products = useApiResource(productoService.listar, fallbackProductos)
  const categories = useApiResource(categoriaService.listar, fallbackCategorias)
  const units = useApiResource(unidadService.listar, fallbackUnidades)
  const [query, setQuery] = useState('')
  const [form, setForm] = useState(initialForm)
  const [open, setOpen] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const filtered = useMemo(() => products.data.filter((item) => item.nombre.toLowerCase().includes(query.toLowerCase())), [products.data, query])

  async function create(event) {
    event.preventDefault()
    if (!form.nombre || !form.stockMinimo || !form.categoriaId || !form.unidadId) return setError('Completa los campos obligatorios.')
    try {
      const { data } = await productoService.crear({ ...form, stockActual: Number(form.stockActual), stockMinimo: Number(form.stockMinimo), categoriaId: Number(form.categoriaId), unidadId: Number(form.unidadId) })
      products.setData([data, ...products.data])
      setForm(initialForm)
      setOpen(false)
      setMessage('Producto registrado correctamente.')
      setError('')
    } catch (requestError) {
      setError(getApiError(requestError))
    }
  }

  return (
    <>
      <PageHeader title="Productos" description="Catalogo maestro de insumos y niveles de stock." actions={<button className="button primary" onClick={() => setOpen(!open)}><Icon name="plus" size={17} /> Nuevo producto</button>} />
      <ResourceNotice error={products.error} />
      {message && <Notice tone="success">{message}</Notice>}
      {open && <Card className="form-card"><div className="card-title"><div><span>Catalogo</span><h3>Registrar producto</h3></div></div><form className="form-grid" onSubmit={create}><Field label="Nombre del producto"><input value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} placeholder="Ej. Shampoo hotelero" /></Field><Field label="Categoria"><select value={form.categoriaId} onChange={(e) => setForm({ ...form, categoriaId: e.target.value })}><option value="">Seleccionar</option>{categories.data.map((item) => <option value={item.id} key={item.id}>{item.nombre}</option>)}</select></Field><Field label="Unidad de medida"><select value={form.unidadId} onChange={(e) => setForm({ ...form, unidadId: e.target.value })}><option value="">Seleccionar</option>{units.data.map((item) => <option value={item.id} key={item.id}>{item.nombre}</option>)}</select></Field><Field label="Stock minimo"><input type="number" min="0" value={form.stockMinimo} onChange={(e) => setForm({ ...form, stockMinimo: e.target.value })} /></Field>{error && <div className="form-full"><Notice tone="danger">{error}</Notice></div>}<div className="form-actions form-full"><button className="button primary">Guardar producto</button><button type="button" className="button subtle" onClick={() => setOpen(false)}>Cancelar</button></div></form></Card>}
      <Card>
        <div className="toolbar"><SearchBox value={query} onChange={setQuery} placeholder="Buscar por nombre..." /><button className="button subtle" onClick={products.reload}><Icon name="refresh" size={16} /> Actualizar</button></div>
        {products.loading ? <Loader /> : filtered.length === 0 ? <Empty /> : <div className="table-wrap"><table><thead><tr><th>Producto</th><th>Categoria</th><th>Unidad</th><th>Stock actual</th><th>Stock minimo</th><th>Estado</th></tr></thead><tbody>{filtered.map((item) => { const critical = Number(item.stockActual) <= Number(item.stockMinimo); return <tr key={item.id}><td><strong>{item.nombre}</strong></td><td>{item.categoria?.nombre || '-'}</td><td>{item.unidad?.abreviatura || item.unidad?.nombre || '-'}</td><td>{item.stockActual}</td><td>{item.stockMinimo}</td><td><Badge tone={critical ? 'danger' : 'success'}>{critical ? 'Stock bajo' : 'Disponible'}</Badge></td></tr> })}</tbody></table></div>}
      </Card>
    </>
  )
}
