package com.travel.hotel.controller;

import com.travel.hotel.dto.ApiResponse;
import com.travel.hotel.dto.HotelDTO;
import com.travel.hotel.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {
    
    private final HotelService hotelService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<HotelDTO>> createHotel(@Valid @RequestBody HotelDTO hotelDTO) {
        HotelDTO createdHotel = hotelService.createHotel(hotelDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hotel created successfully", createdHotel));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelDTO>> getHotelById(@PathVariable Long id) {
        HotelDTO hotel = hotelService.getHotelById(id);
        return ResponseEntity.ok(ApiResponse.success("Hotel retrieved successfully", hotel));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<HotelDTO>>> getAllHotels() {
        List<HotelDTO> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(ApiResponse.success("Hotels retrieved successfully", hotels));
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<HotelDTO>>> getAvailableHotels() {
        List<HotelDTO> hotels = hotelService.getAvailableHotels();
        return ResponseEntity.ok(ApiResponse.success("Available hotels retrieved successfully", hotels));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<HotelDTO>>> searchHotelsByLocation(
            @RequestParam String location) {
        List<HotelDTO> hotels = hotelService.searchHotelsByLocation(location);
        return ResponseEntity.ok(ApiResponse.success("Hotels searched successfully", hotels));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelDTO>> updateHotel(
            @PathVariable Long id, 
            @Valid @RequestBody HotelDTO hotelDTO) {
        HotelDTO updatedHotel = hotelService.updateHotel(id, hotelDTO);
        return ResponseEntity.ok(ApiResponse.success("Hotel updated successfully", updatedHotel));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.ok(ApiResponse.success("Hotel deleted successfully", null));
    }
    
    @GetMapping("/{id}/check-availability")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkAvailability(@PathVariable Long id) {
        boolean available = hotelService.checkAvailability(id);
        return ResponseEntity.ok(
                ApiResponse.success("Hotel availability checked", 
                        Map.of("available", available, "hotelId", id != null)));
    }
    
    @PostMapping("/{id}/reserve")
    public ResponseEntity<ApiResponse<Void>> reserveRoom(@PathVariable Long id) {
        hotelService.reserveRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Room reserved successfully", null));
    }
}

