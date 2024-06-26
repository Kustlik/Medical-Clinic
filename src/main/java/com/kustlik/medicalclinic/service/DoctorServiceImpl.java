package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import com.kustlik.medicalclinic.service.validator.DoctorValidator;
import com.kustlik.medicalclinic.service.validator.MedicalFacilityValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorValidator doctorValidator;
    private final MedicalFacilityValidator medicalFacilityValidator;

    public Page<Doctor> getDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    public Doctor getDoctor(String email) {
        return doctorValidator.doctorExists(email);
    }

    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        doctorValidator.validateDoctorCreation(doctor);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor assignDoctorToMedicalFacility(Long doctorID, Long medicalFacilityID) {
        Doctor doctor = doctorValidator.doctorExists(doctorID);
        MedicalFacility medicalFacility = medicalFacilityValidator.medicalFacilityExists(medicalFacilityID);
        doctorValidator.validateDoctorToMedicalFacilityAssignment(doctor, medicalFacility);
        doctor.getMedicalFacilities().add(medicalFacility);
        return doctorRepository.save(doctor);
    }
}
