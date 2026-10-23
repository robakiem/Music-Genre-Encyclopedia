import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useLang } from '../context/LanguageContext.jsx'
import { useStore } from '../context/StoreContext.jsx'

export default function RegisterPage() {
  const { t } = useLang()
  const { register } = useStore()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)

  const submit = async (e) => {
    e.preventDefault()
    const res = await register(username, email, password)
    if (res.ok) navigate('/')
    else setError(t(res.error))
  }

  return (
    <article className="mge-article mge-auth">
      <h1 className="mge-firstheading">{t('registerTitle')}</h1>
      <hr className="mge-heading-rule" />
      {error && <div className="mge-notice error">{error}</div>}
      <form className="mge-auth-form" onSubmit={submit}>
        <label>{t('username')}</label>
        <input value={username} onChange={(e) => setUsername(e.target.value)} autoFocus required />
        <label>{t('email')}</label>
        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        <label>{t('password')}</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        <button type="submit" className="mge-btn primary">{t('register')}</button>
      </form>
      <p className="mge-auth-switch">
        {t('haveAccount')} <Link to="/login">{t('login')}</Link>
      </p>
    </article>
  )
}
