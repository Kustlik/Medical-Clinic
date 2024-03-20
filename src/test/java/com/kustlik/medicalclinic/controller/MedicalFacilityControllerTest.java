package com.kustlik.medicalclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.factory.DoctorFactory;
import com.kustlik.medicalclinic.factory.MedicalFacilityFactory;
import com.kustlik.medicalclinic.model.dto.medical_facility.MedicalFacilityDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.model.mapper.MedicalFacilityMapper;
import com.kustlik.medicalclinic.service.MedicalFacilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalFacilityControllerTest {
    @Autowired
    private MedicalFacilityMapper medicalFacilityMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MedicalFacilityService medicalFacilityService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMedicalFacilities_MedicalFacilitiesExists_ListOfMedicalFacilitiesReturned() throws Exception {
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        List<MedicalFacility> medicalFacilities = List.of(medicalFacility);
        when(medicalFacilityService.getMedicalFacilities()).thenReturn(medicalFacilities);
        // When

        // Then
        mockMvc.perform(get("/medical_facilities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].buildingNumber").value("43"))
                .andExpect(jsonPath("$[0].city").value("Warszawa"))
                .andExpect(jsonPath("$[0].name").value("Covermedi"))
                .andExpect(jsonPath("$[0].street").value("Opolska"))
                .andExpect(jsonPath("$[0].zipCode").value("65-346"));
    }

    @Test
    void getMedicalFacility_MedicalFacilityDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Medical facility does not exist.";
        Long id = MedicalFacilityFactory.getMedicalFacility().getId();
        when(medicalFacilityService.getMedicalFacility(id)).thenThrow(MedicalFacilityDoesNotExistException.class);
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
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        Long id = medicalFacility.getId();
        when(medicalFacilityService.getMedicalFacility(id)).thenReturn(medicalFacility);
        // When

        // Then
        mockMvc.perform(get("/medical_facilities/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildingNumber").value("43"))
                .andExpect(jsonPath("$.city").value("Warszawa"))
                .andExpect(jsonPath("$.name").value("Covermedi"))
                .andExpect(jsonPath("$.street").value("Opolska"))
                .andExpect(jsonPath("$.zipCode").value("65-346"));
    }

    @Test
    void createMedicalFacility_MedicalFacilityWithEmptyFieldsIsGiven_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "All valid fields should be properly filled.";
        MedicalFacilityDTO medicalFacility = MedicalFacilityFactory.getMedicalFacilityDTO(
                "43",
                "Warszawa",
                null,
                "Opolska",
                null,
                null);
        when(medicalFacilityService.createMedicalFacility(any())).thenThrow(EmptyFieldException.class);
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
        MedicalFacilityDTO medicalFacilityDTO = MedicalFacilityFactory.getMedicalFacilityDTO();
        MedicalFacility medicalFacility = medicalFacilityMapper.toMedicalFacility(medicalFacilityDTO);
        when(medicalFacilityService.createMedicalFacility(any())).thenThrow(MedicalFacilityExistsException.class);
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
        MedicalFacility medicalFacility = medicalFacilityMapper.toMedicalFacility(medicalFacilityDTO);
        when(medicalFacilityService.createMedicalFacility(any())).thenReturn(medicalFacility);
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
        Long doctorId = 1L;
        Long medicalFacilityId = 1L;
        when(medicalFacilityService.assignMedicalFacilityToDoctor(medicalFacilityId, doctorId)).thenThrow(DoctorToMedicalFacilityAssignmentExistsException.class);
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
        Long doctorId = 1L;
        Long medicalFacilityId = 1L;
        when(medicalFacilityService.assignMedicalFacilityToDoctor(medicalFacilityId, doctorId)).thenThrow(DoctorDoesNotExistException.class);
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
        Long medicalFacilityId = 1L;
        when(medicalFacilityService.assignMedicalFacilityToDoctor(medicalFacilityId, doctorId)).thenThrow(MedicalFacilityDoesNotExistException.class);
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
        Doctor doctor = DoctorFactory.getDoctor();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        medicalFacility.getDoctors().add(doctor);
        when(medicalFacilityService.assignMedicalFacilityToDoctor(medicalFacilityId, doctorId)).thenReturn(medicalFacility);
        // When

        // Then
        mockMvc.perform(post("/medical_facilities/{medicalFacilityId}/assign", medicalFacilityId).content(objectMapper.writeValueAsString(doctorId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.buildingNumber").value("43"))
                .andExpect(jsonPath("$.city").value("Warszawa"))
                .andExpect(jsonPath("$.name").value("Covermedi"))
                .andExpect(jsonPath("$.street").value("Opolska"))
                .andExpect(jsonPath("$.zipCode").value("65-346"))
                .andExpect(jsonPath("$.doctorIds.[0]").value("1"));
    }
}
