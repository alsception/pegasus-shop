package org.alsception.pegasus.features.products;

// Wrapper class for the list of products

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PopularProductsWrapper 
{
    @JsonProperty("popularProducts")
    private List<PopularProduct> popularProducts;

    public PopularProductsWrapper(List<PopularProduct> popularProducts) {
        this.popularProducts = popularProducts;
    }

    public List<PopularProduct> getPopularProducts() {
        return popularProducts;
    }
}