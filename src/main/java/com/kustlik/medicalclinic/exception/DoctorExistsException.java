package com.kustlik.medicalclinic.exception;

public class DoctorExistsException extends RuntimeException {
    public DoctorExistsException(String message) {
        super(message);
    }
}
