import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'
import tailwindcss from '@tailwindcss/vite'


// https://vite.dev/config/

export default defineConfig({
  plugins: [
    svelte(),
    tailwindcss(),
  ],
  //Ovo je dodato ali nepomaze za stari televizor
 /* build: {
    target: 'es2015', // Ne chrome53, probaj prvo es2015
    cssTarget: 'chrome53',
    minify: 'terser',
    terserOptions: {
      compress: {
        ecma: 5
      }
    }
  },
  optimizeDeps: {
    esbuildOptions: {
      target: 'es5'
    }
  } */
  
  server: {
    host: true, // omogućava pristup sa spoljnog IP-a
    port: 5173, // ili bilo koji drugi port
  }

})