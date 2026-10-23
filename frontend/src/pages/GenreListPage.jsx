import { Link, useSearchParams } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'

export default function GenreListPage() {
  const { t, articleLang } = useLang()
  const { genres } = useStore()
  const [params] = useSearchParams()
  const q = (params.get('q') || '').toLowerCase().trim()

  const filtered = q
    ? genres.filter(
        (g) =>
          g.name.uk.toLowerCase().includes(q) ||
          g.name.en.toLowerCase().includes(q) ||
          g.slug.includes(q),
      )
    : genres

  const sorted = [...filtered].sort((a, b) =>
    (a.name[articleLang] || a.name.uk).localeCompare(b.name[articleLang] || b.name.uk),
  )

  return (
    <article className="mge-article">
      <h1 className="mge-firstheading">{t('allGenres')}</h1>
      <hr className="mge-heading-rule" />
      {q && (
        <p className="mge-search-note">
          {t('search')}: <strong>{params.get('q')}</strong> — {sorted.length}
        </p>
      )}
      <ul className="mge-genre-list">
        {sorted.map((g) => (
          <li key={g.id}>
            <Link to={`/genre/${g.slug}`}>{g.name[articleLang] || g.name.uk}</Link>
            {articleLang !== 'en' && <span className="mge-genre-alt"> ({g.name.en})</span>}
          </li>
        ))}
      </ul>
      {sorted.length === 0 && <p>{t('notFound')}</p>}
    </article>
  )
}
