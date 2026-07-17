export default function BarChart({ data }) {
  if (!data || data.length === 0) return <div className="empty-state">Sin datos para mostrar.</div>
  const max = Math.max(...data.map((d) => d.value), 1)
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', padding: '8px 0' }}>
      {data.map((item) => (
        <div key={item.label} style={{ display: 'grid', gridTemplateColumns: '140px 1fr 48px', gap: '8px', alignItems: 'center' }}>
          <span style={{ fontSize: '13px', color: 'var(--text-muted)', textAlign: 'right', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }} title={item.label}>{item.label}</span>
          <div style={{ background: 'var(--surface-2, #e5e7eb)', borderRadius: '4px', height: '20px', overflow: 'hidden' }}>
            <div style={{ background: 'var(--accent, #3b82f6)', height: '100%', width: `${(item.value / max) * 100}%`, borderRadius: '4px', transition: 'width 0.3s ease' }} />
          </div>
          <span style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text)', textAlign: 'right' }}>{item.value}</span>
        </div>
      ))}
    </div>
  )
}
