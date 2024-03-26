package com.kustlik.medicalclinic.factory;

import com.kustlik.medicalclinic.model.dto.patient.PatientCreationDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientPasswordDTO;
import com.kustlik.medicalclinic.model.entity.Patient;

import java.time.LocalDate;

public class PatientFactory {
    public static Patient getPatient() {
        return getPatient(
                1L,
                "jankow@gmail.com",
                "12345",
                "Jan",
                "Kowalski",
                "password123",
                LocalDate.of(2000, 1, 1));
    }

    public static Patient getPatient(Long id, String email, String idCardNo, String firstName, String lastName, String password, LocalDate birthday) {
        return Patient.builder()
                .id(id)
                .email(email)
                .idCardNo(idCardNo)
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .birthday(birthday)
                .build();
    }

    public static PatientDTO getPatientDTO() {
        return getPatientDTO(
                "jankow@gmail.com",
                "Jan",
                "Kowalski",
                LocalDate.of(2000, 1, 1)
        );
    }

    public static PatientDTO getPatientDTO(String email, String firstName, String lastName, LocalDate birthday) {
        return PatientDTO.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .birthday(birthday)
                .build();
    }

    public static PatientCreationDTO getPatientCreationDTO() {
        return getPatientCreationDTO(
                "jankow@gmail.com",
                "12345",
                "Jan",
                "Kowalski",
                "password123",
                LocalDate.of(2000, 1, 1));
    }

    public static PatientCreationDTO getPatientCreationDTO(String email, String idCardNo, String firstName, String lastName, String password, LocalDate birthday) {
        return PatientCreationDTO.builder()
                .email(email)
                .idCardNo(idCardNo)
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .birthday(birthday)
                .build();
    }

    public static PatientPasswordDTO getPatientPasswordDTO() {
        return getPatientPasswordDTO(
                "jankow@gmail.com",
                "password123");
    }

    public static PatientPasswordDTO getPatientPasswordDTO(String email, String password) {
        return PatientPasswordDTO.builder()
                .email(email)
                .password(password)
                .build();
    }
}
