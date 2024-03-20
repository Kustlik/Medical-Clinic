package com.kustlik.medicalclinic.model.dto.patient;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class PatientDTO {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final LocalDate birthday;
    private final List<Long> visitIds;
}
