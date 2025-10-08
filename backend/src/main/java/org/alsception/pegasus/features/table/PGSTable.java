package org.alsception.pegasus.features.table;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "pgs_tables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PGSTable 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String number;

    private Integer capacity;
    private String position;
    private String rayon;
    private String department;
    private String comment;   
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "reserved", nullable = false)
    private Boolean reserved = true;

    public PGSTable(Long id, String number, Integer capacity, String position, String rayon, String department) {
        this.id = id;
        this.number = number;
        this.capacity = capacity;
        this.position = position;
        this.rayon = rayon;
        this.department = department;
    }
    
    public PGSTable(String number, Integer capacity, String position, String rayon, String department) {
        this.number = number;
        this.capacity = capacity;
        this.position = position;
        this.rayon = rayon;
        this.department = department;
    }

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