/***
 * This file is just a collection of utility functions, used in more then 1 places
 * Created: 10/6/25
 * Author: Alsception
 */
const badgeInfo = 'novo'

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
                    <span class="indicator-item badge badge-info dark:bg-violet-800 dark:text-primary">🔥 `+badgeInfo+`</span><br>
                    <div class=" tooltip tooltip-info cursor-pointer" data-tip="`+tooltip+` - Stiglo prije manje od `+minDiff+` minuta ">
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

export function formatTime2(dateStr: string | number | Date | null | undefined): string 
{
    if (dateStr == null) return '';

    const date = new Date(dateStr);
    let output = '';  

    const formattedDateTime = formattedTime(date)

    output = formattedDateTime

    return output;
}

export function formatDate(
  dateStr: string | number | Date | null | undefined, 
  tooltip: string, 
  minDiff: number): string 
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


export const formattedTime = (value: string | number | Date | null | undefined): string => 
{
    if (!value) return "-";
    const date = new Date(value);
    if (isNaN(date.getTime())) return "-";
    return date.toLocaleTimeString("en-GB", { hour: "2-digit", minute: "2-digit", hour12: false });
};

export function formatActive(active: boolean | null | undefined)
{
    if(active === null || active === undefined) return '';
    return active ? "🟢 Aktivan" : "🔴 Neaktivan";
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
    switch (status?.toUpperCase()) 
    {
      case "READY":
      case "DELIVERED":
      case "SERVED":
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

export function getOrderStatusLabel(status: string ): string 
{
    switch (status?.toUpperCase()) 
    {
      case "READY":      
        return "SPREMNO";

      case "DELIVERED":
      case "SERVED":
        return "DOSTAVLJENO"

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
        return status;
    }
}

export function getOrderCardBgClass(status: string | null | undefined): string 
{
    /**
     * Tip narudžbe	Boja pozadine (Dark Mode)	Boja teksta/border-a	Opis
    Žuta (Na čekanju)	bg-yellow-900/20	text-yellow-500/80	Boja ćilibara, suptilna i topla.
    Plava (U obradi)	bg-blue-900/20	text-blue-500/80	Tamna teget-plava, smirena.
    Zelena (Završeno)	bg-emerald-900/20	text-emerald-500/80	Duboka šumska zelena, odmara oči.
    */

    switch (status?.toUpperCase()) {
      case "WAITING":
        return  "bg-base-200 "
                +"dark:bg-[linear-gradient(135deg,_#1d1d1d_0%,_#0d0d0d_100%)] "
                +"border border-zinc-200 dark:border-zinc-700 ";

      case "IN_PREPARATION":
        return "bg-base-200 "
                +"dark:bg-[linear-gradient(135deg,_#1d1d1d_0%,_#0d0d0d_100%)] "
                +"border border-zinc-200 dark:border-zinc-700 ";

      case "READY":
      case "DELIVERED":
      case "SERVED":
        return "bg-gradient-to-br border border-zinc-200  dark:border-zinc-800 "+
                "from-slate-100 dark:from-slate-900 via-green-100 dark:via-green-950  to-emerald-100 "+
                "dark:to-emerald-900 "

      case "CANCELLED":
      case "REFUNDED":
        return "warning";

      case "RETURNED":
        return "error";

      default:
        return "bg-base-200 border-2 border-zinc-200  dark:border-zinc-800 "
                + " dark:bg-[#111113]";
    }
  }

/**
 * 
 * @param price
 * @returns  Cena sa euro znakom i formatirano sa zarezom za decimalu i tackom za hiljade kako treba
 */
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

/**Cena, bez euro znaka */
export function formatPriceRaw(price: number | undefined): string 
{
  if (price === undefined || price === null) 
  {
    return "0,00";
  }
  
  return new Intl.NumberFormat("de-DE", { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(price);
}  

// Prvo pretvorimo u string sa fiksne dve decimale (3.14) i podelimo na delove
export function getPriceWholePart(price: number | undefined): string 
{
  if (price === undefined || price === null) 
  {
    return "0";
  }

  const formatiranaCena = price.toFixed(2); 
  const delovi = formatiranaCena.split('.');
  const ceoBroj = delovi[0]; 

  return ceoBroj;
}

export function getPriceCents(price: number | undefined): string 
{
   if (price === undefined || price === null) 
  {
    return "0";
  }
  
  const formatiranaCena = price.toFixed(2); 
  const delovi = formatiranaCena.split('.');
  const centi = delovi[1];

  return centi;
}

export function getFormattedPrice(price: number | undefined): string 
{
  return `<div class="flex items-baseline font-bold">
            <span class="text-3xl"> `+getPriceWholePart(price)+`</span>
            <span class="text-md ml-0">,`+getPriceCents(price)+`</span>
            <span class="text-2xl ml-1">€</span>
          </div>`;
}
