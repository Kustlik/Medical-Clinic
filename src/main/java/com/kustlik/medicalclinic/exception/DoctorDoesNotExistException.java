package com.kustlik.medicalclinic.exception;

public class DoctorDoesNotExistException extends RuntimeException {
    public DoctorDoesNotExistException(String message) {
        super(message);
    }
}
