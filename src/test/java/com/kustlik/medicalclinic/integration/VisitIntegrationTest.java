package com.kustlik.medicalclinic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.factory.VisitFactory;
import com.kustlik.medicalclinic.model.dto.visit.VisitDTO;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.model.mapper.VisitMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"file:src/test/resources/sql/doctor_clear_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/sql/doctor_insert_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/sql/patient_clear_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/sql/patient_insert_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/sql/visit_clear_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/sql/visit_insert_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class VisitIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VisitMapper visitMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getFreeVisits_FreeVisitsExists_ListOfVisitDTOReturned() throws Exception {
        // Given

        // When

        // Then
        mockMvc.perform(get("/visits/available"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        "2029-12-01T12:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        "2029-12-01T12:30:00"))
                .andExpect(jsonPath("$[0].doctorId").value("2"))
                .andExpect(jsonPath("$[0].patientId").value(IsNull.nullValue()));
    }

    @Test
    void getFreeVisitsByDoctor_FreeVisitsExists_ListOfVisitDTOReturned() throws Exception {
        // Given
        Long doctorId = 2L;

        // When

        // Then
        mockMvc.perform(get("/visits/available/doctor/{doctorId}", doctorId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        "2029-12-01T12:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        "2029-12-01T12:30:00"))
                .andExpect(jsonPath("$[0].doctorId").value("2"))
                .andExpect(jsonPath("$[0].patientId").value(IsNull.nullValue()));
    }

    @Test
    void getVisitsByPatient_ListOfVisitsForPatientExists_ListOfVisitDTOReturned() throws Exception {
        // Given
        Long patientId = 1L;
        // When

        // Then
        mockMvc.perform(get("/visits/patient/{patientId}", patientId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentStart").value(
                        "2029-12-01T13:00:00"))
                .andExpect(jsonPath("$[0].appointmentEnd").value(
                        "2029-12-01T13:30:00"))
                .andExpect(jsonPath("$[0].doctorId").value("2"))
                .andExpect(jsonPath("$[0].patientId").value("1"));
    }

    @Test
    void createVisit_DoctorWithGivenIdDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        Long doctorId = 5L;
        String exceptionMsg = "Doctor does not exist.";
        VisitDTO visitDTO = VisitFactory.getVisitDTO();
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
        VisitDTO visitDTO = VisitFactory.getVisitDTO(
                LocalDateTime.of(2029, 1, 1, 12, 0),
                null,
                null,
                null);
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
        VisitDTO visitDTO = VisitFactory.getVisitDTO(
                LocalDateTime.of(2029, 1, 1, 12, 0),
                LocalDateTime.of(2029, 1, 1, 12, 5),
                null,
                null);
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
        Long doctorId = 2L;
        VisitDTO visitDTO = VisitFactory.getVisitDTO(
                LocalDateTime.of(2029, 12, 1, 12, 15),
                LocalDateTime.of(2029, 12, 1, 12, 45),
                null,
                null);
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
        // When

        // Then
        mockMvc.perform(post("/visits/doctor/{doctorId}", doctorId).content(objectMapper.writeValueAsString(visitDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentStart").value(
                        "2029-01-01T12:00:00"))
                .andExpect(jsonPath("$.appointmentEnd").value(
                        "2029-01-01T12:30:00"))
                .andExpect(jsonPath("$.doctorId").value("1"))
                .andExpect(jsonPath("$.patientId").value(IsNull.nullValue()));
    }

    @Test
    void assignVisitToPatient_PatientGivenToAssignmentDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Patient does not exist.";
        Long visitId = 1L;
        Long patientId = 5L;
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
        Long visitId = 5L;
        Long patientId = 1L;
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
        Long visitId = 3L;
        Long patientId = 1L;
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
        // When

        // Then
        mockMvc.perform(patch("/visits/patient/{patientId}", patientId).content(objectMapper.writeValueAsString(visitId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentStart").value(
                        "2029-12-01T12:00:00"))
                .andExpect(jsonPath("$.appointmentEnd").value(
                        "2029-12-01T12:30:00"))
                .andExpect(jsonPath("$.doctorId").value("2"))
                .andExpect(jsonPath("$.patientId").value("1"));
    }
}
