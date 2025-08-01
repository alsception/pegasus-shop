package org.alsception.pegasus.core.config;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PGSConfigRepository extends JpaRepository<PGSConfig, Long> 
{
    Optional<PGSConfig> findByName(String name);
        
    List<PGSConfig> findByNameContaining(String name);
}