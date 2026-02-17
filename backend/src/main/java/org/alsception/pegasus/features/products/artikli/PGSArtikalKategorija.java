package org.alsception.pegasus.features.products.artikli;

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
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pgs_artikli_kategorije")
public class PGSArtikalKategorija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;    
    
    @Column(unique = true, nullable = false)
    private String name;

    // Dvosmerna veza
    @ToString.Exclude
    @OneToMany(mappedBy = "kategorija", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PGSArtikal> artikli;

    @Column(nullable = true, updatable = false)
    private LocalDateTime created;
    
    @Column(nullable = true)
    private LocalDateTime updated;       
    
    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }
}