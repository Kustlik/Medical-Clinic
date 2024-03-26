package com.kustlik.medicalclinic.service.validator;

import com.kustlik.medicalclinic.exception.DoctorDoesNotExistException;
import com.kustlik.medicalclinic.exception.DoctorExistsException;
import com.kustlik.medicalclinic.exception.DoctorToMedicalFacilityAssignmentExistsException;
import com.kustlik.medicalclinic.exception.EmptyFieldException;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DoctorValidator {
    private final DoctorRepository doctorRepository;

    public void validateDoctorCreation(Doctor doctor) {
        doctorHasValidFields(doctor);
        doctorWithSameEmailDoesNotExist(doctor);
    }

    public void validateDoctorToMedicalFacilityAssignment(Doctor doctor, MedicalFacility medicalFacility) {
        doctorIsNotAssignedToSameMedicalFacility(doctor, medicalFacility);
    }

    public boolean validateDoctor(Doctor doctor) {
        return Stream.of(
                        doctor.getEmail(),
                        doctor.getFirstName(),
                        doctor.getLastName(),
                        doctor.getPassword(),
                        doctor.getSpecialisation())
                .noneMatch(Objects::isNull);
    }

    public Doctor doctorExists(Long doctorID) {
        if (doctorID == null)
            throw new EmptyFieldException("Doctor ID cannot be empty.");
        var existingDoctor = doctorRepository.findById(doctorID);
        if (existingDoctor.isEmpty())
            throw new DoctorDoesNotExistException("Doctor with given ID does not exist.");
        return existingDoctor.get();
    }

    public Doctor doctorExists(String email) {
        return doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorDoesNotExistException("Doctor not found."));
    }

    private void doctorHasValidFields(Doctor doctor) {
        if (doctor == null || !validateDoctor(doctor))
            throw new EmptyFieldException("No empty argument is allowed.");
    }

    private void doctorWithSameEmailDoesNotExist(Doctor doctor) {
        var existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
        if (existingDoctor.isPresent())
            throw new DoctorExistsException("Doctor with given email exists.");
    }

    private void doctorIsNotAssignedToSameMedicalFacility(Doctor doctor, MedicalFacility medicalFacility) {
        var existingAssignment = doctor.getMedicalFacilities().stream()
                .filter(x -> Objects.equals(x.getId(), medicalFacility.getId()))
                .findFirst();
        if (existingAssignment.isPresent())
            throw new DoctorToMedicalFacilityAssignmentExistsException("Doctor is already assigned to this facility.");
    }
}
