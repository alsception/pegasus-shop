<script lang="ts">
import { onMount } from "svelte";
import { writable } from "svelte/store";
import { auth } from "../../core/services/SessionStore";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export let selectedCategory: number;
export let onSelect: (category: number) => void = () => {};

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

//koristimo li ovo????
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

/**
 * TODO: ovde treba sve ovo ukloniti i ostaviti samo jednostavne kategorije, number,
 * bez ucitavanja sa servera, jer nekoristimo
*/
onMount(async () => 
{

  loading.set(true);
  
  try 
  {
    const res = await fetch(API_BASE_URL + "/product-categories/tree", 
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${$auth.token}`,
        "Content-Type": "application/json",
      },
    });
    
    if (res.ok) 
    {
      const data: Category[] = await res.json();
      const flattenedCategories = flattenCategories(data);
      categories.set(flattenedCategories);
    }    
  } 
  catch (e) 
  {
    categories.set([]);
    console.error('Error fetching categories:', e);
  } 
  finally 
  {
    loading.set(false);
  }
});

function clickChange(value: any) 
{
  if (selectedCategory != value) 
  {
    selectedCategory = value;
    onSelect(value);
  } 
  else 
  {
    selectedCategory = 0;
    onSelect(0);
  }

  closeMenu();
}

const closeMenu = () => {
    if (typeof document !== 'undefined') {
      (document.activeElement as HTMLElement)?.blur();
    }
  };
</script>

<div class="form-control w-full max-w-xs">
  
  {#if $loading}
    <div tabindex="0" role="button" class="btn btn-dash ">
        <i class="fas fa-search"></i>
        <span class="loading loading-dots loading-xs"></span>
      </div>
  {:else}  

    <div class="dropdown z-49">
      <div tabindex="0" role="button" class="btn btn-dash p-[0.4rem]">
        Kategorije
      </div>
      <ul tabindex="-1" class="dropdown-content menu rounded-box z-9991 w-[240px] p-2 shadow-sm bg-base-200 dark:bg-zinc-950 rounded-lg
      pointer-events-auto border border-primary/30"
      style="right: 0.5rem; backdrop-filter: blur(12px) !important; 
        -webkit-backdrop-filter: blur(16px) !important;">
        {#each $categories as category}
          <!-- svelte-ignore a11y_no_static_element_interactions -->
          <li class="bg-transparent" class:selected-cat={category.id === selectedCategory}>
            <!-- svelte-ignore a11y_click_events_have_key_events -->
            <div 
              class="text-md"
              style="font-weight: {category.id === selectedCategory} ? 'bold' : 'normal'}; "
              on:click={()=>clickChange(category.id)}
              >{category.name}</div>
          </li>
        {/each}
      </ul>
    </div>
  {/if}
</div>
<style> 
  .selected-cat{
    color: var(--color-primary);
    background-color: var(--color-base-300);
    font-weight: bold !important;
    font-style: bold;
  }
</style>