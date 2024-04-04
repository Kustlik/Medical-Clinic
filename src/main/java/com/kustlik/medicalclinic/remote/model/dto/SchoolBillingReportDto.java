package com.kustlik.medicalclinic.remote.model.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class SchoolBillingReportDto {
    private final double totalFees;
    private final List<ParentBillingReportDto> parentBillingReports;
}
