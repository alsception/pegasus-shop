package org.alsception.pegasus.features.reservation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PGSReservationRepository extends JpaRepository<PGSReservation, Long> {
    
    // Find by date
    List<PGSReservation> findByDan(LocalDate dan);
    
    // Find by name (case-insensitive, partial match)
    List<PGSReservation> findByImeContainingIgnoreCase(String ime);
    
    // Find by date and name
    List<PGSReservation> findByDanAndImeContainingIgnoreCase(LocalDate dan, String ime);
    
    // Find by date or name (more flexible search)
    List<PGSReservation> findByDanOrImeContainingIgnoreCase(LocalDate dan, String ime);

     // Find by date range
    List<PGSReservation> findByDanBetween(LocalDate dateFrom, LocalDate dateTo);
    
    // Find by date range and name
    List<PGSReservation> findByDanBetweenAndImeContainingIgnoreCase(LocalDate dateFrom, LocalDate dateTo, String ime);
    
    // Find from date onwards
    List<PGSReservation> findByDanGreaterThanEqualAndImeContainingIgnoreCase(LocalDate date, String ime);
    
}