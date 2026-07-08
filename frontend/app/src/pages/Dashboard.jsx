import { useMemo } from 'react'
import { Card, Icon, PageHeader, ResourceNotice } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { notificacionService, productoService, solicitudService } from '../services/api'
import { fallbackNotificaciones, fallbackProductos, fallbackSolicitudes } from '../data/fallbackData'
import { formatDate } from '../utils/formatters'

const fallback = { productos: fallbackProductos, solicitudes: fallbackSolicitudes, notificaciones: fallbackNotificaciones }

export default function Dashboard() {
  const { data, loading, error } = useApiResource(async () => {
    const [productos, solicitudes, notificaciones] = await Promise.all([productoService.listar(), solicitudService.listar(), notificacionService.listar()])
    return { data: { productos: productos.data, solicitudes: solicitudes.data, notificaciones: notificaciones.data } }
  }, fallback)
  const alertas = useMemo(() => data.productos.filter((item) => Number(item.stockActual) <= Number(item.stockMinimo)), [data.productos])
  const pendientes = data.solicitudes.filter((item) => item.estado === 'PENDIENTE')
  const stockTotal = data.productos.reduce((sum, item) => sum + Number(item.stockActual || 0), 0)

  return (
    <>
      <PageHeader title="Dashboard ejecutivo" description="Resumen operativo del inventario y prioridades del dia." actions={<span className="date-chip">Actualizado hoy</span>} />
      <ResourceNotice error={error} />
      <div className="stats-grid">
        <Metric icon="box" label="Productos registrados" value={loading ? '...' : data.productos.length} note="Catalogo activo" />
        <Metric icon="chart" label="Stock consolidado" value={loading ? '...' : stockTotal} note="Unidades disponibles" />
        <Metric icon="warning" label="Alertas activas" value={loading ? '...' : alertas.length} note="Requieren atencion" tone="danger" />
        <Metric icon="clipboard" label="Solicitudes pendientes" value={loading ? '...' : pendientes.length} note="Por gestionar" tone="warning" />
      </div>
      <div className="dashboard-grid">
        <Card>
          <div className="card-title"><div><span>Estado de inventario</span><h3>Productos con prioridad</h3></div><a href="/alertas">Ver alertas</a></div>
          <div className="priority-list">
            {alertas.slice(0, 4).map((product) => {
              const percentage = Math.min(100, Math.round((Number(product.stockActual) / Math.max(1, Number(product.stockMinimo))) * 100))
              return <div className="priority-row" key={product.id}><div className="priority-name"><strong>{product.nombre}</strong><span>{product.categoria?.nombre || 'Sin categoria'}</span></div><div className="progress"><i style={{ width: `${percentage}%` }} /></div><strong className="stock-danger">{product.stockActual} / {product.stockMinimo}</strong></div>
            })}
          </div>
        </Card>
        <Card>
          <div className="card-title"><div><span>Actividad reciente</span><h3>Notificaciones</h3></div><Icon name="bell" size={19} /></div>
          <div className="activity-list">
            {data.notificaciones.slice(0, 4).map((item) => <div className="activity-row" key={item.id}><i className={item.leido ? 'read' : ''} /><div><p>{item.mensaje}</p><span>{formatDate(item.fecha)}</span></div></div>)}
          </div>
        </Card>
      </div>
    </>
  )
}

function Metric({ icon, label, value, note, tone = '' }) {
  return <Card className={`metric ${tone}`}><div className="metric-icon"><Icon name={icon} size={21} /></div><div><span>{label}</span><strong>{value}</strong><small>{note}</small></div></Card>
}
