package com.travel.hotel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private Integer starRating;
    
    @Column(nullable = false)
    private BigDecimal pricePerNight;
    
    @Column(nullable = false)
    private Integer availableRooms;
    
    @Column(nullable = false)
    private Integer totalRooms;
    
    @Column(nullable = false)
    private String amenities;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

