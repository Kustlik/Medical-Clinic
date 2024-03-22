package com.kustlik.medicalclinic.model.mapper;

import com.kustlik.medicalclinic.model.dto.patient.PatientCreationDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientPasswordDTO;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.model.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(source = "visits", target = "visitIds", qualifiedByName = "visitListToIdList")
    PatientDTO toDto(Patient patient);

    Patient toPatient(PatientCreationDTO patientDTO);

    Patient toPatient(PatientDTO patientDTO);

    Patient toPatient(PatientPasswordDTO patientDTO);

    @Named("visitListToIdList")
    static List<Long> visitListToIdList(List<Visit> visits) {
        if (visits == null) {
            return new ArrayList<>();
        } else {
            return visits.stream().map(Visit::getId).collect(Collectors.toList());
        }
    }
}
