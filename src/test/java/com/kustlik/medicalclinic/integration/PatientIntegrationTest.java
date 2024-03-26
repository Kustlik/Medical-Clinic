package com.kustlik.medicalclinic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustlik.medicalclinic.exception.EmptyFieldException;
import com.kustlik.medicalclinic.exception.PatientDoesNotExistException;
import com.kustlik.medicalclinic.exception.PatientExistsException;
import com.kustlik.medicalclinic.factory.PatientFactory;
import com.kustlik.medicalclinic.model.dto.patient.PatientCreationDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientPasswordDTO;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.model.mapper.PatientMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"file:src/test/resources/sql/patient_clear_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/sql/patient_insert_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PatientIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPatients_PatientsExists_ListOfPatientDTOReturned() throws Exception {
        // Then
        mockMvc.perform(get("/patients"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("boczek@gmail.com"))
                .andExpect(jsonPath("$[0].firstName").value("Jerzy"))
                .andExpect(jsonPath("$[0].lastName").value("Bolek"))
                .andExpect(jsonPath("$[0].birthday",
                        Matchers.is(LocalDate.of(1980, 2, 2).toString())))
                .andExpect(jsonPath("$[1].email").value("karkow@gmail.com"))
                .andExpect(jsonPath("$[1].firstName").value("Karol"))
                .andExpect(jsonPath("$[1].lastName").value("Wojczyk"))
                .andExpect(jsonPath("$[1].birthday",
                        Matchers.is(LocalDate.of(1990, 6, 12).toString())));
    }

    @Test
    void getPatient_PatientDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Patient with given email does not exist.";
        String email = PatientFactory.getPatient().getEmail();
        // Then
        mockMvc.perform(get("/patients/{email}", email))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(PatientDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void getPatient_PatientExists_PatientDTOReturned() throws Exception {
        // Given
        String email = "boczek@gmail.com";
        // Then
        mockMvc.perform(get("/patients/{email}", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("boczek@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Jerzy"))
                .andExpect(jsonPath("$.lastName").value("Bolek"))
                .andExpect(jsonPath("$.birthday",
                        Matchers.is(LocalDate.of(1980, 2, 2).toString())));
    }

    @Test
    void createPatient_PatientWithEmptyFieldsIsGiven_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "No empty argument is allowed.";
        PatientCreationDTO patient = PatientFactory.getPatientCreationDTO(
                "jankow@gmail.com",
                null,
                "Jan",
                null,
                "password123",
                null);
        // Then
        mockMvc.perform(post("/patients").content(objectMapper.writeValueAsString(patient)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(EmptyFieldException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createPatient_PatientWithSameEmailExists_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "Patient with given email exists.";
        PatientCreationDTO patientDTO = PatientFactory.getPatientCreationDTO(
                "boczek@gmail.com",
                "12345",
                "Jan",
                "Kowalski",
                "password123",
                LocalDate.of(2000, 1, 1));
        // Then
        mockMvc.perform(post("/patients").content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(PatientExistsException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createPatient_PatientWithValidBodyIsGiven_PatientDTOReturned() throws Exception {
        // Given
        PatientCreationDTO patientDTO = PatientFactory.getPatientCreationDTO();
        // Then
        mockMvc.perform(post("/patients").content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jankow@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.birthday",
                        Matchers.is(LocalDate.of(2000, 1, 1).toString())));
    }

    @Test
    void deletePatient_PatientWithGivenEmailDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Patient with given email does not exist.";
        String email = PatientFactory.getPatient().getEmail();
        // Then
        mockMvc.perform(delete("/patients/{email}", email))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(PatientDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void deletePatient_PatientWithValidEmailExists_NoContentReturned() throws Exception {
        // Given
        String email = "boczek@gmail.com";
        // Then
        mockMvc.perform(delete("/patients/{email}", email))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void editPatient_PatientWithGivenEmailDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Patient with given email does not exist.";
        PatientDTO patientDTO = PatientFactory.getPatientDTO();
        Patient patient = patientMapper.toPatient(patientDTO);
        String email = patient.getEmail();
        // Then
        mockMvc.perform(put("/patients/{email}", email).content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(PatientDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void editPatient_PatientWithNewEmailGivenToEditIsPresent_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "Patient with given email exists.";
        PatientDTO patientDTO = PatientFactory.getPatientDTO(
                "karkow@gmail.com",
                "12345",
                "Jan",
                LocalDate.of(1980, 2, 2)
        );
        String email = "boczek@gmail.com";
        // Then
        mockMvc.perform(put("/patients/{email}", email).content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(PatientExistsException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void editPatient_PatientWithSomeEmptyFieldsIsGiven_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "No empty argument to edit is allowed.";
        PatientDTO patientDTO = PatientFactory.getPatientDTO(
                "zimczyn23@gmail.com",
                "Karol",
                null,
                null);
        String email = "boczek@gmail.com";
        // Then
        mockMvc.perform(put("/patients/{email}", email).content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(EmptyFieldException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void editPatient_PatientWithValidBodyIsGiven_PatientDTOReturned() throws Exception {
        // Given
        PatientDTO patientDTO = PatientFactory.getPatientDTO(
                "zimczyn23@gmail.com",
                "Karol",
                "Kowalski",
                LocalDate.of(2000, 1, 1));
        String email = "boczek@gmail.com";
        // Then
        mockMvc.perform(put("/patients/{email}", email).content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("zimczyn23@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Karol"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.birthday",
                        Matchers.is(LocalDate.of(2000, 1, 1).toString())));
    }

    @Test
    void editPatientPassword_PatientWithGivenEmailDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Patient with given email does not exist.";
        PatientPasswordDTO patientDTO = PatientFactory.getPatientPasswordDTO();
        String email = patientDTO.getEmail();
        // Then
        mockMvc.perform(patch("/patients/{email}", email).content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(PatientDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void editPatientPassword_PatientWithEmptyFieldsIsGiven_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "Password is empty.";
        PatientPasswordDTO patientDTO = PatientFactory.getPatientPasswordDTO(
                "jankow@gmail.com",
                null);
        String email = patientDTO.getEmail();
        // Then
        mockMvc.perform(patch("/patients/{email}", email).content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(EmptyFieldException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void editPatientPassword_PatientWithValidBodyIsGiven_NoContentReturned() throws Exception {
        // Given
        PatientPasswordDTO patientDTO = PatientFactory.getPatientPasswordDTO(
                "boczek@gmail.com",
                "admin123");
        String email = patientDTO.getEmail();
        // Then
        mockMvc.perform(patch("/patients/{email}", email).content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
