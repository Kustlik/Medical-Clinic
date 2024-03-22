package com.kustlik.medicalclinic.model.mapper;

import com.kustlik.medicalclinic.model.dto.doctor.DoctorCreationDTO;
import com.kustlik.medicalclinic.model.dto.doctor.DoctorDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.model.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(source = "medicalFacilities", target = "medicalFacilityIds", qualifiedByName = "facilityListToIdList")
    @Mapping(source = "visits", target = "visitIds", qualifiedByName = "visitListToIdList")
    DoctorDTO toDto(Doctor doctor);

    Doctor toDoctor(DoctorCreationDTO doctorDTO);

    @Named("facilityListToIdList")
    static List<Long> facilityListToIdList(List<MedicalFacility> medicalFacilities) {
        if (medicalFacilities == null) {
            return new ArrayList<>();
        } else {
            return medicalFacilities.stream().map(MedicalFacility::getId).collect(Collectors.toList());
        }
    }

    @Named("visitListToIdList")
    static List<Long> visitListToIdList(List<Visit> visits) {
        if (visits == null) {
            return new ArrayList<>();
        } else {
            return visits.stream().map(Visit::getId).collect(Collectors.toList());
        }
    }
}
