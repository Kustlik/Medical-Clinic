package com.kustlik.medicalclinic.model.dto.doctor;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class DoctorDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String specialisation;
    private List<Long> medicalFacilityIds;


}
