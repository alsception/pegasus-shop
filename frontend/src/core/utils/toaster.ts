/**https://zerodevx.github.io/svelte-toast/ */

import { toast } from '@zerodevx/svelte-toast';
import CartDetails from '../../features/cart/CartDetails.svelte';

/**
 * Display a success toast notification
 * @param message - The success message to display
 */
export function showSuccessToast(message: string): void 
{
  let checkmark = '<i class="fa fa-check fa-lg text-green-600 font-bold" aria-hidden="true"></i> &nbsp;';
  toast.push(checkmark + message, {
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
  let checkmark = '<i class="fa fa-check fa-lg text-green-600 font-bold" aria-hidden="true"></i> &nbsp;';
  let message = 'Dodato u '
  let link = '<a href="#/cart" class="pgs-hyperlink font-bold">košaricu</a>';
  let text = /*'<span style="color: green;">' +*/ checkmark + message + link /*+ '</span>'*/;
  toast.push( text, {
    theme: {
      '--toastColor': 'black',
      '--toastBackground': '#eee',
      '--toastBarBackground': '#2F855A',
      '--toastBarHeight': 0
    },
    pausable: true
  });
}

export function showInfoToast(message: String, type: String | Number): void 
{
  let checkmark = '<i class="fa fa-check fa-lg text-green-600 font-bold" aria-hidden="true"></i> &nbsp;';
  let plusmark = '<i class="fa fa-plus fa-lg text-blue-600 font-bold" aria-hidden="true"></i> &nbsp;';
  let infomark = '<i class="fa fa-info-circle fa-lg text-blue-600 font-bold" aria-hidden="true"></i> &nbsp;';
  /*let link = '<a href="#/cart" class="pgs-hyperlink font-bold">cart</a>';*/
  let text = checkmark + message/* + link*/;

  switch(type)
  {

    case 1:
      text = plusmark + message;
      break;

    case 2:
      text = checkmark + message;
      break;
  
    default:
      text = infomark + message;
      break;

  }

  toast.push( text, {
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
