package com.kustlik.medicalclinic.controller;

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
import com.kustlik.medicalclinic.service.PatientService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {
    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPatients_PatientsExists_ListOfPatientsReturned() throws Exception {
        // Given
        Patient patient = PatientFactory.getPatient();
        List<Patient> patients = List.of(patient);
        when(patientService.getPatients()).thenReturn(patients);
        // When

        // Then
        mockMvc.perform(get("/patients"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("jankow@gmail.com"))
                .andExpect(jsonPath("$[0].firstName").value("Jan"))
                .andExpect(jsonPath("$[0].lastName").value("Kowalski"))
                .andExpect(jsonPath("$[0].birthday",
                        Matchers.is(LocalDate.of(2000, 1, 1).toString())));
    }

    @Test
    void getPatient_PatientDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Patient does not exist.";
        String email = PatientFactory.getPatient().getEmail();
        when(patientService.getPatient(email)).thenThrow(PatientDoesNotExistException.class);
        // When

        // Then
        mockMvc.perform(get("/patients/{email}", email))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(PatientDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void getPatient_PatientExists_PatientReturned() throws Exception {
        // Given
        Patient patient = PatientFactory.getPatient();
        String email = patient.getEmail();
        when(patientService.getPatient(email)).thenReturn(patient);
        // When

        // Then
        mockMvc.perform(get("/patients/{email}", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jankow@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.birthday",
                        Matchers.is(LocalDate.of(2000, 1, 1).toString())));
    }

    @Test
    void createPatient_PatientWithEmptyFieldsIsGiven_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "All valid fields should be properly filled.";
        PatientCreationDTO patient = PatientFactory.getPatientCreationDTO(
                "jankow@gmail.com",
                null,
                "Jan",
                null,
                "password123",
                null);
        when(patientService.createPatient(any())).thenThrow(EmptyFieldException.class);
        // When

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
        String exceptionMsg = "Patient already exists.";
        PatientCreationDTO patientDTO = PatientFactory.getPatientCreationDTO();
        when(patientService.createPatient(any())).thenThrow(PatientExistsException.class);
        // When

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
        Patient patient = patientMapper.toPatient(patientDTO);
        when(patientService.createPatient(any())).thenReturn(patient);
        // When

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
        String exceptionMsg = "Patient does not exist.";
        String email = PatientFactory.getPatient().getEmail();
        doThrow(PatientDoesNotExistException.class).when(patientService).deletePatient(email);
        // When

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
        String email = PatientFactory.getPatient().getEmail();
        doNothing().when(patientService).deletePatient(email);
        // When

        // Then
        mockMvc.perform(delete("/patients/{email}", email))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(result -> Assertions.assertDoesNotThrow(() ->
                        patientService.deletePatient(email)));
    }

    @Test
    void editPatient_PatientWithGivenEmailDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Patient does not exist.";
        PatientDTO patientDTO = PatientFactory.getPatientDTO();
        Patient patient = patientMapper.toPatientEdit(patientDTO);
        String email = patient.getEmail();
        when(patientService.editPatient(any(), any())).thenThrow(PatientDoesNotExistException.class);
        // When

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
        String exceptionMsg = "Patient already exists.";
        PatientDTO patientDTO = PatientFactory.getPatientDTO();
        Patient patient = patientMapper.toPatientEdit(patientDTO);
        String email = patient.getEmail();
        when(patientService.editPatient(any(), any())).thenThrow(PatientExistsException.class);
        // When

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
        String exceptionMsg = "All valid fields should be properly filled.";
        PatientDTO patientDTO = PatientFactory.getPatientDTO(
                "karczek@gmail.com",
                "Karol",
                null,
                null);
        Patient patient = patientMapper.toPatientEdit(patientDTO);
        String email = patient.getEmail();
        when(patientService.editPatient(any(), any())).thenThrow(EmptyFieldException.class);
        // When

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
                "karczek@gmail.com",
                "Karol",
                "Kowalski",
                LocalDate.of(2000, 1, 1));
        Patient patient = patientMapper.toPatientEdit(patientDTO);
        String email = patient.getEmail();
        when(patientService.editPatient(any(), any())).thenReturn(patient);
        // When

        // Then
        mockMvc.perform(put("/patients/{email}", email).content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("karczek@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Karol"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.birthday",
                        Matchers.is(LocalDate.of(2000, 1, 1).toString())));
    }

    @Test
    void editPatientPassword_PatientWithGivenEmailDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Patient does not exist.";
        PatientPasswordDTO patientDTO = PatientFactory.getPatientPasswordDTO();
        Patient patientPassword = patientMapper.toPatientPasswordEdit(patientDTO);
        String email = patientDTO.getEmail();
        doThrow(PatientDoesNotExistException.class).when(patientService).editPatientPassword(any(), any());
        // When

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
        String exceptionMsg = "All valid fields should be properly filled.";
        PatientPasswordDTO patientDTO = PatientFactory.getPatientPasswordDTO(
                "jankow@gmail.com",
                null);
        Patient patientPassword = patientMapper.toPatientPasswordEdit(patientDTO);
        String email = patientDTO.getEmail();
        doThrow(EmptyFieldException.class).when(patientService).editPatientPassword(any(), any());
        // When

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
                "jankow@gmail.com",
                "admin123");
        Patient patientPassword = patientMapper.toPatientPasswordEdit(patientDTO);
        String email = patientDTO.getEmail();
        doNothing().when(patientService).editPatientPassword(email, patientPassword);
        // When

        // Then
        mockMvc.perform(patch("/patients/{email}", email).content(objectMapper.writeValueAsString(patientDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(result -> Assertions.assertDoesNotThrow(() ->
                        patientService.editPatientPassword(email, patientPassword)));
    }
}
