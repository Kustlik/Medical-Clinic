package com.kustlik.medicalclinic.model.dto.doctor;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class DoctorCreationDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String specialisation;
}
