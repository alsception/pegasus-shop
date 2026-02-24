/**
 * Ovaj objekata predstavlja radni dan. 
 * Ako je radno vreme od 10:00 AM do 01:00 AM sledec dana posle ponoci
 * svi orderi ce imati isti DailySession
 * 
 */


package org.alsception.pegasus.features.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pgs_daily_sessions")
@Data //Lombook for getters and setters
@NoArgsConstructor
public class PGSDailySession 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    @Column(precision = 19, scale = 4)
    private BigDecimal startingCash; // Novac za kusur pri otvaranju

    @Column(precision = 19, scale = 4)
    private BigDecimal endingCash;   // Stvarno stanje u kasi na kraju

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "session_status") // Govori Hibernate-u da koristi Postgres tip
    private PGSSessionStatus status;

    @OneToMany(mappedBy = "session")
    private List<PGSOrder> orders;

}