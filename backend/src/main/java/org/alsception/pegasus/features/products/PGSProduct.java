package org.alsception.pegasus.features.products;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alsception.pegasus.features.products.PGSReview;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

@Data //Lombok for getters and setters
@NoArgsConstructor
@Entity
@Table(name = "pgs_products")
public class PGSProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, length = 15, nullable = false)
    private String code;
    
    @Column(nullable = true)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal basePrice; 

    @Column(length = 3, nullable = false)
    private String baseCurrency = "EUR"; // Default currency
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal shippingCost; 
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal taxPercent; 
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal discount; 
    
    @Column
    private String discountType;
    
    @Column(length = 255)
    private String imageUrl;
    
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Getter(AccessLevel.NONE)
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(nullable = true, updatable = false)
    private LocalDateTime created;
    
    @Column(nullable = true)
    private LocalDateTime modified;
    
    //TODO: USE DTO INSTEAD OF THIS
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PGSReview> reviews;
    
    // SKU (Stock Keeping Unit)
    @Column(length = 50, nullable = true)
    private String unit;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal weightKg;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal lengthCm;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal widthCm;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal heightCm;
    
    @Column(length = 100)
    private String brand;   
    
    @Column
    private String comment;   
    
    @Column
    private String other;   
    
    @Column
    private Boolean marked = false;   
    
    @Version
    private Integer version; // Optimistic locking to prevent concurrent modification issues    

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = true)
    private PGSProductCategory category;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "pgs_product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag") 
    private Set<String> tags = new HashSet<>(); 
    
    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        modified = LocalDateTime.now();
    }
    
    public BigDecimal getTaxAmount()
    {
        return this.basePrice.multiply(this.taxPercent).divide(BigDecimal.valueOf(100));
    }
    
    public boolean isActive() {
        return Boolean.TRUE.equals(active); // vraća true samo ako je true
    }
}