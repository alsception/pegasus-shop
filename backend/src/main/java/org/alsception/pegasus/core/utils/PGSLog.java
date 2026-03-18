package org.alsception.pegasus.core.utils;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PGSLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;

    private String username;
    
    private String action;
    
    private String type;

    private LocalDateTime timestamp;

    public PGSLog() {}

    public PGSLog(String username, String action) {
        this.username = username;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    // Getteri i setteri
}
