package com.travel.hotel.service;

import com.travel.hotel.dto.HotelDTO;
import com.travel.hotel.entity.Hotel;
import com.travel.hotel.exception.ResourceNotFoundException;
import com.travel.hotel.exception.InsufficientRoomsException;
import com.travel.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {
    
    private final HotelRepository hotelRepository;
    
    @Transactional
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        Hotel hotel = mapToEntity(hotelDTO);
        hotel.setAvailable(hotel.getAvailableRooms() > 0);
        Hotel savedHotel = hotelRepository.save(hotel);
        return mapToDTO(savedHotel);
    }
    
    public HotelDTO getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        return mapToDTO(hotel);
    }
    
    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<HotelDTO> getAvailableHotels() {
        return hotelRepository.findByAvailableTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<HotelDTO> searchHotelsByLocation(String location) {
        return hotelRepository.findByLocation(location).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public HotelDTO updateHotel(Long id, HotelDTO hotelDTO) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        
        hotel.setName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setAddress(hotelDTO.getAddress());
        hotel.setStarRating(hotelDTO.getStarRating());
        hotel.setPricePerNight(hotelDTO.getPricePerNight());
        hotel.setAvailableRooms(hotelDTO.getAvailableRooms());
        hotel.setTotalRooms(hotelDTO.getTotalRooms());
        hotel.setAmenities(hotelDTO.getAmenities());
        hotel.setAvailable(hotelDTO.getAvailableRooms() > 0);
        
        Hotel updatedHotel = hotelRepository.save(hotel);
        return mapToDTO(updatedHotel);
    }
    
    @Transactional
    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hotel not found with id: " + id);
        }
        hotelRepository.deleteById(id);
    }
    
    @Transactional
    public boolean checkAvailability(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        return hotel.getAvailable() && hotel.getAvailableRooms() > 0;
    }
    
    @Transactional
    public void reserveRoom(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        
        if (hotel.getAvailableRooms() <= 0) {
            throw new InsufficientRoomsException("No rooms available for hotel: " + hotelId);
        }
        
        hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
        hotel.setAvailable(hotel.getAvailableRooms() > 0);
        hotelRepository.save(hotel);
    }
    
    private HotelDTO mapToDTO(Hotel hotel) {
        return new HotelDTO(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getAddress(),
                hotel.getStarRating(),
                hotel.getPricePerNight(),
                hotel.getAvailableRooms(),
                hotel.getTotalRooms(),
                hotel.getAmenities(),
                hotel.getAvailable()
        );
    }
    
    private Hotel mapToEntity(HotelDTO dto) {
        Hotel hotel = new Hotel();
        hotel.setId(dto.getId());
        hotel.setName(dto.getName());
        hotel.setLocation(dto.getLocation());
        hotel.setAddress(dto.getAddress());
        hotel.setStarRating(dto.getStarRating());
        hotel.setPricePerNight(dto.getPricePerNight());
        hotel.setAvailableRooms(dto.getAvailableRooms());
        hotel.setTotalRooms(dto.getTotalRooms());
        hotel.setAmenities(dto.getAmenities());
        hotel.setAvailable(dto.getAvailable() != null ? dto.getAvailable() : true);
        return hotel;
    }
}

