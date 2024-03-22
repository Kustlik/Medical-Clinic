package com.kustlik.medicalclinic.exception;

public class PatientExistsException extends RuntimeException {
    public PatientExistsException(String message) {
        super(message);
    }
}
