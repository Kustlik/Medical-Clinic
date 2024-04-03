package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.factory.DoctorFactory;
import com.kustlik.medicalclinic.factory.MedicalFacilityFactory;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import com.kustlik.medicalclinic.repository.MedicalFacilityRepository;
import com.kustlik.medicalclinic.service.validator.DoctorValidator;
import com.kustlik.medicalclinic.service.validator.MedicalFacilityValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class MedicalFacilityServiceTest {
    private MedicalFacilityRepository medicalFacilityRepository;
    private MedicalFacilityValidator medicalFacilityValidator;
    private DoctorRepository doctorRepository;
    private DoctorValidator doctorValidator;
    private MedicalFacilityService medicalFacilityService;

    @BeforeEach
    void setup() {
        this.medicalFacilityRepository = Mockito.mock(MedicalFacilityRepository.class);
        this.medicalFacilityValidator = Mockito.mock(MedicalFacilityValidator.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.doctorValidator = Mockito.mock(DoctorValidator.class);
        this.medicalFacilityService = new MedicalFacilityServiceImpl(medicalFacilityRepository, medicalFacilityValidator, doctorRepository, doctorValidator);
    }

    @Test
    void getMedicalFacilities_NoMedicalFacilityExists_PageOfMedicalFacilityReturned() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<MedicalFacility> medicalFacilities = new ArrayList<>();
        Page<MedicalFacility> medicalFacilityPage = new PageImpl<>(medicalFacilities);
        when(medicalFacilityRepository.findAll(pageable)).thenReturn(medicalFacilityPage);
        // When
        var result = medicalFacilityService.getMedicalFacilities(pageable);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Collections.EMPTY_LIST, result.getContent());
    }

    @Test
    void getMedicalFacilities_MedicalFacilitiesExists_PageOfMedicalFacilityReturned() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<MedicalFacility> medicalFacilities = new ArrayList<>();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        medicalFacilities.add(medicalFacility);
        Page<MedicalFacility> medicalFacilityPage = new PageImpl<>(medicalFacilities);
        when(medicalFacilityRepository.findAll(pageable)).thenReturn(medicalFacilityPage);
        // When
        var result = medicalFacilityService.getMedicalFacilities(pageable);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(medicalFacilities, result.getContent());
    }

    @Test
    void getMedicalFacility_MedicalFacilityExists_MedicalFacilityReturned() {
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(medicalFacilityValidator.medicalFacilityExists(anyLong())).thenReturn(medicalFacility);
        // When
        var result = medicalFacilityService.getMedicalFacility(anyLong());
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(medicalFacility, result);
    }

    @Test
    void createMedicalFacility_MedicalFacilityWithSameIdDoesNotExist_MedicalFacilityReturned() {
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(medicalFacilityRepository.save(medicalFacility)).thenReturn(medicalFacility);
        // When
        var result = medicalFacilityService.createMedicalFacility(medicalFacility);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(medicalFacility, result);
    }

    @Test
    void assignMedicalFacilityToDoctor_AssignmentIsNotPresent_MedicalFacilityWithAssignedDoctorReturned() {
        // Given
        Long doctorID = 1L;
        Long medicalFacilityID = 1L;
        Doctor doctor = DoctorFactory.getDoctor();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(doctorValidator.doctorExists(doctorID)).thenReturn(doctor);
        when(medicalFacilityValidator.medicalFacilityExists(medicalFacilityID)).thenReturn(medicalFacility);
        when(doctorRepository.save(any())).thenReturn(doctor);
        // When
        var result = medicalFacilityService.assignMedicalFacilityToDoctor(medicalFacilityID, doctorID);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(doctor.getMedicalFacilities().contains(medicalFacility));
    }
}
