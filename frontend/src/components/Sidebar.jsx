import { useEffect, useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'

export default function Sidebar({ open }) {
  const { t } = useLang()
  const { genres, isAdmin, getPendingRevisions, getComplaints } = useStore()
  const navigate = useNavigate()
  const location = useLocation()
  const [counts, setCounts] = useState({ pending: 0, complaints: 0 })

  useEffect(() => {
    if (!isAdmin) {
      setCounts({ pending: 0, complaints: 0 })
      return
    }
    let cancelled = false
    Promise.all([getPendingRevisions(), getComplaints('OPEN')])
      .then(([revs, comps]) => {
        if (!cancelled) setCounts({ pending: revs?.length || 0, complaints: comps?.length || 0 })
      })
      .catch(() => {})
    return () => { cancelled = true }
    // refetch when navigating (counts change after moderation/complaint actions)
  }, [isAdmin, getPendingRevisions, getComplaints, location.pathname])

  const goRandom = () => {
    if (!genres.length) return
    const g = genres[Math.floor(Math.random() * genres.length)]
    navigate(`/genre/${g.slug}`)
  }

  return (
    <nav className={`mge-sidebar${open ? ' open' : ''}`} aria-label={t('navMain')}>
      <h3 className="mge-sidebar-h">{t('navMain')}</h3>
      <ul>
        <li><Link to="/">{t('home')}</Link></li>
        <li><Link to="/genres">{t('allGenres')}</Link></li>
        <li><button className="mge-link-btn" onClick={goRandom}>{t('randomGenre')}</button></li>
      </ul>

      {isAdmin && (
        <>
          <h3 className="mge-sidebar-h">{t('roleAdmin')}</h3>
          <ul>
            <li>
              <Link to="/moderation">
                {t('moderation')}
                {counts.pending > 0 && <span className="mge-count">{counts.pending}</span>}
              </Link>
            </li>
            <li>
              <Link to="/complaints">
                {t('complaints')}
                {counts.complaints > 0 && <span className="mge-count">{counts.complaints}</span>}
              </Link>
            </li>
          </ul>
        </>
      )}
    </nav>
  )
}
