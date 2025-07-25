// tailwind.config.js
module.exports = {
  content: ['./src/**/*.{html,js,svelte,ts}'],
  darkMode: 'class', // ✅ important!
  theme: {
    extend: {},
  },
  plugins: [require("daisyui")],
  safelist: [
    //These classes will always be loaded
    'hover:border-red-600',
    'hover:border-blue-600',
    'hover:border-green-600',
    'hover:border-yellow-300',
    'hover:border-orange-300',
    'hover:border-violet-600',
    'color-gray-100',
    'color-blue-100',
    'color-sky-100',
    'color-sky-50',
    'color-zinc-50',
    'color-neutral-50',
    'color-stone-50',
    'color-zinc-100',
    'color-slate-50',
    'color-slate-100',
    'color-slate-200',
    'color-slate-300',
    'color-slate-400',
    'color-slate-500',
    'color-slate-600',
    'color-slate-700',
    'color-slate-800',
    'color-slate-900',
    'color-slate-950',
    'color-neutral-100',
    'color-stone-100',
  ],
};
