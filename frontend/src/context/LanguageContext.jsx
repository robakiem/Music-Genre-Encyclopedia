import { createContext, useContext, useState, useCallback } from 'react'
import { translations } from '../i18n/translations.js'

const LanguageContext = createContext(null)

const UI_KEY = 'mge.uiLang'
const ART_KEY = 'mge.articleLang'

function readStored(key, fallback) {
  try {
    return localStorage.getItem(key) || fallback
  } catch {
    return fallback
  }
}

export function LanguageProvider({ children }) {
  const [lang, setLangState] = useState(() => readStored(UI_KEY, 'uk'))
  const [articleLang, setArticleLangState] = useState(() => readStored(ART_KEY, 'uk'))

  const setLang = useCallback((value) => {
    setLangState(value)
    try { localStorage.setItem(UI_KEY, value) } catch { /* ignore */ }
  }, [])

  const setArticleLang = useCallback((value) => {
    setArticleLangState(value)
    try { localStorage.setItem(ART_KEY, value) } catch { /* ignore */ }
  }, [])

  const t = useCallback(
    (key) => translations[lang]?.[key] ?? translations.uk[key] ?? key,
    [lang],
  )

  return (
    <LanguageContext.Provider value={{ lang, setLang, articleLang, setArticleLang, t }}>
      {children}
    </LanguageContext.Provider>
  )
}

export function useLang() {
  const ctx = useContext(LanguageContext)
  if (!ctx) throw new Error('useLang must be used within LanguageProvider')
  return ctx
}
