package com.black.bmitracker.domain.exception;

public class InvalidMeasurementException extends RuntimeException {
    public InvalidMeasurementException(String message) {
        super(message);
    }
}