import { useState } from 'react'
import { Card, Field, Icon, Loader, Notice, PageHeader, ResourceNotice, SearchBox } from '../components/ui'
import { fallbackProveedores } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { getApiError, proveedorService } from '../services/api'

const initial = { nombre: '', ruc: '', telefono: '', correo: '', direccion: '' }

export default function Proveedores() {
  const suppliers = useApiResource(proveedorService.listar, fallbackProveedores)
  const [query, setQuery] = useState('')
  const [form, setForm] = useState(initial)
  const [open, setOpen] = useState(false)
  const [status, setStatus] = useState({ tone: '', text: '' })
  const filtered = suppliers.data.filter((item) => item.nombre.toLowerCase().includes(query.toLowerCase()))

  async function submit(event) {
    event.preventDefault()
    if (!form.nombre) return setStatus({ tone: 'danger', text: 'El nombre del proveedor es obligatorio.' })
    try {
      const { data } = await proveedorService.crear({ ...form, ruc: form.ruc ? Number(form.ruc) : null })
      suppliers.setData([data, ...suppliers.data])
      setForm(initial)
      setOpen(false)
      setStatus({ tone: 'success', text: 'Proveedor registrado correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  return (
    <>
      <PageHeader title="Proveedores" description="Directorio de aliados comerciales y datos de contacto." actions={<button className="button primary" onClick={() => setOpen(!open)}><Icon name="plus" size={17} /> Nuevo proveedor</button>} />
      <ResourceNotice error={suppliers.error} />{status.text && <Notice tone={status.tone}>{status.text}</Notice>}
      {open && <Card className="form-card"><div className="card-title"><div><span>Directorio</span><h3>Registrar proveedor</h3></div></div><form className="form-grid" onSubmit={submit}><Field label="Razon social"><input value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} /></Field><Field label="RUC"><input type="number" value={form.ruc} onChange={(e) => setForm({ ...form, ruc: e.target.value })} /></Field><Field label="Telefono"><input value={form.telefono} onChange={(e) => setForm({ ...form, telefono: e.target.value })} /></Field><Field label="Correo"><input type="email" value={form.correo} onChange={(e) => setForm({ ...form, correo: e.target.value })} /></Field><Field label="Direccion"><input value={form.direccion} onChange={(e) => setForm({ ...form, direccion: e.target.value })} /></Field><div className="form-actions form-full"><button className="button primary">Guardar proveedor</button></div></form></Card>}
      <Card><div className="toolbar"><SearchBox value={query} onChange={setQuery} placeholder="Buscar proveedor..." /></div>{suppliers.loading ? <Loader /> : <div className="table-wrap"><table><thead><tr><th>Proveedor</th><th>RUC</th><th>Telefono</th><th>Correo</th><th>Direccion</th></tr></thead><tbody>{filtered.map((item) => <tr key={item.id}><td><strong>{item.nombre}</strong></td><td>{item.ruc || '-'}</td><td>{item.telefono || '-'}</td><td>{item.correo || item.email || '-'}</td><td>{item.direccion || '-'}</td></tr>)}</tbody></table></div>}</Card>
    </>
  )
}
