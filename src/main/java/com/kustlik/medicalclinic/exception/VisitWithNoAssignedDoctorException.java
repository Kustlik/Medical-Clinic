package com.kustlik.medicalclinic.exception;

public class VisitWithNoAssignedDoctorException extends RuntimeException {
    public VisitWithNoAssignedDoctorException(String message) {
        super(message);
    }
}
