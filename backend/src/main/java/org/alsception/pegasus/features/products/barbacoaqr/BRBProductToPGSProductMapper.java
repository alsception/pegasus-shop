package org.alsception.pegasus.features.products.barbacoaqr;

import org.alsception.pegasus.features.products.PGSProduct;
import org.alsception.pegasus.features.products.barbacoaqr.BRBProduct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class BRBProductToPGSProductMapper {
    
    /**
     * Konvertuje BRBProduct u PGSProduct
     * 
     * @param brbProduct Izvorni BRBProduct objekat
     * @return Novi PGSProduct objekat
     */
    public PGSProduct map(BRBProduct brbProduct) {
        if (brbProduct == null) {
            return null;
        }
        
        PGSProduct pgsProduct = new PGSProduct();
        
        pgsProduct.setBaseCurrency("EUR");
        pgsProduct.setStockQuantity(0);
        
        // Mapiranje osnovnih polja
        pgsProduct.setCode(generateCode(brbProduct));
        pgsProduct.setName(brbProduct.getTitle());
        pgsProduct.setDescription(brbProduct.getDescription());
        
        // Mapiranje cijene
        BigDecimal price = brbProduct.getPriceBottle() != null 
            ? brbProduct.getPriceBottle() 
            : BigDecimal.ZERO;
        pgsProduct.setBasePrice(price);
        
        // Mapiranje slike
        pgsProduct.setImageUrl(brbProduct.getImageUrl());
        
        // Mapiranje aktivnosti
        Boolean isActive = brbProduct.getOrdersActive() != null 
            ? brbProduct.getOrdersActive() 
            : true;
        pgsProduct.setActive(isActive);
        
        // Mapiranje jedinice (amount)
        pgsProduct.setUnit(brbProduct.getAmount());
        
        // Mapiranje komentara/dodatnih informacija
        if (brbProduct.getParentTitle() != null) {
            pgsProduct.setComment("Parent: " + brbProduct.getParentTitle());
        }
        
        // Mapiranje department-a prema product_type_id
        if (brbProduct.getProductTypeId() != null) {
            // Prilagodi logiku prema svojim potrebama
            // 1 = KITCHEN, 2 = BAR
            pgsProduct.setDepartment(brbProduct.getProductTypeId());
        }
        
        // Dodavanje tagova iz filtera
        if (brbProduct.getFilters() != null && !brbProduct.getFilters().isEmpty()) {
            brbProduct.getFilters().forEach(filter -> {
                if (filter.getTitle() != null) {
                    pgsProduct.getTags().add(filter.getTitle());
                }
                if (filter.getCategoryTitle() != null) {
                    pgsProduct.getTags().add(filter.getCategoryTitle());
                }
            });
        }
        
        // Dodavanje ostalih informacija
        StringBuilder otherInfo = new StringBuilder();
        
        if (brbProduct.getAlcoholPercent() != null) {
            otherInfo.append("Alcohol: ").append(brbProduct.getAlcoholPercent()).append("% ");
        }
        
        if (brbProduct.getPromotion() != null && brbProduct.getPromotion() > 0) {
            otherInfo.append("Promotion: ").append(brbProduct.getPromotion()).append(" ");
        }
        
        if (brbProduct.getVideoUrl() != null) {
            otherInfo.append("Video: ").append(brbProduct.getVideoUrl()).append(" ");
        }
        
        if (otherInfo.length() > 0) {
            pgsProduct.setOther(otherInfo.toString().trim());
        }
        
        return pgsProduct;
    }
    
    /**
     * Generiše jedinstveni kod za PGSProduct
     * Možeš prilagoditi logiku prema potrebama
     */
    private String generateCode(BRBProduct brbProduct) {
        if (brbProduct.getProductId() != null) {
            return "BRB-" + brbProduct.getProductId();
        }
        if (brbProduct.getId() != null) {
            return "BRB-" + brbProduct.getId();
        }
        return "BRB-UNKNOWN";
    }
    
    /**
     * Mapira listu BRBProduct objekata u listu PGSProduct objekata
     */
    public List<PGSProduct> mapList(List<BRBProduct> brbProducts) {
        if (brbProducts == null || brbProducts.isEmpty()) {
            return new ArrayList<>();
        }
        
        return brbProducts.stream()
                .map(this::map)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}