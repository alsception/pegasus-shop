/***
 * This file is just a collection of utility functions, used in more then 1 places
 * Created: 10/6/25
 * Author: Alsception
 */
const badgeInfo = 'new'

export function isNew(dateStr: string | number | Date | null | undefined, minDiff: number): boolean 
{
    if (dateStr == null) return false;

    const date = new Date(dateStr);
    const now = new Date();
    const diffInMs = now.getTime() - date.getTime();
    const diffInMinutes = diffInMs / (1000 * 60);

    if (diffInMinutes < minDiff) 
    {
        return true;
    }

    return false;
}


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
export function formatTime(dateStr: string | number | Date | null | undefined, tooltip: string, minDiff: number): string 
{
    if (dateStr == null) return '';

    const date = new Date(dateStr);
    const now = new Date();
    const diffInMs = now.getTime() - date.getTime();
    const diffInMinutes = diffInMs / (1000 * 60);
    let output = '';  

    const formattedDateTime = formattedTime(date) /*date.toLocaleString('en-GB', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    });*/

    if (diffInMinutes < minDiff) 
    {
        output = `<div>
                    <div class=" tooltip tooltip-info cursor-pointer" data-tip="`+tooltip+` - Stiglo prije manje od `+minDiff+` minuta ">
                        <span class="text-primary font-bold"> ${formattedDateTime} </span>
                    </div>
                    <span class="indicator-item badge badge-info dark:text-black dark:bg-amber-300">🔥 `+badgeInfo+`</span>
                  </div>`;
    }
    else
    {
        output = formattedDateTime
    }

    return output;
}

export function formatTime2(dateStr: string | number | Date | null | undefined): string 
{
    if (dateStr == null) return '';

    const date = new Date(dateStr);
    let output = '';  

    const formattedDateTime = formattedTime(date)

    output = formattedDateTime

    return output;
}

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
                    <span class="indicator-item badge badge-accent dark:text-black dark:bg-violet-500">`+badgeInfo+`</span>
                    <br>
                    <div class=" tooltip tooltip-info cursor-pointer" data-tip="`+tooltip+`">
                        <span class="text-primary font-bold"> ${formattedDateTime} </span>
                    </div>
                  </div>`;
    }
    else
    {
        output = formattedDateTime
    }

    return output;
}


export const formattedTime = (value: string | number | Date | null | undefined): string => {
    if (!value) return "-";
    const date = new Date(value);
    if (isNaN(date.getTime())) return "-";
    return date.toLocaleTimeString("en-GB", { hour: "2-digit", minute: "2-digit", hour12: false });
};

export function formatActive(active: boolean | null | undefined)
{
    if(active === null || active === undefined) return '';
    return active ? "🟢 Active" : "🔴 Disabled";
}

export function formatCommentInfo(comment: string | number | Date | null | undefined): string 
{
    if (comment == null || comment === '') return '';
    
    return `<div class="tooltip tooltip-info cursor-pointer" data-tip="`+comment+`">
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

/**Instead of declaring function like a normal people do, why dont we just declare a constant variable (const) that holds an arrow function expression.*/
export const formatDateTime = (value: string | number | Date | null | undefined): string => 
{
    if (!value) return "-";

    const date = new Date(value);
    if (isNaN(date.getTime())) return "-";

    return date.toLocaleString("en-GB");
};

export function getOrderStatusColor(status: string | null | undefined): string 
{
    switch (status?.toUpperCase()) {
      case "READY":
      case "DELIVERED":
        return "success";
      case "CANCELLED":
      case "REFUNDED":
        return "warning";
      case "RETURNED":
        return "error";
      case "IN_PREPARATION":
        return "info";
      case "WAITING":
        return "warning";
      default:
        return "secondary";
    }
  }

export function getOrderStatusLabel(status: string | null | undefined): string 
{
    switch (status?.toUpperCase()) 
    {
      case "READY":      
        return "SPREMNO";
      case "DELIVERED":
        return "SERVIRANO"
      case "CANCELLED":
      case "REFUNDED":
        return "warning";
      case "RETURNED":
        return "error";
      case "IN_PREPARATION":
        return "U PRIPREMI";
      case "WAITING":
        return "NA ČEKANJU";
      default:
        return "-";
    }
}

export function formatPrice(price: number | undefined): string 
{
  if (price === undefined || price === null) 
  {
    return "0,00 €";
  }
  
  return new Intl.NumberFormat("de-DE", { style: "currency", currency: "EUR" }).format(
    price,
  )

}  
