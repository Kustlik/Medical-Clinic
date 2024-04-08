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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
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
    void getVisits_ListOfVisitsExists_PageOfVisitReturned() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        Page<Visit> visitPage = new PageImpl<>(visits);
        when(visitRepository.findAll(pageable)).thenReturn(visitPage);
        // When
        var result = visitService.getVisits(pageable);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(visits, result.getContent());
    }

    @Test
    public void getVisits_ListOfVisitsWithGivenVisitDateExists_PageOfVisitReturned() {
        // Given
        LocalDate searchDate = VisitFactory.getVisit().getAppointmentStart().toLocalDate();
        Pageable pageable = PageRequest.of(0, 10);
        List<Visit> expectedVisits = List.of(VisitFactory.getVisit());
        Page<Visit> expectedPage = new PageImpl<>(expectedVisits, pageable, 1);
        when(visitRepository.findByPatientIdIsNotNullAndAppointmentStartGreaterThanEqualAndAppointmentStartLessThan(pageable,
                searchDate.atStartOfDay(), searchDate.plusDays(1).atStartOfDay()))
                .thenReturn(expectedPage);

        // When
        var result = visitService.getVisits(pageable, searchDate);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedPage.getContent(), result.getContent());
        Assertions.assertEquals(1, result.getContent().size());
        Visit actualVisit = result.getContent().get(0);
        Assertions.assertEquals(searchDate, actualVisit.getAppointmentStart().toLocalDate());
    }

    @Test
    public void getVisits_ListOfVisitsWithGivenVisitDateDoesNotExist_EmptyPageReturned() {
        // Given
        LocalDate searchDate = VisitFactory.getVisit().getAppointmentStart().toLocalDate();
        Pageable pageable = PageRequest.of(0, 10);
        List<Visit> expectedVisits = List.of();
        Page<Visit> expectedPage = new PageImpl<>(expectedVisits, pageable, 1);
        when(visitRepository.findByPatientIdIsNotNullAndAppointmentStartGreaterThanEqualAndAppointmentStartLessThan(pageable,
                searchDate.atStartOfDay(), searchDate.plusDays(1).atStartOfDay()))
                .thenReturn(Page.empty());

        // When
        var result = visitService.getVisits(pageable, searchDate);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedPage.getContent(), result.getContent());
        Assertions.assertEquals(0, result.getContent().size());
    }

    @Test
    void getFreeVisits_ListOfFreeVisitsExists_PageOfVisitReturned() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        Page<Visit> visitPage = new PageImpl<>(visits);
        when(visitRepository.findByPatientIdIsNull(pageable)).thenReturn(visitPage);
        // When
        var result = visitService.getFreeVisits(pageable);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(visits, result.getContent());
    }

    @Test
    void getVisitsByDoctor_ListOfFreeVisitsExists_ListOfVisitReturned() {
        // Given
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        when(visitRepository.findByDoctorId(any())).thenReturn(visits);
        // When
        var result = visitRepository.findByDoctorId(any());
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
