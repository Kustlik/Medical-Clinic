package com.kustlik.medicalclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.factory.DoctorFactory;
import com.kustlik.medicalclinic.factory.VisitFactory;
import com.kustlik.medicalclinic.model.dto.doctor.DoctorCreationDTO;
import com.kustlik.medicalclinic.model.dto.visit.VisitDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.model.mapper.DoctorMapper;
import com.kustlik.medicalclinic.model.mapper.VisitMapper;
import com.kustlik.medicalclinic.service.DoctorService;
import com.kustlik.medicalclinic.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VisitControllerTest {
    private static final int YEAR = LocalDateTime.now().getYear() + 1;
    @Autowired
    private VisitMapper visitMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VisitService visitService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getFreeVisits_FreeVisitsExists_ListOfVisitDTOReturned() throws Exception {
        // Given
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        when(visitService.getFreeVisits()).thenReturn(visits);
        // When

        // Then
        mockMvc.perform(get("/visits/available"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        String.valueOf(YEAR) + "-01-01T12:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        String.valueOf(YEAR) + "-01-01T12:30:00"));
    }

    @Test
    void getFreeVisitsByDoctor_FreeVisitsExists_ListOfVisitDTOReturned() throws Exception {
        // Given
        Long doctorId = 1L;
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        when(visitService.getFreeVisitsByDoctor(any())).thenReturn(visits);
        // When

        // Then
        mockMvc.perform(get("/visits/available/doctor/{doctorId}", doctorId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        String.valueOf(YEAR) + "-01-01T12:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        String.valueOf(YEAR) + "-01-01T12:30:00"));
    }

    @Test
    void getVisitsByPatient_ListOfVisitsForPatientExists_ListOfVisitDTOReturned() throws Exception {
        // Given
        Long patientId = 1L;
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        when(visitService.getVisitsByPatient(any())).thenReturn(visits);
        // When

        // Then
        mockMvc.perform(get("/visits/patient/{patientId}", patientId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        String.valueOf(YEAR) + "-01-01T12:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        String.valueOf(YEAR) + "-01-01T12:30:00"));
    }

    @Test
    void createVisit_DoctorWithGivenIdDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        Long doctorId = 1L;
        String exceptionMsg = "Doctor does not exist.";
        VisitDTO visitDTO = VisitFactory.getVisitDTO();
        when(visitService.createVisit(any(), any())).thenThrow(DoctorDoesNotExistException.class);
        // When

        // Then
        mockMvc.perform(post("/visits/doctor/{doctorId}", doctorId).content(objectMapper.writeValueAsString(visitDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(DoctorDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createVisit_BodyWithSomeEmptyFieldsGiven_ThenIsBadRequest() throws Exception {
        // Given
        Long doctorId = 1L;
        String exceptionMsg = "All valid fields should be properly filled.";
        VisitDTO visitDTO = VisitFactory.getVisitDTO();
        when(visitService.createVisit(any(), any())).thenThrow(EmptyFieldException.class);
        // When

        // Then
        mockMvc.perform(post("/visits/doctor/{doctorId}", doctorId).content(objectMapper.writeValueAsString(visitDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(EmptyFieldException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createVisit_VisitWithWrongTimePeriodIsGiven_ThenIsBadRequest() throws Exception {
        // Given
        Long doctorId = 1L;
        String exceptionMsg = "Wrong time period.";
        VisitDTO visitDTO = VisitFactory.getVisitDTO();
        when(visitService.createVisit(any(), any())).thenThrow(InvalidDateTimeException.class);
        // When

        // Then
        mockMvc.perform(post("/visits/doctor/{doctorId}", doctorId).content(objectMapper.writeValueAsString(visitDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(InvalidDateTimeException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createVisit_VisitGivenOverlapsWithExistingOne_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "Visit already exists.";
        Long doctorId = 1L;
        VisitDTO visitDTO = VisitFactory.getVisitDTO();
        when(visitService.createVisit(any(), any())).thenThrow(VisitExistsException.class);
        // When

        // Then
        mockMvc.perform(post("/visits/doctor/{doctorId}", doctorId).content(objectMapper.writeValueAsString(visitDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(VisitExistsException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createVisit_ValidBodyIsGivenToCreation_CreatedVisitDTOReturned() throws Exception {
        // Given
        Long doctorId = 1L;
        Visit visit = VisitFactory.getVisit();
        VisitDTO visitDTO = VisitFactory.getVisitDTO();
        when(visitService.createVisit(any(), any())).thenReturn(visit);
        // When

        // Then
        mockMvc.perform(post("/visits/doctor/{doctorId}", doctorId).content(objectMapper.writeValueAsString(visitDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentStart").value(
                        String.valueOf(YEAR) + "-01-01T12:00:00"))
                .andExpect(jsonPath("$.appointmentEnd").value(
                        String.valueOf(YEAR) + "-01-01T12:30:00"));
    }

    @Test
    void assignVisitToPatient_PatientGivenToAssignmentDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Patient does not exist.";
        Long visitId = 1L;
        Long patientId = 1L;
        when(visitService.assignVisitToPatient(any(), any())).thenThrow(PatientDoesNotExistException.class);
        // When

        // Then
        mockMvc.perform(patch("/visits/patient/{patientId}", patientId).content(objectMapper.writeValueAsString(visitId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(PatientDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void assignVisitToPatient_VisitGivenToAssignmentDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Visit does not exist.";
        Long visitId = 1L;
        Long patientId = 1L;
        when(visitService.assignVisitToPatient(any(), any())).thenThrow(VisitDoesNotExistException.class);
        // When

        // Then
        mockMvc.perform(patch("/visits/patient/{patientId}", patientId).content(objectMapper.writeValueAsString(visitId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(VisitDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void assignVisitToPatient_VisitGivenToAssignmentIsInThePast_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "Wrong time period.";
        Long visitId = 1L;
        Long patientId = 1L;
        when(visitService.assignVisitToPatient(any(), any())).thenThrow(InvalidDateTimeException.class);
        // When

        // Then
        mockMvc.perform(patch("/visits/patient/{patientId}", patientId).content(objectMapper.writeValueAsString(visitId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(InvalidDateTimeException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void assignVisitToPatient_ValidBodyIsGivenToAssignment_AssignedVisitDTOReturned() throws Exception {
        // Given
        Long visitId = 1L;
        Long patientId = 1L;
        Visit visit = VisitFactory.getVisit();
        when(visitService.assignVisitToPatient(any(), any())).thenReturn(visit);
        // When

        // Then
        mockMvc.perform(patch("/visits/patient/{patientId}", patientId).content(objectMapper.writeValueAsString(visitId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentStart").value(
                        String.valueOf(YEAR) + "-01-01T12:00:00"))
                .andExpect(jsonPath("$.appointmentEnd").value(
                        String.valueOf(YEAR) + "-01-01T12:30:00"));
    }
}
