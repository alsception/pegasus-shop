package org.alsception.pegasus.features.notifications;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pgs_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PGSNotification 
{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column()
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String text;
    
    @Column(name = "from_user")
    private String from;
    
    @Column(name = "to_user")
    private String to;

    @Column(nullable = false)
    private Boolean read = false;

    /* @Enumerated(EnumType.STRING) */
    @Column(length = 20)
    private String type;
    
    @CreationTimestamp
    @Column(name = "created", nullable = true, updatable = false)
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