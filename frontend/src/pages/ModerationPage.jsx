import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'
import MarkdownBlock from '../components/MarkdownBlock.jsx'

export default function ModerationPage() {
  const { t } = useLang()
  const { isAdmin, getPendingRevisions, approveRevision, rejectRevision } = useStore()
  const [pending, setPending] = useState([])
  const [loading, setLoading] = useState(true)
  const [reasons, setReasons] = useState({})

  const load = useCallback(() => {
    setLoading(true)
    getPendingRevisions()
      .then((list) => setPending(list || []))
      .catch(() => setPending([]))
      .finally(() => setLoading(false))
  }, [getPendingRevisions])

  useEffect(() => { if (isAdmin) load() }, [isAdmin, load])

  if (!isAdmin) {
    return (
      <article className="mge-article">
        <h1 className="mge-firstheading">{t('moderationTitle')}</h1>
        <hr className="mge-heading-rule" />
        <div className="mge-notice">{t('adminOnly')} <Link to="/login">{t('login')}</Link></div>
      </article>
    )
  }

  const onApprove = async (id) => { await approveRevision(id); load() }
  const onReject = async (id) => { await rejectRevision(id, reasons[id]); load() }

  return (
    <article className="mge-article">
      <h1 className="mge-firstheading">{t('moderationTitle')}</h1>
      <hr className="mge-heading-rule" />
      <p>{t('moderationIntro')}</p>

      {loading ? (
        <p>…</p>
      ) : pending.length === 0 ? (
        <div className="mge-notice success">{t('noPending')}</div>
      ) : (
        pending.map((r) => (
          <div key={r.id} className="mge-mod-card">
            <div className="mge-mod-head">
              <div>
                <strong>{t('forArticle')}:</strong> {r.genreName}{' '}
                <span className="mge-badge light">{r.lang.toUpperCase()}</span>
              </div>
              <div className="mge-mod-meta">
                {t('proposedBy')}: {r.authorName} · {new Date(r.createdAt).toLocaleString()}
              </div>
            </div>

            <div className="mge-mod-preview">
              <MarkdownBlock text={r.content.summary} className="mge-prose" />
              {(r.content.sections || []).map((s) => (
                <div key={s.id}>
                  <h3>{s.title}</h3>
                  <MarkdownBlock text={s.body} className="mge-prose" />
                </div>
              ))}
            </div>

            <div className="mge-mod-actions">
              <button className="mge-btn primary" onClick={() => onApprove(r.id)}>✔ {t('approve')}</button>
              <input
                className="mge-reason-input"
                placeholder={t('rejectReason')}
                value={reasons[r.id] || ''}
                onChange={(e) => setReasons((p) => ({ ...p, [r.id]: e.target.value }))}
              />
              <button className="mge-btn danger" onClick={() => onReject(r.id)}>✘ {t('reject')}</button>
            </div>
          </div>
        ))
      )}
    </article>
  )
}
