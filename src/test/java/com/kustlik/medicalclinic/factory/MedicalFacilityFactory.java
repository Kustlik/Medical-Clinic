package com.kustlik.medicalclinic.factory;

import com.kustlik.medicalclinic.model.dto.medical_facility.MedicalFacilityDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;

import java.util.ArrayList;
import java.util.List;

public class MedicalFacilityFactory {
    public static MedicalFacility getMedicalFacility(){
        return getMedicalFacility(
                1L,
                "43",
                "Warszawa",
                "Covermedi",
                "Opolska",
                "65-346",
                new ArrayList<>());
    }

    public static MedicalFacility getMedicalFacility(Long id, String buildingNumber, String city, String name, String street, String zipCode, List<Doctor> doctors){
        return MedicalFacility.builder()
                .id(id)
                .buildingNumber(buildingNumber)
                .city(city)
                .name(name)
                .street(street)
                .zipCode(zipCode)
                .doctors(doctors)
                .build();
    }

    public static MedicalFacilityDTO getMedicalFacilityDTO(){
        return getMedicalFacilityDTO(
                "43",
                "Warszawa",
                "Covermedi",
                "Opolska",
                "65-346",
                new ArrayList<>());
    }

    public static MedicalFacilityDTO getMedicalFacilityDTO(String buildingNumber, String city, String name, String street, String zipCode, List<Long> doctorIds){
        return MedicalFacilityDTO.builder()
                .buildingNumber(buildingNumber)
                .city(city)
                .name(name)
                .street(street)
                .zipCode(zipCode)
                .doctorIds(doctorIds)
                .build();
    }
}
