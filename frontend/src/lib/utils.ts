/***
 * This file is just a collection of utility functions, used in more then 1 places
 * Created: 10/6/25
 * Author: Alsception
 */


/**
 * @param dateStr Date in whatever format there is
 * @param tooltip Non-obligatory tooltip
 * @param minDiff Minimum difference in minutes to change background and tooltip
 * @returns HTML. If date passed is <diffInMinutes> older then <now>, it will be with different background and tooltip.
 * 
 * 
 * TODO: This should be split in 2 functions, one for formatting date and another for tooltip.
 * 
 * 
 */
export function formatDate(dateStr: string | number | Date | null | undefined, tooltip: string, minDiff: number): string 
{
    if (dateStr == null) return '';

    const date = new Date(dateStr);
    const now = new Date();
    const diffInMs = now.getTime() - date.getTime();
    const diffInMinutes = diffInMs / (1000 * 60);
    let output = '';  

    const formattedDateTime = date.toLocaleString('en-GB', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    });

    if (diffInMinutes < minDiff) 
    {
        output = `<div>
                    <span class="indicator-item badge badge-accent">new</span>
                    <br>
                    <div class="bg-amber-100 dark:bg-blue-950 tooltip cursor-pointer" data-tip="`+tooltip+`">
                        <span class="text-accent dark:text-yellow-300 font-bold"> ${formattedDateTime} </span>
                    </div>
                  </div>`;
    }
    else
    {
        output = formattedDateTime
    }

    return output;
}

export function formatActive(active: boolean | null | undefined)
{
    if(active === null || active === undefined) return '';
    return active ? "🟢 Active" : "🔴 Disabled";
}

export function formatCommentInfo(comment: string | number | Date | null | undefined): string 
{
    if (comment == null || comment === '') return '';
    
    return `<div class="tooltip cursor-pointer" data-tip="`+comment+`">
                <i class="fas fa-circle-info text-blue-400 hover:text-blue-600 cursor-pointer"></i>        
            </div>`;

}

export function formatCode(code: string | null | undefined): string
{       
    if (code == null || code === '') return '';
    
    // Split code into chunks of 5 characters and join with '-'
    const formatted = code.match(/.{1,5}/g)?.join('-') ?? '';
    
    return formatted;
}
    
