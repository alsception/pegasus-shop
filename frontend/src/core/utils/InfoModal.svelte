<dialog id="info-modal" class="modal modal-bottom sm:modal-middle w-full">
  <div class="modal-box" style="min-width: min-content;background-color:white">
    <!-- svelte-ignore a11y_missing_content -->
    <h3 class="text-lg font-bold" id="info-modal-title"></h3>
    <p class="py-4 text-red-500 text-xl mt-8 text-center dark:text-red-400 bg-error-content p-2" id="info-modal-content">
    </p>
    <div class="modal-action">
      <form method="dialog">         
        <button class="btn">Close</button>
      </form>
    </div>
  </div>
</dialog>