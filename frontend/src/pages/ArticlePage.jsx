import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'
import { ApiError } from '../api/client.js'
import ArticleTabs from '../components/ArticleTabs.jsx'
import Infobox from '../components/Infobox.jsx'
import TableOfContents from '../components/TableOfContents.jsx'
import MarkdownBlock from '../components/MarkdownBlock.jsx'
import NotFoundPage from './NotFoundPage.jsx'

export default function ArticlePage() {
  const { slug } = useParams()
  const { t, articleLang, setArticleLang } = useLang()
  const { getGenreBySlug, currentUser, fileComplaint } = useStore()

  const [genre, setGenre] = useState(null)
  const [status, setStatus] = useState('loading') // loading | ok | notfound | error
  const [showComplaint, setShowComplaint] = useState(false)
  const [reason, setReason] = useState('')
  const [notice, setNotice] = useState(null)

  useEffect(() => {
    let cancelled = false
    setStatus('loading')
    getGenreBySlug(slug)
      .then((g) => { if (!cancelled) { setGenre(g); setStatus('ok') } })
      .catch((e) => { if (!cancelled) setStatus(e instanceof ApiError && e.status === 404 ? 'notfound' : 'error') })
    return () => { cancelled = true }
  }, [slug, getGenreBySlug])

  if (status === 'loading') return <article className="mge-article"><p>…</p></article>
  if (status === 'notfound') return <NotFoundPage />
  if (status === 'error') return <article className="mge-article"><div className="mge-notice error">{t('notFound')}</div></article>

  const content = genre.article.content[articleLang] || genre.article.content.uk
  const title = genre.name[articleLang] || genre.name.uk
  const updated = new Date(genre.article.updatedAt).toLocaleDateString(
    articleLang === 'uk' ? 'uk-UA' : 'en-GB',
    { year: 'numeric', month: 'long', day: 'numeric' },
  )

  const submitComplaint = async (e) => {
    e.preventDefault()
    if (!reason.trim()) return
    try {
      await fileComplaint({ articleId: genre.article.id, lang: articleLang, reason: reason.trim() })
      setShowComplaint(false)
      setReason('')
      setNotice(t('complaintSubmitted'))
    } catch {
      setNotice(t('mustLoginToComplain'))
    }
  }

  return (
    <article className="mge-article">
      <ArticleTabs slug={slug} />

      <h1 className="mge-firstheading">{title}</h1>
      <div className="mge-article-sub">{t('fromEncyclopedia')}</div>
      <hr className="mge-heading-rule" />

      <div className="mge-article-toolbar">
        <div className="mge-art-lang">
          <span>{t('language')}:</span>
          <button className={articleLang === 'uk' ? 'active' : ''} onClick={() => setArticleLang('uk')}>Українська</button>
          <button className={articleLang === 'en' ? 'active' : ''} onClick={() => setArticleLang('en')}>English</button>
        </div>
        <div className="mge-art-actions">
          <Link className="mge-sm-btn" to={`/genre/${slug}/edit`}>✎ {t('editIntro')}</Link>
          <button className="mge-sm-btn" onClick={() => { setShowComplaint((v) => !v); setNotice(null) }}>⚐ {t('reportProblem')}</button>
        </div>
      </div>

      {notice && <div className="mge-notice success">{notice}</div>}

      {showComplaint && (
        <form className="mge-complaint-box" onSubmit={submitComplaint}>
          {currentUser ? (
            <>
              <label>{t('complaintReason')}</label>
              <textarea value={reason} onChange={(e) => setReason(e.target.value)} rows={3} />
              <div className="mge-form-actions">
                <button type="submit" className="mge-btn primary">{t('submitComplaint')}</button>
                <button type="button" className="mge-btn" onClick={() => setShowComplaint(false)}>{t('cancel')}</button>
              </div>
            </>
          ) : (
            <p>{t('mustLoginToComplain')} <Link to="/login">{t('login')}</Link></p>
          )}
        </form>
      )}

      <Infobox genre={genre} />

      <MarkdownBlock text={content.summary} className="mge-prose mge-lead" />

      <TableOfContents sections={content.sections} />

      {content.sections.map((s, i) => (
        <section key={s.id} className="mge-section">
          <h2 id={s.id} className="mge-section-h">
            <span className="mge-section-num">{i + 1}</span> {s.title}
          </h2>
          <MarkdownBlock text={s.body} className="mge-prose" />
        </section>
      ))}

      {content.references?.length > 0 && (
        <section className="mge-section">
          <h2 className="mge-section-h">{t('references')}</h2>
          <ol className="mge-refs">
            {content.references.map((r, i) => (
              <li key={i}>{r}</li>
            ))}
          </ol>
        </section>
      )}

      <div className="mge-categories">
        <span className="mge-cat-label">{t('categories')}:</span>
        <Link to="/genres">{t('infoboxTitle')}</Link>
        <Link to={`/genre/${slug}`}>{title}</Link>
      </div>

      <div className="mge-lastedited">
        {t('lastEdited')}: {updated}
      </div>
    </article>
  )
}
