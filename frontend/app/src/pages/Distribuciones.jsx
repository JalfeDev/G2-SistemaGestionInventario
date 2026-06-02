import { useState } from 'react'
import { Badge, Card, Field, Loader, Notice, PageHeader, ResourceNotice } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { distribucionService, getApiError, habitacionService, productoService } from '../services/api'
import { formatDate } from '../utils/formatters'

export default function Distribuciones() {
  const products = useApiResource(productoService.listar)
  const rooms = useApiResource(habitacionService.listar)
  const details = useApiResource(() => distribucionService.listar())
  const [form, setForm] = useState({ productoId: '', habitacionId: '', cantidad: '' })
  const [filters, setFilters] = useState({ fechaInicio: '', fechaFin: '', habitacionId: '' })
  const [status, setStatus] = useState({ tone: '', text: '' })

  async function submit(event) {
    event.preventDefault()
    const cantidad = Number(form.cantidad)
    const producto = products.data.find((item) => item.id === Number(form.productoId))
    if (!form.productoId || !form.habitacionId || form.cantidad === '') return setStatus({ tone: 'danger', text: 'Completa los campos obligatorios.' })
    if (cantidad <= 0) return setStatus({ tone: 'danger', text: 'La cantidad debe ser mayor a cero.' })
    if (producto && cantidad > Number(producto.stockActual)) return setStatus({ tone: 'danger', text: 'La cantidad no puede superar el stock disponible.' })
    try {
      const detail = (await distribucionService.registrar({ cantidad, productoId: Number(form.productoId), habitacionId: Number(form.habitacionId) })).data
      details.setData([detail, ...details.data])
      products.reload()
      setForm({ productoId: '', habitacionId: '', cantidad: '' })
      setStatus({ tone: 'success', text: 'Distribucion registrada correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  async function filterHistory(event) {
    event.preventDefault()
    try {
      const params = Object.fromEntries(Object.entries(filters).filter(([, value]) => value))
      const { data } = await distribucionService.listar(params)
      details.setData(data)
      setStatus({ tone: '', text: '' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  return (
    <>
      <PageHeader eyebrow="Housekeeping" title="Distribucion de insumos" description="Entrega controlada de productos para habitaciones y operacion diaria." />
      <ResourceNotice error={products.error || rooms.error || details.error} />
      <div className="split-grid">
        <Card className="form-card sticky-card"><div className="card-title"><div><span>Nueva entrega</span><h3>Asignar insumos</h3></div></div><form className="stack-form" onSubmit={submit}><Field label="Habitacion"><select value={form.habitacionId} onChange={(e) => setForm({ ...form, habitacionId: e.target.value })}><option value="">Seleccionar habitacion</option>{rooms.data.map((item) => <option value={item.id} key={item.id}>Habitacion {item.numero} - Piso {item.piso}</option>)}</select></Field><Field label="Producto"><select value={form.productoId} onChange={(e) => setForm({ ...form, productoId: e.target.value })}><option value="">Seleccionar producto</option>{products.data.map((item) => <option value={item.id} key={item.id}>{item.nombre} ({item.stockActual} disponibles)</option>)}</select></Field><Field label="Cantidad"><input type="number" min="0.01" step="0.01" value={form.cantidad} onChange={(e) => setForm({ ...form, cantidad: e.target.value })} /></Field>{status.text && <Notice tone={status.tone}>{status.text}</Notice>}<button className="button primary wide">Confirmar distribucion</button></form></Card>
        <Card><div className="card-title"><div><span>Housekeeping</span><h3>Historial de distribuciones</h3></div><Badge tone="success">En linea</Badge></div><form className="form-grid" onSubmit={filterHistory}><Field label="Desde"><input type="date" value={filters.fechaInicio} onChange={(e) => setFilters({ ...filters, fechaInicio: e.target.value })} /></Field><Field label="Hasta"><input type="date" value={filters.fechaFin} onChange={(e) => setFilters({ ...filters, fechaFin: e.target.value })} /></Field><Field label="Habitacion"><select value={filters.habitacionId} onChange={(e) => setFilters({ ...filters, habitacionId: e.target.value })}><option value="">Todas</option>{rooms.data.map((item) => <option value={item.id} key={item.id}>{item.numero}</option>)}</select></Field><div className="form-actions"><button className="button subtle">Filtrar historial</button></div></form>{details.loading ? <Loader /> : <div className="compact-list">{details.data.map((item) => <div className="compact-row" key={item.id}><div><strong>{item.producto?.nombre || 'Producto'}</strong><span>Habitacion {item.distribucion?.habitacion?.numero || '-'} - {formatDate(item.distribucion?.fecha)}</span></div><strong>{item.cantidad} und</strong></div>)}</div>}</Card>
      </div>
    </>
  )
}
