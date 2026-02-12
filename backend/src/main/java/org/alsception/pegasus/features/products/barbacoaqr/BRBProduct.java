package org.alsception.pegasus.features.products.barbacoaqr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "brb_products")
public class BRBProduct {
    
    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // === NESTED CLASS ZA FILTER ===
    @Data
    public static class Filter {
        @JsonProperty("category_title")
        private String categoryTitle;
        
        @JsonProperty("title")
        private String title;
    }
    
    @Id
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("product_id")
    @Column(name = "product_id")
    private Long productId;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("amount")
    private String amount;
    
    @JsonProperty("has_amount")
    @Column(name = "has_amount")
    private Boolean hasAmount;
    
    @JsonProperty("parent_title")
    @Column(name = "parent_title")
    private String parentTitle;
    
    @JsonProperty("schedule_active")
    @Column(name = "schedule_active")
    private Boolean scheduleActive;
    
    @JsonProperty("image_url")
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @JsonProperty("video_url")
    @Column(name = "video_url", length = 500)
    private String videoUrl;
    
    @JsonProperty("icon")
    private String icon;
    
    @JsonProperty("orders_active")
    @Column(name = "orders_active")
    private Boolean ordersActive;
    
    @JsonProperty("price_bottle")
    @Column(name = "price_bottle", precision = 10, scale = 2)
    private BigDecimal priceBottle;
    
    @JsonProperty("product_type_id")
    @Column(name = "product_type_id")
    private Integer productTypeId;
    
    @JsonProperty("alcohol_percent")
    @Column(name = "alcohol_percent")
    private Double alcoholPercent;
    
    @JsonProperty("promotion")
    private Integer promotion;
    
    @JsonProperty("description")
    @Column(length = 1000)
    private String description;
    
    // === ČUVAJ KAO JSON STRING ===
    
    @Column(name = "addition_categories", columnDefinition = "TEXT")
    private String additionCategoriesJson;
    
    @Column(name = "children", columnDefinition = "TEXT")
    private String childrenJson;
    
    @Column(name = "filters", columnDefinition = "TEXT")
    private String filtersJson;
    
    @Column(name = "allergens", columnDefinition = "TEXT")
    private String allergensJson;
    
    // === GETTERI I SETTERI ZA LISTE ===
    
    @JsonProperty("addition_categories")
    @Transient
    public List<Object> getAdditionCategories() {
        return jsonToObjectList(additionCategoriesJson);
    }
    
    @JsonProperty("addition_categories")
    public void setAdditionCategories(List<Object> list) {
        this.additionCategoriesJson = listToJson(list);
    }
    
    @JsonProperty("children")
    @Transient
    public List<Object> getChildren() {
        return jsonToObjectList(childrenJson);
    }
    
    @JsonProperty("children")
    public void setChildren(List<Object> list) {
        this.childrenJson = listToJson(list);
    }
    
    @JsonProperty("filters")
    @Transient
    public List<Filter> getFilters() {
        return jsonToFilterList(filtersJson);
    }
    
    @JsonProperty("filters")
    public void setFilters(List<Filter> list) {
        this.filtersJson = filterListToJson(list);
    }
    
    @JsonProperty("allergens")
    @Transient
    public List<Object> getAllergens() {
        return jsonToObjectList(allergensJson);
    }
    
    @JsonProperty("allergens")
    public void setAllergens(List<Object> list) {
        this.allergensJson = listToJson(list);
    }
    
    // === HELPER METODE ===
    
    private String listToJson(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
    
    private List<Object> jsonToObjectList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Object>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }
    
    private String filterListToJson(List<Filter> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
    
    private List<Filter> jsonToFilterList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Filter>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }
}