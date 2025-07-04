package org.alsception.pegasus.features.products;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data //Lombook for getters and setters
@NoArgsConstructor
public class PopularProduct 
{
    private String name;
    private Double averageRating;

    public PopularProduct(String name, Double averageRating) {
        this.name = name;
        this.averageRating = averageRating;
    }

}