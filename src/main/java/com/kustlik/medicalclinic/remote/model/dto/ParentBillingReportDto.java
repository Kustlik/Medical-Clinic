package com.kustlik.medicalclinic.remote.model.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class ParentBillingReportDto {
    private final int parentId;
    private final double totalFees;
    private final int numberOfPaidHours;
    private final List<ChildReportDto> childrenReport;
}
