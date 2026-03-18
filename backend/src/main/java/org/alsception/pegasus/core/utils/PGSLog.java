package org.alsception.pegasus.core.utils;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String action;

    private LocalDateTime timestamp;

    public UserActionLog() {}

    public UserActionLog(String username, String action) {
        this.username = username;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    // Getteri i setteri
}
