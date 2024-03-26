package com.kustlik.medicalclinic.exception;

public class DoctorToMedicalFacilityAssignmentExistsException extends RuntimeException {
    public DoctorToMedicalFacilityAssignmentExistsException(String message) {
        super(message);
    }
}
