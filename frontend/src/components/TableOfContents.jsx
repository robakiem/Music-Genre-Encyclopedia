import { useLang } from '../context/LanguageContext.jsx'

export default function TableOfContents({ sections }) {
  const { t } = useLang()
  if (!sections?.length) return null

  return (
    <nav className="mge-toc" aria-label={t('contents')}>
      <div className="mge-toc-title">{t('contents')}</div>
      <ol>
        {sections.map((s, i) => (
          <li key={s.id}>
            <a href={`#${s.id}`}>
              <span className="mge-toc-num">{i + 1}</span> {s.title}
            </a>
          </li>
        ))}
      </ol>
    </nav>
  )
}
