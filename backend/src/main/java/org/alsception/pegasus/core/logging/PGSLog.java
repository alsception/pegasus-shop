package org.alsception.pegasus.core.logging;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pgs_logs")
@Getter 
@Setter 
@NoArgsConstructor // Menja prazan konstruktor
@AllArgsConstructor // Korisno za testove i buildere
@Builder // Omogućava lakše kreiranje objekata: PGSLog.builder().action("...").build()
public class PGSLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "resource_code", length = 100)
    private String resourceCode;    

    @Column(nullable = true)
    private String username;    

    @Column(nullable = true)
    private String action;    

    private String type;
    
    @Column(length = 45) // Dovoljno za IPv6
    private String ip;

    @Column(name = "user_agent", columnDefinition = "TEXT") // UserAgent može biti veoma dugačak
    private String userAgent;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime timestamp;

    public PGSLog(String username, String action) {
        this.username = username;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}