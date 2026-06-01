import { useMemo, useState } from 'react'
import { Card, Empty, Loader, PageHeader, ResourceNotice, SearchBox } from '../components/ui'
import { fallbackDetallesIngreso } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { detalleIngresoService } from '../services/api'
import { formatDate, money } from '../utils/formatters'

export default function HistorialPrecios() {
  const history = useApiResource(detalleIngresoService.listar, fallbackDetallesIngreso)
  const [query, setQuery] = useState('')
  const filtered = useMemo(() => history.data.filter((item) => `${item.producto?.nombre} ${item.proveedor?.nombre}`.toLowerCase().includes(query.toLowerCase())), [history.data, query])

  return (
    <>
      <PageHeader title="Historial de precios" description="Consulta costos registrados por producto y proveedor en cada ingreso." />
      <ResourceNotice error={history.error} />
      <Card><div className="toolbar"><SearchBox value={query} onChange={setQuery} placeholder="Buscar producto o proveedor..." /></div>{history.loading ? <Loader /> : filtered.length === 0 ? <Empty /> : <div className="table-wrap"><table><thead><tr><th>Fecha de ingreso</th><th>Producto</th><th>Proveedor</th><th>Cantidad</th><th>Costo unitario</th><th>Total</th></tr></thead><tbody>{filtered.map((item) => <tr key={item.id}><td>{formatDate(item.ingresoInventario?.fechaIngreso)}</td><td><strong>{item.producto?.nombre || '-'}</strong></td><td>{item.proveedor?.nombre || '-'}</td><td>{item.cantidad}</td><td>{money(item.costoUnitario)}</td><td><strong>{money(Number(item.cantidad) * Number(item.costoUnitario))}</strong></td></tr>)}</tbody></table></div>}</Card>
    </>
  )
}
