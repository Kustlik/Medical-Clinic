package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.MedicalFacility;

import java.util.List;

public interface MedicalFacilityService {
    List<MedicalFacility> getMedicalFacilities();

    MedicalFacility getMedicalFacility(Long id);

    MedicalFacility createMedicalFacility(MedicalFacility medicalFacility);

    MedicalFacility assignMedicalFacilityToDoctor(Long medicalFacilityID, Long doctorID);
}
