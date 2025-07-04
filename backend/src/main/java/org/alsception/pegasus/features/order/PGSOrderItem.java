package org.alsception.pegasus.features.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.alsception.pegasus.features.products.PGSProduct;

@Data
@Entity
@Table(name = "pgs_order_items")
public class PGSOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private PGSOrder order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private PGSProduct product;

    private int quantity;//TODO: WHAT IF DECIMAL OR LITRI???
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal price; 
    
    @Column(nullable = true, updatable = false)
    private LocalDateTime created;
    
    @Column(nullable = true)
    private LocalDateTime modified;
    
    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        modified = LocalDateTime.now();
    }

    public PGSOrderItem() {}

    public Long getId() {
        return id;
    }

    public PGSOrder getOrder() {
        return order;
    }

    public void setOrder(PGSOrder order) {
        this.order = order;
    }

    public PGSProduct getProduct() {
        return product;
    }

    public void setProduct(PGSProduct product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
