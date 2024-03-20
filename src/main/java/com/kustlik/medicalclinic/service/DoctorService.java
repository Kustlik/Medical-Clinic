package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.Doctor;

import java.util.List;

public interface DoctorService {
    List<Doctor> getDoctors();
    Doctor getDoctor(String email);
    Doctor createDoctor(Doctor doctor);
    Doctor assignDoctorToMedicalFacility(Long doctorID, Long medicalFacilityID);
}
