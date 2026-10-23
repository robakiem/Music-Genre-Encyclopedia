import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import HomePage from './pages/HomePage.jsx'
import GenreListPage from './pages/GenreListPage.jsx'
import ArticlePage from './pages/ArticlePage.jsx'
import EditArticlePage from './pages/EditArticlePage.jsx'
import HistoryPage from './pages/HistoryPage.jsx'
import ModerationPage from './pages/ModerationPage.jsx'
import ComplaintsPage from './pages/ComplaintsPage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/genres" element={<GenreListPage />} />
        <Route path="/genre/:slug" element={<ArticlePage />} />
        <Route path="/genre/:slug/edit" element={<EditArticlePage />} />
        <Route path="/genre/:slug/history" element={<HistoryPage />} />
        <Route path="/moderation" element={<ModerationPage />} />
        <Route path="/complaints" element={<ComplaintsPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Layout>
  )
}
