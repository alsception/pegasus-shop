<!-- Modal.svelte -->
<script lang="ts">
    import { createEventDispatcher, onMount } from 'svelte';
    
    // Define a flexible data type to handle various data structures
    type DataItem = Record<string, any>;
    
    export let showModal = false;
    export let title = "Reviews";
    export let data: DataItem[] = [];
  
    const dispatch = createEventDispatcher();
    
    function closeModal(): void {
      dispatch('close');
    }
    
    function handleKeydown(event: KeyboardEvent): void {
      if (event.key === 'Escape') {
        closeModal();
      }
    }
    
    function handleModalBackdropKeydown(event: KeyboardEvent): void {
      // Handle Enter or Space key to close modal (same as clicking outside)
      if (event.key === 'Enter' || event.key === ' ') {
        closeModal();
        event.preventDefault();
      }
    }
    
    function handleClickOutside(event: MouseEvent): void {
      if (event.target === event.currentTarget) {
        closeModal();
      }
    }
    
    onMount(() => {
      document.addEventListener('keydown', handleKeydown);
      
      return () => {
        document.removeEventListener('keydown', handleKeydown);
      };
    });
    // Function to generate star rating display
    function getStars(rating: number): string {
        /* const fullStars = '⭐'.repeat(rating);
        const emptyStars = '☆'.repeat(5 - rating);
        return fullStars + emptyStars;
 */
        const goldStars = '⭐'.repeat(rating);
        const grayStars = '<span class="gray-stars">'+'★'.repeat(5 - rating)+'</span>';
        return goldStars + grayStars + '&nbsp;&nbsp;' + rating + '/' + 5;
    }
  </script>
  
  {#if showModal}
    <div 
      class="modal-backdrop" 
      on:click={handleClickOutside}
      on:keydown={handleModalBackdropKeydown}
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
      tabindex="0"
      
    >
      <div class="modal-content">
        <div class="modal-header">
          <h2 id="modal-title">{title}</h2>
          <button 
            class="close-button" 
            on:click={closeModal}
            aria-label="Close modal"
          >×</button>
        </div>
        <div class="modal-body">
          {#if data.length > 0}

            <table>
              <thead>
                <tr>
                  {#each Object.keys(data[0]) as key}
                    <th>{key.toUpperCase()}</th>
                  {/each}
                </tr>
              </thead>
              <tbody>
                {#each data as item}
                  <tr>
                    {#each Object.keys(item) as key}

<!--
                    <strong>{review.reviewer}:</strong> {review.text} (⭐ {review.rating})

                    -->
                    {#if key === 'rating'}
                    <td>
                        <div class="rating" aria-label="Rating: {item[key]} out of 5 stars">
                            {@html getStars(item[key])}
                        </div> 
                        
                    </td>
                    {:else}
                    <td>{item[key]}</td>

                    {/if}


                    {/each}
                  </tr>
                {/each}
              </tbody>
            </table>
            
          {:else}
            <p>No data available</p>
          {/if}
        </div>
        <div class="modal-footer">
          <button on:click={closeModal}>Close</button>
        </div>
      </div>
    </div>
  {/if}
  
  <style>
    .modal-backdrop {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    }
    
    .modal-content {
      background-color: white;
      border-radius: 5px;
      width: 80%;
      max-width: 800px;
      max-height: 90vh;
      overflow-y: auto;
/*       box-shadow: 0 2px 8px rgba(0, 0, 0, 0.25);
 */    }
    
    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
      border-bottom: 1px solid #eee;
    }
    
    .modal-header h2 {
      margin: 0;
      font-size: 1.5rem;
    }
    
    .close-button {
      background: none;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
    }
    
    .modal-body {
      padding: 1rem;
      max-height: 60vh;
      overflow-y: auto;
    }
    
    .modal-footer {
      padding: 1rem;
      border-top: 1px solid #eee;
      display: flex;
      justify-content: flex-end;
    }
    
    table {
      width: 100%;
      border-collapse: collapse;
    }
    
    th, td {
      text-align: left;
      padding: 8px;
      border-bottom: 1px solid #ddd;
    }
    
    th {
      background-color: #f2f2f2;
    }
    
    tr:hover {
      background-color: #f5f5f5;
    }
    
    button {
      padding: 0.5rem 1rem;
      background-color: #4CAF50;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    
    button:hover {
      background-color: #45a049;
    }
    :global(.gray-stars) {
        color: #ccc; /* Gray color for empty stars */
        font-size: large;
        }
  </style>