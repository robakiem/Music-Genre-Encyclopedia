import { createContext, useContext, useState, useCallback, useEffect } from 'react'
import { api, setToken, getToken, ApiError } from '../api/client.js'

// App store backed by the Spring Boot REST API.
// Auth state (token + user) is kept in localStorage; domain data (genres,
// revisions, complaints) is fetched on demand by the pages.

const StoreContext = createContext(null)
const USER_KEY = 'mge.user'

function readUser() {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}
function writeUser(user) {
  try {
    if (user) localStorage.setItem(USER_KEY, JSON.stringify(user))
    else localStorage.removeItem(USER_KEY)
  } catch { /* ignore */ }
}

export function StoreProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(readUser)
  const [genres, setGenres] = useState([])
  const [genresLoading, setGenresLoading] = useState(true)

  const refreshGenres = useCallback(async () => {
    setGenresLoading(true)
    try {
      const list = await api.get('/api/genres')
      setGenres(list || [])
    } catch {
      setGenres([])
    } finally {
      setGenresLoading(false)
    }
  }, [])

  useEffect(() => {
    // If a token was removed elsewhere, drop the cached user.
    if (!getToken()) {
      setCurrentUser(null)
      writeUser(null)
    }
    refreshGenres()
  }, [refreshGenres])

  // ---- Auth ----
  const login = useCallback(async (username, password) => {
    try {
      const res = await api.post('/api/auth/login', { username, password })
      setToken(res.token)
      const user = { username: res.username, role: res.role }
      writeUser(user)
      setCurrentUser(user)
      return { ok: true }
    } catch (e) {
      return { ok: false, error: e instanceof ApiError && e.status === 401 ? 'invalidCredentials' : 'invalidCredentials' }
    }
  }, [])

  const register = useCallback(async (username, email, password) => {
    try {
      const res = await api.post('/api/auth/register', { username, email, password })
      setToken(res.token)
      const user = { username: res.username, role: res.role }
      writeUser(user)
      setCurrentUser(user)
      return { ok: true }
    } catch (e) {
      return { ok: false, error: e instanceof ApiError && e.status === 409 ? 'userExists' : 'userExists' }
    }
  }, [])

  const logout = useCallback(() => {
    setToken(null)
    writeUser(null)
    setCurrentUser(null)
  }, [])

  // ---- Genres / articles ----
  const getGenreBySlug = useCallback((slug) => api.get(`/api/genres/${slug}`), [])

  // ---- Revisions / moderation ----
  const proposeRevision = useCallback(({ articleId, lang, content }) =>
    api.post(`/api/articles/${articleId}/revisions`, { lang, content }), [])

  const getRevisions = useCallback((articleId) =>
    api.get(`/api/articles/${articleId}/revisions`), [])

  const getPendingRevisions = useCallback(() => api.get('/api/moderation/revisions'), [])

  const approveRevision = useCallback((revisionId) =>
    api.post(`/api/moderation/revisions/${revisionId}/approve`), [])

  const rejectRevision = useCallback((revisionId, reason) =>
    api.post(`/api/moderation/revisions/${revisionId}/reject`, { reason }), [])

  // ---- Complaints ----
  const fileComplaint = useCallback(({ articleId, lang, reason }) =>
    api.post('/api/complaints', { articleId, lang, reason }), [])

  const getComplaints = useCallback((status) =>
    api.get(`/api/complaints${status ? `?status=${status}` : ''}`), [])

  const resolveComplaint = useCallback((complaintId, decision) =>
    api.post(`/api/complaints/${complaintId}/resolve`, { decision }), [])

  const value = {
    genres, genresLoading, refreshGenres,
    currentUser,
    isAdmin: currentUser?.role === 'ADMIN',
    login, register, logout,
    getGenreBySlug,
    proposeRevision, getRevisions, getPendingRevisions, approveRevision, rejectRevision,
    fileComplaint, getComplaints, resolveComplaint,
  }

  return <StoreContext.Provider value={value}>{children}</StoreContext.Provider>
}

export function useStore() {
  const ctx = useContext(StoreContext)
  if (!ctx) throw new Error('useStore must be used within StoreProvider')
  return ctx
}
