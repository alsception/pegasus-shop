package org.alsception.pegasus.features.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alsception.pegasus.core.users.PGSUser;

@Data //Lombook for getters and setters
@NoArgsConstructor
@Entity
@Table(name = "pgs_orders")
public class PGSOrder
{
    @Id
    /*@GeneratedValue(strategy = GenerationType.IDENTITY)*/
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    //@JsonBackReference
    private PGSUser user;
    
    private String code;
    private String email;
    private String name;
    private String address;
    private String paymentMethod;
    private String currency;
    private String status;
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal price; 
    
    //@JsonIgnore
    @JsonManagedReference
    //⚠️ Ovo može izazvati probleme sa performansama ako lista items sadrži mnogo podataka.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PGSOrderItem> items = new ArrayList<>();
    
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
    
}