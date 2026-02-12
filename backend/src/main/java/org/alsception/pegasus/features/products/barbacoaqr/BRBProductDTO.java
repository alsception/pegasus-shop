package org.alsception.pegasus.features.products.barbacoaqr;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import org.alsception.pegasus.features.products.barbacoaqr.BRBProduct.Filter;

@Data
public class BRBProductDTO {
    private Long id;
    private Long productId;
    private String title;
    private String amount;
    private Boolean hasAmount;
    private String parentTitle;
    private Boolean scheduleActive;
    private String imageUrl;
    private String videoUrl;
    private String icon;
    private Boolean ordersActive;
    private BigDecimal priceBottle;
    private Integer productTypeId;
    private Double alcoholPercent;
    private Integer promotion;
    private String description;
    private List<Object> additionCategories;
    private List<Object> children;
    private List<Filter> filters;
    private List<Object> allergens;
}