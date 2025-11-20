<script lang="ts">
  import { onMount } from 'svelte';
    import { fade } from 'svelte/transition';
    import { fly, blur, scale } from 'svelte/transition';
 // Helper to convert hex color to r,g,b
  function hexToRgb(hex: string): string {
    const bigint = parseInt(hex.slice(1), 16);
    const r = (bigint >> 16) & 255;
    const g = (bigint >> 8) & 255;
    const b = bigint & 255;
    return `${r}, ${g}, ${b}`;
  }
  const NUM_LINES = 69;
  let lines: {
    x2: number;
    y2: number;
    opacity: number;
    strokeWidth: number;
    color: string;
  }[] = [];

  const getRandomInt = (min: number, max: number) =>
    Math.floor(Math.random() * (max - min + 1)) + min;

  const colors = ['#00ffff', '#ff00ff', '#0000ff']; // cyan, magenta, lime

  const generateLines = () => {
    lines = Array.from({ length: NUM_LINES }, (_, i) => ({
      x2: getRandomInt(10, 90),
      y2: getRandomInt(10, 90),
      opacity: getRandomInt(5, 25),
      strokeWidth: getRandomInt(1, 2),
      color: colors[i % colors.length]
    }));
  };

  onMount(() => {
    generateLines();
    window.addEventListener('click', generateLines);
    return () => window.removeEventListener('click', generateLines);
  });
</script>
<div style="position: fixed; top: 0px; left 0px; width: 100%; height:100%;">
<svg viewBox="0 0 100 100" preserveAspectRatio="xMidYMid meet">
  <circle cx="50" cy="50" r="1" fill="white" stroke="black" />
  {#each lines as { x2, y2, opacity, strokeWidth, color }, i}
    <line
      x1="50"
      y1="50"
      {x2}
      {y2}
      stroke={`rgba(${hexToRgb(color)}, 0.${opacity})`}
      stroke-linecap="round"
      stroke-width={strokeWidth}
      class="pulse"
      style={`animation-delay: -${i % 6}s`}
      
    />
  {/each}
</svg>
</div>
<style>
 /*  html,
  body {
    margin: 0;
    height: 100vh;
    background: #111;
  } */

  svg {
    width: 100%;
    height: 100%;
    background: #000000;
    z-index: 5;
  }

  .pulse {
    transform-origin: center;
    animation: pulse 4s infinite alternate;
  }

  @keyframes pulse {
    0% {
      transform: scale(1);
    }
    100% {
      transform: scale(0.69);
    }
  }
</style>
