package org.alsception.pegasus.features.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<PGSOrder, Long> 
{

    @Query("SELECT o FROM PGSOrder o WHERE o.user.username = :username")
    List<PGSOrder> findByUsername(@Param("username") String username);
    
    @Query("SELECT o FROM PGSOrder o WHERE o.user.id = :userId")
    List<PGSOrder> findByUserId(@Param("userId") Long userId);

}