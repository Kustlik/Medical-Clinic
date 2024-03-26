package com.kustlik.medicalclinic.service.validator;

import com.kustlik.medicalclinic.exception.EmptyFieldException;
import com.kustlik.medicalclinic.exception.MedicalFacilityDoesNotExistException;
import com.kustlik.medicalclinic.exception.MedicalFacilityExistsException;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.repository.MedicalFacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MedicalFacilityValidator {
    private final MedicalFacilityRepository medicalFacilityRepository;

    public void validateMedicalFacilityCreation(MedicalFacility medicalFacility) {
        medicalFacilityHasValidFields(medicalFacility);
        medicalFacilityWithSameNameDoesNotExist(medicalFacility);
    }

    public MedicalFacility medicalFacilityExists(Long medicalFacilityID) {
        if (medicalFacilityID == null)
            throw new EmptyFieldException("Medical facility ID cannot be empty.");
        var existingMedicalFacility = medicalFacilityRepository.findById(medicalFacilityID);
        if (existingMedicalFacility.isEmpty())
            throw new MedicalFacilityDoesNotExistException("Medical facility with given ID does not exist.");
        return existingMedicalFacility.get();
    }

    public boolean validateMedicalFacility(MedicalFacility medicalFacility) {
        return Stream.of(
                        medicalFacility.getName(),
                        medicalFacility.getCity(),
                        medicalFacility.getZipCode(),
                        medicalFacility.getStreet(),
                        medicalFacility.getBuildingNumber())
                .noneMatch(Objects::isNull);
    }

    private void medicalFacilityHasValidFields(MedicalFacility medicalFacility) {
        if (medicalFacility == null || !validateMedicalFacility(medicalFacility))
            throw new EmptyFieldException("No empty argument is allowed.");
    }

    private void medicalFacilityWithSameNameDoesNotExist(MedicalFacility medicalFacility) {
        var existingFacility = medicalFacilityRepository.findByName(medicalFacility.getName());
        if (existingFacility.isPresent())
            throw new MedicalFacilityExistsException("Medical facility with given name exists.");
    }
}
