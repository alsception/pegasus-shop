/* import 'core-js/stable';
import 'regenerator-runtime/runtime';


window.onerror = function(msg, url, lineNo, columnNo, error) {
  document.body.innerHTML = `
    <div style="color: red; font-size: 30px; padding: 20px;">
      ERROR: ${msg}<br>
      File: ${url}<br>
      Line: ${lineNo}
    </div>
  `;
  return false;
}; 

 */
// Sad importuj
import { mount } from 'svelte'
import './main.css'
import './neobrutalism.css'
import App from './App.svelte'

const app = mount(App, {
  target: document.getElementById('app')!,
})

const element = document.getElementById('js-test');
if (element) {
  element.remove();
}

export default app