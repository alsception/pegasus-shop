/**https://zerodevx.github.io/svelte-toast/ */

import { toast } from '@zerodevx/svelte-toast';

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
