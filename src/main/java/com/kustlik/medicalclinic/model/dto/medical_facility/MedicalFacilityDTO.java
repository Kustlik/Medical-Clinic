package com.kustlik.medicalclinic.model.dto.medical_facility;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class MedicalFacilityDTO {
    private final String name;
    private final String city;
    private final String zipCode;
    private final String street;
    private final String buildingNumber;
    private final List<Long> doctorIds;
}
