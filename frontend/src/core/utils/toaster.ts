/**https://zerodevx.github.io/svelte-toast/ */

import { toast } from '@zerodevx/svelte-toast';
import CartDetails from '../../features/cart/CartDetails.svelte';

/**
 * Display a success toast notification
 * @param message - The success message to display
 */
export function showSuccessToast(message: string): void {
  toast.push('✅ ' + message, {
    theme: {
      '--toastColor': 'mintcream',
      '--toastBackground': 'rgb(72, 187, 120)',
      '--toastBarBackground': '#2F855A',
      '--toastBarHeight': 0
    }
  });
}

export function showAddSuccessToastWithLink(): void 
{
  let message = 'Product added to '
  let link = '<a href="#/cart" target="_blank" class="pgs-hyperlink font-bold">cart</a>';
  toast.push('✅ ' + message + link, {
    theme: {
      '--toastColor': 'black',
      '--toastBackground': '#eee',
      '--toastBarBackground': '#2F855A',
      '--toastBarHeight': 0
    },
    pausable: true
  });
}

/**
 * 
 * @param message For now we not gonna use it, but some day, it should be usefull to see cart content on hover...
 */
export function showSuccessToastWithCartComponent(message: string): void 
{
  toast.push({
    component: {
      src: CartDetails as any, // where `src` is a Svelte component, any is a hack
      props: {
        title: 'A Dummy Cookie Component'
      },
      sendIdTo: 'toastId' // send toast id to `toastId` prop
    },
    dismissable: false,
    initial: 0,
    theme: {
      '--toastPadding': '0',
      '--toastMsgPadding': '0'
    }
  });
}

 
/**
 * Display an error toast notification
 * @param errorMessage - The error message to display
 */
export function showErrorToast(errorMessage: string): void {
  toast.push('😡 ' + errorMessage, {
    theme: {
      '--toastColor': 'red',
      '--toastBackground': 'pink',
      '--toastBarBackground': '#2F855A',
      '--toastBarHeight': 0
    }
  });
}

//This one stays open longer, ovo zapravo neradi?
export function showErrorToastLongDuration(errorMessage: string): void {
  toast.push('😡 ' + errorMessage, {
    theme: {
      '--toastColor': 'red',
      '--toastBackground': 'pink',
      '--toastBarBackground': '#2F855A',
      '--toastBarHeight': 0
    }, duration: 20000 
  });
}

//This one stays open unles closed
export function showErrorToastNoExp(errorMessage: string): void {
  toast.push('😡 ' + errorMessage, {
    theme: {
      '--toastColor': 'red',
      '--toastBackground': 'pink',
      '--toastBarBackground': '#2F855A',
      '--toastBarHeight': 0
    },   initial: 0
  });
}
