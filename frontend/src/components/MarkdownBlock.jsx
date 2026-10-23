import { renderMarkdown } from '../utils/markdown.js'

export default function MarkdownBlock({ text, className }) {
  return (
    <div
      className={className}
      dangerouslySetInnerHTML={{ __html: renderMarkdown(text) }}
    />
  )
}
