package com.kustlik.medicalclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustlik.medicalclinic.controller.status.VisitStatus;
import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.factory.VisitFactory;
import com.kustlik.medicalclinic.model.dto.visit.VisitDTO;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    private static final int YEAR = 2029;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VisitService visitService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getVisits_WithStatusAll_ListOfVisitDTOReturned() throws Exception {
        // When
        Visit visit = VisitFactory.getVisit();
        Page<Visit> visitPage = new PageImpl<>(List.of(visit));
        when(visitService.getVisits(any(Pageable.class))).thenReturn(visitPage);
        // Then
        mockMvc.perform(get("/visits")
                        .param("status", VisitStatus.ALL.toString())
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        YEAR + "-01-01T12:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        YEAR + "-01-01T12:30:00"));
    }

    @Test
    void getFree_WithStatusAvailable_ListOfVisitDTOReturned() throws Exception {
        // Given
        Visit visit = VisitFactory.getVisit();
        Page<Visit> visitPage = new PageImpl<>(List.of(visit));
        when(visitService.getFreeVisits(any(Pageable.class))).thenReturn(visitPage);
        // Then
        mockMvc.perform(get("/visits")
                        .param("status", VisitStatus.AVAILABLE.toString())
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        YEAR + "-01-01T12:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        YEAR + "-01-01T12:30:00"));
    }

    @Test
    void getVisitsByDoctor_WithStatusAll_ListOfVisitDTOReturned() throws Exception {
        // Given
        Long doctorId = 1L;
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        when(visitService.getVisitsByDoctor(any())).thenReturn(visits);
        // Then
        mockMvc.perform(get("/visits/doctor/{doctorId}", doctorId)
                        .param("status", VisitStatus.ALL.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        YEAR + "-01-01T12:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        YEAR + "-01-01T12:30:00"));
    }

    @Test
    void getVisitsByDoctor_WithStatusAvailable_ListOfVisitDTOReturned() throws Exception {
        // Given
        Long doctorId = 1L;
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        when(visitService.getFreeVisitsByDoctor(any())).thenReturn(visits);
        // Then
        mockMvc.perform(get("/visits/doctor/{doctorId}", doctorId)
                        .param("status", VisitStatus.AVAILABLE.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        YEAR + "-01-01T12:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        YEAR + "-01-01T12:30:00"));
    }

    @Test
    void getVisitsByPatient_ListOfVisitsForPatientExists_ListOfVisitDTOReturned() throws Exception {
        // Given
        Long patientId = 1L;
        Visit visit = VisitFactory.getVisit();
        List<Visit> visits = List.of(visit);
        when(visitService.getVisitsByPatient(any())).thenReturn(visits);
        // Then
        mockMvc.perform(get("/visits/patient/{patientId}", patientId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        YEAR + "-01-01T12:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        YEAR + "-01-01T12:30:00"));
    }

    @Test
    void createVisit_DoctorWithGivenIdDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        Long doctorId = 1L;
        String exceptionMsg = "Doctor does not exist.";
        VisitDTO visitDTO = VisitFactory.getVisitDTO();
        when(visitService.createVisit(any(), any())).thenThrow(new DoctorDoesNotExistException(exceptionMsg));
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
        when(visitService.createVisit(any(), any())).thenThrow(new EmptyFieldException(exceptionMsg));
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
        when(visitService.createVisit(any(), any())).thenThrow(new InvalidDateTimeException(exceptionMsg));
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
        when(visitService.createVisit(any(), any())).thenThrow(new VisitExistsException(exceptionMsg));
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
        // Then
        mockMvc.perform(post("/visits/doctor/{doctorId}", doctorId).content(objectMapper.writeValueAsString(visitDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentStart").value(
                        YEAR + "-01-01T12:00:00"))
                .andExpect(jsonPath("$.appointmentEnd").value(
                        YEAR + "-01-01T12:30:00"));
    }

    @Test
    void assignVisitToPatient_PatientGivenToAssignmentDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Patient does not exist.";
        Long visitId = 1L;
        Long patientId = 1L;
        when(visitService.assignVisitToPatient(any(), any())).thenThrow(new PatientDoesNotExistException(exceptionMsg));
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
        when(visitService.assignVisitToPatient(any(), any())).thenThrow(new VisitDoesNotExistException(exceptionMsg));
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
        when(visitService.assignVisitToPatient(any(), any())).thenThrow(new InvalidDateTimeException(exceptionMsg));
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
        // Then
        mockMvc.perform(patch("/visits/patient/{patientId}", patientId).content(objectMapper.writeValueAsString(visitId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentStart").value(
                        YEAR + "-01-01T12:00:00"))
                .andExpect(jsonPath("$.appointmentEnd").value(
                        YEAR + "-01-01T12:30:00"));
    }
}
