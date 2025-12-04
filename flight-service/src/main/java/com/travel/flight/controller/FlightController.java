package com.travel.flight.controller;

import com.travel.flight.dto.ApiResponse;
import com.travel.flight.dto.FlightDTO;
import com.travel.flight.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {
    
    private final FlightService flightService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<FlightDTO>> createFlight(@Valid @RequestBody FlightDTO flightDTO) {
        FlightDTO createdFlight = flightService.createFlight(flightDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Flight created successfully", createdFlight));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDTO>> getFlightById(@PathVariable Long id) {
        FlightDTO flight = flightService.getFlightById(id);
        return ResponseEntity.ok(ApiResponse.success("Flight retrieved successfully", flight));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<FlightDTO>>> getAllFlights() {
        List<FlightDTO> flights = flightService.getAllFlights();
        return ResponseEntity.ok(ApiResponse.success("Flights retrieved successfully", flights));
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<FlightDTO>>> getAvailableFlights() {
        List<FlightDTO> flights = flightService.getAvailableFlights();
        return ResponseEntity.ok(ApiResponse.success("Available flights retrieved successfully", flights));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FlightDTO>>> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination) {
        List<FlightDTO> flights = flightService.searchFlights(origin, destination);
        return ResponseEntity.ok(ApiResponse.success("Flights searched successfully", flights));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDTO>> updateFlight(
            @PathVariable Long id, 
            @Valid @RequestBody FlightDTO flightDTO) {
        FlightDTO updatedFlight = flightService.updateFlight(id, flightDTO);
        return ResponseEntity.ok(ApiResponse.success("Flight updated successfully", updatedFlight));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok(ApiResponse.success("Flight deleted successfully", null));
    }
    
    @GetMapping("/{id}/check-availability")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkAvailability(@PathVariable Long id) {
        boolean available = flightService.checkAvailability(id);
        return ResponseEntity.ok(
                ApiResponse.success("Flight availability checked", 
                        Map.of("available", available, "flightId", id != null)));
    }
    
    @PostMapping("/{id}/reserve")
    public ResponseEntity<ApiResponse<Void>> reserveSeat(@PathVariable Long id) {
        flightService.reserveSeat(id);
        return ResponseEntity.ok(ApiResponse.success("Seat reserved successfully", null));
    }
}

