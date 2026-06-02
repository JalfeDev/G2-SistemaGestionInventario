import { useMemo, useState } from 'react'
import { Badge, Card, Empty, Field, Icon, Loader, Notice, PageHeader, ResourceNotice, SearchBox } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { categoriaService, getApiError, productoService, unidadService } from '../services/api'

const initialForm = { nombre: '', stockActual: '0', stockMinimo: '', categoriaId: '', unidadId: '' }

export default function Productos() {
  const products = useApiResource(productoService.listar)
  const categories = useApiResource(categoriaService.listar)
  const units = useApiResource(unidadService.listar)
  const [query, setQuery] = useState('')
  const [form, setForm] = useState(initialForm)
  const [editingId, setEditingId] = useState(null)
  const [open, setOpen] = useState(false)
  const [status, setStatus] = useState({ tone: '', text: '' })
  const filtered = useMemo(() => products.data.filter((item) => item.nombre.toLowerCase().includes(query.toLowerCase())), [products.data, query])

  function openCreate() {
    setForm(initialForm)
    setEditingId(null)
    setOpen(true)
    setStatus({ tone: '', text: '' })
  }

  function openEdit(item) {
    setForm({ nombre: item.nombre, stockActual: item.stockActual, stockMinimo: item.stockMinimo, categoriaId: item.categoria?.id || '', unidadId: item.unidad?.id || '' })
    setEditingId(item.id)
    setOpen(true)
    setStatus({ tone: '', text: '' })
  }

  async function save(event) {
    event.preventDefault()
    if (!form.nombre || form.stockMinimo === '' || !form.categoriaId || !form.unidadId) return setStatus({ tone: 'danger', text: 'Completa los campos obligatorios.' })
    if (Number(form.stockMinimo) < 0) return setStatus({ tone: 'danger', text: 'El stock minimo no puede ser negativo.' })
    const payload = { nombre: form.nombre, stockMinimo: Number(form.stockMinimo), categoriaId: Number(form.categoriaId), unidadId: Number(form.unidadId) }
    if (!editingId) payload.stockActual = Number(form.stockActual)
    try {
      const { data } = editingId ? await productoService.actualizar(editingId, payload) : await productoService.crear(payload)
      products.setData(editingId ? products.data.map((item) => item.id === editingId ? data : item) : [data, ...products.data])
      setOpen(false)
      setStatus({ tone: 'success', text: editingId ? 'Producto actualizado correctamente.' : 'Producto registrado correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  async function remove(item) {
    if (!window.confirm(`Eliminar ${item.nombre}?`)) return
    try {
      await productoService.eliminar(item.id)
      products.setData(products.data.filter((product) => product.id !== item.id))
      setStatus({ tone: 'success', text: 'Producto eliminado correctamente.' })
    } catch (error) {
      setStatus({
        tone: 'danger',
        text: error.response?.status === 409
          ? 'No se puede eliminar porque tiene movimientos registrados.'
          : getApiError(error),
      })
    }
  }

  return (
    <>
      <PageHeader title="Productos" description="Catalogo maestro de insumos y niveles de stock." actions={<button className="button primary" onClick={openCreate}><Icon name="plus" size={17} /> Nuevo producto</button>} />
      <ResourceNotice error={products.error || categories.error || units.error} />
      {status.text && <Notice tone={status.tone}>{status.text}</Notice>}
      {open && <Card className="form-card"><div className="card-title"><div><span>Catalogo</span><h3>{editingId ? 'Editar producto' : 'Registrar producto'}</h3></div></div><form className="form-grid" onSubmit={save}><Field label="Nombre del producto"><input value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} /></Field><Field label="Categoria"><select value={form.categoriaId} onChange={(e) => setForm({ ...form, categoriaId: e.target.value })}><option value="">Seleccionar</option>{categories.data.map((item) => <option value={item.id} key={item.id}>{item.nombre}</option>)}</select></Field><Field label="Unidad de medida"><select value={form.unidadId} onChange={(e) => setForm({ ...form, unidadId: e.target.value })}><option value="">Seleccionar</option>{units.data.map((item) => <option value={item.id} key={item.id}>{item.nombre}</option>)}</select></Field><Field label="Stock minimo"><input type="number" min="0" step="0.01" value={form.stockMinimo} onChange={(e) => setForm({ ...form, stockMinimo: e.target.value })} /></Field><div className="form-actions form-full"><button className="button primary">Guardar producto</button><button type="button" className="button subtle" onClick={() => setOpen(false)}>Cancelar</button></div></form></Card>}
      <Card>
        <div className="toolbar"><SearchBox value={query} onChange={setQuery} placeholder="Buscar por nombre..." /><button className="button subtle" onClick={products.reload}><Icon name="refresh" size={16} /> Actualizar</button></div>
        {products.loading ? <Loader /> : filtered.length === 0 ? <Empty /> : <div className="table-wrap"><table><thead><tr><th>Producto</th><th>Categoria</th><th>Unidad</th><th>Stock actual</th><th>Stock minimo</th><th>Estado</th><th>Acciones</th></tr></thead><tbody>{filtered.map((item) => { const critical = Number(item.stockActual) <= Number(item.stockMinimo); return <tr key={item.id}><td><strong>{item.nombre}</strong></td><td>{item.categoria?.nombre || '-'}</td><td>{item.unidad?.abreviatura || item.unidad?.nombre || '-'}</td><td>{item.stockActual}</td><td>{item.stockMinimo}</td><td><Badge tone={critical ? 'danger' : 'success'}>{critical ? 'Stock bajo' : 'Disponible'}</Badge></td><td><div className="form-actions"><button className="button subtle" onClick={() => openEdit(item)}>Editar</button><button className="button subtle" onClick={() => remove(item)}>Eliminar</button></div></td></tr> })}</tbody></table></div>}
      </Card>
    </>
  )
}
