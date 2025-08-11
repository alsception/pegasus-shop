export function showInfoModal(/*title: string, */message: string): void 
{
    const contentEl = document.getElementById("info-modal-content");
    const dialogEl = document.getElementById("info-modal") as HTMLDialogElement;
    
    if (contentEl) {
        /* let errorMessage = "An error occurred";
        
        if (typeof error === 'string') {
            // Handle case where error is a string like "Error: {json...}"
            if (error.startsWith('Error: {')) {
                try {
                    const jsonPart = error.substring(7); // Remove "Error: " prefix
                    const parsedError = JSON.parse(jsonPart);
                    errorMessage = parsedError.message || errorMessage;
                } catch (e) {
                    errorMessage = error; // Fallback to original string
                }
            } else {
                errorMessage = error;
            }
        } else if (error && error.message) {
            // Handle case where error is already an object
            errorMessage = error.message;
        } */
        
        contentEl.textContent = message;
    }
    
    if (dialogEl) {
        dialogEl.showModal();
    }
  }