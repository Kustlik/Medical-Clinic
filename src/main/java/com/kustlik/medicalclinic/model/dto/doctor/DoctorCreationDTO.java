package com.kustlik.medicalclinic.model.dto.doctor;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class DoctorCreationDTO {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final String specialisation;
}
