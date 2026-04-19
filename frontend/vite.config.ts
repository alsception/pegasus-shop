import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/

export default defineConfig({

  plugins: [
    svelte(),
    tailwindcss(),
  ],  
  
  server: {
    host: true, // omogućava pristup sa spoljnog IP-a
    port: 5173, 
    proxy: {
      '/api': 'http://localhost:8080'
    }
  },

  /* za sad nece da koristimo ovo jer imamo svoju mount_frontend.sh scriptu
  build: {
    outDir: '../backend/src/main/resources/static',
    emptyOutDir: true,
  }*/

})