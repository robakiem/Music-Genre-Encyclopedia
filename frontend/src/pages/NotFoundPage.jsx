import { Link } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'

export default function NotFoundPage() {
  const { t } = useLang()
  return (
    <article className="mge-article">
      <h1 className="mge-firstheading">{t('notFound')}</h1>
      <hr className="mge-heading-rule" />
      <p><Link to="/genres">{t('backToGenres')}</Link></p>
    </article>
  )
}
