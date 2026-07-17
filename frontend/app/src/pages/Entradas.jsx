import { useEffect, useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { Card, Empty, Field, Loader, Notice, PageHeader, ResourceNotice } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { detalleSolicitudService, getApiError, ingresoService, productoService, proveedorService, solicitudService } from '../services/api'
import { formatDate, money } from '../utils/formatters'

const initial = { productoId: '', proveedorId: '', cantidad: '', costoUnitario: '', observacion: '' }

export default function Entradas() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const solicitudId = searchParams.get('solicitudId')
  const products = useApiResource(productoService.listar)
  const suppliers = useApiResource(proveedorService.listar)
  const details = useApiResource(ingresoService.listar)
  const [form, setForm] = useState(initial)
  const [status, setStatus] = useState({ tone: '', text: '' })
  const [solicitudContext, setSolicitudContext] = useState(null)
  const [loadingSolicitud, setLoadingSolicitud] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const total = Number(form.cantidad || 0) * Number(form.costoUnitario || 0)
  const entradaDesdeSolicitud = Boolean(solicitudContext)
  const permitirRegistro = Boolean(solicitudId)

  useEffect(() => {
    let cancelado = false

    if (!solicitudId) {
      const timer = window.setTimeout(() => {
        if (!cancelado) setSolicitudContext(null)
      }, 0)
      return () => {
        cancelado = true
        window.clearTimeout(timer)
      }
    }

    const timer = window.setTimeout(() => {
      const id = Number(solicitudId)
      if (!Number.isFinite(id)) {
        setStatus({ tone: 'danger', text: 'Solicitud invalida.' })
        return
      }

      setLoadingSolicitud(true)
      setStatus({ tone: '', text: '' })
      Promise.all([solicitudService.buscar(id), detalleSolicitudService.listar()])
        .then(([solicitudResponse, detallesResponse]) => {
          if (cancelado) return
          const solicitud = solicitudResponse.data
          const detallesSolicitud = detallesResponse.data.filter((item) => item.solicitud?.id === id)
          if (solicitud.estado !== 'APROBADO') {
            setSolicitudContext(null)
            setStatus({ tone: 'danger', text: 'Solo una solicitud aprobada puede registrarse como recepcion.' })
            return
          }
          if (detallesSolicitud.length !== 1) {
            setSolicitudContext(null)
            setStatus({ tone: 'danger', text: 'La recepcion desde solicitud requiere una solicitud de un solo producto.' })
            return
          }

          const detalle = detallesSolicitud[0]
          setSolicitudContext({ solicitud, detalle })
          setForm({
            productoId: String(detalle.producto?.id || ''),
            proveedorId: '',
            cantidad: String(detalle.cantidadSolicitada || ''),
            costoUnitario: '',
            observacion: `Recepcion de solicitud #${id}`,
          })
        })
        .catch((error) => {
          if (!cancelado) setStatus({ tone: 'danger', text: getApiError(error) })
        })
        .finally(() => {
          if (!cancelado) setLoadingSolicitud(false)
        })
    }, 0)

    return () => {
      cancelado = true
      window.clearTimeout(timer)
    }
  }, [solicitudId])

  async function submit(event) {
    event.preventDefault()
    if (submitting) return
    if (!permitirRegistro) return setStatus({ tone: 'danger', text: 'Debes registrar la entrada desde una solicitud aprobada.' })
    const cantidad = Number(form.cantidad)
    const costoUnitario = Number(form.costoUnitario)
    if (!form.productoId || !form.proveedorId || form.cantidad === '' || form.costoUnitario === '') return setStatus({ tone: 'danger', text: 'Completa los campos obligatorios.' })
    if (cantidad <= 0) return setStatus({ tone: 'danger', text: 'La cantidad debe ser mayor a cero.' })
    if (costoUnitario < 0) return setStatus({ tone: 'danger', text: 'El costo unitario no puede ser negativo.' })
    setSubmitting(true)
    try {
      const payload = {
        cantidad,
        costoUnitario,
        observacion: form.observacion,
        productoId: Number(form.productoId),
        proveedorId: Number(form.proveedorId),
      }
      if (entradaDesdeSolicitud) payload.solicitudId = solicitudContext.solicitud.id

      const detail = (await ingresoService.registrar(payload)).data
      details.setData([detail, ...details.data])
      products.reload()
      setForm(initial)
      setSolicitudContext(null)
      if (entradaDesdeSolicitud) navigate('/entradas', { replace: true })
      setStatus({
        tone: 'success',
        text: entradaDesdeSolicitud
          ? 'Recepcion registrada correctamente. La solicitud quedo atendida.'
          : 'Entrada manual registrada correctamente.',
      })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <>
      <PageHeader title="Entradas de inventario" description="Registra recepciones de insumos y su costo de compra." />
      <ResourceNotice error={products.error || suppliers.error || details.error} />
      {loadingSolicitud && <Notice tone="info">Cargando solicitud aprobada...</Notice>}
      {entradaDesdeSolicitud && (
        <Notice tone="info">
          Recepcion vinculada a solicitud #{solicitudContext.solicitud.id}. El producto y la cantidad aprobada estan bloqueados para mantener la trazabilidad.
        </Notice>
      )}
      <div className="split-grid">
        <Card className="form-card sticky-card">
          <div className="card-title"><div><span>{entradaDesdeSolicitud ? 'Solicitud aprobada' : 'Nueva recepcion'}</span><h3>Registrar entrada</h3></div></div>
          {!permitirRegistro ? (
            <div className="stack-form">
              <Empty text="Las entradas de almacen deben registrarse desde una solicitud aprobada." />
              <Notice tone="info">Ve a Solicitudes y usa Registrar recepcion en una solicitud aprobada por gerencia.</Notice>
              <Link className="button primary wide" to="/solicitudes">Ir a solicitudes</Link>
            </div>
          ) : (
            <form className="stack-form" onSubmit={submit}>
              <Field label="Producto">
                <select value={form.productoId} disabled={entradaDesdeSolicitud} onChange={(event) => setForm({ ...form, productoId: event.target.value })}>
                  <option value="">Seleccionar producto</option>
                  {products.data.map((item) => <option value={item.id} key={item.id}>{item.nombre}</option>)}
                </select>
              </Field>
              <Field label="Proveedor">
                <select value={form.proveedorId} onChange={(event) => setForm({ ...form, proveedorId: event.target.value })}>
                  <option value="">Seleccionar proveedor</option>
                  {suppliers.data.map((item) => <option value={item.id} key={item.id}>{item.nombre}</option>)}
                </select>
              </Field>
              <div className="two-columns">
                <Field label="Cantidad">
                  <input type="number" min="0.01" step="0.01" readOnly={entradaDesdeSolicitud} value={form.cantidad} onChange={(event) => setForm({ ...form, cantidad: event.target.value })} />
                </Field>
                <Field label="Costo unitario">
                  <input type="number" min="0" step="0.01" value={form.costoUnitario} onChange={(event) => setForm({ ...form, costoUnitario: event.target.value })} />
                </Field>
              </div>
              <Field label="Observacion">
                <textarea value={form.observacion} onChange={(event) => setForm({ ...form, observacion: event.target.value })} placeholder="Detalle opcional" />
              </Field>
              <div className="total-box"><span>Total estimado</span><strong>{money(total)}</strong></div>
              {status.text && <Notice tone={status.tone}>{status.text}</Notice>}
              <button className="button primary wide" disabled={submitting || loadingSolicitud}>{submitting ? 'Registrando...' : 'Registrar entrada'}</button>
            </form>
          )}
        </Card>
        <Card>
          <div className="card-title"><div><span>Trazabilidad</span><h3>Ultimas recepciones</h3></div></div>
          {details.loading ? <Loader /> : <div className="compact-list">{details.data.slice(0, 8).map((item) => <div className="compact-row" key={item.id}><div><strong>{item.producto?.nombre || 'Producto'}</strong><span>{item.proveedor?.nombre || 'Proveedor'} - {formatDate(item.ingresoInventario?.fechaIngreso)}</span>{item.ingresoInventario?.solicitud?.id && <span>Solicitud #{item.ingresoInventario.solicitud.id}</span>}</div><div className="align-right"><strong>{item.cantidad} und</strong><span>{money(item.costoTotal)} total</span></div></div>)}</div>}
        </Card>
      </div>
    </>
  )
}
