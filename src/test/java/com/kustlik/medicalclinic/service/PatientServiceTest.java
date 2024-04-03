package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.factory.PatientFactory;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.repository.PatientRepository;
import com.kustlik.medicalclinic.service.validator.PatientValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PatientServiceTest {
    private PatientRepository patientRepository;
    private PatientValidator patientValidator;
    private PatientService patientService;

    @BeforeEach
    void setup() {
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.patientValidator = Mockito.mock(PatientValidator.class);
        this.patientService = new PatientServiceImpl(patientRepository, patientValidator);
    }

    @Test
    void getPatients_NoPatientExists_PageOfPatientReturned() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Patient> patients = new ArrayList<>();
        Page<Patient> patientPage = new PageImpl<>(patients);
        when(patientRepository.findAll(pageable)).thenReturn(patientPage);
        // When
        var result = patientService.getPatients(pageable);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Collections.EMPTY_LIST, result.getContent());
    }

    @Test
    void getPatients_PatientsExists_PageOfPatientReturned() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Patient> patients = new ArrayList<>();
        Patient patient = PatientFactory.getPatient();
        patients.add(patient);
        Page<Patient> patientPage = new PageImpl<>(patients);
        when(patientRepository.findAll(pageable)).thenReturn(patientPage);
        // When
        var result = patientService.getPatients(pageable);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(patients, result.getContent());
    }

    @Test
    void getPatient_PatientExists_PatientReturned() {
        // Given
        Patient patient = PatientFactory.getPatient();
        when(patientValidator.patientExists(patient.getEmail())).thenReturn(patient);
        // When
        var result = patientService.getPatient(patient.getEmail());
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(patient, result);
    }

    @Test
    void createPatient_PatientWithSameEmailDoesNotExist_PatientReturned() {
        // Given
        Patient patient = PatientFactory.getPatient();
        when(patientRepository.save(patient)).thenReturn(patient);
        // When
        var result = patientService.createPatient(patient);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(patient, result);
    }

    @Test
    void deletePatient_PatientWithGivenEmailDoesExists_ExceptionIsNotThrown() {
        // Given
        Patient patient = PatientFactory.getPatient();
        String email = patient.getEmail();
        when(patientValidator.patientExists(email)).thenReturn(patient);
        //Then
        Assertions.assertDoesNotThrow(() ->
                patientService.deletePatient(email));
    }

    @Test
    void editPatient_PatientWithValidDataIsGivenToEdit_PatientReturnedAndUpdated() {
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
        when(patientValidator.patientExists(email)).thenReturn(patient);
        when(patientRepository.save(any())).thenReturn(patient);
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
    void editPatientPassword_PatientWithValidDataIsGivenToEdit_PatientPasswordUpdated() {
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
        when(patientValidator.patientExists(email)).thenReturn(patient);
        //When
        patientService.editPatientPassword(email, toEdit);
        //Then
        Assertions.assertEquals(toEdit.getPassword(), patient.getPassword());
    }
}
