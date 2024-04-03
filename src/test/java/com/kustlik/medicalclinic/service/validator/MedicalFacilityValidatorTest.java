package com.kustlik.medicalclinic.service.validator;

import com.kustlik.medicalclinic.exception.EmptyFieldException;
import com.kustlik.medicalclinic.exception.MedicalFacilityDoesNotExistException;
import com.kustlik.medicalclinic.exception.MedicalFacilityExistsException;
import com.kustlik.medicalclinic.factory.MedicalFacilityFactory;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.repository.MedicalFacilityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class MedicalFacilityValidatorTest {
    MedicalFacilityRepository medicalFacilityRepository;
    MedicalFacilityValidator medicalFacilityValidator;

    @BeforeEach
    void setup() {
        this.medicalFacilityRepository = Mockito.mock(MedicalFacilityRepository.class);
        this.medicalFacilityValidator = new MedicalFacilityValidator(medicalFacilityRepository);
    }

    @Test
    void validateMedicalFacilityCreation_NullMedicalFacilityIsGiven_EmptyFieldExceptionThrown() {
        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> medicalFacilityValidator.validateMedicalFacilityCreation(null));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateMedicalFacilityCreation_MedicalFacilityWithSomeEmptyFieldsIsGiven_EmptyFieldExceptionThrown() {
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility(
                1L,
                "jankow@gmail.com",
                "Jan",
                null,
                "password123",
                null,
                null);
        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> medicalFacilityValidator.validateMedicalFacilityCreation(medicalFacility));
        String expectedMessage = "No empty argument is allowed.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateMedicalFacilityCreation_MedicalFacilityWithSameNameDoesExists_MedicalFacilityExistsExceptionThrown() {
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(medicalFacilityRepository.findByName(medicalFacility.getName())).thenReturn(Optional.of(medicalFacility));
        // Then
        var exception = Assertions.assertThrows(MedicalFacilityExistsException.class,
                () -> medicalFacilityValidator.validateMedicalFacilityCreation(medicalFacility));
        String expectedMessage = "Medical facility with given name exists.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateMedicalFacilityCreation_MedicalFacilityWithSameNameDoesNotExist_NoExceptionThrown() {
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(medicalFacilityRepository.findByName(medicalFacility.getName())).thenReturn(Optional.empty());
        // When
        medicalFacilityValidator.validateMedicalFacilityCreation(medicalFacility);
    }

    @Test
    void validateMedicalFacility_MedicalFacilityWithNullFieldsIsGiven_FalseReturned() {
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility(
                1L,
                "43",
                null,
                "Covermedi",
                null,
                "65-346",
                null);
        // When
        var result = medicalFacilityValidator.validateMedicalFacility(medicalFacility);
        // Then
        Assertions.assertFalse(result);
    }

    @Test
    void validateMedicalFacility_MedicalFacilityWithValidFieldsIsGiven_TrueReturned() {
        // Given
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        // When
        var result = medicalFacilityValidator.validateMedicalFacility(medicalFacility);
        // Then
        Assertions.assertTrue(result);
    }

    @Test
    void medicalFacilityExists_MedicalFacilityIdGivenIsNull_EmptyFieldExceptionExceptionThrown() {
        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> medicalFacilityValidator.medicalFacilityExists(null));
        String expectedMessage = "Medical facility ID cannot be empty.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void medicalFacilityExists_MedicalFacilityWithGivenIdDoesNotExist_MedicalFacilityDoesNotExistExceptionThrown() {
        // Given
        Long medicalFacilityID = 1L;
        when(medicalFacilityRepository.findById(medicalFacilityID)).thenReturn(Optional.empty());
        // Then
        var exception = Assertions.assertThrows(MedicalFacilityDoesNotExistException.class,
                () -> medicalFacilityValidator.medicalFacilityExists(medicalFacilityID));
        String expectedMessage = "Medical facility with given ID does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void medicalFacilityExists_MedicalFacilityWithGivenIdDoesExists_MedicalFacilityReturned() {
        // Given
        Long medicalFacilityID = 1L;
        MedicalFacility medicalFacility = MedicalFacilityFactory.getMedicalFacility();
        when(medicalFacilityRepository.findById(medicalFacilityID)).thenReturn(Optional.of(medicalFacility));
        // When
        var result = medicalFacilityValidator.medicalFacilityExists(medicalFacilityID);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(medicalFacility, result);
    }
}
