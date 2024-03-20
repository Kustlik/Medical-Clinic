package com.kustlik.medicalclinic.model.mapper;

import com.kustlik.medicalclinic.model.dto.patient.PatientCreationDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientPasswordDTO;
import com.kustlik.medicalclinic.model.entity.Patient;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.ComponentScan;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDTO toDto(Patient patient);
    Patient toPatient(PatientCreationDTO patientDTO);
    Patient toPatientEdit(PatientDTO patientDTO);
    Patient toPatientPasswordEdit(PatientPasswordDTO patientDTO);
}
