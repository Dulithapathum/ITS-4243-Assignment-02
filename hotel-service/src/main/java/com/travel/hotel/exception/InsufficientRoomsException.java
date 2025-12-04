package com.travel.hotel.exception;

public class InsufficientRoomsException extends RuntimeException {
    public InsufficientRoomsException(String message) {
        super(message);
    }
}

