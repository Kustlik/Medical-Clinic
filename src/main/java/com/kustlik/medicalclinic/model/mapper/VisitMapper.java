package com.kustlik.medicalclinic.model.mapper;

import com.kustlik.medicalclinic.exception.VisitWithNoAssignedDoctorException;
import com.kustlik.medicalclinic.model.dto.visit.VisitCreationDTO;
import com.kustlik.medicalclinic.model.dto.visit.VisitDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.model.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    @Mapping(source = "doctor", target = "doctorId", qualifiedByName = "doctorToId")
    @Mapping(source = "patient", target = "patientId", qualifiedByName = "patientToId")
    VisitDTO toDto(Visit visit);

    Visit toVisit(VisitDTO visitDTO);

    Visit toVisit(VisitCreationDTO visitCreationDTO);

    @Named("doctorToId")
    static Long doctorToId(Doctor doctor) {
        if (doctor != null) {
            return doctor.getId();
        } else {
            return null;
        }
    }

    @Named("patientToId")
    static Long patientToId(Patient patient) {
        if (patient != null) {
            return patient.getId();
        } else {
            return null;
        }
    }
}
