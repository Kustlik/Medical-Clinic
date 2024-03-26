package com.kustlik.medicalclinic.error;

import com.kustlik.medicalclinic.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    public RestResponseEntityExceptionHandler() {
        super();
    }

    @ExceptionHandler(value = {EmptyFieldException.class})
    protected ResponseEntity<Object> handleNullData(final EmptyFieldException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = {PatientDoesNotExistException.class})
    protected ResponseEntity<Object> handlePatientDoesNotExist(final PatientDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value = {PatientExistsException.class})
    protected ResponseEntity<Object> handlePatientExists(final PatientExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = {DoctorDoesNotExistException.class})
    protected ResponseEntity<Object> handleDoctorDoesNotExist(final DoctorDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value = {DoctorExistsException.class})
    protected ResponseEntity<Object> handleDoctorExists(final DoctorExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = {MedicalFacilityDoesNotExistException.class})
    protected ResponseEntity<Object> handleMedicalFacilityDoesNotExist(final MedicalFacilityDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value = {MedicalFacilityExistsException.class})
    protected ResponseEntity<Object> handleMedicalFacilityExists(final MedicalFacilityExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = {DoctorToMedicalFacilityAssignmentDoesNotExistsException.class})
    protected ResponseEntity<Object> handleDoctorToMedicalFacilityAssignmentDoesNotExists(final DoctorToMedicalFacilityAssignmentDoesNotExistsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value = {DoctorToMedicalFacilityAssignmentExistsException.class})
    protected ResponseEntity<Object> handleDoctorToMedicalFacilityAssignmentExists(final DoctorToMedicalFacilityAssignmentExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = {VisitWithNoAssignedDoctorException.class})
    protected ResponseEntity<Object> handleVisitWithNoAssignedDoctor(final VisitWithNoAssignedDoctorException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value = {VisitExistsException.class})
    protected ResponseEntity<Object> handleVisitExists(final VisitExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = {VisitDoesNotExistException.class})
    protected ResponseEntity<Object> handleVisitDoesNotExist(final VisitDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value = {InvalidDateTimeException.class})
    protected ResponseEntity<Object> handleInvalidDateTime(final InvalidDateTimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = {NoSuchOptionException.class})
    protected ResponseEntity<Object> handleNoSuchOption(final NoSuchOptionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
