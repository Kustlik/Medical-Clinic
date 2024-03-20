package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.factory.DoctorFactory;
import com.kustlik.medicalclinic.factory.MedicalFacilityFactory;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import com.kustlik.medicalclinic.repository.MedicalFacilityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DoctorServiceTest {
    private DoctorRepository doctorRepository;
    private MedicalFacilityRepository medicalFacilityRepository;
    private DoctorService doctorService;

    @BeforeEach
    void setup(){
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.medicalFacilityRepository = Mockito.mock(MedicalFacilityRepository.class);
        this.doctorService = new DoctorServiceImpl(doctorRepository, medicalFacilityRepository);
    }

    @Test
    void getDoctors_ListOfDoctorExists_ListOfDoctorReturned(){
        // Given
        List<Doctor> doctors = new ArrayList<>();
        when(doctorRepository.findAll()).thenReturn(doctors);

        // When
        var result = doctorService.getDoctors();

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Collections.EMPTY_LIST, result);
    }

    @Test
    void getDoctors_DoctorsExists_ListOfDoctorReturned(){
        // Given
        List<Doctor> doctors = new ArrayList<>();
        Doctor doctor = DoctorFactory.getDoctor();
        doctors.add(doctor);
        when(doctorRepository.findAll()).thenReturn(doctors);

        // When
        var result = doctorService.getDoctors();

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(doctors.contains(doctor));
    }

    @Test
    void createDoctor_NullDoctorIsGiven_EmptyFieldExceptionThrown(){
        // Given

        // When

        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> doctorService.createDoctor(null));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createDoctor_DoctorWithSomeEmptyFieldsIsGiven_EmptyFieldExceptionThrown(){
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

        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> doctorService.createDoctor(doctor));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createDoctor_DoctorWithSameEmailDoesExists_DoctorExistsExceptionThrown(){
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.of(doctor));

        // When

        // Then
        var exception = Assertions.assertThrows(DoctorExistsException.class,
                () -> doctorService.createDoctor(doctor));
        String expectedMessage = "Doctor with given email exists.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createDoctor_DoctorWithSameEmailDoesNotExist_DoctorReturned(){
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        // When
        var result = doctorService.createDoctor(doctor);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(doctor, result);
    }

    @Test
    void assignDoctorToMedicalFacility_DoctorWithGivenIdDoesNotExist_DoctorDoesNotExistExceptionThrown(){
        // Given
        Long doctorID = 1L;
        Long medicalFacilityID = 1L;
        when(doctorRepository.findById(doctorID)).thenReturn(Optional.empty());

        // When

        // Then
        var exception = Assertions.assertThrows(DoctorDoesNotExistException.class,
                () -> doctorService.assignDoctorToMedicalFacility(doctorID, medicalFacilityID));
        String expectedMessage = "Doctor with given ID does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void assignDoctorToMedicalFacility_MedicalFacilityWithGivenIdDoesNotExist_MedicalFacilityDoesNotExistExceptionThrown(){
        // Given
        Long doctorID = 1L;
        Long medicalFacilityID = 1L;
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorRepository.findById(doctorID)).thenReturn(Optional.of(doctor));
        when(medicalFacilityRepository.findById(medicalFacilityID)).thenReturn(Optional.empty());

        // When

        // Then
        var exception = Assertions.assertThrows(MedicalFacilityDoesNotExistException.class,
                () -> doctorService.assignDoctorToMedicalFacility(doctorID, medicalFacilityID));
        String expectedMessage = "Medical facility with given ID does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void assignDoctorToMedicalFacility_AssignmentIsPresent_DoctorToMedicalFacilityAssignmentExistsExceptionThrown(){
        // Given
        Long doctorID = 1L;
        Long medicalFacilityID = 1L;
        Doctor doctor = DoctorFactory.getDoctor();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        doctor.getMedicalFacilities().add(medicalFacility);
        medicalFacility.getDoctors().add(doctor);
        when(doctorRepository.findById(doctorID)).thenReturn(Optional.of(doctor));
        when(medicalFacilityRepository.findById(medicalFacilityID)).thenReturn(Optional.of(medicalFacility));

        // When

        // Then
        var exception = Assertions.assertThrows(DoctorToMedicalFacilityAssignmentExistsException.class,
                () -> doctorService.assignDoctorToMedicalFacility(doctorID, medicalFacilityID));
        String expectedMessage = "Doctor is already assigned to this facility.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void assignDoctorToMedicalFacility_AssignmentIsNotPresent_DoctorWithAssignedFacilityReturned(){
        // Given
        Long doctorID = 1L;
        Long medicalFacilityID = 1L;
        Doctor doctor = DoctorFactory.getDoctor();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(doctorRepository.findById(doctorID)).thenReturn(Optional.of(doctor));
        when(medicalFacilityRepository.findById(medicalFacilityID)).thenReturn(Optional.of(medicalFacility));
        when(doctorRepository.save(any())).thenReturn(doctor);

        // When
        var result = doctorService.assignDoctorToMedicalFacility(doctorID, medicalFacilityID);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getMedicalFacilities().contains(medicalFacility));
    }
}
