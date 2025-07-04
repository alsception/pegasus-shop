<script lang="ts">
  interface Props {
    value?: string;
    label?: string;
    type?: string;
  }
  
  let { value = $bindable(''), label = '', type = 'text' }: Props = $props();
</script>

<label>
  {label}:
  <input {type} bind:value />
</label>