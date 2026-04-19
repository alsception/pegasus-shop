import { mount } from 'svelte'
import './main.css'
import './neobrutalism.css' //Ovo bi trebalo ukloniti, a da se ne pokvari dizajn...
import App from './App.svelte'

const app = mount(App, {
  target: document.getElementById('app')!,
})

export default app