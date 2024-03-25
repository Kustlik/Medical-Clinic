package com.kustlik.medicalclinic.model.dto.doctor;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class DoctorDTO {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String specialisation;
    private final List<Long> medicalFacilityIds;
    private final List<Long> visitIds;
}
