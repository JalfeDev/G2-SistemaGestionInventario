import { useCallback, useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { Badge, Card, Empty, Field, Icon, Loader, Notice, PageHeader, ResourceNotice } from '../components/ui'
import { fallbackDetallesSolicitud, fallbackProductos, fallbackSolicitudes } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { authService, detalleSolicitudService, getApiError, productoService, solicitudService } from '../services/api'
import { formatDate } from '../utils/formatters'
import { normalizeRole, ROLES } from '../utils/roles'

const ESTADO_LABELS = {
  PENDIENTE: 'PENDIENTE',
  APROBADO: 'APROBADA',
  RECHAZADO: 'RECHAZADA',
  ATENDIDA: 'ATENDIDA',
}

function estadoLabel(estado) {
  return ESTADO_LABELS[estado] || estado || '-'
}

function estadoTone(estado) {
  if (estado === 'APROBADO' || estado === 'ATENDIDA') return 'success'
  if (estado === 'RECHAZADO') return 'danger'
  return 'warning'
}

export default function Solicitudes() {
  const [searchParams] = useSearchParams()
  const productoInicial = searchParams.get('productoId') || ''
  const details = useApiResource(detalleSolicitudService.listar, fallbackDetallesSolicitud)
  const products = useApiResource(productoService.listar, fallbackProductos)
  const [me, setMe] = useState(null)
  const [requests, setRequests] = useState({ data: fallbackSolicitudes, loading: true, error: '' })
  const [open, setOpen] = useState(Boolean(productoInicial))
  const [form, setForm] = useState({ productoId: productoInicial, cantidad: '', comentario: '' })
  const [status, setStatus] = useState({ tone: '', text: '' })
  const [revisando, setRevisando] = useState(null)
  const [motivoRechazo, setMotivoRechazo] = useState('')
  const [guardandoSolicitud, setGuardandoSolicitud] = useState(false)
  const [guardandoRevision, setGuardandoRevision] = useState(false)

  useEffect(() => {
    authService.me()
      .then(({ data }) => setMe({ ...data, rol: normalizeRole(data.rol) }))
      .catch((error) => setRequests({ data: fallbackSolicitudes, loading: false, error: getApiError(error) }))
  }, [])

  const cargarSolicitudes = useCallback((usuario, cancelado = () => false) => {
    if (!usuario) return Promise.resolve()
    setRequests((prev) => ({ ...prev, loading: true }))
    const fetch = usuario.rol === ROLES.GERENTE
      ? solicitudService.listar()
      : solicitudService.listarPorUsuario(usuario.id)
    return fetch
      .then(({ data }) => { if (!cancelado()) setRequests({ data, loading: false, error: '' }) })
      .catch((err) => { if (!cancelado()) setRequests({ data: fallbackSolicitudes, loading: false, error: getApiError(err) }) })
  }, [])

  useEffect(() => {
    if (!me) return undefined
    let cancelado = false
    const timer = window.setTimeout(() => cargarSolicitudes(me, () => cancelado), 0)
    return () => {
      cancelado = true
      window.clearTimeout(timer)
    }
  }, [me, cargarSolicitudes])

  const esGerente = me?.rol === ROLES.GERENTE
  const esAlmacen = me?.rol === ROLES.ENCARGADO_ALMACEN

  async function submit(event) {
    event.preventDefault()
    if (guardandoSolicitud) return
    if (!form.productoId || !form.cantidad) return setStatus({ tone: 'danger', text: 'Selecciona un producto e indica la cantidad.' })
    if (Number(form.cantidad) <= 0) return setStatus({ tone: 'danger', text: 'La cantidad debe ser mayor a cero.' })
    if (localStorage.getItem('hotel_demo')) return setStatus({ tone: 'warning', text: 'Vista demo: la solicitud fue validada localmente. Inicia sesion para registrarla.' })
    if (!me) return setStatus({ tone: 'danger', text: 'Cargando datos de usuario, intenta de nuevo.' })
    setGuardandoSolicitud(true)
    try {
      const payload = {
        solicitanteId: me.id,
        comentario: form.comentario,
        detalles: [{ productoId: Number(form.productoId), cantidad: Number(form.cantidad) }],
      }
      await solicitudService.crear(payload)
      await cargarSolicitudes(me)
      details.reload()
      setForm({ productoId: '', cantidad: '', comentario: '' })
      setOpen(false)
      setStatus({ tone: 'success', text: 'Solicitud de reabastecimiento registrada.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    } finally {
      setGuardandoSolicitud(false)
    }
  }

  async function confirmarRevision() {
    if (guardandoRevision) return
    if (!revisando || !me) return setStatus({ tone: 'danger', text: 'No se pudo identificar la solicitud o el usuario actual.' })
    if (revisando.accion === 'RECHAZADO' && !motivoRechazo.trim()) {
      return setStatus({ tone: 'danger', text: 'Debes indicar el motivo del rechazo.' })
    }
    setGuardandoRevision(true)
    try {
      await solicitudService.revisar(revisando.id, {
        aprobadorId: me.id,
        estado: revisando.accion,
        comentario: revisando.accion === 'RECHAZADO' ? motivoRechazo : undefined,
      })
      await cargarSolicitudes(me)
      setRevisando(null)
      setMotivoRechazo('')
      setStatus({ tone: 'success', text: 'Solicitud actualizada.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    } finally {
      setGuardandoRevision(false)
    }
  }

  return (
    <>
      <PageHeader title="Solicitudes de reabastecimiento" description="Gestiona pedidos de reposicion para mantener la operacion continua." actions={esAlmacen && <button className="button primary" onClick={() => setOpen(!open)}><Icon name="plus" size={17} /> Nueva solicitud</button>} />
      <ResourceNotice error={requests.error || details.error || products.error} />
      {status.text && <Notice tone={status.tone}>{status.text}</Notice>}
      {open && esAlmacen && (
        <Card className="form-card">
          <div className="card-title"><div><span>Abastecimiento</span><h3>Crear solicitud</h3></div></div>
          <form className="form-grid" onSubmit={submit}>
            <Field label="Producto">
              <select value={form.productoId} onChange={(e) => setForm({ ...form, productoId: e.target.value })}>
                <option value="">Seleccionar producto</option>
                {products.data.map((item) => <option key={item.id} value={item.id}>{item.nombre}</option>)}
              </select>
            </Field>
            <Field label="Cantidad solicitada">
              <input type="number" min="0.01" step="0.01" value={form.cantidad} onChange={(e) => setForm({ ...form, cantidad: e.target.value })} />
            </Field>
            <Field label="Comentario">
              <textarea value={form.comentario} onChange={(e) => setForm({ ...form, comentario: e.target.value })} placeholder="Motivo de la solicitud" />
            </Field>
            <div className="form-actions form-full"><button className="button primary" disabled={guardandoSolicitud}>{guardandoSolicitud ? 'Registrando...' : 'Registrar solicitud'}</button></div>
          </form>
        </Card>
      )}
      {revisando && (
        <Card className="form-card">
          <div className="card-title"><div><span>Revision</span><h3>{revisando.accion === 'APROBADO' ? 'Aprobar' : 'Rechazar'} solicitud #{revisando.id}</h3></div></div>
          <div className="form-grid">
            {revisando.accion === 'RECHAZADO' && (
              <Field label="Motivo del rechazo">
                <textarea value={motivoRechazo} onChange={(event) => setMotivoRechazo(event.target.value)} placeholder="Escribe el motivo del rechazo" />
              </Field>
            )}
            <div className="form-actions form-full">
              <button className="button primary" onClick={confirmarRevision} disabled={guardandoRevision}>{guardandoRevision ? 'Guardando...' : 'Confirmar'}</button>
              <button className="button subtle" onClick={() => { setRevisando(null); setMotivoRechazo('') }} disabled={guardandoRevision}>Cancelar</button>
            </div>
          </div>
        </Card>
      )}
      <Card>
        {requests.loading ? <Loader /> : requests.data.length === 0 ? <Empty text="No hay solicitudes para mostrar." /> : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Solicitud</th><th>Fecha</th><th>Producto</th><th>Cantidad</th><th>Solicitante</th><th>Estado</th>
                  {(esGerente || esAlmacen) && <th>Acciones</th>}
                </tr>
              </thead>
              <tbody>
                {requests.data.map((item) => {
                  const detail = details.data.find((entry) => entry.solicitud?.id === item.id)
                  return (
                    <tr key={item.id}>
                      <td><strong>#{item.id}</strong></td>
                      <td>{formatDate(item.fechaSolicitud)}</td>
                      <td>{detail?.producto?.nombre || '-'}</td>
                      <td>{detail?.cantidadSolicitada || '-'}</td>
                      <td>{item.solicitante?.nombres || item.solicitante?.usuario || '-'}</td>
                      <td><Badge tone={estadoTone(item.estado)}>{estadoLabel(item.estado)}</Badge></td>
                      {(esGerente || esAlmacen) && (
                        <td>
                          {esGerente && item.estado === 'PENDIENTE' && (
                            <div className="form-actions">
                              <button className="button subtle" onClick={() => setRevisando({ id: item.id, accion: 'APROBADO' })}>Aprobar</button>
                              <button className="button subtle" onClick={() => setRevisando({ id: item.id, accion: 'RECHAZADO' })}>Rechazar</button>
                            </div>
                          )}
                          {esAlmacen && item.estado === 'APROBADO' && (
                            <Link className="button subtle" to={`/entradas?solicitudId=${item.id}`}>Registrar recepcion</Link>
                          )}
                          {!(esGerente && item.estado === 'PENDIENTE') && !(esAlmacen && item.estado === 'APROBADO') && '-'}
                        </td>
                      )}
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </>
  )
}
