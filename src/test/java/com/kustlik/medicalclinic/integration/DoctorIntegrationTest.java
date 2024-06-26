package com.kustlik.medicalclinic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.factory.DoctorFactory;
import com.kustlik.medicalclinic.model.dto.doctor.DoctorCreationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
@Sql(scripts = {"file:src/test/resources/sql/medical_facility_clear_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/sql/medical_facility_insert_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/sql/doctor_medical_facility_clear_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/sql/doctor_medical_facility_insert_data.sql"},
        config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DoctorIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDoctors_DoctorsExists_ListOfDoctorDTOReturned() throws Exception {
        // Then
        mockMvc.perform(get("/doctors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("jankowski@gmail.com"))
                .andExpect(jsonPath("$[0].firstName").value("Arek"))
                .andExpect(jsonPath("$[0].lastName").value("Nowacki"))
                .andExpect(jsonPath("$[0].specialisation").value("Ortopeda"))
                .andExpect(jsonPath("$[1].email").value("baczynski@gmail.com"))
                .andExpect(jsonPath("$[1].firstName").value("Damian"))
                .andExpect(jsonPath("$[1].lastName").value("Borczek"))
                .andExpect(jsonPath("$[1].specialisation").value("Chiropraktyk"));
    }

    @Test
    void getDoctor_DoctorDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Doctor not found.";
        String email = DoctorFactory.getDoctor().getEmail();
        // Then
        mockMvc.perform(get("/doctors/{email}", email))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(DoctorDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void getDoctor_DoctorExists_DoctorDTOReturned() throws Exception {
        // Given
        String email = "jankowski@gmail.com";
        // Then
        mockMvc.perform(get("/doctors/{email}", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jankowski@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Arek"))
                .andExpect(jsonPath("$.lastName").value("Nowacki"))
                .andExpect(jsonPath("$.specialisation").value("Ortopeda"));
    }

    @Test
    void createDoctor_DoctorWithEmptyFieldsIsGiven_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "No empty argument is allowed.";
        DoctorCreationDTO doctor = DoctorFactory.getDoctorCreationDTO(
                "jankow@gmail.com",
                "Jan",
                null,
                "password123",
                null);
        // Then
        mockMvc.perform(post("/doctors").content(objectMapper.writeValueAsString(doctor)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(EmptyFieldException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createDoctor_DoctorWithSameEmailExists_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "Doctor with given email exists.";
        DoctorCreationDTO doctorDTO = DoctorFactory.getDoctorCreationDTO(
                "jankowski@gmail.com",
                "Jan",
                "Kowalski",
                "password123",
                "Pediatra");
        // Then
        mockMvc.perform(post("/doctors").content(objectMapper.writeValueAsString(doctorDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(DoctorExistsException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createDoctor_DoctorWithValidBodyIsGiven_DoctorDTOReturned() throws Exception {
        // Given
        DoctorCreationDTO doctorDTO = DoctorFactory.getDoctorCreationDTO();
        // Then
        mockMvc.perform(post("/doctors").content(objectMapper.writeValueAsString(doctorDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jankow@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.specialisation").value("Psychiatra"));
    }

    @Test
    void createDoctorAssignment_ExistingAssignmentIsGiven_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "Doctor is already assigned to this facility.";
        Long doctorId = 2L;
        Long medicalFacilityId = 1L;
        // Then
        mockMvc.perform(post("/doctors/{doctorId}/assign", doctorId).content(objectMapper.writeValueAsString(medicalFacilityId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(DoctorToMedicalFacilityAssignmentExistsException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createDoctorAssignment_DoctorDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Doctor with given ID does not exist.";
        Long doctorId = 5L;
        Long medicalFacilityId = 1L;
        // Then
        mockMvc.perform(post("/doctors/{doctorId}/assign", doctorId).content(objectMapper.writeValueAsString(medicalFacilityId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(DoctorDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createDoctorAssignment_MedicalFacilityDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Medical facility with given ID does not exist.";
        Long doctorId = 1L;
        Long medicalFacilityId = 5L;
        // Then
        mockMvc.perform(post("/doctors/{doctorId}/assign", doctorId).content(objectMapper.writeValueAsString(medicalFacilityId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(MedicalFacilityDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createDoctorAssignment_ValidAssignmentIsGiven_DoctorDTOReturned() throws Exception {
        // Given
        Long doctorId = 1L;
        Long medicalFacilityId = 1L;
        // Then
        mockMvc.perform(post("/doctors/{doctorId}/assign", doctorId).content(objectMapper.writeValueAsString(medicalFacilityId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jankowski@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Arek"))
                .andExpect(jsonPath("$.lastName").value("Nowacki"))
                .andExpect(jsonPath("$.specialisation").value("Ortopeda"))
                .andExpect(jsonPath("$.medicalFacilityIds[0]").value("1"));
    }
}
