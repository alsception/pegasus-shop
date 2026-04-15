package org.alsception.pegasus.core.logging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PGSLogRepository extends JpaRepository<PGSLog, Long> {
    
    // Spring automatski pravi upit na osnovu imena metode!
    List<PGSLog> findByUsername(String username);
    
    List<PGSLog> findByAction(String action);
}