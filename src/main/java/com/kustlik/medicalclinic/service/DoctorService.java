package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {
    Page<Doctor> getDoctors(Pageable pageable);

    Doctor getDoctor(String email);

    Doctor createDoctor(Doctor doctor);

    Doctor assignDoctorToMedicalFacility(Long doctorID, Long medicalFacilityID);
}
