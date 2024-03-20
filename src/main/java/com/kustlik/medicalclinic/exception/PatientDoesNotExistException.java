package com.kustlik.medicalclinic.exception;

public class PatientDoesNotExistException extends RuntimeException{
    public PatientDoesNotExistException(String message) {
        super(message);
    }
}
