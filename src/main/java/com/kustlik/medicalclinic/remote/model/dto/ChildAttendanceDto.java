package com.kustlik.medicalclinic.remote.model.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@EqualsAndHashCode
public class ChildAttendanceDto {
    private final LocalDate entryDate;
    private final LocalDate exitDate;
}
