
export interface Product 
{
    id: number;
    name: string;
    code: string;
    active: boolean;
    description: string;

    /**
     * jedan od ova dva ce morati da ide. todo
     */
    priceEur: number;
    basePrice: number;
    imageUrl: string;

    category: string;
    brand: string;
    baseCurrency: string;
    taxPercent: number;
    taxAmount: number;
    taxGroup: string;

    discount: number;
    department: number;
    discountType: string;
    unit: string;
  
    stockQuantity: number;
    comment: string;

    other: string;
    marked?: boolean;

    created?: Date | EpochTimeStamp | string;
    modified?: Date | EpochTimeStamp | string;
}