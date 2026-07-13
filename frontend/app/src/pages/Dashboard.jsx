import BarChart from '../components/BarChart'
import { Card, Empty, Icon, Loader, PageHeader, ResourceNotice } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { dashboardService, notificacionStockService } from '../services/api'
import { formatDate } from '../utils/formatters'

export default function Dashboard() {
  const dashboard = useApiResource(dashboardService.consultar)
  const alertas = useApiResource(notificacionStockService.ultimas)

  const top = dashboard.data?.topProductosConsumidos30Dias || []
  const categorias = dashboard.data?.consumoPorCategoria30Dias || []
  const variaciones = dashboard.data?.variacionStock30Dias || []

  return (
    <>
      <PageHeader title="Dashboard ejecutivo" description="Indicadores de consumo y variacion de stock de los ultimos 30 dias." actions={<span className="date-chip">Actualizado hoy</span>} />
      <ResourceNotice error={dashboard.error} />
      <div className="dashboard-grid">
        <Card>
          <div className="card-title"><div><span>Consumo</span><h3>Top 5 productos mas consumidos (30 dias)</h3></div></div>
          {dashboard.loading ? <Loader /> : top.length === 0 ? <Empty text="Sin consumo registrado en los ultimos 30 dias." /> : <div className="table-wrap"><table><thead><tr><th>Producto</th><th>Cantidad consumida</th><th>Unidad</th></tr></thead><tbody>{top.map((item) => <tr key={item.idProducto}><td><strong>{item.producto}</strong></td><td>{item.cantidadConsumida}</td><td>{item.unidad}</td></tr>)}</tbody></table></div>}
        </Card>
        <Card>
          <div className="card-title"><div><span>Consumo</span><h3>Consumo por categoria (30 dias)</h3></div></div>
          {dashboard.loading ? <Loader /> : <BarChart data={categorias.map((item) => ({ label: item.categoria, value: Number(item.cantidadConsumida) }))} />}
        </Card>
      </div>
      <Card>
        <div className="card-title"><div><span>Inventario</span><h3>Variacion de stock (30 dias)</h3></div></div>
        {dashboard.loading ? <Loader /> : variaciones.length === 0 ? <Empty text="No hay productos registrados." /> : <div className="table-wrap"><table><thead><tr><th>Producto</th><th>Stock actual</th><th>Stock hace 30 dias</th><th>Variacion</th></tr></thead><tbody>{variaciones.map((item) => <tr key={item.idProducto}><td><strong>{item.producto}</strong></td><td>{item.stockActual}</td><td>{item.stockHaceTreintaDias}</td><td className={Number(item.variacionStock) < 0 ? 'stock-danger' : ''}>{item.variacionStock}</td></tr>)}</tbody></table></div>}
      </Card>
      <Card>
        <div className="card-title"><div><span>Stock critico</span><h3>Ultimas notificaciones</h3></div><Icon name="bell" size={19} /></div>
        {alertas.loading ? <Loader /> : alertas.data.length === 0 ? <Empty text="No hay notificaciones de stock critico." /> : <div className="activity-list">{alertas.data.map((item) => <div className="activity-row" key={item.id}><i className={item.resuelta ? 'read' : ''} /><div><p>{item.producto?.nombre || 'Producto'} - stock {item.stockActual} / minimo {item.stockMinimo}</p><span>{formatDate(item.fechaEnvio)}</span></div></div>)}</div>}
      </Card>
    </>
  )
}
