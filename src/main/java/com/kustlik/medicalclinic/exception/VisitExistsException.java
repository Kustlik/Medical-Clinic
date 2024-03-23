package com.kustlik.medicalclinic.exception;

public class VisitExistsException extends RuntimeException {
    public VisitExistsException(String message) {
        super(message);
    }
}
