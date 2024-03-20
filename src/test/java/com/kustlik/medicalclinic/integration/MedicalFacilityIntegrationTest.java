package com.kustlik.medicalclinic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.factory.MedicalFacilityFactory;
import com.kustlik.medicalclinic.model.dto.medical_facility.MedicalFacilityDTO;
import com.kustlik.medicalclinic.model.mapper.MedicalFacilityMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;
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
public class MedicalFacilityIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MedicalFacilityMapper medicalFacilityMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMedicalFacilities_MedicalFacilitiesExists_ListOfMedicalFacilitiesReturned() throws Exception {
        // Given

        // When

        // Then
        mockMvc.perform(get("/medical_facilities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].buildingNumber").value("23"))
                .andExpect(jsonPath("$[0].city").value("Łódź"))
                .andExpect(jsonPath("$[0].name").value("MediSun"))
                .andExpect(jsonPath("$[0].street").value("Piotrkowska"))
                .andExpect(jsonPath("$[0].zipCode").value("23-542"))
                .andExpect(jsonPath("$[1].buildingNumber").value("74"))
                .andExpect(jsonPath("$[1].city").value("Warszawa"))
                .andExpect(jsonPath("$[1].name").value("HospiDeli"))
                .andExpect(jsonPath("$[1].street").value("Młyńska"))
                .andExpect(jsonPath("$[1].zipCode").value("73-325"));
    }

    @Test
    void getMedicalFacility_MedicalFacilityDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Medical facility does not exist.";
        Long id = 5L;
        // When

        // Then
        mockMvc.perform(get("/medical_facilities/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(MedicalFacilityDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void getMedicalFacility_MedicalFacilityExists_MedicalFacilityReturned() throws Exception {
        // Given
        Long id = 1L;
        // When

        // Then
        mockMvc.perform(get("/medical_facilities/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildingNumber").value("23"))
                .andExpect(jsonPath("$.city").value("Łódź"))
                .andExpect(jsonPath("$.name").value("MediSun"))
                .andExpect(jsonPath("$.street").value("Piotrkowska"))
                .andExpect(jsonPath("$.zipCode").value("23-542"));
    }

    @Test
    void createMedicalFacility_MedicalFacilityWithEmptyFieldsIsGiven_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "All valid fields should be properly filled.";
        MedicalFacilityDTO medicalFacility = MedicalFacilityFactory.getMedicalFacilityDTO(
                "28",
                null,
                "KawaSpicjum",
                null,
                "12-593",
                null);
        // When

        // Then
        mockMvc.perform(post("/medical_facilities").content(objectMapper.writeValueAsString(medicalFacility)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(EmptyFieldException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createMedicalFacility_MedicalFacilityWithSameIdExists_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "Medical facility already exists.";
        MedicalFacilityDTO medicalFacilityDTO = MedicalFacilityFactory.getMedicalFacilityDTO(
                "28",
                "Kraków",
                "MediSun",
                "Szmaragdowa",
                "12-593",
                new ArrayList<>());
        // When

        // Then
        mockMvc.perform(post("/medical_facilities").content(objectMapper.writeValueAsString(medicalFacilityDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MedicalFacilityExistsException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createMedicalFacility_MedicalFacilityWithValidBodyIsGiven_MedicalFacilityDTOReturned() throws Exception {
        // Given
        MedicalFacilityDTO medicalFacilityDTO = MedicalFacilityFactory.getMedicalFacilityDTO();
        // When

        // Then
        mockMvc.perform(post("/medical_facilities").content(objectMapper.writeValueAsString(medicalFacilityDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.buildingNumber").value("43"))
                .andExpect(jsonPath("$.city").value("Warszawa"))
                .andExpect(jsonPath("$.name").value("Covermedi"))
                .andExpect(jsonPath("$.street").value("Opolska"))
                .andExpect(jsonPath("$.zipCode").value("65-346"));
    }

    @Test
    void createMedicalFacilityAssignment_ExistingAssignmentIsGiven_ThenIsBadRequest() throws  Exception{
        // Given
        String exceptionMsg = "Doctor is already assigned to this facility.";
        Long doctorId = 2L;
        Long medicalFacilityId = 1L;
        // When

        // Then
        mockMvc.perform(post("/medical_facilities/{medicalFacilityId}/assign", medicalFacilityId).content(objectMapper.writeValueAsString(doctorId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(DoctorToMedicalFacilityAssignmentExistsException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createMedicalFacilityAssignment_DoctorDoesNotExist_ThenIsNotFound() throws  Exception{
        // Given
        String exceptionMsg = "Doctor does not exist.";
        Long doctorId = 5L;
        Long medicalFacilityId = 1L;
        // When

        // Then
        mockMvc.perform(post("/medical_facilities/{medicalFacilityId}/assign", medicalFacilityId).content(objectMapper.writeValueAsString(doctorId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(DoctorDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createMedicalFacilityAssignment_MedicalFacilityDoesNotExist_ThenIsNotFound() throws  Exception{
        // Given
        String exceptionMsg = "Medical facility does not exist.";
        Long doctorId = 1L;
        Long medicalFacilityId = 5L;
        // When

        // Then
        mockMvc.perform(post("/medical_facilities/{medicalFacilityId}/assign", medicalFacilityId).content(objectMapper.writeValueAsString(doctorId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(MedicalFacilityDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createMedicalFacilityAssignment_ValidAssignmentIsGiven_MedicalFacilityDTOReturned() throws  Exception{
        // Given
        Long doctorId = 1L;
        Long medicalFacilityId = 1L;
        // When

        // Then
        mockMvc.perform(post("/medical_facilities/{medicalFacilityId}/assign", medicalFacilityId).content(objectMapper.writeValueAsString(doctorId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.buildingNumber").value("23"))
                .andExpect(jsonPath("$.city").value("Łódź"))
                .andExpect(jsonPath("$.name").value("MediSun"))
                .andExpect(jsonPath("$.street").value("Piotrkowska"))
                .andExpect(jsonPath("$.zipCode").value("23-542"))
                .andExpect(jsonPath("$.doctorIds.[0]").value("2"));
    }
}
