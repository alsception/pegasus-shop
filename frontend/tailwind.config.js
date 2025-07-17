// tailwind.config.js
module.exports = {
  content: ['./src/**/*.{html,js,svelte,ts}'],
  theme: {
    extend: {},
  },
  plugins: [],
  safelist: [
    //These classes will always be loaded
    'hover:border-red-600',
    'hover:border-blue-600',
    'hover:border-green-600',
    'hover:border-yellow-300',
    'hover:border-orange-300',
    'hover:border-violet-600',
  ],
};
