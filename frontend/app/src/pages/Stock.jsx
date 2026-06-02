import { useMemo, useState } from 'react'
import { Badge, Card, Empty, Icon, Loader, PageHeader, ResourceNotice, SearchBox } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { productoService } from '../services/api'

export default function Stock() {
  const products = useApiResource(productoService.listar)
  const [query, setQuery] = useState('')
  const [category, setCategory] = useState('')
  const categories = useMemo(() => [...new Set(products.data.map((item) => item.categoria?.nombre).filter(Boolean))].sort(), [products.data])
  const filtered = useMemo(() => products.data.filter((item) => {
    const matchesName = item.nombre.toLowerCase().includes(query.toLowerCase())
    const matchesCategory = !category || item.categoria?.nombre === category
    return matchesName && matchesCategory
  }), [products.data, query, category])

  return (
    <>
      <PageHeader title="Consulta de stock actual" description="Existencias actualizadas con los ultimos movimientos registrados." />
      <ResourceNotice error={products.error} />
      <Card>
        <div className="toolbar">
          <SearchBox value={query} onChange={setQuery} placeholder="Buscar producto..." />
          <select value={category} onChange={(event) => setCategory(event.target.value)}>
            <option value="">Todas las categorias</option>
            {categories.map((item) => <option key={item}>{item}</option>)}
          </select>
          <button className="button subtle" onClick={products.reload}><Icon name="refresh" size={16} /> Actualizar</button>
        </div>
        {products.loading ? <Loader /> : filtered.length === 0 ? <Empty /> : <div className="table-wrap"><table><thead><tr><th>Producto</th><th>Categoria</th><th>Unidad</th><th>Stock actual</th><th>Stock minimo</th><th>Estado</th></tr></thead><tbody>{filtered.map((item) => { const critical = Number(item.stockActual) <= Number(item.stockMinimo); return <tr className={critical ? 'row-danger' : ''} key={item.id}><td><strong>{item.nombre}</strong></td><td>{item.categoria?.nombre || '-'}</td><td>{item.unidad?.abreviatura || item.unidad?.nombre || '-'}</td><td className={critical ? 'stock-danger' : ''}><strong>{item.stockActual}</strong></td><td>{item.stockMinimo}</td><td><Badge tone={critical ? 'danger' : 'success'}>{critical ? 'Stock bajo' : 'Disponible'}</Badge></td></tr> })}</tbody></table></div>}
      </Card>
    </>
  )
}
