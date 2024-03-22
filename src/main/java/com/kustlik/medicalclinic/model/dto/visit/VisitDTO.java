package com.kustlik.medicalclinic.model.dto.visit;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@EqualsAndHashCode
public class VisitDTO {
    final private LocalDateTime appointmentStart;
    final private LocalDateTime appointmentEnd;
    final private Long doctorId;
    final private Long patientId;
}
