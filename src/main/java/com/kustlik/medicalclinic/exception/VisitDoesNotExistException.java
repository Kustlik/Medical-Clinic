package com.kustlik.medicalclinic.exception;

public class VisitDoesNotExistException extends RuntimeException {
    public VisitDoesNotExistException(String message) {
        super(message);
    }
}
