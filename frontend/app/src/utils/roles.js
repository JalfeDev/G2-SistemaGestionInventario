export const ROLES = {
  ADMINISTRADOR: 'ADMINISTRADOR',
  GERENTE: 'GERENTE',
  ENCARGADO_ALMACEN: 'ENCARGADO_ALMACEN',
  HOUSEKEEPER: 'HOUSEKEEPER',
}

export const BACKEND_ROLES = {
  ADMINISTRADOR: 'ADMINISTRADOR',
  GERENTE: 'GERENTE',
  ALMACEN: 'ALMACEN',
  HOUSEKEEPING: 'HOUSEKEEPING',
}

export const BACKEND_ROLE_NAMES = Object.values(BACKEND_ROLES)

const ROLE_ALIASES = {
  ADMIN: ROLES.ADMINISTRADOR,
  ADMINISTRADOR: ROLES.ADMINISTRADOR,
  ADMINISTRATOR: ROLES.ADMINISTRADOR,
  GERENTE: ROLES.GERENTE,
  ENCARGADO_ALMACEN: ROLES.ENCARGADO_ALMACEN,
  ALMACEN: ROLES.ENCARGADO_ALMACEN,
  HOUSEKEEPER: ROLES.HOUSEKEEPER,
  HOUSEKEEPING: ROLES.HOUSEKEEPER,
}

const ROLE_LABELS = {
  [ROLES.ADMINISTRADOR]: 'Administrador',
  [ROLES.GERENTE]: 'Gerente',
  [ROLES.ENCARGADO_ALMACEN]: 'Encargado de Almacén',
  [ROLES.HOUSEKEEPER]: 'Housekeeping',
}

const DEFAULT_ROUTES = {
  [ROLES.ADMINISTRADOR]: '/usuarios',
  [ROLES.GERENTE]: '/reporte-consumo',
  [ROLES.ENCARGADO_ALMACEN]: '/entradas',
  [ROLES.HOUSEKEEPER]: '/distribuciones',
}

export function normalizeRole(role) {
  if (typeof role !== 'string') return ''
  const normalized = role.trim().toUpperCase().replace(/^ROLE_/, '').replace(/[\s-]+/g, '_')
  return ROLE_ALIASES[normalized] || normalized
}

export function isReleaseRole(role) {
  return Object.values(ROLES).includes(normalizeRole(role))
}

export function isBackendRole(role) {
  return BACKEND_ROLE_NAMES.includes(role)
}

export function roleLabel(role) {
  return ROLE_LABELS[normalizeRole(role)] || role || '-'
}

export function defaultRouteForRole(role) {
  return DEFAULT_ROUTES[normalizeRole(role)] || '/login'
}

export function getStoredUser() {
  try {
    const user = JSON.parse(localStorage.getItem('hotel_user') || '{}')
    return { ...user, rol: normalizeRole(user.rol) }
  } catch {
    return {}
  }
}
