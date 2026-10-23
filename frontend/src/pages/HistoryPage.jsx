import { useState, useEffect, Fragment } from 'react'
import { useParams, useLocation } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'
import ArticleTabs from '../components/ArticleTabs.jsx'
import MarkdownBlock from '../components/MarkdownBlock.jsx'
import NotFoundPage from './NotFoundPage.jsx'

const statusKey = { PENDING: 'statusPending', APPROVED: 'statusApproved', REJECTED: 'statusRejected' }

export default function HistoryPage() {
  const { slug } = useParams()
  const { t, articleLang } = useLang()
  const { getGenreBySlug, getRevisions } = useStore()
  const location = useLocation()
  const [genre, setGenre] = useState(null)
  const [revisions, setRevisions] = useState([])
  const [status, setStatus] = useState('loading')
  const [expanded, setExpanded] = useState(null)

  useEffect(() => {
    let cancelled = false
    setStatus('loading')
    getGenreBySlug(slug)
      .then(async (g) => {
        if (cancelled) return
        setGenre(g)
        const revs = await getRevisions(g.article.id)
        if (!cancelled) { setRevisions(revs || []); setStatus('ok') }
      })
      .catch(() => { if (!cancelled) setStatus('notfound') })
    return () => { cancelled = true }
  }, [slug, getGenreBySlug, getRevisions])

  if (status === 'loading') return <article className="mge-article"><p>…</p></article>
  if (status === 'notfound') return <NotFoundPage />

  const fmt = (iso) => new Date(iso).toLocaleString(articleLang === 'uk' ? 'uk-UA' : 'en-GB')

  return (
    <article className="mge-article">
      <ArticleTabs slug={slug} />
      <h1 className="mge-firstheading">
        {t('historyTitle')}: {genre.name[articleLang] || genre.name.uk}
      </h1>
      <hr className="mge-heading-rule" />

      {location.state?.submitted && <div className="mge-notice success">{t('revisionSubmitted')}</div>}

      {revisions.length === 0 ? (
        <p>{t('noRevisions')}</p>
      ) : (
        <table className="mge-table">
          <thead>
            <tr>
              <th>{t('revisionDate')}</th>
              <th>{t('revisionBy')}</th>
              <th>{t('language')}</th>
              <th>{t('revisionStatus')}</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {revisions.map((r) => (
              <Fragment key={r.id}>
                <tr>
                  <td>{fmt(r.createdAt)}</td>
                  <td>{r.authorName}</td>
                  <td>{r.lang.toUpperCase()}</td>
                  <td><span className={`mge-status ${r.status.toLowerCase()}`}>{t(statusKey[r.status])}</span></td>
                  <td>
                    <button className="mge-link-btn" onClick={() => setExpanded(expanded === r.id ? null : r.id)}>
                      {t('viewContent')}
                    </button>
                  </td>
                </tr>
                {expanded === r.id && (
                  <tr>
                    <td colSpan={5}>
                      <div className="mge-revision-preview">
                        <MarkdownBlock text={r.content.summary} className="mge-prose" />
                        {(r.content.sections || []).map((s) => (
                          <div key={s.id}>
                            <h3>{s.title}</h3>
                            <MarkdownBlock text={s.body} className="mge-prose" />
                          </div>
                        ))}
                        {r.reason && <p className="mge-reject-reason"><strong>{t('rejectReason')}:</strong> {r.reason}</p>}
                      </div>
                    </td>
                  </tr>
                )}
              </Fragment>
            ))}
          </tbody>
        </table>
      )}
    </article>
  )
}
