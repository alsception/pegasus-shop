package org.alsception.pegasus.features.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.alsception.pegasus.features.products.PGSProduct;

@Entity
@Table(name = "pgs_cart_items")
@Data
public class PGSCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id")
    private PGSCart cart;

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

    public PGSCartItem() {}

    public Long getId() {
        return id;
    }

    public PGSCart getCart() {
        return cart;
    }

    public void setCart(PGSCart cart) {
        this.cart = cart;
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
    
    @Override
    public String toString() 
    {
        return "PGSCartItem{" +
                "id=" + id +
                ", cartId=" + (cart != null ? cart.getId() : null) +
                ", productId=" + (product != null ? product.getId() : null) +
                ", quantity=" + quantity +
                ", price=" + price +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }

    
}
