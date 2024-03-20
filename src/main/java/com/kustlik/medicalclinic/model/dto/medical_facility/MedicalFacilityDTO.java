package com.kustlik.medicalclinic.model.dto.medical_facility;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class MedicalFacilityDTO {
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private String buildingNumber;
    private List<Long> doctorIds;
}
