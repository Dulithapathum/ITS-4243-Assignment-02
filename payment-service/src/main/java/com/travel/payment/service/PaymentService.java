package com.travel.payment.service;

import com.travel.payment.dto.BookingStatusUpdateRequest;
import com.travel.payment.dto.PaymentDTO;
import com.travel.payment.dto.PaymentRequest;
import com.travel.payment.dto.ApiResponse;
import com.travel.payment.entity.Payment;
import com.travel.payment.entity.Payment.PaymentStatus;
import com.travel.payment.exception.PaymentProcessingException;
import com.travel.payment.exception.ResourceNotFoundException;
import com.travel.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final WebClient bookingWebClient;
    
    @Transactional
    public PaymentDTO processPayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());
        
        // Simulate payment processing
        try {
            log.info("Processing payment for booking: {}", request.getBookingId());
            
            // Simulate payment gateway call
            boolean paymentSuccess = simulatePaymentGateway(request);
            
            if (paymentSuccess) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaymentDate(LocalDateTime.now());
                log.info("Payment successful: {}", payment.getTransactionId());
                
                Payment savedPayment = paymentRepository.save(payment);
                
                // Update booking status via WebClient
                updateBookingStatus(request.getBookingId(), "CONFIRMED", savedPayment.getId());
                
                return mapToDTO(savedPayment);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                
                // Update booking status to FAILED
                updateBookingStatus(request.getBookingId(), "FAILED", payment.getId());
                
                throw new PaymentProcessingException("Payment processing failed");
            }
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.error("Payment processing error", e);
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage());
        }
    }
    
    private boolean simulatePaymentGateway(PaymentRequest request) {
        // Simulate 95% success rate
        return Math.random() < 0.95;
    }
    
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void updateBookingStatus(Long bookingId, String status, Long paymentId) {
        try {
            BookingStatusUpdateRequest updateRequest = new BookingStatusUpdateRequest(status, paymentId);
            
            bookingWebClient.put()
                    .uri("/api/bookings/{id}/status", bookingId)
                    .bodyValue(updateRequest)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();
            
            log.info("Booking {} status updated to {}", bookingId, status);
        } catch (Exception e) {
            log.error("Failed to update booking status", e);
            // Don't throw exception here to avoid payment rollback
        }
    }
    
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return mapToDTO(payment);
    }
    
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public PaymentDTO getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking: " + bookingId));
        return mapToDTO(payment);
    }
    
    private PaymentDTO mapToDTO(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getBookingId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getPaymentDate(),
                payment.getCreatedAt()
        );
    }
}

