import { useState } from 'react'
import { Badge, Card, Field, Icon, Loader, Notice, PageHeader, ResourceNotice } from '../components/ui'
import { fallbackDetallesSolicitud, fallbackProductos, fallbackSolicitudes } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { authService, detalleSolicitudService, getApiError, productoService, solicitudService } from '../services/api'
import { formatDate } from '../utils/formatters'

export default function Solicitudes() {
  const requests = useApiResource(solicitudService.listar, fallbackSolicitudes)
  const details = useApiResource(detalleSolicitudService.listar, fallbackDetallesSolicitud)
  const products = useApiResource(productoService.listar, fallbackProductos)
  const [open, setOpen] = useState(false)
  const [form, setForm] = useState({ productoId: '', cantidad: '', comentario: '' })
  const [status, setStatus] = useState({ tone: '', text: '' })

  async function submit(event) {
    event.preventDefault()
    if (!form.productoId || !form.cantidad) return setStatus({ tone: 'danger', text: 'Selecciona un producto e indica la cantidad.' })
    if (localStorage.getItem('hotel_demo')) return setStatus({ tone: 'warning', text: 'Vista demo: la solicitud fue validada localmente. Inicia sesion para registrarla.' })
    try {
      const me = (await authService.me()).data
      const payload = {
        solicitanteId: me.id,
        comentario: form.comentario,
        detalles: [
          { productoId: Number(form.productoId), cantidad: Number(form.cantidad) },
        ],
      }
      await solicitudService.crear(payload)
      requests.reload()
      details.reload()
      setForm({ productoId: '', cantidad: '', comentario: '' })
      setOpen(false)
      setStatus({ tone: 'success', text: 'Solicitud de reabastecimiento registrada.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  return (
    <>
      <PageHeader title="Solicitudes de reabastecimiento" description="Gestiona pedidos de reposicion para mantener la operacion continua." actions={<button className="button primary" onClick={() => setOpen(!open)}><Icon name="plus" size={17} /> Nueva solicitud</button>} />
      <ResourceNotice error={requests.error || details.error || products.error} />{status.text && <Notice tone={status.tone}>{status.text}</Notice>}
      {open && <Card className="form-card"><div className="card-title"><div><span>Abastecimiento</span><h3>Crear solicitud</h3></div></div><form className="form-grid" onSubmit={submit}><Field label="Producto"><select value={form.productoId} onChange={(e) => setForm({ ...form, productoId: e.target.value })}><option value="">Seleccionar producto</option>{products.data.map((item) => <option key={item.id} value={item.id}>{item.nombre}</option>)}</select></Field><Field label="Cantidad solicitada"><input type="number" min="0.01" step="0.01" value={form.cantidad} onChange={(e) => setForm({ ...form, cantidad: e.target.value })} /></Field><Field label="Comentario"><textarea value={form.comentario} onChange={(e) => setForm({ ...form, comentario: e.target.value })} placeholder="Motivo de la solicitud" /></Field><div className="form-actions form-full"><button className="button primary">Registrar solicitud</button></div></form></Card>}
      <Card>{requests.loading ? <Loader /> : <div className="table-wrap"><table><thead><tr><th>Solicitud</th><th>Fecha</th><th>Producto</th><th>Cantidad</th><th>Solicitante</th><th>Estado</th></tr></thead><tbody>{requests.data.map((item) => { const detail = details.data.find((entry) => entry.solicitud?.id === item.id); return <tr key={item.id}><td><strong>#{item.id}</strong></td><td>{formatDate(item.fechaSolicitud)}</td><td>{detail?.producto?.nombre || '-'}</td><td>{detail?.cantidadSolicitada || '-'}</td><td>{item.solicitante?.nombres || item.solicitante?.usuario || '-'}</td><td><Badge tone={item.estado === 'APROBADA' ? 'success' : item.estado === 'RECHAZADA' ? 'danger' : 'warning'}>{item.estado}</Badge></td></tr> })}</tbody></table></div>}</Card>
    </>
  )
}
