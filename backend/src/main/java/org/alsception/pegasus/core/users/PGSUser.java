package org.alsception.pegasus.core.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.alsception.pegasus.features.order.PGSOrder;

@Data //Lombook for getters and setters
@NoArgsConstructor
@Entity
@Table(name = "pgs_users")
public class PGSUser 
{
    
    /*
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    We will assign id 
    */
    @Id
    private Long id;
    
    @Column(name = "created", updatable = false)
    private LocalDateTime created;
    
    @Column(nullable = true)
    private LocalDateTime modified;

    @Column(nullable = false, unique = true)
    private String username;
    
    @JsonIgnore //Za svaki slucaj, nikad se nezna :)
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private PGSUserRole role;
    
    @Column
    private String firstName;
    
    @Column
    private String lastName;
    
    @Column
    private String email;
    
    @Column
    private String phone;
    
    @Getter(AccessLevel.NONE)
    @Column(name = "active")
    private Boolean active;    
    
    /**
     * Lomboks toString was actually calling getOrders and generating problems...
     * //TODO: fetch type should be lazy, but this currently causes other problems...
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonManagedReference
    @JsonIgnore //!!! ATTENZIONE - Ovo nebi trebalo da bude ovde tu je samo privremeno ;)
    //Treba da se koristi DTO ko sav normalan svet
    private List<PGSOrder> orders = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        modified = LocalDateTime.now();
    }

    //TODO: Ovo netreba da bude ovde, user netreba da zna sta je userdto
    public PGSUser(org.alsception.pegasus.features.security.AuthRequest userDTO) 
    {
        this.username = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.role = PGSUserRole.USER;
    }
    
    public boolean isActive() {
        return Boolean.TRUE.equals(active); // vraća true samo ako je true
    }
    
    //TODO: remove password from tostring
}
