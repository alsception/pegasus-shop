package org.alsception.pegasus.features.notifications;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController 
{
    
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    // Get all notifications for current user
    @GetMapping
    public ResponseEntity<List<PGSNotification>> getAllNotifications(Principal principal) 
    {
        String username = principal.getName();
        
        logger.debug("Getting notifications for "+username);
        /*List<PGSNotification> notifications = notificationService.getNotificationsForUser(username);*/
        List<PGSNotification> notifications = notificationService.getNotificationsForUser("*");
        return ResponseEntity.ok(notifications);
    }
    
    // Get unread notifications
    @GetMapping("/unread")
    public ResponseEntity<List<PGSNotification>> getUnreadNotifications(Principal principal) {
        String username = principal.getName();
        List<PGSNotification> notifications = notificationService.getUnreadNotifications(username);
        return ResponseEntity.ok(notifications);
    }
    
    // Get read notifications
    @GetMapping("/read")
    public ResponseEntity<List<PGSNotification>> getReadNotifications(Principal principal) {
        String username = principal.getName();
        List<PGSNotification> notifications = notificationService.getReadNotifications(username);
        return ResponseEntity.ok(notifications);
    }
    
    // Get notifications by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PGSNotification>> getNotificationsByType(
            @PathVariable String type,
            Principal principal) {
        String username = principal.getName();
        List<PGSNotification> notifications = notificationService.getNotificationsByType(username, type);
        return ResponseEntity.ok(notifications);
    }
    
    // Get unread count
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Principal principal) {
        String username = principal.getName();
        Long count = notificationService.getUnreadCount(username);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    // Get notification by ID
    @GetMapping("/{id}")
    public ResponseEntity<PGSNotification> getNotificationById(@PathVariable Long id) {
        PGSNotification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }
    
    // Create a new notification
    @PostMapping
    public ResponseEntity<PGSNotification> createNotification(@RequestBody PGSNotification notification) {
        PGSNotification created = notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // Mark notification as read
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
    
    // Mark all notifications as read
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Principal principal) {
        String username = principal.getName();
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok().build();
    }
    
    // Delete a notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    
    // Delete all read notifications
    @DeleteMapping("/read")
    public ResponseEntity<Void> deleteReadNotifications(Principal principal) {
        String username = principal.getName();
        notificationService.deleteReadNotifications(username);
        return ResponseEntity.noContent().build();
    }
}