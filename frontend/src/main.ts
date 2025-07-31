import { mount } from 'svelte'
import './main.css'
import './neobrutalism.css'
import App from './App.svelte'

const app = mount(App, {
  target: document.getElementById('app')!,
})

export default app
