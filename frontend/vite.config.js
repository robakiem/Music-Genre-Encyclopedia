import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// The Spring Boot API will later run on :8080; proxy /api to it in dev.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
