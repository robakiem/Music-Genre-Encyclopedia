import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'
import MarkdownBlock from '../components/MarkdownBlock.jsx'

export default function HomePage() {
  const { t, articleLang } = useLang()
  const { genres, genresLoading, getGenreBySlug } = useStore()
  const [featured, setFeatured] = useState(null)

  useEffect(() => {
    if (!genres.length) return
    let cancelled = false
    getGenreBySlug(genres[0].slug)
      .then((g) => { if (!cancelled) setFeatured(g) })
      .catch(() => {})
    return () => { cancelled = true }
  }, [genres, getGenreBySlug])

  const featuredContent = featured?.article?.content?.[articleLang] || featured?.article?.content?.uk

  return (
    <div className="mge-home">
      <div className="mge-welcome">
        <h1>{t('welcomeTitle')}</h1>
        <p>{t('welcomeText')}</p>
        <p className="mge-stat">
          <strong>{genres.length}</strong> {t('genresCount')}
        </p>
      </div>

      {featured && (
        <section className="mge-featured">
          <h2>{t('featuredGenre')}: <Link to={`/genre/${featured.slug}`}>{featured.name[articleLang] || featured.name.uk}</Link></h2>
          <MarkdownBlock text={featuredContent?.summary} className="mge-prose" />
          <Link className="mge-readmore" to={`/genre/${featured.slug}`}>{t('readMore')} →</Link>
        </section>
      )}

      <section>
        <h2>{t('allGenres')}</h2>
        {genresLoading ? (
          <p>…</p>
        ) : (
          <div className="mge-genre-grid">
            {genres.map((g) => (
              <Link key={g.id} to={`/genre/${g.slug}`} className="mge-genre-card">
                {g.name[articleLang] || g.name.uk}
              </Link>
            ))}
          </div>
        )}
      </section>
    </div>
  )
}
