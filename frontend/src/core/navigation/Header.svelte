<script>
  import PrimaryMenu from "./PrimaryMenu.svelte";
</script>

<div class="navbar shadow-sm fixed bg-slate-950 z-50">
  <div class="navbar-start">
    <div class="dropdown /*bg-neutral-950*/" title="Open Menu">
      <div
        tabindex="0"
        role="button"
        class="btn btn-ghost btn-circle /*bg-neutral-800*/ hover:bg-neutral-700"
      >
        <i class="fas fa-bars text-xl"></i>
      </div>

      <PrimaryMenu />
    </div>
  </div>
  <div class="navbar-center">
    <a href="/#/home">
      <h1
        class="pp text-lg sm:text-xl md:text-3xl lg:text-4xl font-bold bg-gradient-to-r from-sky-400 via-blue-500 to-violet-600 text-transparent bg-clip-text font-mono uppercase tracking-widest drop-shadow-md yellowtail-regular"
      >
        Pegasus
      </h1>
    </a>
  </div>
  <div class="navbar-end">
    <label class="swap swap-rotate mr-4" title="Toggle Dark Mode">
      <input type="checkbox" class="theme-controller" value="dark" />
      <span class="swap-off text-xl">☀️</span>
      <span class="swap-on text-xl">🌙</span>
    </label>
  </div>
</div>
