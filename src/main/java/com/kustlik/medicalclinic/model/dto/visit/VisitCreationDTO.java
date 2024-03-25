package com.kustlik.medicalclinic.model.dto.visit;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@EqualsAndHashCode
public class VisitCreationDTO {
    private final LocalDateTime appointmentStart;
    private final LocalDateTime appointmentEnd;
}
