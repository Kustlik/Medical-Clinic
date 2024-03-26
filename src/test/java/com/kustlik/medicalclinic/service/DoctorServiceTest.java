package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.factory.DoctorFactory;
import com.kustlik.medicalclinic.factory.MedicalFacilityFactory;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import com.kustlik.medicalclinic.service.validator.DoctorValidator;
import com.kustlik.medicalclinic.service.validator.MedicalFacilityValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class DoctorServiceTest {
    private DoctorRepository doctorRepository;
    private DoctorValidator doctorValidator;
    private MedicalFacilityValidator medicalFacilityValidator;
    private DoctorService doctorService;

    @BeforeEach
    void setup() {
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.doctorValidator = Mockito.mock(DoctorValidator.class);
        this.medicalFacilityValidator = Mockito.mock(MedicalFacilityValidator.class);
        this.doctorService = new DoctorServiceImpl(doctorRepository, doctorValidator, medicalFacilityValidator);
    }

    @Test
    void getDoctors_NoDoctorExists_ListOfDoctorReturned() {
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
    void getDoctors_DoctorsExists_ListOfDoctorReturned() {
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
    void getDoctor_DoctorExists_DoctorReturned() {
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorValidator.doctorExists(anyString())).thenReturn(doctor);
        // When
        var result = doctorValidator.doctorExists(anyString());
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(doctor, result);
    }

    @Test
    void createDoctor_DoctorWithSameEmailDoesNotExist_DoctorReturned() {
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        // When
        var result = doctorService.createDoctor(doctor);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(doctor, result);
    }

    @Test
    void assignDoctorToMedicalFacility_AssignmentIsNotPresent_DoctorWithAssignedFacilityReturned() {
        // Given
        Long doctorID = 1L;
        Long medicalFacilityID = 1L;
        Doctor doctor = DoctorFactory.getDoctor();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(doctorValidator.doctorExists(doctorID)).thenReturn(doctor);
        when(medicalFacilityValidator.medicalFacilityExists(medicalFacilityID)).thenReturn(medicalFacility);
        when(doctorRepository.save(any())).thenReturn(doctor);
        // When
        var result = doctorService.assignDoctorToMedicalFacility(doctorID, medicalFacilityID);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getMedicalFacilities().contains(medicalFacility));
    }
}
