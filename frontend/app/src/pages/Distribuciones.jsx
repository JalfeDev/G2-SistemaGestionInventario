import { useState } from 'react'
import { Badge, Card, Field, Loader, Notice, PageHeader, ResourceNotice } from '../components/ui'
import { fallbackDetallesDistribucion, fallbackHabitaciones, fallbackProductos } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { authService, detalleDistribucionService, distribucionService, getApiError, habitacionService, productoService } from '../services/api'
import { formatDate } from '../utils/formatters'

export default function Distribuciones() {
  const products = useApiResource(productoService.listar, fallbackProductos)
  const rooms = useApiResource(habitacionService.listar, fallbackHabitaciones)
  const details = useApiResource(detalleDistribucionService.listar, fallbackDetallesDistribucion)
  const [form, setForm] = useState({ productoId: '', habitacionId: '', cantidad: '' })
  const [status, setStatus] = useState({ tone: '', text: '' })

  async function submit(event) {
    event.preventDefault()
    if (!form.productoId || !form.habitacionId || !form.cantidad) return setStatus({ tone: 'danger', text: 'Completa los campos obligatorios.' })
    const product = products.data.find((item) => item.id === Number(form.productoId))
    if (Number(form.cantidad) > Number(product?.stockActual || 0)) return setStatus({ tone: 'danger', text: `Stock insuficiente. Disponible: ${product?.stockActual || 0}.` })
    if (localStorage.getItem('hotel_demo')) return setStatus({ tone: 'warning', text: 'Vista demo: la distribucion fue validada localmente. Inicia sesion para registrarla.' })
    try {
      const me = (await authService.me()).data
      const distribution = (await distribucionService.crear({ habitacion: { id: Number(form.habitacionId) }, usuario: { id: me.id } })).data
      const detail = (await detalleDistribucionService.crear({ cantidad: Number(form.cantidad), distribucion: { id: distribution.id }, producto: { id: Number(form.productoId) } })).data
      details.setData([detail, ...details.data])
      setForm({ productoId: '', habitacionId: '', cantidad: '' })
      setStatus({ tone: 'success', text: 'Distribucion registrada correctamente.' })
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
        <Card><div className="card-title"><div><span>Housekeeping</span><h3>Historial reciente</h3></div><Badge tone="success">En linea</Badge></div>{details.loading ? <Loader /> : <div className="compact-list">{details.data.slice(0, 8).map((item) => <div className="compact-row" key={item.id}><div><strong>{item.producto?.nombre || 'Producto'}</strong><span>Habitacion {item.distribucion?.habitacion?.numero || '-'} - {formatDate(item.distribucion?.fecha)}</span></div><strong>{item.cantidad} und</strong></div>)}</div>}</Card>
      </div>
    </>
  )
}
