package com.kustlik.medicalclinic.model.dto.patient;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class PatientPasswordDTO {
    private final String email;
    private final String password;
}
