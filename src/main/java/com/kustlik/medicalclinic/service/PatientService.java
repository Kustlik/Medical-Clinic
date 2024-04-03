package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    Page<Patient> getPatients(Pageable pageable);

    Patient getPatient(String email);

    Patient createPatient(Patient patient);

    void deletePatient(String email);

    Patient editPatient(String email, Patient newPatientData);

    void editPatientPassword(String email, Patient password);
}
