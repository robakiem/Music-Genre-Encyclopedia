import { useLang } from '../context/LanguageContext.jsx'

export default function Infobox({ genre }) {
  const { t, articleLang } = useLang()
  if (!genre?.infobox?.length) return null

  return (
    <aside className="mge-infobox">
      <div className="mge-infobox-title">{genre.name[articleLang] || genre.name.uk}</div>
      <div className="mge-infobox-sub">{t('infoboxTitle')}</div>
      <table>
        <tbody>
          {genre.infobox.map((row) => (
            <tr key={row.key}>
              <th>{t(row.key)}</th>
              <td>{row[articleLang] || row.uk}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </aside>
  )
}
