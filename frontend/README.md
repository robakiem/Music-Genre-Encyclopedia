# Music Genre Encyclopedia — Frontend (React SPA)

Wikipedia-style single-page application for the Music Genre Encyclopedia.
This is the **Web Application (React SPA)** container from the C4 diagrams.

## Running

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173

## Demo accounts

| Username | Password | Role  |
|----------|----------|-------|
| `admin`  | `admin`  | ADMIN |
| `demo`   | `demo`   | USER  |

You can also register a new regular user. State (edits, revisions,
complaints, accounts) is kept in `localStorage` so the demo survives reloads.

## Features (mapped to the C4 component diagram)

| Diagram component      | In the SPA                                              |
|------------------------|--------------------------------------------------------|
| Genre Controller       | Genre list + article pages (`/genres`, `/genre/:slug`) |
| Article Controller     | Read article + **Propose edit** (`/genre/:slug/edit`)  |
| Moderation Controller  | Admin moderation queue (`/moderation`)                 |
| Complaint Controller   | File complaint on an article + admin review (`/complaints`) |
| Auth Controller        | Login / register, USER vs ADMIN roles                  |

### Community editing + moderation workflow
1. A logged-in user opens an article and clicks **Propose an edit**.
2. The edit becomes a revision with status `PENDING` (visible in the article's **History** tab).
3. An admin reviews it in the **Moderation** queue and **Approves** (content goes live) or **Rejects** (with a reason).

### Bilingual
- UI language switch (UK / EN) in the header.
- Each article holds content in both Ukrainian and English; switch with the
  article-language toggle on the article page.

## Backend connection

The frontend is **wired to the Spring Boot REST API**:

- `src/api/client.js` — fetch wrapper that attaches the JWT (`Authorization: Bearer`).
- `src/context/StoreContext.jsx` — auth + data calls to `/api/*`.
- Vite proxies `/api` → `http://localhost:8080` in dev (see `vite.config.js`).

**Run both:** start the backend first (`cd ../backend && ./mvnw.cmd spring-boot:run`),
then `npm run dev` here. Log in with the seeded `admin`/`admin` or `demo`/`demo`.
The in-browser demo store has been removed; the app now reads/writes real data in PostgreSQL.

## Tech
- React 18 + Vite
- react-router-dom
- marked + DOMPurify (sanitized rendering of community-edited markdown)
