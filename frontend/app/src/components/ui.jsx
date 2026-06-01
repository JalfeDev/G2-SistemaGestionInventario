export function Icon({ name, size = 20 }) {
  const paths = {
    dashboard: 'M3 13h8V3H3v10Zm10 8h8V11h-8v10ZM3 21h8v-6H3v6Zm10-12h8V3h-8v6Z',
    box: 'm12 3 8 4-8 4-8-4 8-4Zm-8 4v10l8 4 8-4V7m-8 4v10',
    arrowDown: 'M12 3v12m0 0 4-4m-4 4-4-4M4 19h16',
    rooms: 'M4 21V5a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v16M9 21v-5h6v5M8 8h.01M12 8h.01M16 8h.01M8 12h.01M12 12h.01M16 12h.01',
    truck: 'M10 17h4V5H2v12h3m9-8h4l4 4v4h-3m-9 0a2 2 0 1 1-4 0 2 2 0 0 1 4 0Zm9 0a2 2 0 1 1-4 0 2 2 0 0 1 4 0Z',
    users: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2m13-18a4 4 0 0 1 0 8M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm13 10v-2a4 4 0 0 0-3-3.87',
    upload: 'M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4m13-7-4-4-4 4m4-4v12',
    chart: 'M3 3v18h18M7 15l4-4 4 3 5-7',
    clipboard: 'M9 5h6m-6 4h6m-6 4h6m-6 4h4M7 3h10a2 2 0 0 1 2 2v16H5V5a2 2 0 0 1 2-2Z',
    bell: 'M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9m-8 13h4',
    logout: 'M10 17l5-5-5-5m5 5H3m10-9h6a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-6',
    menu: 'M4 6h16M4 12h16M4 18h16',
    search: 'm21 21-4.35-4.35m2.35-5.15a7.5 7.5 0 1 1-15 0 7.5 7.5 0 0 1 15 0Z',
    plus: 'M12 5v14m-7-7h14',
    refresh: 'M20 11a8.1 8.1 0 0 0-15.5-2M4 4v5h5m-5 4a8.1 8.1 0 0 0 15.5 2m.5 5v-5h-5',
    check: 'm5 12 4 4L19 6',
    warning: 'M12 3 2 21h20L12 3Zm0 6v4m0 4h.01',
  }
  return (
    <svg className="icon" width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d={paths[name] || paths.box} />
    </svg>
  )
}

export function PageHeader({ eyebrow = 'Gestion hotelera', title, description, actions }) {
  return (
    <div className="page-header">
      <div>
        <p className="eyebrow">{eyebrow}</p>
        <h1>{title}</h1>
        {description && <p className="page-description">{description}</p>}
      </div>
      {actions && <div className="header-actions">{actions}</div>}
    </div>
  )
}

export function Card({ children, className = '' }) {
  return <section className={`card ${className}`}>{children}</section>
}

export function Notice({ children, tone = 'info' }) {
  return <div className={`notice notice-${tone}`}>{children}</div>
}

export function Badge({ children, tone = 'neutral' }) {
  return <span className={`badge badge-${tone}`}>{children}</span>
}

export function Loader({ rows = 4 }) {
  return <div className="skeleton-list">{Array.from({ length: rows }, (_, index) => <div className="skeleton" key={index} />)}</div>
}

export function Empty({ text = 'No hay registros para mostrar.' }) {
  return <div className="empty-state">{text}</div>
}

export function ResourceNotice({ error }) {
  if (!error) return null
  return <Notice tone="warning">{error} Puedes seguir explorando la interfaz con la informacion temporal.</Notice>
}

export function SearchBox({ value, onChange, placeholder = 'Buscar...' }) {
  return (
    <label className="search-box">
      <Icon name="search" size={17} />
      <input value={value} onChange={(event) => onChange(event.target.value)} placeholder={placeholder} />
    </label>
  )
}

export function Field({ label, children, hint }) {
  return (
    <label className="field">
      <span>{label}</span>
      {children}
      {hint && <small>{hint}</small>}
    </label>
  )
}
