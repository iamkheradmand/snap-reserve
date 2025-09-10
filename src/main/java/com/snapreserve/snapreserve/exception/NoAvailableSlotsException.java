package com.snapreserve.snapreserve.exception;

public class NoAvailableSlotsException extends RuntimeException {
    public NoAvailableSlotsException(String message) {
        super(message);
    }
}
