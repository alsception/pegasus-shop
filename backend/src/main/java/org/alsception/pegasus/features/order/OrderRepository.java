package org.alsception.pegasus.features.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<PGSOrder, Long> 
{    
    // ========== LAZY LOADING: Orders will be loaded without items ==========

    @Query( "SELECT o FROM PGSOrder o WHERE o.user.username = :username ORDER BY o.created DESC" )
    List<PGSOrder> findByUsername(@Param("username") String username);

    @Query( "SELECT o FROM PGSOrder o WHERE o.user.username = :username AND o.code LIKE :code ORDER BY o.created DESC" )
    List<PGSOrder> findByUsernameAndCode(@Param( "username" ) String username, @Param( "code" ) String code);

    @Query( "SELECT o FROM PGSOrder o WHERE o.user.id = :userId" )
    List<PGSOrder> findByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM PGSOrder o WHERE o.user.username = :username AND o.status = :status ORDER BY o.created DESC")
    List<PGSOrder> findOrdersByUsernameAndStatus(@Param("username") String username, @Param("status") String status);

    // ========== LAZY LOADING: Orders will be loaded WITH items (eager loading with JOIN FETCH) ==========

    @Query("""
       SELECT DISTINCT o FROM PGSOrder o
       LEFT JOIN FETCH o.items
       WHERE o.user.username = :username
       ORDER BY o.created DESC
       """)
    List<PGSOrder> findByUsernameWithItems(@Param("username") String username);

    @Query("""
       SELECT DISTINCT o FROM PGSOrder o
       LEFT JOIN FETCH o.items
       WHERE o.user.username = :username AND o.code LIKE :code
       ORDER BY o.created DESC
       """)
    List<PGSOrder> findByUsernameAndCodeWithItems(@Param("username") String username);

    @Query("""
       SELECT DISTINCT o FROM PGSOrder o
       LEFT JOIN FETCH o.items
       WHERE o.user.id = :userId
       ORDER BY o.created DESC
       """)
    List<PGSOrder> findByUserIdWithItems(@Param("userId") Long userId);

    @Query("""
        SELECT DISTINCT o FROM PGSOrder o 
        LEFT JOIN FETCH o.items 
        WHERE o.user.username = :username AND o.status = :status 
        ORDER BY o.created DESC
        """)
    List<PGSOrder> findOrdersByUsernameAndStatusWithItems(@Param("username") String username, @Param("status") String status);

    
}