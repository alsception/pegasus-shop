package org.alsception.pegasus.features.cart;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alsception.pegasus.features.users.PGSUser;
import org.alsception.pegasus.features.table.PGSTable;

@Data //Lombook for getters and setters
@NoArgsConstructor
@Entity
@Table(name = "pgs_carts")
public class PGSCart 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private PGSUser user;

    @JsonManagedReference
    //⚠️ Ovo može izazvati probleme sa performansama ako lista items sadrži mnogo podataka.
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PGSCartItem> items = new ArrayList<>();
    
    @Column(nullable = true, updatable = false)
    private LocalDateTime created;
    
    @Column(nullable = true)
    private LocalDateTime modified;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private PGSTable table;
    
    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        modified = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public List<PGSCartItem> getItems() {
        return items;
    }

    public void addItem(PGSCartItem item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(PGSCartItem item) {
        items.remove(item);
        item.setCart(null);
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(item -> item.getProduct().getBasePrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int getTotalProductTypes()
    {
        if(this.items==null)
            return 0;
        
        return this.items.size();
    }
    
    public int getTotalItems()
    {
        if(this.items==null)
            return 0;
        
        return items.stream()
                .mapToInt(PGSCartItem::getQuantity)
                .sum();
    }
}
