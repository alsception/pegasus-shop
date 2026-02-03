package org.alsception.pegasus.features.notifications;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    
    // Get all notifications for a user
    public List<PGSNotification> getNotificationsForUser(String username) {
        return notificationRepository.findByToOrderByCreatedDesc(username);
    }
    
    // Get unread notifications for a user
    public List<PGSNotification> getUnreadNotifications(String username) {
        return notificationRepository.findByToAndReadFalseOrderByCreatedDesc(username);
    }
    
    // Get read notifications for a user
    public List<PGSNotification> getReadNotifications(String username) {
        return notificationRepository.findByToAndReadTrueOrderByCreatedDesc(username);
    }
    
    // Get notifications by type
    public List<PGSNotification> getNotificationsByType(String username, String type) {
        return notificationRepository.findByToAndTypeOrderByCreatedDesc(username, type);
    }
    
    // Get unread count
    public Long getUnreadCount(String username) {
        return notificationRepository.countByToAndReadFalse(username);
    }
    
    // Create a new notification
    @Transactional
    public PGSNotification createNotification(PGSNotification notification) {
        return notificationRepository.save(notification);
    }
    
    // Create notification with builder pattern
    @Async //fire and forget
    @Transactional
    public void createNotification(String title, String text, String from, String to, String type) 
    {
        logger.debug("Creating notification");
        PGSNotification notification = new PGSNotification();
        notification.setTitle(title);
        notification.setText(text);
        notification.setFrom(from);
        notification.setTo(to);
        notification.setType(type);
        notification.setRead(false);
        notificationRepository.save(notification);
        logger.debug("Notification saved");
    }
    
    // Mark notification as read
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
    
    // Mark all notifications as read for a user
    @Transactional
    public void markAllAsRead(String username) {
        notificationRepository.markAllAsReadForUser(username);
    }
    
    // Delete a notification
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    // Delete all read notifications for a user
    @Transactional
    public void deleteReadNotifications(String username) {
        notificationRepository.deleteByToAndReadTrue(username);
    }
    
    // Get notification by ID
    public PGSNotification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }
}