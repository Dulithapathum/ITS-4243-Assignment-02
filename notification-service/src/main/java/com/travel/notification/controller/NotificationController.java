package com.travel.notification.controller;

import com.travel.notification.dto.ApiResponse;
import com.travel.notification.dto.NotificationDTO;
import com.travel.notification.dto.NotificationRequest;
import com.travel.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<NotificationDTO>> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        NotificationDTO notification = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification sent successfully", notification));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationDTO>> getNotificationById(@PathVariable Long id) {
        NotificationDTO notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(ApiResponse.success("Notification retrieved successfully", notification));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("User notifications retrieved successfully", notifications));
    }
    
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotificationsByBookingId(@PathVariable Long bookingId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByBookingId(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Booking notifications retrieved successfully", notifications));
    }
}

