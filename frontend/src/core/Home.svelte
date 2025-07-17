<script lang="ts">
  import { onMount } from "svelte";
  import { getCurrentRole } from "./services/SessionStore";

  type Item = {
    title: string;
    description: string;
    icon: string;
    href: string;
    color: string;
    admin: boolean
    customer: boolean
    default: boolean  
  };

  const allItems: Item[] = [
  {
    title: "Users",
    description: "Manage and view users",
    icon: "users",
    href: "/users",
    color: "blue",
    default: false,
    admin: true,
    customer: false
  },
  {
    title: "Shop",
    description: "Browse products",
    icon: "box",
    href: "/products?listView=false",
    color: "green",
    default: false,
    admin: true,
    customer: true
  },
  {
    title: "Products administration",
    description: "Manage store items",
    icon: "box",
    href: "/products?listView=true",
    color: "yellow",
    default: false,
    admin: true,
    customer: false
  },
  {
    title: "My Cart",
    description: "View, update or delete items in your cart",
    icon: "shopping-basket",
    href: "/cart",
    color: "orange",
    default: false,
    admin: true,
    customer: true
  },
  {
    title: "Orders",
    description: "View and manage orders",
    icon: "truck",
    href: "/orders",
    color: "red",
    default: false,
    admin: true,
    customer: true
  },
  {
    title: "Gallery",
    description:
      "View and manage picture gallery. Billions of free pictures, by Unsplash API",
    icon: "image",
    href: "/pix",
    color: "violet",
    default: false,
    admin: true,
    customer: true
  },
  ];

  const role = getCurrentRole();  

const getItemsByRole = (role: 'ADMIN' | 'CUSTOMER'): Item[] => {
  return allItems.filter((route) => {
    if (route.default) return true;
    if (role === 'ADMIN' || 'EMPLOYEE') return route.admin === true;
    //TODO: employee treba da bude za sebe..
    if (role === 'CUSTOMER') return route.customer === true;
    return false;
  });
};

let items = getItemsByRole(role);

async function addItemsWithDelay(
  sourceItems: Item[],
  addFn: (item: Item) => void,
  delay = 60
) {
  for (const item of sourceItems) {
    addFn(item);
    await new Promise(resolve => setTimeout(resolve, delay));
  }
}
let displayedItems: Item[] = [];

onMount(() => {
  addItemsWithDelay(items, item => displayedItems = [...displayedItems, item]);

});

function getHoverBorderClass(color: string): string {
  if(color === 'orange' || color === 'yellow')
    return `hover:border-${color}-300`;
  else return `hover:border-${color}-600`;
}

</script>
<!-- hover:bg-gradient-to-r hover:from-[#fde68a]  hover:to-[#f59e0b] -->

<div
  class="max-w-7xl mx-auto px-6 py-10 
  grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 scale-up-center-normal bg-base-300"
>

  {#each displayedItems as item}
   <a
    href="#{item.href}"
    class={`block p-6 rounded-2xl bg-base-100 text-center 
            scale-up-center-normal transition-all duration-200 
            hover:-translate-y-1 hover:bg-accent/5 border-3 border-base-100 ${getHoverBorderClass(item.color)}`}>
      <div class="text-2xl font-semibold scale-up-center-normal">
        <i class="fas fa-{item.icon} w-5 mr-2"></i>
      </div>
      <h2 class=" text-2xl font-semibold">{item.title}</h2>
      <p
      class=" mt-2 text-base text-primary/66"
      >
      {item.description}
      </p>
    </a>
  {/each}
</div>

<div style="display: none;">
Color palete:
<ul>
  <li><span class="badge badge-xs bg-primary"></span> bg-primary</li> 
  <li><span class="badge badge-xs bg-secondary"></span> bg-secondary</li> 
  <li><span class="badge badge-xs bg-accent"></span> bg-accent</li>
  <li><span class="badge badge-xs bg-base"></span> bg-base</li>
  <li><span class="badge badge-xs bg-base-100"></span> bg-base-100</li>
  <li><span class="badge badge-xs bg-base-200"></span> bg-base-200</li>
  <li><span class="badge badge-xs bg-base-300"></span> bg-base-300</li>
</ul>

<div class="overflow-x-auto"><div class="whitespace-nowrap"><table><thead><tr><th></th><th><!---->Color name<!----></th><th><!---->CSS variable<!----></th><th><!---->Where to use<!----></th></tr></thead><tbody><tr><td><span class="badge bg-primary"></span></td><td><!---->primary<!----></td><td><code>--color-primary</code></td><td><!---->Primary brand color, The main color of your brand<!----></td></tr><tr><td><span class="badge bg-primary-content"></span></td><td><!---->primary-content<!----></td><td><code>--color-primary-content</code></td><td><!---->Foreground content color to use on<!----><code>primary</code><!---->color<!----><br><br></td></tr><tr><td><span class="badge bg-secondary"></span></td><td><!---->secondary<!----></td><td><code>--color-secondary</code></td><td><!---->Secondary brand color, The optional, secondary color of your brand<!----></td></tr><tr><td><span class="badge bg-secondary-content"></span></td><td><!---->secondary-content<!----></td><td><code>--color-secondary-content</code></td><td><!---->Foreground content color to use on<!----><code>secondary</code><!---->color<!----><br><br></td></tr><tr><td><span class="badge bg-accent"></span></td><td><!---->accent<!----></td><td><code>--color-accent</code></td><td><!---->Accent brand color, The optional, accent color of your brand<!----></td></tr><tr><td><span class="badge bg-accent-content"></span></td><td><!---->accent-content<!----></td><td><code>--color-accent-content</code></td><td><!---->Foreground content color to use on<!----><code>accent</code><!---->color<!----><br><br></td></tr><tr><td><span class="badge bg-neutral"></span></td><td><!---->neutral<!----></td><td><code>--color-neutral</code></td><td><!---->Neutral dark color, For not-saturated parts of UI<!----></td></tr><tr><td><span class="badge bg-neutral-content"></span></td><td><!---->neutral-content<!----></td><td><code>--color-neutral-content</code></td><td><!---->Foreground content color to use on neutral color<!----><br><br></td></tr><tr><td><span class="badge bg-base-100"></span></td><td><!---->base-100<!----></td><td><code>--color-base-100</code></td><td><!---->Base surface color of page, used for blank backgrounds<!----></td></tr><tr><td><span class="badge bg-base-200"></span></td><td><!---->base-200<!----></td><td><code>--color-base-200</code></td><td><!---->Base color, darker shade, to create elevations<!----></td></tr><tr><td><span class="badge bg-base-300"></span></td><td><!---->base-300<!----></td><td><code>--color-base-300</code></td><td><!---->Base color, even more darker shade, to create elevations<!----></td></tr><tr><td><span class="badge bg-base-content"></span></td><td><!---->base-content<!----></td><td><code>--color-base-content</code></td><td><!---->Foreground content color to use on<!----><code>base</code><!---->color<!----><br><br></td></tr><tr><td><span class="badge bg-info"></span></td><td><!---->info<!----></td><td><code>--color-info</code></td><td><!---->Info color, For informative/helpful messages<!----></td></tr><tr><td><span class="badge bg-info-content"></span></td><td><!---->info-content<!----></td><td><code>--color-info-content</code></td><td><!---->Foreground content color to use on<!----><code>info</code><!---->color<!----></td></tr><tr><td><span class="badge bg-success"></span></td><td><!---->success<!----></td><td><code>--color-success</code></td><td><!---->Success color, For success/safe messages<!----></td></tr><tr><td><span class="badge bg-success-content"></span></td><td><!---->success-content<!----></td><td><code>--color-success-content</code></td><td><!---->Foreground content color to use on<!----><code>success</code><!---->color<!----></td></tr><tr><td><span class="badge bg-warning"></span></td><td><!---->warning<!----></td><td><code>--color-warning</code></td><td><!---->Warning color, For warning/caution messages<!----></td></tr><tr><td><span class="badge bg-warning-content"></span></td><td><!---->warning-content<!----></td><td><code>--color-warning-content</code></td><td><!---->Foreground content color to use on<!----><code>warning</code><!---->color<!----></td></tr><tr><td><span class="badge bg-error"></span></td><td><!---->error<!----></td><td><code>--color-error</code></td><td><!---->Error color, For error/danger/destructive messages<!----></td></tr><tr><td><span class="badge bg-error-content"></span></td><td><!---->error-content<!----></td><td><code>--color-error-content</code></td><td><!---->Foreground content color to use on<!----><code>error</code><!---->color<!----></td></tr></tbody></table></div></div>
</div>


                                <style>


/*********************************/
/* ----------------------------------------------
* Generated by Gradienty on 2025-06-02 16:00
* animation scale-up-center-normal
* ----------------------------------------
*/
  @keyframes scale-up-center-normal {0% { transform: scale(0.5); } 100% { transform: scale(1);} }

.scale-up-center-normal { 
  animation: scale-up-center-normal 0.25s ease-out 0s 1 normal both;}
                                </style>