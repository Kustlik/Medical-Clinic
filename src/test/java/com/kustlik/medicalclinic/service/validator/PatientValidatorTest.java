package com.kustlik.medicalclinic.service.validator;

import com.kustlik.medicalclinic.exception.PatientDoesNotExistException;
import com.kustlik.medicalclinic.exception.PatientExistsException;
import com.kustlik.medicalclinic.exception.EmptyFieldException;
import com.kustlik.medicalclinic.factory.PatientFactory;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.repository.PatientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PatientValidatorTest {
    PatientRepository patientRepository;
    PatientValidator patientValidator;

    @BeforeEach
    void setup() {
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.patientValidator = new PatientValidator(patientRepository);
    }

    @Test
    void validatePatientCreation_NullPatientIsGiven_EmptyFieldExceptionThrown() {
        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientValidator.validatePatientCreation(null));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validatePatientCreation_PatientWithSomeEmptyFieldsIsGiven_EmptyFieldExceptionThrown() {
        // Given
        Patient patient = PatientFactory.getPatient(
                1L,
                "jankow@gmail.com",
                "123da",
                null,
                "Kowalski",
                null,
                null);
        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientValidator.validatePatientCreation(patient));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validatePatientCreation_PatientWithSameEmailDoesExists_PatientExistsExceptionThrown() {
        // Given
        Patient patient = PatientFactory.getPatient();
        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.of(patient));
        // Then
        var exception = Assertions.assertThrows(PatientExistsException.class,
                () -> patientValidator.validatePatientCreation(patient));
        String expectedMessage = "Patient with given email exists.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validatePatientCreation_PatientWithSameEmailDoesNotExist_NoExceptionThrown() {
        // Given
        Patient patient = PatientFactory.getPatient();
        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.empty());
        // When
        patientValidator.validatePatientCreation(patient);
    }

    @Test
    void validateEditPatientData_PatientWithSomeEmptyFieldsIsGiven_EmptyFieldExceptionThrown() {
        // Given
        Patient patient = PatientFactory.getPatient(
                1L,
                "jankow@gmail.com",
                "123da",
                null,
                "Kowalski",
                null,
                null);
        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientValidator.validateEditPatientData(patient.getEmail(), patient));
        String expectedMessage = "No empty argument to edit is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateEditPatientData_PatientWithSameEmailDoesExists_PatientExistsExceptionThrown() {
        // Given
        Patient patient = PatientFactory.getPatient();
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.of(patient));
        // Then
        var exception = Assertions.assertThrows(PatientExistsException.class,
                () -> patientValidator.validateEditPatientData(anyString(), patient));
        String expectedMessage = "Patient with given email exists.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateEditPatientData_PatientWithValidDataToEditIsGiven_NoExceptionThrown() {
        // Given
        Patient patient = PatientFactory.getPatient();
        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.empty());
        // When
        patientValidator.validateEditPatientData(patient.getEmail(), patient);
    }

    @Test
    void validatePasswordChange_PasswordToEditIsBlank_EmptyFieldExceptionThrown() {
        // Given
        Patient patient = PatientFactory.getPatient(
                null,
                "jankow@gmail.com",
                null,
                null,
                null,
                null,
                null);
        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientValidator.validatePasswordChange(patient));
        String expectedMessage = "Password is empty.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validatePasswordChange_ValidPasswordToChangeIsGiven_NoExceptionThrown() {
        // Given
        Patient patient = PatientFactory.getPatient(
                null,
                "jankow@gmail.com",
                null,
                null,
                null,
                "admin123",
                null);
        // When
        patientValidator.validatePasswordChange(patient);
    }

    @Test
    void validatePatient_PatientWithNullFieldsIsGiven_FalseReturned() {
        // Given
        Patient patient = PatientFactory.getPatient(
                1L,
                "jankow@gmail.com",
                "abc123",
                null,
                "Jankowski",
                null,
                null);
        // When
        var result = patientValidator.validatePatient(patient);
        // Then
        Assertions.assertFalse(result);
    }

    @Test
    void validatePatient_PatientWithValidFieldsIsGiven_TrueReturned() {
        // Given
        Patient patient = PatientFactory.getPatient();
        // When
        var result = patientValidator.validatePatient(patient);
        // Then
        Assertions.assertTrue(result);
    }

    @Test
    void validateEdit_PatientWithNullFieldsIsGiven_FalseReturned() {
        // Given
        Patient patient = PatientFactory.getPatient(
                null,
                "jankow@gmail.com",
                "abc123",
                null,
                "Jankowski",
                null,
                null);
        // When
        var result = patientValidator.validateEdit(patient);
        // Then
        Assertions.assertFalse(result);
    }

    @Test
    void validateEdit_PatientWithValidFieldsIsGiven_TrueReturned() {
        // Given
        Patient patient = PatientFactory.getPatient();
        // When
        var result = patientValidator.validateEdit(patient);
        // Then
        Assertions.assertTrue(result);
    }

    @Test
    void patientExists_PatientWithGivenEmailDoesNotExist_PatientDoesNotExistExceptionThrown() {
        // Given
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // Then
        var exception = Assertions.assertThrows(PatientDoesNotExistException.class,
                () -> patientValidator.patientExists(anyString()));
        String expectedMessage = "Patient with given email does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void patientExists_PatientWithGivenEmailDoesExists_PatientReturned() {
        // Given
        Patient patient = PatientFactory.getPatient();
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.of(patient));
        // When
        var result = patientValidator.patientExists(anyString());
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(patient, result);
    }
}
