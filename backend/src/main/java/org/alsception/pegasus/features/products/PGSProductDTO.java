package org.alsception.pegasus.features.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a data transfer object (dto) for PGSProduct entity,
 * used to deliver data to frontend, without exposing all data.
 * Maybe we dont need all these fields. I just throw what i thought could be useful...
 */

@Data //Lombook for getters and setters
@NoArgsConstructor
public class PGSProductDTO 
{
    private Long id;    
    private String code;    
    private String name;    
    private String description;   

    private PGSProductCategory categoryRaw;
    private String category;
    
    private String brand;   
    private String model;   
    private String type;   
    
    private String imageUrl;    
    
    private String baseCurrency;    
    private BigDecimal basePrice;    
    private BigDecimal priceEur;
    private BigDecimal taxPercent; 
    private BigDecimal taxAmount; 
    private BigDecimal discount;     
    private String discountType;
    
    private String unit;
    private BigDecimal weightKg;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
    
    private BigDecimal shippingCost; 
    
    private List<PGSReview> reviews;   
    
    private Integer stockQuantity;
    
    private String comment;   
    private String other;  
    private Boolean marked;   

    private LocalDateTime created;
    private LocalDateTime modified;
    
    private Boolean active;   
    
    public PGSProductDTO(PGSProduct p) 
    {
        this.id = p.getId();
        this.name = getTextField(p.getName());
        this.code = getTextField(p.getCode());
        this.priceEur = p.getBasePrice();
        this.description = getTextField(p.getDescription());
        this.imageUrl = getTextField(p.getImageUrl());
        
        this.discountType = getTextField(p.getDiscountType());
        this.baseCurrency = getTextField(p.getBaseCurrency());
        this.unit = getTextField(p.getUnit());
        this.stockQuantity = p.getStockQuantity();       
        
        this.discount = p.getDiscount();
        this.basePrice = p.getBasePrice();
        this.comment = getTextField(p.getComment());
        this.other = getTextField(p.getOther());
        
        this.created = p.getCreated();
        this.modified = p.getModified();
        this.active = p.isActive();
        
        this.shippingCost = p.getShippingCost();
        this.taxAmount = p.getTaxAmount();
        this.taxPercent = p.getTaxPercent();
        this.discount = p.getDiscount();
        this.categoryRaw = p.getCategory();                
        this.category = p.getCategory() == null ? "" : p.getCategory().getName();                
    }
    
    public String getTextField(String field)
    {
        if(field == null) return "";
        return field;
    }
}
