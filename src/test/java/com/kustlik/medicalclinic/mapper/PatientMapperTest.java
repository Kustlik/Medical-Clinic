package com.kustlik.medicalclinic.mapper;

import com.kustlik.medicalclinic.model.dto.patient.PatientCreationDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientPasswordDTO;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.model.mapper.PatientMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

public class PatientMapperTest {
    PatientMapper patientMapper;

    @BeforeEach
    void setup() {
        this.patientMapper = Mappers.getMapper(PatientMapper.class);
    }

    @Test
    void toDto_PatientIsMappedToDTO_PatientDTOReturned() {
        //Given
        Patient patient = Patient.builder()
                .id(1L)
                .email("jankow@gmail.com")
                .idCardNo("12345")
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password123")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        PatientDTO patientDTO = PatientDTO.builder()
                .email("jankow@gmail.com")
                .firstName("Jan")
                .lastName("Kowalski")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        //When
        var result = patientMapper.toDto(patient);
        //Then
        Assertions.assertInstanceOf(PatientDTO.class, result);
        Assertions.assertEquals(patient.getEmail(), patientDTO.getEmail());
        Assertions.assertEquals(patient.getFirstName(), patientDTO.getFirstName());
        Assertions.assertEquals(patient.getLastName(), patientDTO.getLastName());
        Assertions.assertEquals(patient.getBirthday(), patientDTO.getBirthday());
    }

    @Test
    void toPatient_PatientCreationDTOIsMappedToPatient_PatientReturned() {
        //Given
        PatientCreationDTO patientDTO = PatientCreationDTO.builder()
                .email("jankow@gmail.com")
                .idCardNo("12345")
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password123")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Patient patient = Patient.builder()
                .email("jankow@gmail.com")
                .idCardNo("12345")
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password123")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        //When
        var result = patientMapper.toPatient(patientDTO);
        //Then
        Assertions.assertInstanceOf(Patient.class, result);
        Assertions.assertEquals(patient.getEmail(), patientDTO.getEmail());
        Assertions.assertEquals(patient.getIdCardNo(), patientDTO.getIdCardNo());
        Assertions.assertEquals(patient.getFirstName(), patientDTO.getFirstName());
        Assertions.assertEquals(patient.getLastName(), patientDTO.getLastName());
        Assertions.assertEquals(patient.getPassword(), patientDTO.getPassword());
        Assertions.assertEquals(patient.getBirthday(), patientDTO.getBirthday());
    }

    @Test
    void toPatient_PatientDTOIsMappedToPatient_PatientReturned() {
        //Given
        PatientDTO patientDTO = PatientDTO.builder()
                .email("jankow@gmail.com")
                .firstName("Jan")
                .lastName("Kowalski")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Patient patient = Patient.builder()
                .email("jankow@gmail.com")
                .firstName("Jan")
                .lastName("Kowalski")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        //When
        var result = patientMapper.toPatient(patientDTO);
        //Then
        Assertions.assertInstanceOf(Patient.class, result);
        Assertions.assertEquals(patient.getEmail(), patientDTO.getEmail());
        Assertions.assertEquals(patient.getFirstName(), patientDTO.getFirstName());
        Assertions.assertEquals(patient.getLastName(), patientDTO.getLastName());
        Assertions.assertEquals(patient.getBirthday(), patientDTO.getBirthday());
    }

    @Test
    void toPatient_PatientPasswordDTOIsMappedToPatient_PatientReturned() {
        //Given
        PatientPasswordDTO patientDTO = PatientPasswordDTO.builder()
                .email("jankow@gmail.com")
                .password("password123")
                .build();
        Patient patient = Patient.builder()
                .email("jankow@gmail.com")
                .password("password123")
                .build();
        //When
        var result = patientMapper.toPatient(patientDTO);
        //Then
        Assertions.assertInstanceOf(Patient.class, result);
        Assertions.assertEquals(patient.getEmail(), patientDTO.getEmail());
        Assertions.assertEquals(patient.getPassword(), patientDTO.getPassword());
    }
}
