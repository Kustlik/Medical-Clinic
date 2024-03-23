package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.factory.DoctorFactory;
import com.kustlik.medicalclinic.factory.PatientFactory;
import com.kustlik.medicalclinic.factory.VisitFactory;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import com.kustlik.medicalclinic.repository.PatientRepository;
import com.kustlik.medicalclinic.repository.VisitRepository;
import com.kustlik.medicalclinic.service.validator.VisitValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class VisitServiceTest {
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private VisitRepository visitRepository;
    private VisitValidator visitValidator;
    private VisitService visitService;

    @BeforeEach
    void setup() {
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.visitRepository = Mockito.mock(VisitRepository.class);
        this.visitValidator = Mockito.mock(VisitValidator.class);
        this.visitService = new VisitServiceImpl(doctorRepository, patientRepository, visitRepository, visitValidator);
    }

    @Test
    void getFreeVisits_ListOfFreeVisitsExists_ListOfVisitReturned() {
        // Given
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        when(visitRepository.findByPatientIdIsNull()).thenReturn(visits);
        // When
        var result = visitService.getFreeVisits();

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(visits, result);
    }

    @Test
    void getFreeVisitsByDoctor_ListOfFreeVisitsExists_ListOfVisitReturned() {
        // Given
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        when(visitRepository.findByDoctorIdAndPatientIdIsNull(any())).thenReturn(visits);
        // When
        var result = visitRepository.findByDoctorIdAndPatientIdIsNull(any());

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(visits, result);
    }

    @Test
    void getVisitsByPatient_ListOfVisitsForPatientExists_ListOfVisitReturned() {
        // Given
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        when(visitRepository.findByPatientId(any())).thenReturn(visits);
        // When
        var result = visitService.getVisitsByPatient(any());

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(visits, result);
    }

    @Test
    void createVisit_ValidVisitToCreationIsGiven_CreatedVisitReturned() {
        // Given
        Visit visit = VisitFactory.getVisit();
        Doctor doctor = DoctorFactory.getDoctor();
        when(doctorRepository.findById(any())).thenReturn(Optional.of(doctor));
        when(visitRepository.save(visit)).thenReturn(visit);
        // When
        var result = visitService.createVisit(visit, doctor.getId());

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(visit, result);
        Assertions.assertEquals(doctor, result.getDoctor());
    }

    @Test
    void assignVisitToPatient_ValidVisitToAssignIsGiven_AssignedVisitReturned() {
        // Given
        Visit visit = VisitFactory.getVisit();
        Patient patient = PatientFactory.getPatient();
        when(patientRepository.findById(any())).thenReturn(Optional.of(patient));
        when(visitValidator.visitExists(visit.getId())).thenReturn(visit);
        when(visitRepository.save(visit)).thenReturn(visit);
        // When
        var result = visitService.assignVisitToPatient(visit.getId(), patient.getId());

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(visit, result);
        Assertions.assertEquals(patient, result.getPatient());
    }
}
