package com.travel.booking.client;

import com.travel.booking.dto.ApiResponse;
import com.travel.booking.dto.FlightDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "flight-service", url = "${services.flight.url}")
public interface FlightServiceClient {
    
    @GetMapping("/api/flights/{id}")
    ApiResponse<FlightDTO> getFlightById(@PathVariable("id") Long id);
    
    @GetMapping("/api/flights/{id}/check-availability")
    ApiResponse<Map<String, Boolean>> checkAvailability(@PathVariable("id") Long id);
    
    @PostMapping("/api/flights/{id}/reserve")
    ApiResponse<Void> reserveSeat(@PathVariable("id") Long id);
}

