package org.alsception.pegasus.features.cart;

import java.util.Optional;
import org.alsception.pegasus.features.users.PGSUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<PGSCart, Long> 
{
    Optional<PGSCart> findByUser(PGSUser user);
    
    Optional<PGSCart> findByUserId(Long userId);
}