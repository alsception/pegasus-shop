<dialog id="info-modal" class="modal modal-bottom sm:modal-middle w-full">
  <div class="modal-box" style="min-width: min-content;">
    <h3 class="text-lg font-bold" id="info-modal-title"></h3>
    <p class="py-4" id="info-modal-content">
    </p>
    <div class="modal-action">
      <form method="dialog">         
        <button class="btn">Close</button>
      </form>
    </div>
  </div>
</dialog>


<!-- 
<script>
  export let title = '';
  export let content = '';
  let modalElement;

  // Function to open the modal
  export function open() {
    modalElement.showModal();
  }

  // Function to close the modal
  export function close() {
    modalElement.close();
  }
</script>

<dialog
  id="info-modal"
  class="modal modal-bottom sm:modal-middle w-full"
  bind:this={modalElement}
>
  <div class="modal-box" style="min-width: min-content;">
    <h3 class="text-lg font-bold">{title}</h3>
    <p class="py-4">{content}</p>
    <div class="modal-action">
      <form method="dialog">
        <button class="btn" on:click={close}>Close</button>
      </form>
    </div>
  </div>
</dialog>


--->