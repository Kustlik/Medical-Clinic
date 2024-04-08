package com.kustlik.medicalclinic.service.validator;

import com.kustlik.medicalclinic.exception.EmptyFieldException;
import com.kustlik.medicalclinic.exception.PatientDoesNotExistException;
import com.kustlik.medicalclinic.exception.PatientExistsException;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class PatientValidator {
    final private PatientRepository patientRepository;

    public void validatePatientCreation(Patient patient) {
        patientHasValidFields(patient);
        patientWithSameEmailDoesNotExist(patient);
    }

    public void validateEditPatientData(String email, Patient newPatientData) {
        patientHasValidFieldsToEdit(newPatientData);
        if (!email.equalsIgnoreCase(newPatientData.getEmail())) {
            patientWithSameEmailDoesNotExist(newPatientData);
        }
    }

    public void validatePasswordChange(Patient newPatientData) {
        patientPasswordIsNotNullNorBlank(newPatientData);
    }

    public boolean validatePatient(Patient patient) {
        return Stream.of(
                        patient.getEmail(),
                        patient.getIdCardNo(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getPassword(),
                        patient.getBirthday())
                .noneMatch(Objects::isNull);
    }

    public boolean validateEdit(Patient patient) {
        return Stream.of(
                        patient.getEmail(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getBirthday())
                .noneMatch(Objects::isNull);
    }

    public Patient patientExists(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistException("Patient with given email does not exist."));
    }

    public Patient patientExists(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientDoesNotExistException("Patient with given ID does not exist."));
    }

    private void patientPasswordIsNotNullNorBlank(Patient newPatientData) {
        if (newPatientData == null || newPatientData.getPassword() == null || newPatientData.getPassword().isBlank()) {
            throw new EmptyFieldException("Password is empty.");
        }
    }

    private void patientHasValidFields(Patient patient) {
        if (patient == null || !validatePatient(patient))
            throw new EmptyFieldException("No empty argument is allowed.");
    }

    private void patientHasValidFieldsToEdit(Patient toEdit) {
        if (toEdit == null || !validateEdit(toEdit))
            throw new EmptyFieldException("No empty argument to edit is allowed.");
    }

    private void patientWithSameEmailDoesNotExist(Patient patient) {
        var existingDoctor = patientRepository.findByEmail(patient.getEmail());
        if (existingDoctor.isPresent())
            throw new PatientExistsException("Patient with given email exists.");
    }
}
