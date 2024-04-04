package com.kustlik.medicalclinic.remote.model.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class ChildReportDto {
    private final int childId;
    private final String firstName;
    private final String lastName;
    private final List<ChildAttendanceDto> attendances;
    private final int numberOfPaidHours;
}
