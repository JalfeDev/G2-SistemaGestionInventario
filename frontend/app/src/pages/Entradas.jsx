import { useState } from 'react'
import { Card, Field, Loader, Notice, PageHeader, ResourceNotice } from '../components/ui'
import { fallbackDetallesIngreso, fallbackProductos, fallbackProveedores } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { authService, detalleIngresoService, getApiError, ingresoService, productoService, proveedorService } from '../services/api'
import { formatDate, money } from '../utils/formatters'

const initial = { productoId: '', proveedorId: '', cantidad: '', costoUnitario: '', observacion: '' }

export default function Entradas() {
  const products = useApiResource(productoService.listar, fallbackProductos)
  const suppliers = useApiResource(proveedorService.listar, fallbackProveedores)
  const details = useApiResource(detalleIngresoService.listar, fallbackDetallesIngreso)
  const [form, setForm] = useState(initial)
  const [status, setStatus] = useState({ tone: '', text: '' })
  const total = Number(form.cantidad || 0) * Number(form.costoUnitario || 0)

  async function submit(event) {
    event.preventDefault()
    if (!form.productoId || !form.proveedorId || !form.cantidad || !form.costoUnitario) return setStatus({ tone: 'danger', text: 'Completa los campos obligatorios.' })
    if (localStorage.getItem('hotel_demo')) {
      setStatus({ tone: 'warning', text: 'Vista demo: la entrada fue validada localmente. Inicia sesion contra el backend para registrarla.' })
      return
    }
    try {
      const me = (await authService.me()).data
      const ingreso = (await ingresoService.crear({ observacion: form.observacion, usuario: { id: me.id } })).data
      const detail = (await detalleIngresoService.crear({ cantidad: Number(form.cantidad), costoUnitario: Number(form.costoUnitario), ingresoInventario: { id: ingreso.id }, producto: { id: Number(form.productoId) }, proveedor: { id: Number(form.proveedorId) } })).data
      details.setData([detail, ...details.data])
      setForm(initial)
      setStatus({ tone: 'success', text: 'Entrada registrada correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  return (
    <>
      <PageHeader title="Entradas de inventario" description="Registra recepciones de insumos y su costo de compra." />
      <ResourceNotice error={products.error || suppliers.error || details.error} />
      <div className="split-grid">
        <Card className="form-card sticky-card"><div className="card-title"><div><span>Nueva recepcion</span><h3>Registrar entrada</h3></div></div><form className="stack-form" onSubmit={submit}><Field label="Producto"><select value={form.productoId} onChange={(e) => setForm({ ...form, productoId: e.target.value })}><option value="">Seleccionar producto</option>{products.data.map((item) => <option value={item.id} key={item.id}>{item.nombre}</option>)}</select></Field><Field label="Proveedor"><select value={form.proveedorId} onChange={(e) => setForm({ ...form, proveedorId: e.target.value })}><option value="">Seleccionar proveedor</option>{suppliers.data.map((item) => <option value={item.id} key={item.id}>{item.nombre}</option>)}</select></Field><div className="two-columns"><Field label="Cantidad"><input type="number" min="0.01" step="0.01" value={form.cantidad} onChange={(e) => setForm({ ...form, cantidad: e.target.value })} /></Field><Field label="Costo unitario"><input type="number" min="0.01" step="0.01" value={form.costoUnitario} onChange={(e) => setForm({ ...form, costoUnitario: e.target.value })} /></Field></div><Field label="Observacion"><textarea value={form.observacion} onChange={(e) => setForm({ ...form, observacion: e.target.value })} placeholder="Detalle opcional" /></Field><div className="total-box"><span>Total estimado</span><strong>{money(total)}</strong></div>{status.text && <Notice tone={status.tone}>{status.text}</Notice>}<button className="button primary wide">Registrar entrada</button></form></Card>
        <Card><div className="card-title"><div><span>Trazabilidad</span><h3>Ultimas recepciones</h3></div></div>{details.loading ? <Loader /> : <div className="compact-list">{details.data.slice(0, 8).map((item) => <div className="compact-row" key={item.id}><div><strong>{item.producto?.nombre || 'Producto'}</strong><span>{item.proveedor?.nombre || 'Proveedor'} - {formatDate(item.ingresoInventario?.fechaIngreso)}</span></div><div className="align-right"><strong>{item.cantidad} und</strong><span>{money(item.costoUnitario)} c/u</span></div></div>)}</div>}</Card>
      </div>
    </>
  )
}
