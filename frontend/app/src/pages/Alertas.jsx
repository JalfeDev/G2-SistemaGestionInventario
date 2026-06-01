import { Card, Empty, Icon, Loader, PageHeader, ResourceNotice } from '../components/ui'
import { fallbackProductos } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { productoService } from '../services/api'

const fallback = fallbackProductos.filter((item) => Number(item.stockActual) <= Number(item.stockMinimo))

export default function Alertas() {
  const alerts = useApiResource(productoService.alertas, fallback)
  return (
    <>
      <PageHeader title="Alertas de stock minimo" description="Prioriza productos que requieren reposicion para evitar quiebres operativos." actions={<button className="button subtle" onClick={alerts.reload}><Icon name="refresh" size={16} /> Actualizar</button>} />
      <ResourceNotice error={alerts.error} />
      <div className="alert-summary"><div className="alert-symbol"><Icon name="warning" size={22} /></div><div><strong>{alerts.data.length} productos requieren atencion</strong><span>Revisa los niveles y genera solicitudes de reabastecimiento.</span></div></div>
      <Card>{alerts.loading ? <Loader /> : alerts.data.length === 0 ? <Empty text="Todo esta bajo control. No existen alertas de stock." /> : <div className="table-wrap"><table><thead><tr><th>Producto</th><th>Categoria</th><th>Unidad</th><th>Stock actual</th><th>Stock minimo</th><th>Faltante</th></tr></thead><tbody>{alerts.data.map((item) => <tr key={item.id}><td><strong>{item.nombre}</strong></td><td>{item.categoria?.nombre || '-'}</td><td>{item.unidad?.abreviatura || item.unidad?.nombre || '-'}</td><td className="stock-danger"><strong>{item.stockActual}</strong></td><td>{item.stockMinimo}</td><td><strong>{Math.max(0, Number(item.stockMinimo) - Number(item.stockActual))}</strong></td></tr>)}</tbody></table></div>}</Card>
    </>
  )
}
