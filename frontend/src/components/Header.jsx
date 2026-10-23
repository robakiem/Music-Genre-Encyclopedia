import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'

export default function Header({ onToggleSidebar }) {
  const { t, lang, setLang } = useLang()
  const { currentUser, isAdmin, logout } = useStore()
  const [query, setQuery] = useState('')
  const navigate = useNavigate()

  const onSearch = (e) => {
    e.preventDefault()
    const q = query.trim()
    navigate(q ? `/genres?q=${encodeURIComponent(q)}` : '/genres')
  }

  return (
    <header className="mge-header">
      <div className="mge-header-left">
        <button
          className="mge-hamburger"
          aria-label="menu"
          onClick={onToggleSidebar}
        >
          ☰
        </button>
        <Link to="/" className="mge-logo">
          <span className="mge-logo-globe">🎵</span>
          <span className="mge-logo-text">
            <strong>{t('siteName')}</strong>
            <small>{t('siteTagline')}</small>
          </span>
        </Link>
      </div>

      <form className="mge-search" onSubmit={onSearch} role="search">
        <input
          type="search"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder={t('searchPlaceholder')}
          aria-label={t('search')}
        />
        <button type="submit">{t('search')}</button>
      </form>

      <div className="mge-header-right">
        <div className="mge-lang-switch" title={t('uiLanguage')}>
          <button
            className={lang === 'uk' ? 'active' : ''}
            onClick={() => setLang('uk')}
          >
            UK
          </button>
          <button
            className={lang === 'en' ? 'active' : ''}
            onClick={() => setLang('en')}
          >
            EN
          </button>
        </div>

        {currentUser ? (
          <div className="mge-user">
            <span className="mge-user-name">
              {currentUser.username}
              {isAdmin && <span className="mge-badge">{t('roleAdmin')}</span>}
            </span>
            <button className="mge-link-btn" onClick={logout}>
              {t('logout')}
            </button>
          </div>
        ) : (
          <div className="mge-user">
            <Link to="/login" className="mge-link">{t('login')}</Link>
            <Link to="/register" className="mge-link">{t('register')}</Link>
          </div>
        )}
      </div>
    </header>
  )
}
