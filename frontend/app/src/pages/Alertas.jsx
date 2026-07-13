import { useMemo } from 'react'
import { Badge, Card, Empty, Icon, Loader, PageHeader, ResourceNotice } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { notificacionStockService, productoService } from '../services/api'
import { formatDate } from '../utils/formatters'

export default function Alertas() {
  const alerts = useApiResource(productoService.alertas)
  const notificaciones = useApiResource(notificacionStockService.listar)
  const sortedAlerts = useMemo(() => [...alerts.data].sort((left, right) => (
    Number(right.stockMinimo) - Number(right.stockActual)
  ) - (
    Number(left.stockMinimo) - Number(left.stockActual)
  )), [alerts.data])
  return (
    <>
      <PageHeader title="Alertas de stock minimo" description="Prioriza productos que requieren reposicion. El backend registra alertas automaticas y envia correo si HU-10 esta habilitada." actions={<button className="button subtle" onClick={alerts.reload}><Icon name="refresh" size={16} /> Actualizar</button>} />
      <ResourceNotice error={alerts.error} />
      <div className="alert-summary"><div className="alert-symbol"><Icon name="warning" size={22} /></div><div><strong>{sortedAlerts.length} productos requieren atencion</strong><span>Revisa los niveles y coordina la reposicion necesaria.</span></div></div>
      <Card>{alerts.loading ? <Loader /> : sortedAlerts.length === 0 ? <Empty text="Todo esta bajo control. No existen alertas de stock." /> : <div className="table-wrap"><table><thead><tr><th>Producto</th><th>Categoria</th><th>Unidad</th><th>Stock actual</th><th>Stock minimo</th><th>Faltante</th></tr></thead><tbody>{sortedAlerts.map((item) => <tr key={item.id}><td><strong>{item.nombre}</strong></td><td>{item.categoria?.nombre || '-'}</td><td>{item.unidad?.abreviatura || item.unidad?.nombre || '-'}</td><td className="stock-danger"><strong>{item.stockActual}</strong></td><td>{item.stockMinimo}</td><td><strong>{Math.max(0, Number(item.stockMinimo) - Number(item.stockActual))}</strong></td></tr>)}</tbody></table></div>}</Card>
      <Card>
        <div className="card-title"><div><span>Auditoria</span><h3>Notificaciones de stock critico</h3></div></div>
        <ResourceNotice error={notificaciones.error} />
        {notificaciones.loading ? <Loader /> : notificaciones.data.length === 0 ? <Empty text="No se han generado notificaciones de stock critico." /> : <div className="table-wrap"><table><thead><tr><th>Producto</th><th>Tipo</th><th>Fecha</th><th>Enviado</th><th>Resuelta</th></tr></thead><tbody>{notificaciones.data.map((item) => <tr key={item.id}><td><strong>{item.producto?.nombre || '-'}</strong></td><td>{item.tipo}</td><td>{formatDate(item.fechaEnvio)}</td><td><Badge tone={item.enviado ? 'success' : 'danger'}>{item.enviado ? 'Si' : 'No'}</Badge></td><td><Badge tone={item.resuelta ? 'success' : 'warning'}>{item.resuelta ? 'Si' : 'No'}</Badge></td></tr>)}</tbody></table></div>}
      </Card>
    </>
  )
}
