package com.kustlik.medicalclinic.model.mapper;

import com.kustlik.medicalclinic.model.dto.doctor.DoctorCreationDTO;
import com.kustlik.medicalclinic.model.dto.doctor.DoctorDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(source = "medicalFacilities", target = "medicalFacilityIds", qualifiedByName = "facilityListToIdList")
    DoctorDTO toDto(Doctor doctor);
    Doctor toDoctor(DoctorCreationDTO doctorDTO);

    @Named("facilityListToIdList")
    static List<Long> facilityListToIdList(List<MedicalFacility> medicalFacilities){
        if(medicalFacilities == null){
            return new ArrayList<>();
        }
        else{
            return medicalFacilities.stream().map(MedicalFacility::getId).collect(Collectors.toList());
        }
    }
}
