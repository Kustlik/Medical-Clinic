package com.kustlik.medicalclinic.model.mapper;

import com.kustlik.medicalclinic.model.dto.medical_facility.MedicalFacilityDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MedicalFacilityMapper {
    @Named("doctorListToIdList")
    static List<Long> doctorListToIdList(List<Doctor> doctors) {
        if (doctors == null) {
            return new ArrayList<>();
        }
        return doctors.stream().map(Doctor::getId).collect(Collectors.toList());
    }

    @Mapping(source = "doctors", target = "doctorIds", qualifiedByName = "doctorListToIdList")
    MedicalFacilityDTO toDto(MedicalFacility medicalFacility);

    MedicalFacility toMedicalFacility(MedicalFacilityDTO medicalFacilityDTO);
}
