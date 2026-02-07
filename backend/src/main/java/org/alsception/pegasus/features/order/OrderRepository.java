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

   @Query("""
         SELECT DISTINCT o FROM PGSOrder o
         LEFT JOIN FETCH o.items
         LEFT JOIN FETCH o.user u
         WHERE u.username = :username
         ORDER BY o.created ASC
         """)
   List<PGSOrder> findByUsernameWithItems(@Param("username") String username);

   @Query("""
         SELECT DISTINCT o FROM PGSOrder o
         LEFT JOIN FETCH o.items
         LEFT JOIN FETCH o.user u
         WHERE u.username = :username AND o.code LIKE :code
         ORDER BY o.created ASC
         """)
   List<PGSOrder> findByUsernameAndCodeWithItems(@Param("username") String username, @Param("code") String code);

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