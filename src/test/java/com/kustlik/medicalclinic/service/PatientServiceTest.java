package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.exception.EmptyFieldException;
import com.kustlik.medicalclinic.exception.PatientDoesNotExistException;
import com.kustlik.medicalclinic.exception.PatientExistsException;
import com.kustlik.medicalclinic.factory.PatientFactory;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.repository.PatientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class PatientServiceTest {
    private PatientRepository patientRepository;
    private PatientService patientService;

    @BeforeEach
    void setup(){
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.patientService = new PatientServiceImpl(patientRepository);
    }

    @Test
    void getPatients_ListOfPatientExists_ListOfPatientReturned(){
        // Given
        List<Patient> patients = new ArrayList<>();
        when(patientRepository.findAll()).thenReturn(patients);

        // When
        var result = patientService.getPatients();

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Collections.EMPTY_LIST, result);
    }

    @Test
    void getPatients_PatientsExists_ListOfPatientReturned(){
        // Given
        List<Patient> patients = new ArrayList<>();
        Patient patient = PatientFactory.getPatient();
        patients.add(patient);
        when(patientRepository.findAll()).thenReturn(patients);

        // When
        var result = patientService.getPatients();

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(patients.contains(patient));
    }

    @Test
    void createPatient_NullPatientIsGiven_EmptyFieldExceptionThrown(){
        // Given

        // When

        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientService.createPatient(null));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createPatient_PatientWithSomeEmptyFieldsIsGiven_EmptyFieldExceptionThrown(){
        // Given
        Patient patient = PatientFactory.getPatient(
                1L,
                "jankow@gmail.com",
                null,
                "Jan",
                null,
                "password123",
                null);

        // When

        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientService.createPatient(patient));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createPatient_PatientWithSameEmailDoesExists_PatientExistsExceptionThrown(){
        // Given
        Patient patient = PatientFactory.getPatient();
        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.of(patient));

        // When

        // Then
        var exception = Assertions.assertThrows(PatientExistsException.class,
                () -> patientService.createPatient(patient));
        String expectedMessage = "Patient with given email exists.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createPatient_PatientWithSameEmailDoesNotExist_PatientReturned(){
        // Given
        Patient patient = PatientFactory.getPatient();
        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.save(patient)).thenReturn(patient);

        // When
        var result = patientService.createPatient(patient);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(patient, result);
    }

    @Test
    void deletePatient_NullEmailIsGiven_PatientDoesNotExistExceptionThrown(){
        // Given
        when(patientRepository.findByEmail(null)).thenReturn(Optional.empty());

        //When

        //Then
        var exception = Assertions.assertThrows(PatientDoesNotExistException.class,
                () -> patientService.deletePatient(null));
        String expectedMessage = "Patient with given email does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void deletePatient_PatientWithGivenEmailDoesNotExist_PatientDoesNotExistExceptionThrown(){
        // Given
        String email = PatientFactory.getPatient().getEmail();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());

        //When

        //Then
        var exception = Assertions.assertThrows(PatientDoesNotExistException.class,
                () -> patientService.deletePatient(email));
        String expectedMessage = "Patient with given email does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void deletePatient_PatientWithGivenEmailDoesExists_ExceptionIsNotThrown(){
        // Given
        Patient patient = PatientFactory.getPatient();
        String email = patient.getEmail();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));

        //When

        //Then
        Assertions.assertDoesNotThrow(() ->
                patientService.deletePatient(email));
    }

    @Test
    void editPatient_PatientWithGivenEmailDoesNotExist_PatientDoesNotExistExceptionThrown(){
        // Given
        String email = PatientFactory.getPatient().getEmail();
        Patient toEdit = PatientFactory.getPatient(
                null,
                "karkow@gmail.com",
                null,
                "Karol",
                "Kowalski",
                null,
                LocalDate.of(2000, 1, 1)
        );
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());

        //When

        //Then
        var exception = Assertions.assertThrows(PatientDoesNotExistException.class,
                () -> patientService.editPatient(email, toEdit));
        String expectedMessage = "Patient with given email does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void editPatient_NullEmailIsGivenToEdit_PatientDoesNotExistExceptionThrown(){
        // Given
        Patient toEdit = PatientFactory.getPatient(
                null,
                "karkow@gmail.com",
                null,
                "Karol",
                "Kowalski",
                null,
                LocalDate.of(2000, 1, 1)
        );
        when(patientRepository.findByEmail(null)).thenReturn(Optional.empty());

        //When

        //Then
        var exception = Assertions.assertThrows(PatientDoesNotExistException.class,
                () -> patientService.editPatient(null, toEdit));
        String expectedMessage = "Patient with given email does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void editPatient_NullPatientIsGivenToEdit_PatientDoesNotExistExceptionThrown(){
        // Given
        Patient patient = PatientFactory.getPatient();
        String email = patient.getEmail();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));

        //When

        //Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientService.editPatient(email, null));
        String expectedMessage = "No empty argument to edit is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void editPatient_PatientWithSomeEmptyFieldsIsGiven_EmptyFieldExceptionThrown(){
        // Given
        Patient patient = PatientFactory.getPatient();
        String email = patient.getEmail();
        Patient toEdit = PatientFactory.getPatient(
                null,
                null,
                null,
                "Karol",
                "Grzybek",
                "pass123",
                null
        );
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));

        //When

        //Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientService.editPatient(email, toEdit));
        String expectedMessage = "No empty argument to edit is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void editPatient_PatientWithNewEmailGivenToEditIsPresent_PatientExistsExceptionThrown(){
        // Given
        Patient patientToEdit = PatientFactory.getPatient();
        String patientToEditEmail = patientToEdit.getEmail();
        Patient patientExistingInRepository = PatientFactory.getPatient(
                2L,
                "karkow@gmail.com",
                "12345abcd",
                "Adam",
                "Koński",
                "admin123",
                LocalDate.of(1990, 1, 1)
        );
        Patient dataToEdit = PatientFactory.getPatient(
                null,
                "karkow@gmail.com",
                null,
                "Karol",
                "Kowalski",
                null,
                LocalDate.of(2000, 1, 1)
        );
        when(patientRepository.findByEmail("jankow@gmail.com")).thenReturn(Optional.of(patientToEdit));
        when(patientRepository.findByEmail("karkow@gmail.com")).thenReturn(Optional.of(patientExistingInRepository));

        //When

        //Then
        var exception = Assertions.assertThrows(PatientExistsException.class,
                () -> patientService.editPatient(patientToEditEmail, dataToEdit));
        String expectedMessage = "New email is not available.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void editPatient_PatientWithValidDataIsGivenToEdit_PatientReturnedAndUpdated(){
        // Given
        Patient patient = PatientFactory.getPatient();
        String email = patient.getEmail();
        Patient toEdit = PatientFactory.getPatient(
                null,
                "karkow@gmail.com",
                null,
                "Karol",
                "Kowalski",
                null,
                LocalDate.of(2000, 1, 1)
        );
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));
        when(patientRepository.findByEmail(toEdit.getEmail())).thenReturn(Optional.empty());

        //When
        var result = patientService.editPatient(email, toEdit);

        //Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(toEdit.getEmail(), result.getEmail());
        Assertions.assertEquals(toEdit.getFirstName(), result.getFirstName());
        Assertions.assertEquals(toEdit.getLastName(), result.getLastName());
        Assertions.assertEquals(toEdit.getBirthday(), result.getBirthday());
    }

    @Test
    void editPatientPassword_NullEmailIsGiven_PatientDoesNotExistExceptionThrown(){
        // Given
        Patient toEdit = PatientFactory.getPatient(
                null,
                null,
                null,
                null,
                null,
                "admin123",
                null
        );
        when(patientRepository.findByEmail(null)).thenReturn(Optional.empty());

        //When

        //Then
        var exception = Assertions.assertThrows(PatientDoesNotExistException.class,
                () -> patientService.editPatientPassword(null, toEdit));
        String expectedMessage = "Patient with given email does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void editPatientPassword_NullPatientIsGivenToEdit_EmptyFieldExceptionThrown(){
        // Given
        Patient patient = PatientFactory.getPatient();
        String email = patient.getEmail();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));

        //When

        //Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientService.editPatientPassword(email, null));
        String expectedMessage = "Password is empty.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void editPatientPassword_NullPasswordIsGivenToEdit_EmptyFieldExceptionThrown(){
        // Given
        Patient patient = PatientFactory.getPatient();
        String email = patient.getEmail();
        Patient toEdit = PatientFactory.getPatient(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));

        //When

        //Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientService.editPatientPassword(email, toEdit));
        String expectedMessage = "Password is empty.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void editPatientPassword_BlankPasswordIsGivenToEdit_EmptyFieldExceptionThrown(){
        // Given
        Patient patient = PatientFactory.getPatient();
        String email = patient.getEmail();
        Patient toEdit = PatientFactory.getPatient(
                null,
                null,
                null,
                null,
                null,
                " ",
                null
        );
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));

        //When

        //Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> patientService.editPatientPassword(email, toEdit));
        String expectedMessage = "Password is empty.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void editPatientPassword_PatientWithGivenEmailDoesNotExist_PatientDoesNotExistExceptionThrown(){
        // Given
        String email = PatientFactory.getPatient().getEmail();
        Patient toEdit = PatientFactory.getPatient(
                null,
                null,
                null,
                null,
                null,
                "admin123",
                null
        );
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());

        //When

        //Then
        var exception = Assertions.assertThrows(PatientDoesNotExistException.class,
                () -> patientService.editPatientPassword(email, toEdit));
        String expectedMessage = "Patient with given email does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void editPatientPassword_PatientWithValidDataIsGivenToEdit_PatientPasswordUpdated(){
        // Given
        Patient patient = PatientFactory.getPatient();
        String email = patient.getEmail();
        Patient toEdit = PatientFactory.getPatient(
                null,
                null,
                null,
                null,
                null,
                "admin123",
                null
        );
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));

        //When
        patientService.editPatientPassword(email, toEdit);

        //Then
        Assertions.assertEquals(toEdit.getPassword(), patient.getPassword());
    }
}
