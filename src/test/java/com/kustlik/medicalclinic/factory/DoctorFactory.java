package com.kustlik.medicalclinic.factory;

import com.kustlik.medicalclinic.model.dto.doctor.DoctorCreationDTO;
import com.kustlik.medicalclinic.model.dto.doctor.DoctorDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;

import java.util.ArrayList;
import java.util.List;

public class DoctorFactory {
    public static Doctor getDoctor(){
        return getDoctor(
                1L,
                "jankow@gmail.com",
                "Jan",
                "Kowalski",
                "password123",
                "Psychiatra",
                new ArrayList<>());
    }

    public static Doctor getDoctor(Long id, String email, String firstName, String lastName, String password, String specialisation, List<MedicalFacility> medicalFacilities){
        return Doctor.builder()
                .id(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .specialisation(specialisation)
                .medicalFacilities(medicalFacilities)
                .build();
    }

    public static DoctorDTO getDoctorDTO(){
        return getDoctorDTO(
                "jankow@gmail.com",
                "Jan",
                "Kowalski",
                "Psychiatra",
                new ArrayList<>()
        );
    }

    public static DoctorDTO getDoctorDTO(String email, String firstName, String lastName, String specialisation, List<Long> medicalFacilityIds){
        return DoctorDTO.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .specialisation(specialisation)
                .medicalFacilityIds(medicalFacilityIds)
                .build();
    }

    public static DoctorCreationDTO getDoctorCreationDTO(){
        return getDoctorCreationDTO(
                "jankow@gmail.com",
                "Jan",
                "Kowalski",
                "password123",
                "Psychiatra");
    }

    public static DoctorCreationDTO getDoctorCreationDTO(String email, String firstName, String lastName, String password, String specialisation){
        return DoctorCreationDTO.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .specialisation(specialisation)
                .build();
    }
}
