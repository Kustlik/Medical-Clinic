package com.kustlik.medicalclinic.exception;

public class MedicalFacilityExistsException extends RuntimeException {
    public MedicalFacilityExistsException(String message) {
        super(message);
    }
}
