package com.kustlik.medicalclinic.model.mapper;

import com.kustlik.medicalclinic.exception.DoctorDoesNotExistException;
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

    @Named("doctorToId")
    static Long doctorToId(Doctor doctor){
        if(doctor != null){
            return doctor.getId();
        }
        else {
            throw new DoctorDoesNotExistException("The visit has no doctor assigned.");
        }
    }

    @Named("patientToId")
    static Long doctorToId(Patient patient){
        if(patient != null){
            return patient.getId();
        }
        else {
            return null;
        }
    }
}
