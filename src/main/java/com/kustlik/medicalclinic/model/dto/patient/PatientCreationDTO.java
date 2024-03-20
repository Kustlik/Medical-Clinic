package com.kustlik.medicalclinic.model.dto.patient;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class PatientCreationDTO {
    private final String email;
    private final String idCardNo;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final LocalDate birthday;
}
