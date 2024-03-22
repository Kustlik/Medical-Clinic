package com.kustlik.medicalclinic.error;

import com.kustlik.medicalclinic.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    public RestResponseEntityExceptionHandler() {
        super();
    }

    @ExceptionHandler(value = {EmptyFieldException.class})
    protected ResponseEntity<Object> handleNullData(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "All valid fields should be properly filled.";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {PatientDoesNotExistException.class})
    protected ResponseEntity<Object> handlePatientDoesNotExist(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Patient does not exist.";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {PatientExistsException.class})
    protected ResponseEntity<Object> handlePatientExists(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Patient already exists.";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {DoctorDoesNotExistException.class})
    protected ResponseEntity<Object> handleDoctorDoesNotExist(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Doctor does not exist.";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {DoctorExistsException.class})
    protected ResponseEntity<Object> handleDoctorExists(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Doctor already exists.";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {MedicalFacilityDoesNotExistException.class})
    protected ResponseEntity<Object> handleMedicalFacilityDoesNotExist(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Medical facility does not exist.";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {MedicalFacilityExistsException.class})
    protected ResponseEntity<Object> handleMedicalFacilityExists(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Medical facility already exists.";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {DoctorToMedicalFacilityAssignmentDoesNotExistsException.class})
    protected ResponseEntity<Object> handleDoctorToMedicalFacilityAssignmentDoesNotExists(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Doctor is not assigned to this facility.";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {DoctorToMedicalFacilityAssignmentExistsException.class})
    protected ResponseEntity<Object> handleDoctorToMedicalFacilityAssignmentExists(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Doctor is already assigned to this facility.";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {VisitWithNoAssignedDoctorException.class})
    protected ResponseEntity<Object> handleVisitWithNoAssignedDoctor(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
