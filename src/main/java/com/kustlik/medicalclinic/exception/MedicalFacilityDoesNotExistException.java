package com.kustlik.medicalclinic.exception;

public class MedicalFacilityDoesNotExistException extends RuntimeException{
    public MedicalFacilityDoesNotExistException(String message) {
        super(message);
    }
}
