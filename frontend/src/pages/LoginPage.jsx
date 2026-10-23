import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'

export default function LoginPage() {
  const { t } = useLang()
  const { login } = useStore()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)

  const submit = async (e) => {
    e.preventDefault()
    const res = await login(username, password)
    if (res.ok) navigate('/')
    else setError(t(res.error))
  }

  return (
    <article className="mge-article mge-auth">
      <h1 className="mge-firstheading">{t('loginTitle')}</h1>
      <hr className="mge-heading-rule" />
      <div className="mge-notice">{t('demoHint')}</div>
      {error && <div className="mge-notice error">{error}</div>}
      <form className="mge-auth-form" onSubmit={submit}>
        <label>{t('username')}</label>
        <input value={username} onChange={(e) => setUsername(e.target.value)} autoFocus />
        <label>{t('password')}</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        <button type="submit" className="mge-btn primary">{t('login')}</button>
      </form>
      <p className="mge-auth-switch">
        {t('noAccount')} <Link to="/register">{t('register')}</Link>
      </p>
    </article>
  )
}
