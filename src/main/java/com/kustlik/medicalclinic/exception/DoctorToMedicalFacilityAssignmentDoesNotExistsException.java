package com.kustlik.medicalclinic.exception;

public class DoctorToMedicalFacilityAssignmentDoesNotExistsException extends RuntimeException {
    public DoctorToMedicalFacilityAssignmentDoesNotExistsException(String message) {
        super(message);
    }
}
