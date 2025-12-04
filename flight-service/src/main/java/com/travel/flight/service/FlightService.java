package com.travel.flight.service;

import com.travel.flight.dto.FlightDTO;
import com.travel.flight.entity.Flight;
import com.travel.flight.exception.ResourceNotFoundException;
import com.travel.flight.exception.InsufficientSeatsException;
import com.travel.flight.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {
    
    private final FlightRepository flightRepository;
    
    @Transactional
    public FlightDTO createFlight(FlightDTO flightDTO) {
        Flight flight = mapToEntity(flightDTO);
        flight.setAvailable(flight.getAvailableSeats() > 0);
        Flight savedFlight = flightRepository.save(flight);
        return mapToDTO(savedFlight);
    }
    
    public FlightDTO getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
        return mapToDTO(flight);
    }
    
    public List<FlightDTO> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<FlightDTO> getAvailableFlights() {
        return flightRepository.findByAvailableTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<FlightDTO> searchFlights(String origin, String destination) {
        return flightRepository.findByOriginAndDestination(origin, destination).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public FlightDTO updateFlight(Long id, FlightDTO flightDTO) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
        
        flight.setFlightNumber(flightDTO.getFlightNumber());
        flight.setAirline(flightDTO.getAirline());
        flight.setOrigin(flightDTO.getOrigin());
        flight.setDestination(flightDTO.getDestination());
        flight.setDepartureTime(flightDTO.getDepartureTime());
        flight.setArrivalTime(flightDTO.getArrivalTime());
        flight.setPrice(flightDTO.getPrice());
        flight.setAvailableSeats(flightDTO.getAvailableSeats());
        flight.setTotalSeats(flightDTO.getTotalSeats());
        flight.setAvailable(flightDTO.getAvailableSeats() > 0);
        
        Flight updatedFlight = flightRepository.save(flight);
        return mapToDTO(updatedFlight);
    }
    
    @Transactional
    public void deleteFlight(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new ResourceNotFoundException("Flight not found with id: " + id);
        }
        flightRepository.deleteById(id);
    }
    
    @Transactional
    public boolean checkAvailability(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + flightId));
        return flight.getAvailable() && flight.getAvailableSeats() > 0;
    }
    
    @Transactional
    public void reserveSeat(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + flightId));
        
        if (flight.getAvailableSeats() <= 0) {
            throw new InsufficientSeatsException("No seats available for flight: " + flightId);
        }
        
        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        flight.setAvailable(flight.getAvailableSeats() > 0);
        flightRepository.save(flight);
    }
    
    private FlightDTO mapToDTO(Flight flight) {
        return new FlightDTO(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getAirline(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.getAvailableSeats(),
                flight.getTotalSeats(),
                flight.getAvailable()
        );
    }
    
    private Flight mapToEntity(FlightDTO dto) {
        Flight flight = new Flight();
        flight.setId(dto.getId());
        flight.setFlightNumber(dto.getFlightNumber());
        flight.setAirline(dto.getAirline());
        flight.setOrigin(dto.getOrigin());
        flight.setDestination(dto.getDestination());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setPrice(dto.getPrice());
        flight.setAvailableSeats(dto.getAvailableSeats());
        flight.setTotalSeats(dto.getTotalSeats());
        flight.setAvailable(dto.getAvailable() != null ? dto.getAvailable() : true);
        return flight;
    }
}

