<script lang="ts">
import { onMount } from "svelte";
import { writable } from "svelte/store";
import { auth } from "../../core/services/SessionStore";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export let selectedCategory: string = "";
export let onSelect: (category: string) => void = () => {};

interface Category {
  id: number;
  name: string;
  children?: Category[];
}

interface FlatCategory {
  id: number;
  name: string;
  fullName: string;
  level: number;
  hasChildren: boolean;
}

const categories = writable<FlatCategory[]>([]);
const loading = writable(true);

// Function to flatten hierarchical categories
function flattenCategories(categoryList: Category[], level: number = 0): FlatCategory[] {
  const result: FlatCategory[] = [];
  
  for (const category of categoryList) {
    const hasChildren = category.children && category.children.length > 0;
    const indent = "  ".repeat(level); // 2 spaces per level
    const prefix = level > 0 ? "└─ " : "";
    
    result.push({
      id: category.id,
      name: category.name,
      fullName: `${indent}${prefix}${category.name}`,
      level: level,
      hasChildren: hasChildren || false
    });
    
    // Recursively add children
    if (hasChildren) {
      result.push(...flattenCategories(category.children!, level + 1));
    }
  }
  
  return result;
}

onMount(async () => {
  console.log('calling product categories');
  loading.set(true);
  
  try {
    const res = await fetch(API_BASE_URL + "/product-categories/tree", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${$auth.token}`,
        "Content-Type": "application/json",
      },
    });
    
    if (res.ok) {
      const data: Category[] = await res.json();
      const flattenedCategories = flattenCategories(data);
      categories.set(flattenedCategories);
    } else {
      console.log('not ok');
    }
  } catch (e) {
    categories.set([]);
    console.error('Error fetching categories:', e);
  } finally {
    loading.set(false);
  }
});

function handleChange(event: Event) {
  console.log('changed');
  const value = (event.target as HTMLSelectElement).value;
  selectedCategory = value;
  onSelect(value);
}

function clickChange(value: any) {
  console.log('changed2');
  selectedCategory = value;
  onSelect(value);
}
</script>

<div class="form-control w-full max-w-xs">
  
  {#if $loading}
    <div tabindex="0" role="button" class="btn btn-secondary w-[200px]">
        <i class="fas fa-search"></i>
        Loading<span class="loading loading-dots loading-xs"></span>
      </div>
  {:else}  

    <div class="dropdown z-49">
      <div tabindex="0" role="button" class="btn btn-secondary w-[200px]">
        <i class="fas fa-search"></i>
        Search by category
      </div>
      <ul tabindex="-1" class="dropdown-content menu bg-base-300 rounded-box z-1 w-[466px] p-2 shadow-sm">
        {#each $categories as category}
          <!-- svelte-ignore a11y_no_static_element_interactions -->
          <li>
            <!-- svelte-ignore a11y_click_events_have_key_events -->
            <div 
              style="font-weight: {category.hasChildren ? 'bold' : 'normal'}; "
              on:click={()=>clickChange(category.id)}
              >{category.name}</div>
          </li>
        {/each}
      </ul>
    </div>
  {/if}
</div>


<!-- old, select instead of 
{#if $loading}
    <select class="select select-bordered w-full" disabled>
      <option>Loading categories...</option>
    </select>
  {:else}
    <select
      class="select select-bordered w-full"
      bind:value={selectedCategory}
      on:change={handleChange}
    >
      <option value="">All categories</option>
      {#each $categories as category}
        <option 
          value="{category.id} - {category.name}"
          style="font-weight: {category.hasChildren ? 'bold' : 'normal'}; font-family: monospace;"
        >
          {category.fullName}
        </option>
      {/each}
    </select> -->