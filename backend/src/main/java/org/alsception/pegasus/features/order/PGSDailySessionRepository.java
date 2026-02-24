package org.alsception.pegasus.features.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PGSDailySessionRepository extends JpaRepository<PGSDailySession, Long> {

    // Pronalazi jedinu otvorenu sesiju (ako postoji)
    Optional<PGSDailySession> findByStatus(PGSSessionStatus status);

    // Provera da li je status OPEN (vraća true/false)
    boolean existsByStatus(PGSSessionStatus status);

    // Opciono: Pronađi zadnju zatvorenu sesiju (da povučeš "endingCash" kao novi "startingCash")
    PGSDailySession findFirstByStatusOrderByClosedAtDesc(PGSSessionStatus status);
}