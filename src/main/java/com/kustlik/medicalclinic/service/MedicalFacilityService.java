package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicalFacilityService {
    Page<MedicalFacility> getMedicalFacilities(Pageable pageable);

    MedicalFacility getMedicalFacility(Long id);

    MedicalFacility createMedicalFacility(MedicalFacility medicalFacility);

    MedicalFacility assignMedicalFacilityToDoctor(Long medicalFacilityID, Long doctorID);
}
