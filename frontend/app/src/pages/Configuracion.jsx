import { useState } from 'react'
import { Card, Field, Loader, Notice, PageHeader, ResourceNotice } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { categoriaService, getApiError, productoService, unidadService } from '../services/api'

const emptyCategory = { id: null, nombre: '' }
const emptyUnit = { id: null, nombre: '', abreviatura: '' }

export default function Configuracion() {
  const products = useApiResource(productoService.listar)
  const categories = useApiResource(categoriaService.listar)
  const units = useApiResource(unidadService.listar)
  const [categoryForm, setCategoryForm] = useState(emptyCategory)
  const [unitForm, setUnitForm] = useState(emptyUnit)
  const [stockForm, setStockForm] = useState({ productoId: '', stockMinimo: '' })
  const [status, setStatus] = useState({ tone: '', text: '' })

  async function saveCategory(event) {
    event.preventDefault()
    if (!categoryForm.nombre.trim()) return setStatus({ tone: 'danger', text: 'El nombre de la categoria es obligatorio.' })
    try {
      const { data } = categoryForm.id ? await categoriaService.actualizar(categoryForm.id, { nombre: categoryForm.nombre }) : await categoriaService.crear({ nombre: categoryForm.nombre })
      categories.setData(categoryForm.id ? categories.data.map((item) => item.id === categoryForm.id ? data : item) : [...categories.data, data])
      setCategoryForm(emptyCategory)
      setStatus({ tone: 'success', text: 'Categoria guardada correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  async function removeCategory(id) {
    try {
      await categoriaService.eliminar(id)
      categories.setData(categories.data.filter((item) => item.id !== id))
      setStatus({ tone: 'success', text: 'Categoria eliminada correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  async function saveUnit(event) {
    event.preventDefault()
    if (!unitForm.nombre.trim() || !unitForm.abreviatura.trim()) return setStatus({ tone: 'danger', text: 'Nombre y abreviatura son obligatorios.' })
    try {
      const payload = { nombre: unitForm.nombre, abreviatura: unitForm.abreviatura }
      const { data } = unitForm.id ? await unidadService.actualizar(unitForm.id, payload) : await unidadService.crear(payload)
      units.setData(unitForm.id ? units.data.map((item) => item.id === unitForm.id ? data : item) : [...units.data, data])
      setUnitForm(emptyUnit)
      setStatus({ tone: 'success', text: 'Unidad de medida guardada correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  async function removeUnit(id) {
    try {
      await unidadService.eliminar(id)
      units.setData(units.data.filter((item) => item.id !== id))
      setStatus({ tone: 'success', text: 'Unidad de medida eliminada correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  async function saveMinimum(event) {
    event.preventDefault()
    if (!stockForm.productoId || stockForm.stockMinimo === '') return setStatus({ tone: 'danger', text: 'Selecciona producto e indica stock minimo.' })
    if (Number(stockForm.stockMinimo) < 0) return setStatus({ tone: 'danger', text: 'El stock minimo no puede ser negativo.' })
    try {
      const { data } = await productoService.actualizar(Number(stockForm.productoId), { stockMinimo: Number(stockForm.stockMinimo) })
      products.setData(products.data.map((item) => item.id === data.id ? data : item))
      setStockForm({ productoId: '', stockMinimo: '' })
      setStatus({ tone: 'success', text: 'Stock minimo actualizado correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  return (
    <>
      <PageHeader title="Configuracion del sistema" description="Administra niveles minimos y catalogos base del inventario." />
      <ResourceNotice error={products.error || categories.error || units.error} />
      {status.text && <Notice tone={status.tone}>{status.text}</Notice>}
      <Card className="form-card"><div className="card-title"><div><span>Alertas</span><h3>Stock minimo por producto</h3></div></div><form className="form-grid" onSubmit={saveMinimum}><Field label="Producto"><select value={stockForm.productoId} onChange={(event) => setStockForm({ ...stockForm, productoId: event.target.value, stockMinimo: products.data.find((item) => item.id === Number(event.target.value))?.stockMinimo ?? '' })}><option value="">Seleccionar</option>{products.data.map((item) => <option value={item.id} key={item.id}>{item.nombre}</option>)}</select></Field><Field label="Stock minimo"><input type="number" min="0" step="0.01" value={stockForm.stockMinimo} onChange={(event) => setStockForm({ ...stockForm, stockMinimo: event.target.value })} /></Field><div className="form-actions"><button className="button primary">Guardar minimo</button></div></form></Card>
      <div className="split-grid">
        <Card><div className="card-title"><div><span>Catalogo</span><h3>Categorias</h3></div></div><form className="stack-form" onSubmit={saveCategory}><Field label="Nombre"><input value={categoryForm.nombre} onChange={(event) => setCategoryForm({ ...categoryForm, nombre: event.target.value })} /></Field><div className="form-actions"><button className="button primary">{categoryForm.id ? 'Actualizar' : 'Crear'}</button>{categoryForm.id && <button type="button" className="button subtle" onClick={() => setCategoryForm(emptyCategory)}>Cancelar</button>}</div></form>{categories.loading ? <Loader /> : <div className="compact-list">{categories.data.map((item) => <div className="compact-row" key={item.id}><strong>{item.nombre}</strong><div className="form-actions"><button className="button subtle" onClick={() => setCategoryForm(item)}>Editar</button><button className="button subtle" onClick={() => removeCategory(item.id)}>Eliminar</button></div></div>)}</div>}</Card>
        <Card><div className="card-title"><div><span>Catalogo</span><h3>Unidades de medida</h3></div></div><form className="stack-form" onSubmit={saveUnit}><Field label="Nombre"><input value={unitForm.nombre} onChange={(event) => setUnitForm({ ...unitForm, nombre: event.target.value })} /></Field><Field label="Abreviatura"><input value={unitForm.abreviatura} onChange={(event) => setUnitForm({ ...unitForm, abreviatura: event.target.value })} /></Field><div className="form-actions"><button className="button primary">{unitForm.id ? 'Actualizar' : 'Crear'}</button>{unitForm.id && <button type="button" className="button subtle" onClick={() => setUnitForm(emptyUnit)}>Cancelar</button>}</div></form>{units.loading ? <Loader /> : <div className="compact-list">{units.data.map((item) => <div className="compact-row" key={item.id}><div><strong>{item.nombre}</strong><span>{item.abreviatura}</span></div><div className="form-actions"><button className="button subtle" onClick={() => setUnitForm(item)}>Editar</button><button className="button subtle" onClick={() => removeUnit(item.id)}>Eliminar</button></div></div>)}</div>}</Card>
      </div>
    </>
  )
}
