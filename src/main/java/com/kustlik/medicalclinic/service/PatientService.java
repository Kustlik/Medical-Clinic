package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.Patient;

import java.util.List;

public interface PatientService {
    List<Patient> getPatients();
    Patient getPatient(String email);
    Patient createPatient(Patient patient);
    void deletePatient(String email);
    Patient editPatient(String email, Patient newPatientData);
    void editPatientPassword(String email, Patient password);
}
