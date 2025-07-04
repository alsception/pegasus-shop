package org.alsception.pegasus.features.products;

import java.util.List;
import org.alsception.pegasus.features.products.PGSProduct;
import org.alsception.pegasus.features.products.PGSReview;
import org.alsception.pegasus.core.exception.ProductValidationException;

public class ProductValidator {

    public static void validate(PGSProduct product) 
    {
        if (product == null) {
            throw new ProductValidationException("Product cannot be null");
        }
        
        validateCode(product);
        final List<PGSReview> reviews = product.getReviews();
        
        if(reviews!=null && !reviews.isEmpty())
        {
            for(PGSReview r : reviews)
            {
                validateRating(r);
            }
        }
    }

    private static void validateCode(PGSProduct product) throws IllegalArgumentException 
    {
        String code = product.getCode();
        if (code == null || code.length() != 15) 
        {
            throw new ProductValidationException("Product code must be exactly 15 characters long (you have " + (code==null?"0":code.length())+")");
        }
    }
    
    //TODO: validate review 1-5
    public static void validate(PGSReview review) 
    {
        if (review == null) {
            throw new ProductValidationException("Review cannot be null");
        }
        
        validateRating(review);
    }
    
    private static void validateRating(PGSReview review) throws IllegalArgumentException 
    {
        int rating = review.getRating();
        if (rating < 1 || rating>5) 
        {
            throw new ProductValidationException("Rating must be from 1-5 (you have " + rating + ")");
        }
    }
}
