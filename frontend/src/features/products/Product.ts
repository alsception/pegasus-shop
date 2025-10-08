
export interface Product 
{
    id: number;
    name: string;
    code: string;
    active: boolean;
    description: string;

    priceEur: number;
    basePrice: number;
    priceUsd: number;
    imageUrl: string;
    reviews: ProductReview[];

    category: string;
    brand: string;
    baseCurrency: string;
    taxPercent: number;
    taxAmount: number;
    taxGroup: string;

    discount: number;
    discountType: string;
    unit: string;
  
    stockQuantity: number;
    comment: string;

    other: string;
    marked?: boolean;

    created?: Date | EpochTimeStamp | string;
    modified?: Date | EpochTimeStamp | string;
}

// Define ProductReview type
type ProductReview = 
{
    id: number;
    rating: number;
    reviewer: string;
    text: string;
};
