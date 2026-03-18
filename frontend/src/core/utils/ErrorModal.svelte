<dialog id="error-modal" class="modal modal-bottom sm:modal-middle w-full">
  <div class="modal-box" style="min-width: min-content;background-color:var(--color-base-200);">
    <!-- svelte-ignore a11y_missing_content -->
    <h3 class="text-lg font-bold text-primary/80 hidden" id="error-modal-title">ERROR</h3>
    <p class="py-4 text-red-500 text-xl mt-2 text-center dark:text-red-400 bg-error-content p-2" id="error-modal-content" style="word-break: break-word;">
    </p>
    <div class="modal-action">
      <form method="dialog">         
        <button class="btn btn-secondary mt-4">Zatvori</button>
      </form>
    </div>
  </div>
</dialog>