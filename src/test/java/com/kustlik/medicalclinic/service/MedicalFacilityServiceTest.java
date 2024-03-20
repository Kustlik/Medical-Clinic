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

public class MedicalFacilityServiceTest {
    private MedicalFacilityRepository medicalFacilityRepository;
    private DoctorRepository doctorRepository;
    private MedicalFacilityService medicalFacilityService;

    @BeforeEach
    void setup(){
        this.medicalFacilityRepository = Mockito.mock(MedicalFacilityRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.medicalFacilityService = new MedicalFacilityServiceImpl(medicalFacilityRepository, doctorRepository);
    }

    @Test
    void getMedicalFacilities_ListOfMedicalFacilityExists_ListOfMedicalFacilityReturned(){
        // Given
        List<MedicalFacility> medicalFacilities = new ArrayList<>();
        when(medicalFacilityRepository.findAll()).thenReturn(medicalFacilities);

        // When
        var result = medicalFacilityService.getMedicalFacilities();

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Collections.EMPTY_LIST, result);
    }

    @Test
    void getMedicalFacilities_MedicalFacilitiesExists_ListOfMedicalFacilityReturned(){
        // Given
        List<MedicalFacility> medicalFacilities = new ArrayList<>();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        medicalFacilities.add(medicalFacility);
        when(medicalFacilityRepository.findAll()).thenReturn(medicalFacilities);

        // When
        var result = medicalFacilityService.getMedicalFacilities();

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(medicalFacilities.contains(medicalFacility));
    }

    @Test
    void createMedicalFacility_NullMedicalFacilityIsGiven_EmptyFieldExceptionThrown(){
        // Given

        // When

        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> medicalFacilityService.createMedicalFacility(null));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createMedicalFacility_MedicalFacilityWithSomeEmptyFieldsIsGiven_EmptyFieldExceptionThrown(){
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility(
                1L,
                "43",
                null,
                "Covermedi",
                null,
                "65-346",
                null);

        // When

        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> medicalFacilityService.createMedicalFacility(medicalFacility));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createMedicalFacility_MedicalFacilityWithSameIdDoesExists_MedicalFacilityExistsExceptionThrown(){
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(medicalFacilityRepository.findByName(medicalFacility.getName())).thenReturn(Optional.of(medicalFacility));

        // When

        // Then
        var exception = Assertions.assertThrows(MedicalFacilityExistsException.class,
                () -> medicalFacilityService.createMedicalFacility(medicalFacility));
        String expectedMessage = "Medical facility with given name exists.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createMedicalFacility_MedicalFacilityWithSameIdDoesNotExist_MedicalFacilityReturned(){
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(medicalFacilityRepository.findByName(medicalFacility.getName())).thenReturn(Optional.empty());
        when(medicalFacilityRepository.save(medicalFacility)).thenReturn(medicalFacility);

        // When
        var result = medicalFacilityService.createMedicalFacility(medicalFacility);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(medicalFacility, result);
    }

    @Test
    void assignMedicalFacilityToDoctor_DoctorWithGivenIdDoesNotExist_DoctorDoesNotExistExceptionThrown(){
        // Given
        Long doctorID = 1L;
        Long medicalFacilityID = 1L;
        when(doctorRepository.findById(doctorID)).thenReturn(Optional.empty());

        // When

        // Then
        var exception = Assertions.assertThrows(DoctorDoesNotExistException.class,
                () -> medicalFacilityService.assignMedicalFacilityToDoctor(medicalFacilityID, doctorID));
        String expectedMessage = "Doctor with given ID does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void assignMedicalFacilityToDoctor_MedicalFacilityWithGivenIdDoesNotExist_MedicalFacilityDoesNotExistExceptionThrown(){
        // Given
        Long doctorID = 1L;
        Long medicalFacilityID = 1L;
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorRepository.findById(doctorID)).thenReturn(Optional.of(doctor));
        when(medicalFacilityRepository.findById(medicalFacilityID)).thenReturn(Optional.empty());

        // When

        // Then
        var exception = Assertions.assertThrows(MedicalFacilityDoesNotExistException.class,
                () -> medicalFacilityService.assignMedicalFacilityToDoctor(medicalFacilityID, doctorID));
        String expectedMessage = "Medical facility with given ID does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void assignMedicalFacilityToDoctor_AssignmentIsPresent_DoctorToMedicalFacilityAssignmentExistsExceptionThrown(){
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
                () -> medicalFacilityService.assignMedicalFacilityToDoctor(doctorID, medicalFacilityID));
        String expectedMessage = "Doctor is already assigned to this facility.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void assignMedicalFacilityToDoctor_AssignmentIsNotPresent_MedicalFacilityWithAssignedDoctorReturned(){
        // Given
        Long doctorID = 1L;
        Long medicalFacilityID = 1L;
        Doctor doctor = DoctorFactory.getDoctor();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(doctorRepository.findById(doctorID)).thenReturn(Optional.of(doctor));
        when(medicalFacilityRepository.findById(medicalFacilityID)).thenReturn(Optional.of(medicalFacility));
        when(doctorRepository.save(any())).thenReturn(doctor);

        // When
        var result = medicalFacilityService.assignMedicalFacilityToDoctor(medicalFacilityID, doctorID);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(doctor.getMedicalFacilities().contains(medicalFacility));
    }
}
