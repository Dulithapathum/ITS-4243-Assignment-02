package com.travel.notification.service;

import com.travel.notification.dto.NotificationDTO;
import com.travel.notification.dto.NotificationRequest;
import com.travel.notification.entity.Notification;
import com.travel.notification.entity.Notification.NotificationStatus;
import com.travel.notification.exception.ResourceNotFoundException;
import com.travel.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    @Transactional
    public NotificationDTO sendNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setBookingId(request.getBookingId());
        notification.setRecipient(request.getRecipient());
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setStatus(NotificationStatus.PENDING);
        
        // Simulate sending notification
        try {
            // In a real application, integrate with email/SMS services
            log.info("Sending {} notification to {} for booking {}", 
                    request.getType(), request.getRecipient(), request.getBookingId());
            
            // Simulate successful send
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            
            log.info("Notification sent successfully: {}", request.getSubject());
        } catch (Exception e) {
            log.error("Failed to send notification", e);
            notification.setStatus(NotificationStatus.FAILED);
        }
        
        Notification savedNotification = notificationRepository.save(notification);
        return mapToDTO(savedNotification);
    }
    
    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return mapToDTO(notification);
    }
    
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<NotificationDTO> getNotificationsByBookingId(Long bookingId) {
        return notificationRepository.findByBookingId(bookingId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    private NotificationDTO mapToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getUserId(),
                notification.getBookingId(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getType(),
                notification.getStatus(),
                notification.getSentAt(),
                notification.getCreatedAt()
        );
    }
}

