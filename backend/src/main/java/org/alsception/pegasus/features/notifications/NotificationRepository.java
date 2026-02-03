package org.alsception.pegasus.features.notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<PGSNotification, Long> {
    
    // Find all notifications for a specific user
    List<PGSNotification> findByToOrderByCreatedDesc(String to);
    
    // Find unread notifications for a user
    List<PGSNotification> findByToAndReadFalseOrderByCreatedDesc(String to);
    
    // Find read notifications for a user
    List<PGSNotification> findByToAndReadTrueOrderByCreatedDesc(String to);
    
    // Find notifications by type for a user
    List<PGSNotification> findByToAndTypeOrderByCreatedDesc(String to, String type);
    
    // Find notifications from a specific user
    List<PGSNotification> findByFromOrderByCreatedDesc(String from);
    
    // Count unread notifications for a user
    Long countByToAndReadFalse(String to);
    
    // Mark notification as read
    @Modifying
    @Query("UPDATE PGSNotification n SET n.read = true WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);
    
    // Mark all notifications as read for a user
    @Modifying
    @Query("UPDATE PGSNotification n SET n.read = true WHERE n.to = :to AND n.read = false")
    void markAllAsReadForUser(@Param("to") String to);
    
    // Delete old read notifications (optional cleanup method)
    void deleteByToAndReadTrue(String to);
}