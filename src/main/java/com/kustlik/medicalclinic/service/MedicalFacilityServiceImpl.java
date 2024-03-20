package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import com.kustlik.medicalclinic.repository.MedicalFacilityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MedicalFacilityServiceImpl implements MedicalFacilityService{
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final DoctorRepository doctorRepository;
    public List<MedicalFacility> getMedicalFacilities(){
        return medicalFacilityRepository.findAll();
    }
    public MedicalFacility getMedicalFacility(Long id){
        return medicalFacilityRepository.findById(id)
                .orElseThrow(() -> new MedicalFacilityDoesNotExistException("Medical facility not found."));
    }

    @Transactional
    public MedicalFacility createMedicalFacility(MedicalFacility medicalFacility){
        if (medicalFacility == null || !medicalFacility.validateMedicalFacility())
            throw new EmptyFieldException("No empty argument is allowed.");
        var existingFacility = medicalFacilityRepository.findByName(medicalFacility.getName());
        if (existingFacility.isPresent())
            throw new MedicalFacilityExistsException("Medical facility with given name exists.");
        return medicalFacilityRepository.save(medicalFacility);
    }

    @Transactional
    public MedicalFacility assignMedicalFacilityToDoctor(Long medicalFacilityID, Long doctorID){
        if (doctorID == null || medicalFacilityID == null)
            throw new EmptyFieldException("No empty argument is allowed.");
        var existingDoctor = doctorRepository.findById(doctorID);
        if (existingDoctor.isEmpty())
            throw new DoctorDoesNotExistException("Doctor with given ID does not exist.");
        var existingMedicalFacility = medicalFacilityRepository.findById(doctorID);
        if (existingMedicalFacility.isEmpty())
            throw new MedicalFacilityDoesNotExistException("Medical facility with given ID does not exist.");
        var existingAssignment = existingDoctor.get().getMedicalFacilities().stream()
                .filter(x -> Objects.equals(x.getId(), medicalFacilityID))
                .findFirst();
        if (existingAssignment.isPresent())
            throw new DoctorToMedicalFacilityAssignmentExistsException("Doctor is already assigned to this facility.");
        existingDoctor.get().getMedicalFacilities().add(existingMedicalFacility.get());
        doctorRepository.save(existingDoctor.get());
        return existingMedicalFacility.get();
    }
}
