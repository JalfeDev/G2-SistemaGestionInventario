import BarChart from '../components/BarChart'
import { Card, Empty, Icon, Loader, PageHeader, ResourceNotice } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { dashboardService, notificacionStockService } from '../services/api'
import { formatDate } from '../utils/formatters'

function numberOrZero(value) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function textOrDash(value) {
  return value ?? '-'
}

export default function Dashboard() {
  const dashboard = useApiResource(dashboardService.consultar)
  const alertas = useApiResource(notificacionStockService.ultimas)

  const top = dashboard.data?.topProductosConsumidos30Dias || []
  const categorias = dashboard.data?.consumoPorCategoria30Dias || []
  const variaciones = dashboard.data?.variacionStock30Dias || []
  const resumen = dashboard.data?.resumen

  return (
    <>
      <PageHeader title="Dashboard ejecutivo" description="Indicadores de consumo y variacion de stock de los ultimos 30 dias." actions={<span className="date-chip">Actualizado hoy</span>} />
      <ResourceNotice error={dashboard.error} />
      {dashboard.loading ? <Loader /> : resumen && (
        <div className="stats-grid">
          <Card><div className="metric"><div className="metric-icon"><Icon name="box" size={20} /></div><div><span>Productos</span><strong>{resumen.totalProductos}</strong><small>Total registrados</small></div></div></Card>
          <Card><div className="metric warning"><div className="metric-icon"><Icon name="warning" size={20} /></div><div><span>Stock bajo</span><strong>{resumen.productosStockBajo}</strong><small>Requieren atencion</small></div></div></Card>
          <Card><div className="metric"><div className="metric-icon"><Icon name="clipboard" size={20} /></div><div><span>Solicitudes</span><strong>{resumen.solicitudesPendientes}</strong><small>Pendientes</small></div></div></Card>
          <Card><div className="metric danger"><div className="metric-icon"><Icon name="bell" size={20} /></div><div><span>Notificaciones</span><strong>{resumen.notificacionesStockActivas}</strong><small>Stock critico activo</small></div></div></Card>
        </div>
      )}
      <div className="dashboard-grid">
        <Card>
          <div className="card-title"><div><span>Consumo</span><h3>Top 5 productos mas consumidos (30 dias)</h3></div></div>
          {dashboard.loading ? <Loader /> : top.length === 0 ? <Empty text="Sin consumo registrado en los ultimos 30 dias." /> : <div className="table-wrap"><table><thead><tr><th>Producto</th><th>Cantidad consumida</th><th>Unidad</th></tr></thead><tbody>{top.map((item, index) => <tr key={item.idProducto ?? `${item.producto}-${index}`}><td><strong>{textOrDash(item.producto)}</strong></td><td>{item.cantidadConsumida ?? 0}</td><td>{textOrDash(item.unidad)}</td></tr>)}</tbody></table></div>}
        </Card>
        <Card>
          <div className="card-title"><div><span>Consumo</span><h3>Consumo por categoria (30 dias)</h3></div></div>
          {dashboard.loading ? <Loader /> : <BarChart data={categorias.map((item) => ({ label: textOrDash(item.categoria), value: numberOrZero(item.cantidadConsumida) }))} />}
        </Card>
      </div>
      <Card>
        <div className="card-title"><div><span>Inventario</span><h3>Variacion de stock (30 dias)</h3></div></div>
        {dashboard.loading ? <Loader /> : variaciones.length === 0 ? <Empty text="No hay productos registrados." /> : <div className="table-wrap"><table><thead><tr><th>Producto</th><th>Stock actual</th><th>Stock hace 30 dias</th><th>Variacion</th></tr></thead><tbody>{variaciones.map((item, index) => <tr key={item.idProducto ?? `${item.producto}-${index}`}><td><strong>{textOrDash(item.producto)}</strong></td><td>{item.stockActual ?? 0}</td><td>{item.stockHaceTreintaDias ?? 0}</td><td className={numberOrZero(item.variacionStock) < 0 ? 'stock-danger' : ''}>{item.variacionStock ?? 0}</td></tr>)}</tbody></table></div>}
      </Card>
      <Card>
        <div className="card-title"><div><span>Stock critico</span><h3>Ultimas notificaciones</h3></div><Icon name="bell" size={19} /></div>
        {alertas.loading ? <Loader /> : alertas.data.length === 0 ? <Empty text="No hay notificaciones de stock critico." /> : <div className="activity-list">{alertas.data.map((item) => <div className="activity-row" key={item.id}><i className={item.resuelta ? 'read' : ''} /><div><p>{item.producto?.nombre || 'Producto'} - stock {item.stockActual} / minimo {item.stockMinimo}</p><span>{formatDate(item.fechaEnvio)}</span></div></div>)}</div>}
      </Card>
    </>
  )
}
