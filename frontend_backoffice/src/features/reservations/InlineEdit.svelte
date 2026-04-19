<script>
  /**
   * @type {any}
   */
   export let value;
  export let type = "text";

  let editing = false;

  function startEdit() {
    editing = true;
  }

  /**
   * @param {{ target: { value: any; }; }} e
   */
  function finishEdit(e) {
    editing = false;
    value = type === "number" ? Number(e.target.value) : e.target.value;
  }

  function onKeyDown(e) {
    if (e.key === "Enter") e.target.blur();
  }
</script>

{#if editing}
  <!-- svelte-ignore a11y_autofocus -->
  <input
    type={type}
    bind:value
    on:blur={finishEdit}
    on:keydown={onKeyDown}
    class="input input-xs input-bordered w-full"
    autofocus
  />
{:else}
  <!-- svelte-ignore a11y_click_events_have_key_events -->
  <!-- svelte-ignore a11y_no_static_element_interactions -->
  <span on:click={startEdit} class="cursor-pointer hover:bg-base-200 p-1 inline-block max-w-[100px] truncate"
    title="{value}">
    {value || "-"}
  </span>
{/if}
