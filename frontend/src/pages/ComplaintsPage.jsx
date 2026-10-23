import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'

export default function ComplaintsPage() {
  const { t } = useLang()
  const { isAdmin, getComplaints, resolveComplaint } = useStore()
  const [complaints, setComplaints] = useState([])
  const [loading, setLoading] = useState(true)
  const [decisions, setDecisions] = useState({})

  const load = useCallback(() => {
    setLoading(true)
    getComplaints()
      .then((list) => setComplaints(list || []))
      .catch(() => setComplaints([]))
      .finally(() => setLoading(false))
  }, [getComplaints])

  useEffect(() => { if (isAdmin) load() }, [isAdmin, load])

  if (!isAdmin) {
    return (
      <article className="mge-article">
        <h1 className="mge-firstheading">{t('complaintsTitle')}</h1>
        <hr className="mge-heading-rule" />
        <div className="mge-notice">{t('adminOnly')} <Link to="/login">{t('login')}</Link></div>
      </article>
    )
  }

  const open = complaints.filter((c) => c.status === 'OPEN')
  const resolved = complaints.filter((c) => c.status === 'RESOLVED')

  const onResolve = async (id) => { await resolveComplaint(id, decisions[id]); load() }

  const renderRow = (c) => (
    <div key={c.id} className={`mge-complaint-card ${c.status.toLowerCase()}`}>
      <div className="mge-complaint-head">
        <span>
          <strong>{t('forArticle')}:</strong> {c.genreName}{' '}
          {c.lang && <span className="mge-badge light">{c.lang.toUpperCase()}</span>}
        </span>
        <span className={`mge-status ${c.status === 'OPEN' ? 'pending' : 'approved'}`}>
          {c.status === 'OPEN' ? t('statusOpen') : t('statusResolved')}
        </span>
      </div>
      <p className="mge-complaint-reason">“{c.reason}”</p>
      <div className="mge-complaint-meta">
        {t('proposedBy')}: {c.authorName} · {new Date(c.createdAt).toLocaleString()}
      </div>
      {c.status === 'OPEN' ? (
        <div className="mge-mod-actions">
          <input
            className="mge-reason-input"
            placeholder={t('resolution')}
            value={decisions[c.id] || ''}
            onChange={(e) => setDecisions((p) => ({ ...p, [c.id]: e.target.value }))}
          />
          <button className="mge-btn primary" onClick={() => onResolve(c.id)}>✔ {t('resolve')}</button>
        </div>
      ) : (
        c.decision && <p className="mge-complaint-decision"><strong>{t('resolution')}:</strong> {c.decision}</p>
      )}
    </div>
  )

  return (
    <article className="mge-article">
      <h1 className="mge-firstheading">{t('complaintsTitle')}</h1>
      <hr className="mge-heading-rule" />

      <h2 className="mge-section-h">{t('openComplaints')}</h2>
      {loading ? (
        <p>…</p>
      ) : open.length === 0 ? (
        <div className="mge-notice success">{t('noComplaints')}</div>
      ) : (
        open.map(renderRow)
      )}

      {resolved.length > 0 && (
        <>
          <h2 className="mge-section-h">{t('statusResolved')}</h2>
          {resolved.map(renderRow)}
        </>
      )}
    </article>
  )
}
