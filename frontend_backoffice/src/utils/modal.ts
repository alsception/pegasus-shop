export function showErrorModal(message: string): void 
{
    const contentEl = document.getElementById("error-modal-content");
    const dialogEl = document.getElementById("error-modal") as HTMLDialogElement;
    
    if (contentEl) 
    {       
        contentEl.textContent = message;
    }
    
    if (dialogEl) 
    {
        dialogEl.showModal();
    }
}

export function showErrorModalWithTitle(title:string, message: any): void 
{
    const contentEl = document.getElementById("error-modal-content");
    const titleEl = document.getElementById("error-modal-title");
    const dialogEl = document.getElementById("error-modal") as HTMLDialogElement;
    
    //If there is only title, it is actualy message hah   
    //???

    if(title && message === undefined)
    {
        message = title;
        title = '';
        if (contentEl) 
        {       
            contentEl.textContent = message;
        }
    }
    else
    {
        if (contentEl) 
        {       
            contentEl.textContent = message;
        }
        if (titleEl) 
        {       
            titleEl.textContent = title;
        }
    }

    if (dialogEl) 
    {
        dialogEl.showModal();
    }
}