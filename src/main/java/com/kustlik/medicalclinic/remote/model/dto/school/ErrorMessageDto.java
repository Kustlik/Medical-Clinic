package com.kustlik.medicalclinic.remote.model.dto.school;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class ErrorMessageDto {
    private final String message;
}
