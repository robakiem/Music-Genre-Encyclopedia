import { useState } from 'react'
import { useLang } from '../context/LanguageContext.jsx'
import Header from './Header.jsx'
import Sidebar from './Sidebar.jsx'

export default function Layout({ children }) {
  const { t } = useLang()
  const [sidebarOpen, setSidebarOpen] = useState(false)

  return (
    <div className="mge-root">
      <Header onToggleSidebar={() => setSidebarOpen((v) => !v)} />
      <div className="mge-body">
        <Sidebar open={sidebarOpen} />
        <main className="mge-main">
          <div className="mge-content">{children}</div>
          <footer className="mge-footer">
            <p>
              {t('siteName')} — {t('siteTagline')}.{' '}
              {t('fromEncyclopedia')}.
            </p>
            <p className="mge-footer-sub">
              Music Genre Encyclopedia · React SPA · Spring Boot · PostgreSQL
            </p>
          </footer>
        </main>
      </div>
    </div>
  )
}
