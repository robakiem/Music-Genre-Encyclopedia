import { useEffect, useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'
import ArticleTabs from '../components/ArticleTabs.jsx'
import NotFoundPage from './NotFoundPage.jsx'

function slugify(s) {
  return (s || 'section').toLowerCase().replace(/[^a-z0-9а-яіїєґ]+/gi, '-').replace(/(^-|-$)/g, '') || 'section'
}

export default function EditArticlePage() {
  const { slug } = useParams()
  const { t, articleLang } = useLang()
  const { getGenreBySlug, currentUser, proposeRevision } = useStore()
  const navigate = useNavigate()

  const [genre, setGenre] = useState(null)
  const [status, setStatus] = useState('loading')
  const [summary, setSummary] = useState('')
  const [sections, setSections] = useState([])
  const [references, setReferences] = useState([])
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    let cancelled = false
    setStatus('loading')
    getGenreBySlug(slug)
      .then((g) => {
        if (cancelled) return
        setGenre(g)
        const base = g.article.content[articleLang] || g.article.content.uk || {}
        setSummary(base.summary || '')
        setSections((base.sections || []).map((s) => ({ ...s })))
        setReferences(base.references || [])
        setStatus('ok')
      })
      .catch(() => { if (!cancelled) setStatus('notfound') })
    return () => { cancelled = true }
  }, [slug, articleLang, getGenreBySlug])

  if (status === 'loading') return <article className="mge-article"><p>…</p></article>
  if (status === 'notfound') return <NotFoundPage />

  if (!currentUser) {
    return (
      <article className="mge-article">
        <ArticleTabs slug={slug} />
        <h1 className="mge-firstheading">{t('editArticle')}</h1>
        <hr className="mge-heading-rule" />
        <div className="mge-notice">
          {t('mustLoginToEdit')} <Link to="/login">{t('login')}</Link>
        </div>
      </article>
    )
  }

  const updateSection = (i, field, value) => {
    setSections((prev) => prev.map((s, idx) => (idx === i ? { ...s, [field]: value } : s)))
  }
  const addSection = () => {
    setSections((prev) => [...prev, { id: `sec-${Date.now()}`, title: '', body: '' }])
  }
  const removeSection = (i) => {
    setSections((prev) => prev.filter((_, idx) => idx !== i))
  }

  const submit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    const cleaned = sections
      .filter((s) => s.title.trim() || s.body.trim())
      .map((s) => ({ id: slugify(s.title) + '-' + Math.random().toString(36).slice(2, 6), title: s.title.trim(), body: s.body }))
    const content = { summary, sections: cleaned, references }
    try {
      await proposeRevision({ articleId: genre.article.id, lang: articleLang, content })
      navigate(`/genre/${slug}/history`, { state: { submitted: true } })
    } catch {
      setSubmitting(false)
    }
  }

  return (
    <article className="mge-article">
      <ArticleTabs slug={slug} />
      <h1 className="mge-firstheading">
        {t('editArticle')}: {genre.name[articleLang] || genre.name.uk}
      </h1>
      <hr className="mge-heading-rule" />

      <div className="mge-notice">{t('editHint')}</div>
      <p className="mge-edit-lang">{t('language')}: <strong>{articleLang === 'uk' ? 'Українська' : 'English'}</strong></p>

      <form className="mge-edit-form" onSubmit={submit}>
        <label className="mge-field-label">{t('summaryLabel')}</label>
        <textarea
          className="mge-wikitext"
          value={summary}
          onChange={(e) => setSummary(e.target.value)}
          rows={4}
        />

        {sections.map((s, i) => (
          <div key={i} className="mge-section-edit">
            <div className="mge-section-edit-head">
              <input
                className="mge-section-title-input"
                placeholder={t('sectionTitle')}
                value={s.title}
                onChange={(e) => updateSection(i, 'title', e.target.value)}
              />
              <button type="button" className="mge-btn danger sm" onClick={() => removeSection(i)}>
                {t('removeSection')}
              </button>
            </div>
            <textarea
              className="mge-wikitext"
              placeholder={t('sectionBody')}
              value={s.body}
              onChange={(e) => updateSection(i, 'body', e.target.value)}
              rows={5}
            />
          </div>
        ))}

        <button type="button" className="mge-btn" onClick={addSection}>+ {t('addSection')}</button>

        <div className="mge-form-actions">
          <button type="submit" className="mge-btn primary" disabled={submitting}>{t('submitRevision')}</button>
          <Link to={`/genre/${slug}`} className="mge-btn">{t('cancel')}</Link>
        </div>
      </form>
    </article>
  )
}
