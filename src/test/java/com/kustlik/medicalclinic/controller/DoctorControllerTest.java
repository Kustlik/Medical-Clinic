package com.kustlik.medicalclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.factory.DoctorFactory;
import com.kustlik.medicalclinic.factory.MedicalFacilityFactory;
import com.kustlik.medicalclinic.model.dto.doctor.DoctorCreationDTO;
import com.kustlik.medicalclinic.model.dto.doctor.DoctorDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.model.mapper.DoctorMapper;
import com.kustlik.medicalclinic.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorService doctorService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDoctors_DoctorsExists_ListOfDoctorsReturned() throws Exception {
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        List<Doctor> doctors = List.of(doctor);
        when(doctorService.getDoctors()).thenReturn(doctors);
        // When

        // Then
        mockMvc.perform(get("/doctors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("jankow@gmail.com"))
                .andExpect(jsonPath("$[0].firstName").value("Jan"))
                .andExpect(jsonPath("$[0].lastName").value("Kowalski"))
                .andExpect(jsonPath("$[0].specialisation").value("Psychiatra"));
    }

    @Test
    void getDoctor_DoctorDoesNotExist_ThenIsNotFound() throws Exception {
        // Given
        String exceptionMsg = "Doctor does not exist.";
        String email = DoctorFactory.getDoctor().getEmail();
        when(doctorService.getDoctor(email)).thenThrow(DoctorDoesNotExistException.class);
        // When

        // Then
        mockMvc.perform(get("/doctors/{email}", email))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(DoctorDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void getDoctor_DoctorExists_DoctorReturned() throws Exception {
        // Given
        Doctor doctor = DoctorFactory.getDoctor();
        String email = doctor.getEmail();
        when(doctorService.getDoctor(email)).thenReturn(doctor);
        // When

        // Then
        mockMvc.perform(get("/doctors/{email}", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jankow@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.specialisation").value("Psychiatra"));
    }

    @Test
    void createDoctor_DoctorWithEmptyFieldsIsGiven_ThenIsBadRequest() throws Exception {
        // Given
        String exceptionMsg = "All valid fields should be properly filled.";
        DoctorCreationDTO doctor = DoctorFactory.getDoctorCreationDTO(
                "jankow@gmail.com",
                "Jan",
                null,
                "password123",
                null);
        when(doctorService.createDoctor(any())).thenThrow(EmptyFieldException.class);
        // When

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
        String exceptionMsg = "Doctor already exists.";
        DoctorCreationDTO doctorDTO = DoctorFactory.getDoctorCreationDTO();
        Doctor doctor = doctorMapper.toDoctor(doctorDTO);
        when(doctorService.createDoctor(any())).thenThrow(DoctorExistsException.class);
        // When

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
        Doctor doctor = doctorMapper.toDoctor(doctorDTO);
        when(doctorService.createDoctor(any())).thenReturn(doctor);
        // When

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
    void createDoctorAssignment_ExistingAssignmentIsGiven_ThenIsBadRequest() throws  Exception{
        // Given
        String exceptionMsg = "Doctor is already assigned to this facility.";
        Long doctorId = 1L;
        Long medicalFacilityId = 1L;
        when(doctorService.assignDoctorToMedicalFacility(doctorId, medicalFacilityId)).thenThrow(DoctorToMedicalFacilityAssignmentExistsException.class);
        // When

        // Then
        mockMvc.perform(post("/doctors/{doctorId}/assign", doctorId).content(objectMapper.writeValueAsString(medicalFacilityId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(DoctorToMedicalFacilityAssignmentExistsException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createDoctorAssignment_DoctorDoesNotExist_ThenIsNotFound() throws  Exception{
        // Given
        String exceptionMsg = "Doctor does not exist.";
        Long doctorId = 1L;
        Long medicalFacilityId = 1L;
        when(doctorService.assignDoctorToMedicalFacility(doctorId, medicalFacilityId)).thenThrow(DoctorDoesNotExistException.class);
        // When

        // Then
        mockMvc.perform(post("/doctors/{doctorId}/assign", doctorId).content(objectMapper.writeValueAsString(medicalFacilityId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(DoctorDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createDoctorAssignment_MedicalFacilityDoesNotExist_ThenIsNotFound() throws  Exception{
        // Given
        String exceptionMsg = "Medical facility does not exist.";
        Long doctorId = 1L;
        Long medicalFacilityId = 1L;
        when(doctorService.assignDoctorToMedicalFacility(doctorId, medicalFacilityId)).thenThrow(MedicalFacilityDoesNotExistException.class);
        // When

        // Then
        mockMvc.perform(post("/doctors/{doctorId}/assign", doctorId).content(objectMapper.writeValueAsString(medicalFacilityId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(MedicalFacilityDoesNotExistException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(exceptionMsg, result.getResponse().getContentAsString()));
    }

    @Test
    void createDoctorAssignment_ValidAssignmentIsGiven_DoctorDTOReturned() throws  Exception{
        // Given
        Long doctorId = 1L;
        Long medicalFacilityId = 1L;
        Doctor doctor = DoctorFactory.getDoctor();
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        doctor.getMedicalFacilities().add(medicalFacility);
        when(doctorService.assignDoctorToMedicalFacility(doctorId, medicalFacilityId)).thenReturn(doctor);
        // When

        // Then
        mockMvc.perform(post("/doctors/{doctorId}/assign", doctorId).content(objectMapper.writeValueAsString(medicalFacilityId)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jankow@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.specialisation").value("Psychiatra"))
                .andExpect(jsonPath("$.medicalFacilityIds[0]").value("1"));
    }
}
