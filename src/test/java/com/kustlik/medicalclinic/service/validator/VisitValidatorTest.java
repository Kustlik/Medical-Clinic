package com.kustlik.medicalclinic.service.validator;

import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.factory.DoctorFactory;
import com.kustlik.medicalclinic.factory.VisitFactory;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.repository.VisitRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class VisitValidatorTest {
    private static final int YEAR = LocalDateTime.now().getYear() + 1;
    private VisitRepository visitRepository;
    private VisitValidator visitValidator;
    @BeforeEach
    void setup() {
        this.visitRepository = Mockito.mock(VisitRepository.class);
        this.visitValidator = new VisitValidator(visitRepository);
    }

    @Test
    void validateVisitCreation_VisitWithSomeEmptyFieldsGiven_EmptyFieldExceptionThrown(){
        // Given
        Long doctorID = 1L;
        Visit visit = VisitFactory.getVisit(
                null,
                LocalDateTime.now().plusDays(1),
                null,
                null,
                null);
        // When

        // Then
        var exception = Assertions.assertThrows(EmptyFieldException.class,
                () -> visitValidator.validateVisitCreation(visit, doctorID));
        String expectedMessage = "Visit has some null fields, please fill everything correctly.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateVisitCreation_VisitGivenIsPast_InvalidDateTimeExceptionThrown(){
        // Given
        Long doctorID = 1L;
        Visit visit = VisitFactory.getVisit(
                1L,
                LocalDateTime.of(1990, 1, 1, 12, 0),
                LocalDateTime.of(1990, 1, 1, 12, 30),
                null,
                null);
        // When

        // Then
        var exception = Assertions.assertThrows(InvalidDateTimeException.class,
                () -> visitValidator.validateVisitCreation(visit, doctorID));
        String expectedMessage = "Unable to create a visit for past dates.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateVisitCreation_VisitGivenEndsBeforeStart_InvalidDateTimeExceptionThrown(){
        // Given
        Long doctorID = 1L;
        Visit visit = VisitFactory.getVisit(
                1L,
                LocalDateTime.of(YEAR, 1, 1, 12, 30),
                LocalDateTime.of(YEAR, 1, 1, 12, 0),
                null,
                null);
        // When

        // Then
        var exception = Assertions.assertThrows(InvalidDateTimeException.class,
                () -> visitValidator.validateVisitCreation(visit, doctorID));
        String expectedMessage = "End time should be after start time.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateVisitCreation_VisitGivenIsNotReservedInFullQuarterOfAnHour_InvalidDateTimeExceptionThrown(){
        // Given
        Long doctorID = 1L;
        Visit visit = VisitFactory.getVisit(
                1L,
                LocalDateTime.of(YEAR, 1, 1, 12, 2),
                LocalDateTime.of(YEAR, 1, 1, 12, 30),
                null,
                null);
        // When

        // Then
        var exception = Assertions.assertThrows(InvalidDateTimeException.class,
                () -> visitValidator.validateVisitCreation(visit, doctorID));
        String expectedMessage = "Visit creation is possible only for a full quarter of an hour.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateVisitCreation_VisitGivenIsShorterThanMinDuration_InvalidDateTimeExceptionThrown(){
        // Given
        int shorterThanMin = VisitValidator.MIN_VISIT_DURATION_IN_MINUTES - 1;
        Long doctorID = 1L;
        Visit visit = VisitFactory.getVisit(
                1L,
                LocalDateTime.of(YEAR, 1, 1, 12, 0),
                LocalDateTime.of(YEAR, 1, 1, 12, shorterThanMin),
                null,
                null);
        // When

        // Then
        var exception = Assertions.assertThrows(InvalidDateTimeException.class,
                () -> visitValidator.validateVisitCreation(visit, doctorID));
        String expectedMessage = "Visit should have minimal duration of " + VisitValidator.MIN_VISIT_DURATION_IN_MINUTES + " min.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateVisitCreation_VisitGivenIsLongerThanMaxDuration_InvalidDateTimeExceptionThrown(){
        // Given
        int longerThanMax = VisitValidator.MAX_VISIT_DURATION_IN_HOURS + 1;
        int hour = 12 + longerThanMax;
        Long doctorID = 1L;
        Visit visit = VisitFactory.getVisit(
                1L,
                LocalDateTime.of(YEAR, 1, 1, 12, 0),
                LocalDateTime.of(YEAR, 1, 1, hour, 0),
                null,
                null);
        // When

        // Then
        var exception = Assertions.assertThrows(InvalidDateTimeException.class,
                () -> visitValidator.validateVisitCreation(visit, doctorID));
        String expectedMessage = "Visit should have maximal duration of " + VisitValidator.MAX_VISIT_DURATION_IN_HOURS + " hours.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateVisitCreation_VisitGivenOverlapsWithExistingOne_VisitExistsExceptionThrown(){
        // Given
        Long doctorID = 1L;
        Visit visit = VisitFactory.getVisit();
        when(visitRepository.findAllOverlapping(
                visit.getAppointmentStart(),
                visit.getAppointmentEnd(),
                doctorID)).thenReturn(List.of(visit));
        // When

        // Then
        var exception = Assertions.assertThrows(VisitExistsException.class,
                () -> visitValidator.validateVisitCreation(visit, doctorID));
        String expectedMessage = "Visit could not be created, there will be another visit at this time.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateVisitCreation_ValidVisitIsGiven_ExceptionIsNotThrown(){
        // Given
        Long doctorID = 1L;
        Visit visit = VisitFactory.getVisit();
        when(visitRepository.findAllOverlapping(
                visit.getAppointmentStart(),
                visit.getAppointmentEnd(),
                doctorID)).thenReturn(List.of());
        // When
        visitValidator.validateVisitCreation(visit, doctorID);

        // Then
    }

    @Test
    void validateVisitAssignment_VisitGivenIsPast_InvalidDateTimeExceptionThrown(){
        // Given
        Visit visit = VisitFactory.getVisit(
                1L,
                LocalDateTime.of(1990, 1, 1, 12, 0),
                LocalDateTime.of(1990, 1, 1, 12, 30),
                null,
                null);
        // When

        // Then
        var exception = Assertions.assertThrows(InvalidDateTimeException.class,
                () -> visitValidator.validateVisitAssignment(visit));
        String expectedMessage = "Unable to create a visit for past dates.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateVisitAssignment_ValidVisitIsGiven_ExceptionIsNotThrown(){
        // Given
        Visit visit = VisitFactory.getVisit();

        // When
        visitValidator.validateVisitAssignment(visit);

        // Then
    }

    @Test
    void visitExists_VisitDoesNotExist_VisitDoesNotExistExceptionThrown(){
        // Given
        Long visitID = 1L;
        when(visitRepository.findById(visitID)).thenReturn(Optional.empty());
        // When

        // Then
        var exception = Assertions.assertThrows(VisitDoesNotExistException.class,
                () -> visitValidator.visitExists(visitID));
        String expectedMessage = "Visit with given ID does not exist.";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void visitExists_VisitDoesExist_VisitReturned(){
        // Given
        Long visitID = 1L;
        Visit visit = VisitFactory.getVisit();
        when(visitRepository.findById(visitID)).thenReturn(Optional.of(visit));
        // When
        var result = visitValidator.visitExists(visitID);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(visit, result);
    }
}
