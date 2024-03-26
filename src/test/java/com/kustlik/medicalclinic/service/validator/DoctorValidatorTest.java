package com.kustlik.medicalclinic.service.validator;

import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.factory.DoctorFactory;
import com.kustlik.medicalclinic.factory.MedicalFacilityFactory;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class DoctorValidatorTest {
    DoctorRepository doctorRepository;
    DoctorValidator doctorValidator;

    @BeforeEach
    void setup() {
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.doctorValidator = new DoctorValidator(doctorRepository);
    }

    @Test
    void validateDoctorCreation_NullDoctorIsGiven_EmptyFieldExceptionThrown() {
        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> doctorValidator.validateDoctorCreation(null));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateDoctorCreation_DoctorWithSomeEmptyFieldsIsGiven_EmptyFieldExceptionThrown() {
        // Given
        Doctor doctor = DoctorFactory.getDoctor(
                1L,
                "jankow@gmail.com",
                "Jan",
                null,
                "password123",
                null,
                null);
        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> doctorValidator.validateDoctorCreation(doctor));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateDoctorCreation_DoctorWithSameEmailDoesExists_DoctorExistsExceptionThrown() {
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.of(doctor));
        // Then
        var exception = Assertions.assertThrows(DoctorExistsException.class,
                () -> doctorValidator.validateDoctorCreation(doctor));
        String expectedMessage = "Doctor with given email exists.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateDoctorCreation_DoctorWithSameEmailDoesNotExist_NoExceptionThrown() {
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.empty());

        // When
        doctorValidator.validateDoctorCreation(doctor);
    }

    @Test
    void validateDoctorToMedicalFacilityAssignment_AssignmentIsPresent_DoctorToMedicalFacilityAssignmentExistsExceptionThrown() {
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        doctor.getMedicalFacilities().add(medicalFacility);
        medicalFacility.getDoctors().add(doctor);
        // Then
        var exception = Assertions.assertThrows(DoctorToMedicalFacilityAssignmentExistsException.class,
                () -> doctorValidator.validateDoctorToMedicalFacilityAssignment(doctor, medicalFacility));
        String expectedMessage = "Doctor is already assigned to this facility.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateDoctorToMedicalFacilityAssignment_AssignmentIsNotPresent_NoExceptionThrown() {
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();

        // When
        doctorValidator.validateDoctorToMedicalFacilityAssignment(doctor, medicalFacility);
    }

    @Test
    void validateDoctor_DoctorWithNullFieldsIsGiven_FalseReturned() {
        // Given
        Doctor doctor = DoctorFactory.getDoctor(
                1L,
                "jankow@gmail.com",
                "Jan",
                null,
                "password123",
                null,
                null);
        // When
        var result = doctorValidator.validateDoctor(doctor);
        // Then
        Assertions.assertFalse(result);
    }

    @Test
    void validateDoctor_DoctorWithValidFieldsIsGiven_TrueReturned() {
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        // When
        var result = doctorValidator.validateDoctor(doctor);
        // Then
        Assertions.assertTrue(result);
    }

    @Test
    void doctorExists_DoctorIdGivenIsNull_EmptyFieldExceptionExceptionThrown() {
        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> doctorValidator.doctorExists((Long) null));
        String expectedMessage = "Doctor ID cannot be empty.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void doctorExists_DoctorWithGivenIdDoesNotExist_DoctorDoesNotExistExceptionThrown() {
        // Given
        Long doctorID = 1L;
        when(doctorRepository.findById(doctorID)).thenReturn(Optional.empty());
        // Then
        var exception = Assertions.assertThrows(DoctorDoesNotExistException.class,
                () -> doctorValidator.doctorExists(doctorID));
        String expectedMessage = "Doctor with given ID does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void doctorExists_DoctorWithGivenIdDoesExists_DoctorReturned() {
        // Given
        Long doctorID = 1L;
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorRepository.findById(doctorID)).thenReturn(Optional.of(doctor));
        // When
        var result = doctorValidator.doctorExists(doctorID);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(doctor, result);
    }

    @Test
    void doctorExists_DoctorWithGivenEmailDoesNotExist_DoctorDoesNotExistExceptionThrown() {
        // Given
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // Then
        var exception = Assertions.assertThrows(DoctorDoesNotExistException.class,
                () -> doctorValidator.doctorExists(anyString()));
        String expectedMessage = "Doctor not found.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void doctorExists_DoctorWithGivenEmailDoesExists_DoctorReturned() {
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.of(doctor));
        // When
        var result = doctorValidator.doctorExists(anyString());
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(doctor, result);
    }
}
