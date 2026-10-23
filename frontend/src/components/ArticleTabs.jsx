import { Link, useLocation } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'

// Wikipedia-style article action tabs (Read / Edit / View history).
export default function ArticleTabs({ slug }) {
  const { t } = useLang()
  const { pathname } = useLocation()

  const tabs = [
    { to: `/genre/${slug}`, label: t('tabRead'), exact: true },
    { to: `/genre/${slug}/edit`, label: t('tabEdit') },
    { to: `/genre/${slug}/history`, label: t('tabHistory') },
  ]

  return (
    <div className="mge-article-tabs">
      <div className="mge-tabs-left">
        <span className="mge-tab active">{t('tabArticle')}</span>
        <span className="mge-tab">{t('tabTalk')}</span>
      </div>
      <div className="mge-tabs-right">
        {tabs.map((tab) => {
          const active = tab.exact ? pathname === tab.to : pathname === tab.to
          return (
            <Link
              key={tab.to}
              to={tab.to}
              className={`mge-tab${active ? ' active' : ''}`}
            >
              {tab.label}
            </Link>
          )
        })}
      </div>
    </div>
  )
}
