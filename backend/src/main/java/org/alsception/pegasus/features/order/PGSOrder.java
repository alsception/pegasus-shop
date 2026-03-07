package org.alsception.pegasus.features.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import org.alsception.pegasus.features.users.PGSUser;
import org.alsception.pegasus.features.table.PGSTable;

@Data //Lombook for getters and setters
@NoArgsConstructor
@Entity
@Table(name = "pgs_orders")
public class PGSOrder
{
    @Id
    /*@GeneratedValue(strategy = GenerationType.IDENTITY)*/
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER) // TODO: za sad je eager, a posle bi mozda trebo da bude lazy
    @JoinColumn(name = "user_id")
    //@JsonBackReference
    private PGSUser user;
    
    private String code;
    private String paymentMethod;
    private String currency;
    private String status;
    private String comment;
    private String stol;
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal price; 
    
    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PGSOrderItem> items = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY) // I ovo
    @JoinColumn(name = "table_id")
    private PGSTable table;

    @Column(nullable = true, updatable = false)
    private LocalDateTime created;
    
    @Column(nullable = true)
    private LocalDateTime modified;

    @Column(nullable = true)
    private LocalDateTime uPripremiAt;

    @Column(nullable = true)
    private LocalDateTime spremnoAt;
    
    @Column(nullable = true)
    private LocalDateTime servedAt;

    // Ključno polje za sinkronizaciju
    private boolean synced = false;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = true)
    private PGSDailySession session;
    
    
    /*//One day, if you have time and will to play with various error messages, we can include this field.
    @Version
    private Integer version; // Optimistic locking to prevent concurrent modification issues    
    */
    
    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        modified = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "PGSOrder{" +
                "id=" + id +
                ", code='" + code + "'" +
                ", status='" + status + "'" +
                ", session=" + (session != null ? session.getId() : "N/A") +
                ", items=" + (items != null ? items.size() : 0) +
                ", created=" + created +
                "}";
    }
    
}