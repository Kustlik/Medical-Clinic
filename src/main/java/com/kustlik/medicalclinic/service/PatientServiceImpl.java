package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.exception.EmptyFieldException;
import com.kustlik.medicalclinic.exception.PatientDoesNotExistException;
import com.kustlik.medicalclinic.exception.PatientExistsException;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatient(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistException("Patient not found."));
    }

    @Transactional
    public Patient createPatient(Patient patient) {
        if (patient == null || !patient.validatePatient())
            throw new EmptyFieldException("No empty argument is allowed.");
        var existingPatient = patientRepository.findByEmail(patient.getEmail());
        if (existingPatient.isPresent())
            throw new PatientExistsException("Patient with given email exists.");
        return patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(String email) {
        var existingPatient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistException("Patient with given email does not exist."));

        patientRepository.delete(existingPatient);
    }

    @Transactional
    public Patient editPatient(String email, Patient newPatientData) {
        var toEdit = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistException("Patient with given email does not exist."));
        validateEditPatientData(email, newPatientData);
        toEdit.update(newPatientData);
        patientRepository.save(toEdit);
        return toEdit;
    }

    @Transactional
    public void editPatientPassword(String email, Patient newPatientData) {
        if (newPatientData == null || newPatientData.getPassword() == null || newPatientData.getPassword().isBlank()) {
            throw new EmptyFieldException("Password is empty.");
        }
        var toEdit = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistException("Patient with given email does not exist."));
        toEdit.setPassword(newPatientData.getPassword());
        patientRepository.save(toEdit);
    }

    private void validateEditPatientData(String email, Patient newPatientData) {
        if (newPatientData == null || !newPatientData.validateEdit())
            throw new EmptyFieldException("No empty argument to edit is allowed.");
        if (!email.equalsIgnoreCase(newPatientData.getEmail())) {
            var patient = patientRepository.findByEmail(newPatientData.getEmail());
            if (patient.isPresent())
                throw new PatientExistsException("New email is not available.");
        }
    }
}
