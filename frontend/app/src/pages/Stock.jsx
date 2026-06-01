import { useMemo, useState } from 'react'
import {
  Badge, Card, Empty, Icon, Loader,
  PageHeader, ResourceNotice, SearchBox
} from '../components/ui'
import { fallbackProductos } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { productoService } from '../services/api'

export default function Stock() {
  // Patrón Service Layer — productoService centraliza la llamada al backend
  const products = useApiResource(productoService.listar, fallbackProductos)
  const [query, setQuery]               = useState('')
  const [categoriaFiltro, setCategoria] = useState('')

  const categorias = useMemo(
    () => [...new Set(products.data.map((p) => p.categoria?.nombre).filter(Boolean))],
    [products.data]
  )

  const filtered = useMemo(
    () =>
      products.data.filter((p) => {
        const coincideNombre    = p.nombre.toLowerCase().includes(query.toLowerCase())
        const coincideCategoria = categoriaFiltro === '' || p.categoria?.nombre === categoriaFiltro
        return coincideNombre && coincideCategoria
      }),
    [products.data, query, categoriaFiltro]
  )

  const criticos = products.data.filter(
    (p) => Number(p.stockActual) <= Number(p.stockMinimo)
  ).length

  return (
    <>
      <PageHeader
        title="Stock actual"
        description="Consulta en tiempo real el nivel de inventario de todos los productos."
        actions={
          <button className="button subtle" onClick={products.reload}>
            <Icon name="refresh" size={16} /> Actualizar
          </button>
        }
      />

      <ResourceNotice error={products.error} />

      {criticos > 0 && (
        <div className="alert-summary">
          <div className="alert-symbol"><Icon name="warning" size={22} /></div>
          <div>
            <strong>{criticos} producto{criticos > 1 ? 's' : ''} con stock crítico</strong>
            <span>Revisa los productos resaltados y considera solicitar reabastecimiento.</span>
          </div>
        </div>
      )}

      <Card>
        <div className="toolbar">
          <SearchBox
            value={query}
            onChange={setQuery}
            placeholder="Buscar por nombre..."
          />
          <select
            value={categoriaFiltro}
            onChange={(e) => setCategoria(e.target.value)}
            className="select-filter"
          >
            <option value="">Todas las categorías</option>
            {categorias.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
        </div>

        {products.loading ? (
          <Loader />
        ) : filtered.length === 0 ? (
          <Empty text="No hay productos que coincidan con los filtros." />
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Categoría</th>
                  <th>Unidad</th>
                  <th>Stock actual</th>
                  <th>Stock mínimo</th>
                  <th>Faltante</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((p) => {
                  const critico  = Number(p.stockActual) <= Number(p.stockMinimo)
                  const faltante = Math.max(0, Number(p.stockMinimo) - Number(p.stockActual))
                  return (
                    <tr key={p.id}>
                      <td><strong>{p.nombre}</strong></td>
                      <td>{p.categoria?.nombre || '-'}</td>
                      <td>{p.unidad?.abreviatura || p.unidad?.nombre || '-'}</td>
                      <td className={critico ? 'stock-danger' : ''}>
                        <strong>{p.stockActual}</strong>
                      </td>
                      <td>{p.stockMinimo}</td>
                      <td>{critico ? <strong>{faltante}</strong> : '-'}</td>
                      <td>
                        <Badge tone={critico ? 'danger' : 'success'}>
                          {critico ? 'Stock crítico' : 'Normal'}
                        </Badge>
                      </td>
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