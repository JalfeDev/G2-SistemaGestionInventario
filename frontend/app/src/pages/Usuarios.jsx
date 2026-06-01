import { useState } from 'react'
import { Badge, Card, Field, Icon, Loader, Notice, PageHeader, ResourceNotice } from '../components/ui'
import { fallbackRoles, fallbackUsuarios } from '../data/fallbackData'
import { useApiResource } from '../hooks/useApiResource'
import { getApiError, rolService, usuarioService } from '../services/api'

const initial = { username: '', password: '', nombres: '', apellidos: '', email: '', rolId: '' }

export default function Usuarios() {
  const users = useApiResource(usuarioService.listar, fallbackUsuarios)
  const roles = useApiResource(rolService.listar, fallbackRoles)
  const [form, setForm] = useState(initial)
  const [open, setOpen] = useState(false)
  const [status, setStatus] = useState({ tone: '', text: '' })

  async function submit(event) {
    event.preventDefault()
    if (!form.username || !form.password || !form.rolId) return setStatus({ tone: 'danger', text: 'Completa usuario, contrasena y rol.' })
    try {
      const { data } = await usuarioService.crear({ ...form, rolId: Number(form.rolId) })
      users.setData([data, ...users.data])
      setForm(initial)
      setOpen(false)
      setStatus({ tone: 'success', text: 'Usuario creado correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  return (
    <>
      <PageHeader title="Usuarios" description="Control de accesos y roles del equipo operativo." actions={<button className="button primary" onClick={() => setOpen(!open)}><Icon name="plus" size={17} /> Nuevo usuario</button>} />
      <ResourceNotice error={users.error || roles.error} />{status.text && <Notice tone={status.tone}>{status.text}</Notice>}
      {open && <Card className="form-card"><div className="card-title"><div><span>Seguridad</span><h3>Crear usuario</h3></div></div><form className="form-grid" onSubmit={submit}><Field label="Usuario"><input value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} /></Field><Field label="Contrasena"><input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></Field><Field label="Nombres"><input value={form.nombres} onChange={(e) => setForm({ ...form, nombres: e.target.value })} /></Field><Field label="Apellidos"><input value={form.apellidos} onChange={(e) => setForm({ ...form, apellidos: e.target.value })} /></Field><Field label="Correo"><input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></Field><Field label="Rol"><select value={form.rolId} onChange={(e) => setForm({ ...form, rolId: e.target.value })}><option value="">Seleccionar</option>{roles.data.map((item) => <option value={item.id} key={item.id}>{item.nombre}</option>)}</select></Field><div className="form-actions form-full"><button className="button primary">Guardar usuario</button></div></form></Card>}
      <Card>{users.loading ? <Loader /> : <div className="table-wrap"><table><thead><tr><th>Colaborador</th><th>Usuario</th><th>Correo</th><th>Rol</th></tr></thead><tbody>{users.data.map((item) => <tr key={item.id}><td><strong>{[item.nombres, item.apellidos].filter(Boolean).join(' ') || item.nombre || '-'}</strong></td><td>{item.usuario || item.username}</td><td>{item.email || '-'}</td><td><Badge>{item.rol || 'Sin rol'}</Badge></td></tr>)}</tbody></table></div>}</Card>
    </>
  )
}
