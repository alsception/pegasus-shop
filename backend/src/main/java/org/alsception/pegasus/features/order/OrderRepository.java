package org.alsception.pegasus.features.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<PGSOrder, Long> {
   // ========== LAZY LOADING: Orders will be loaded without items ==========

   @Query("SELECT o FROM PGSOrder o WHERE o.user.username = :username ORDER BY o.created DESC")
   List<PGSOrder> findByUsername(@Param("username") String username);

   @Query("SELECT o FROM PGSOrder o WHERE o.user.username = :username AND o.code LIKE :code ORDER BY o.created DESC")
   List<PGSOrder> findByUsernameAndCode(@Param("username") String username, @Param("code") String code);

   @Query("SELECT o FROM PGSOrder o WHERE o.user.id = :userId")
   List<PGSOrder> findByUserId(@Param("userId") Long userId);

   @Query("SELECT o FROM PGSOrder o WHERE o.user.username = :username AND o.status = :status ORDER BY o.created DESC")
   List<PGSOrder> findOrdersByUsernameAndStatus(@Param("username") String username, @Param("status") String status);

   // ========== LAZY LOADING: Orders bellow will be loaded WITH items (eager loading with JOIN FETCH) ==========
   
   /**
    *   TODO: ovde moramo filtrirati po daily session a ne po o.created >= CURRENT_DATE
    *
    */

   /**
       * We use this one in production
       * @param username
       * @return
          */
   @Query("""
            SELECT DISTINCT o FROM PGSOrder o
            LEFT JOIN FETCH o.items
            LEFT JOIN FETCH o.user u
            WHERE u.username = :username
            AND (
                  o.created >= CURRENT_DATE
            )
            ORDER BY o.created ASC
         """)
   List<PGSOrder> findByUsernameWithItems(@Param("username") String username);
   
   @Query("""
            SELECT DISTINCT o FROM PGSOrder o
            LEFT JOIN FETCH o.items
            LEFT JOIN FETCH o.user u
            WHERE 
            (
                  o.created >= CURRENT_DATE
            )
            ORDER BY o.created ASC
         """)
   List<PGSOrder> findWithItems();

   @Query("""
         SELECT DISTINCT o FROM PGSOrder o
         LEFT JOIN FETCH o.items
         LEFT JOIN FETCH o.user u
         WHERE u.username = :username 
         AND o.code LIKE :code
         ORDER BY o.created ASC
         """)
   List<PGSOrder> findByUsernameAndCodeWithItems(@Param("username") String username, @Param("code") String code);
   
   /**
       * Used in production by service
       * @param username
       * @param search
       * @return
       */
   @Query("""
         SELECT DISTINCT o FROM PGSOrder o
         LEFT JOIN FETCH o.items
         LEFT JOIN FETCH o.user u
         WHERE u.username = :username 
         AND 
         (
            o.code LIKE :search OR o.stol LIKE :search            
         )
         AND
         (
            o.created >= CURRENT_DATE
         )   
         ORDER BY o.created ASC
         """)
         
   List<PGSOrder> findByUsernameAndCodeOrTableWithItems(@Param("username") String username, @Param("search") String search);
   
   @Query("""
         SELECT DISTINCT o FROM PGSOrder o
         LEFT JOIN FETCH o.items
         LEFT JOIN FETCH o.user u
         WHERE 
         (
            o.code LIKE :search OR o.stol LIKE :search            
         )
         AND
         (
            o.created >= CURRENT_DATE
         )   
         ORDER BY o.created ASC
         """)
         
   List<PGSOrder> findByCodeOrTableWithItems(@Param("search") String search);

   @Query("""
         SELECT DISTINCT o FROM PGSOrder o
         LEFT JOIN FETCH o.items
         WHERE o.user.id = :userId
         ORDER BY o.created ASC
         """)
   List<PGSOrder> findByUserIdWithItems(@Param("userId") Long userId);

   @Query("""
         SELECT DISTINCT o FROM PGSOrder o
         LEFT JOIN FETCH o.items
         WHERE o.user.username = :username AND o.status = :status
         ORDER BY o.created DESC
         """)
   List<PGSOrder> findOrdersByUsernameAndStatusWithItems(@Param("username") String username,
         @Param("status") String status);

   @Query("""
         SELECT DISTINCT o FROM PGSOrder o
         LEFT JOIN FETCH o.items
         WHERE o.id = :id
         ORDER BY o.created DESC
         """)
   List<PGSOrder> findByIdWithItems(@Param("id") Long id);
   
   @Query("""
         SELECT DISTINCT o FROM PGSOrder o
         LEFT JOIN FETCH o.items
         ORDER BY o.created DESC
         """)
    List<PGSOrder> findAllWithItems();
    
    @Query("""
      SELECT DISTINCT o FROM PGSOrder o 
      LEFT JOIN FETCH o.items 
      WHERE o.session.status = 'OPEN'
           AND o.status != 'SERVED'
      ORDER BY o.created DESC
      """)
    List<PGSOrder> findAllInActiveSessionWithItems();
   

   @Modifying
   @Transactional
   @Query("""
         UPDATE PGSOrder o
         SET o.status = :status, o.modified = CURRENT_TIMESTAMP
         WHERE o.id = :id
         """)
   int updateOrderStatus(@Param("id") Long id, @Param("status") String status);

   @Query("""
      SELECT DISTINCT o FROM PGSOrder o
      LEFT JOIN FETCH o.items
      LEFT JOIN FETCH o.user u
      WHERE o.synced = false
      """)
   List<PGSOrder> findBySyncedFalseWithItems();

}