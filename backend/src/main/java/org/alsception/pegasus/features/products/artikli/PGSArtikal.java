package org.alsception.pegasus.features.products.artikli;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pgs_artikli")
public class PGSArtikal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(unique = true, length = 15, nullable = true)
    private String barcode;
    
    // Objekat za relaciju
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kategorija_id", insertable = false, updatable = false)
    private PGSArtikalKategorija kategorija;

    // Primitivni ID dostupan u Javi za brz pristup/filtriranje
    @Column(name = "kategorija_id")
    private Integer kategorijaId;

    @Column(nullable = true, updatable = false)
    private LocalDateTime created;
    
    @Column(nullable = true)
    private LocalDateTime updated;
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal price1; 
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal price2; 
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal price3; 
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal price4; 
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal price5; 
    
    @Column(precision = 19, scale = 2, nullable = true)
    private BigDecimal price6; 
    
    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }
}