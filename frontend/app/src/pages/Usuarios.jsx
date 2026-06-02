import { useMemo, useState } from 'react'
import { Badge, Card, Field, Icon, Loader, Notice, PageHeader, ResourceNotice } from '../components/ui'
import { useApiResource } from '../hooks/useApiResource'
import { getApiError, rolService, usuarioService } from '../services/api'
import { BACKEND_ROLE_NAMES, isBackendRole, normalizeRole, roleLabel } from '../utils/roles'

const initial = { username: '', password: '', nombres: '', apellidos: '', email: '', rolId: '' }

export default function Usuarios() {
  const users = useApiResource(usuarioService.listar)
  const roles = useApiResource(rolService.listar)
  const [form, setForm] = useState(initial)
  const [open, setOpen] = useState(false)
  const [status, setStatus] = useState({ tone: '', text: '' })
  const validRoles = useMemo(() => roles.data
    .filter((role) => isBackendRole(role.nombre))
    .sort((left, right) => BACKEND_ROLE_NAMES.indexOf(left.nombre) - BACKEND_ROLE_NAMES.indexOf(right.nombre)), [roles.data])

  function getValidRoleId(rolId) {
    return validRoles.find((role) => role.id === Number(rolId))?.id
  }

  async function submit(event) {
    event.preventDefault()
    if (!form.username || !form.password || !form.nombres || !form.apellidos || !form.rolId) return setStatus({ tone: 'danger', text: 'Completa nombre, apellido, usuario, contrasena y rol.' })
    const rolId = getValidRoleId(form.rolId)
    if (!rolId) return setStatus({ tone: 'danger', text: 'Selecciona un rol permitido.' })
    try {
      const { data } = await usuarioService.crear({ ...form, rolId })
      users.setData([data, ...users.data])
      setForm(initial)
      setOpen(false)
      setStatus({ tone: 'success', text: 'Usuario creado correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  async function changeRole(user, rolId) {
    const validRoleId = getValidRoleId(rolId)
    if (!validRoleId) return setStatus({ tone: 'danger', text: 'Selecciona un rol permitido.' })
    try {
      const { data } = await usuarioService.cambiarRol(user.id, validRoleId)
      users.setData(users.data.map((item) => item.id === user.id ? data : item))
      setStatus({ tone: 'success', text: 'Rol actualizado correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  async function toggleActive(user) {
    try {
      const { data } = await usuarioService.cambiarActivo(user.id, !user.activo)
      users.setData(users.data.map((item) => item.id === user.id ? data : item))
      setStatus({ tone: 'success', text: data.activo ? 'Usuario activado correctamente.' : 'Usuario desactivado correctamente.' })
    } catch (error) {
      setStatus({ tone: 'danger', text: getApiError(error) })
    }
  }

  return (
    <>
      <PageHeader title="Usuarios" description="Control de accesos y roles del equipo operativo." actions={<button className="button primary" onClick={() => setOpen(!open)}><Icon name="plus" size={17} /> Nuevo usuario</button>} />
      <ResourceNotice error={users.error || roles.error} />{status.text && <Notice tone={status.tone}>{status.text}</Notice>}
      {open && <Card className="form-card"><div className="card-title"><div><span>Seguridad</span><h3>Crear usuario</h3></div></div><form className="form-grid" onSubmit={submit}><Field label="Usuario"><input value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} /></Field><Field label="Contrasena"><input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></Field><Field label="Nombres"><input value={form.nombres} onChange={(e) => setForm({ ...form, nombres: e.target.value })} /></Field><Field label="Apellidos"><input value={form.apellidos} onChange={(e) => setForm({ ...form, apellidos: e.target.value })} /></Field><Field label="Correo"><input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></Field><Field label="Rol"><select value={form.rolId} onChange={(e) => setForm({ ...form, rolId: e.target.value })}><option value="">Seleccionar</option>{validRoles.map((item) => <option value={item.id} key={item.id}>{roleLabel(item.nombre)}</option>)}</select></Field><div className="form-actions form-full"><button className="button primary">Guardar usuario</button></div></form></Card>}
      <Card>{users.loading ? <Loader /> : <div className="table-wrap"><table><thead><tr><th>Colaborador</th><th>Usuario</th><th>Correo</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr></thead><tbody>{users.data.map((item) => <tr key={item.id}><td><strong>{[item.nombres, item.apellidos].filter(Boolean).join(' ') || '-'}</strong></td><td>{item.usuario}</td><td>{item.email || '-'}</td><td><select value={validRoles.find((role) => normalizeRole(role.nombre) === normalizeRole(item.rol))?.id || ''} onChange={(event) => changeRole(item, event.target.value)}>{validRoles.map((role) => <option value={role.id} key={role.id}>{roleLabel(role.nombre)}</option>)}</select></td><td><Badge tone={item.activo ? 'success' : 'danger'}>{item.activo ? 'Activo' : 'Desactivado'}</Badge></td><td><button className="button subtle" onClick={() => toggleActive(item)}>{item.activo ? 'Desactivar' : 'Activar'}</button></td></tr>)}</tbody></table></div>}</Card>
    </>
  )
}
