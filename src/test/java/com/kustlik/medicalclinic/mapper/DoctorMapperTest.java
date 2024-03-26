package com.kustlik.medicalclinic.mapper;

import com.kustlik.medicalclinic.model.dto.doctor.DoctorCreationDTO;
import com.kustlik.medicalclinic.model.dto.doctor.DoctorDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.mapper.DoctorMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class DoctorMapperTest {
    DoctorMapper doctorMapper;

    @BeforeEach
    void setup() {
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
    }

    @Test
    void toDto_DoctorIsMappedToDTO_DoctorDTOReturned() {
        //Given
        Doctor doctor = Doctor.builder()
                .id(1L)
                .email("jankow@gmail.com")
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password123")
                .specialisation("Ortopeda")
                .build();

        DoctorDTO doctorDTO = DoctorDTO.builder()
                .email("jankow@gmail.com")
                .firstName("Jan")
                .lastName("Kowalski")
                .specialisation("Ortopeda")
                .build();
        //When
        var result = doctorMapper.toDto(doctor);
        //Then
        Assertions.assertInstanceOf(DoctorDTO.class, result);
        Assertions.assertEquals(doctor.getEmail(), doctorDTO.getEmail());
        Assertions.assertEquals(doctor.getFirstName(), doctorDTO.getFirstName());
        Assertions.assertEquals(doctor.getLastName(), doctorDTO.getLastName());
        Assertions.assertEquals(doctor.getSpecialisation(), doctorDTO.getSpecialisation());
    }

    @Test
    void toDoctor_DoctorCreationDTOIsMappedToDoctor_DoctorReturned() {
        //Given
        DoctorCreationDTO doctorDTO = DoctorCreationDTO.builder()
                .email("jankow@gmail.com")
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password123")
                .specialisation("Ortopeda")
                .build();
        Doctor doctor = Doctor.builder()
                .email("jankow@gmail.com")
                .firstName("Jan")
                .lastName("Kowalski")
                .password("password123")
                .specialisation("Ortopeda")
                .build();
        //When
        var result = doctorMapper.toDoctor(doctorDTO);
        //Then
        Assertions.assertInstanceOf(Doctor.class, result);
        Assertions.assertEquals(doctor.getEmail(), doctorDTO.getEmail());
        Assertions.assertEquals(doctor.getFirstName(), doctorDTO.getFirstName());
        Assertions.assertEquals(doctor.getLastName(), doctorDTO.getLastName());
        Assertions.assertEquals(doctor.getPassword(), doctorDTO.getPassword());
        Assertions.assertEquals(doctor.getSpecialisation(), doctorDTO.getSpecialisation());
    }
}
