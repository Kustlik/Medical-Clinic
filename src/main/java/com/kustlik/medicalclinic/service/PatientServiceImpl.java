package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.repository.PatientRepository;
import com.kustlik.medicalclinic.service.validator.PatientValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientValidator patientValidator;

    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatient(String email) {
        return patientValidator.patientExists(email);
    }

    @Transactional
    public Patient createPatient(Patient patient) {
        patientValidator.validatePatientCreation(patient);
        return patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(String email) {
        Patient patient = patientValidator.patientExists(email);
        patientRepository.delete(patient);
    }

    @Transactional
    public Patient editPatient(String email, Patient newPatientData) {
        var toEdit = patientValidator.patientExists(email);
        patientValidator.validateEditPatientData(email, newPatientData);
        toEdit.update(newPatientData);
        patientRepository.save(toEdit);
        return toEdit;
    }

    @Transactional
    public void editPatientPassword(String email, Patient newPatientData) {
        patientValidator.validatePasswordChange(newPatientData);
        var toEdit = patientValidator.patientExists(email);
        toEdit.setPassword(newPatientData.getPassword());
        patientRepository.save(toEdit);
    }
}
