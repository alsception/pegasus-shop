package org.alsception.pegasus.features.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<PGSOrder, Long> 
{
    @Query( "SELECT o FROM PGSOrder o WHERE o.user.username = :username ORDER BY o.created DESC" )
    List<PGSOrder> findByUsername(@Param( "username" ) String username);

    @Query( "SELECT o FROM PGSOrder o WHERE o.user.username = :username AND o.code LIKE :code ORDER BY o.created DESC" )
    List<PGSOrder> findByUsernameAndCode(@Param( "username" ) String username, @Param( "code" ) String code);
    
    @Query( "SELECT o FROM PGSOrder o WHERE o.user.id = :userId" )
    List<PGSOrder> findByUserId(@Param("userId") Long userId);
}