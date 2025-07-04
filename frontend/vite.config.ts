import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'
import tailwindcss from '@tailwindcss/vite'


// https://vite.dev/config/

export default defineConfig({
  plugins: [
    svelte(),
    tailwindcss(),
  ],
  /*   zasad nekoristimo, za ngrok tunnel
  server: {
    host: true, // omogućava pristup sa spoljnog IP-a
    port: 5173, // ili bilo koji drugi port
    strictPort: true, // ne koristi alternativni port
    hmr: {
      protocol: 'wss',
      host: '84cd-5-39-135-139.ngrok-free.app',
    }
  }*/

})