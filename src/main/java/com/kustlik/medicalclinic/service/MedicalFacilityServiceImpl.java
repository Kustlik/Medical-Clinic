package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import com.kustlik.medicalclinic.repository.MedicalFacilityRepository;
import com.kustlik.medicalclinic.service.validator.DoctorValidator;
import com.kustlik.medicalclinic.service.validator.MedicalFacilityValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalFacilityServiceImpl implements MedicalFacilityService {
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final MedicalFacilityValidator medicalFacilityValidator;
    private final DoctorRepository doctorRepository;
    private final DoctorValidator doctorValidator;

    public List<MedicalFacility> getMedicalFacilities() {
        return medicalFacilityRepository.findAll();
    }

    public MedicalFacility getMedicalFacility(Long id) {
        return medicalFacilityValidator.medicalFacilityExists(id);
    }

    @Transactional
    public MedicalFacility createMedicalFacility(MedicalFacility medicalFacility) {
        medicalFacilityValidator.validateMedicalFacilityCreation(medicalFacility);
        return medicalFacilityRepository.save(medicalFacility);
    }

    @Transactional
    public MedicalFacility assignMedicalFacilityToDoctor(Long medicalFacilityID, Long doctorID) {
        MedicalFacility medicalFacility = medicalFacilityValidator.medicalFacilityExists(medicalFacilityID);
        Doctor doctor = doctorValidator.doctorExists(doctorID);
        doctorValidator.validateDoctorToMedicalFacilityAssignment(doctor, medicalFacility);
        doctor.getMedicalFacilities().add(medicalFacility);
        doctorRepository.save(doctor);
        return medicalFacility;
    }
}
