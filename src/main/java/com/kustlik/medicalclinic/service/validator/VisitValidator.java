package com.kustlik.medicalclinic.service.validator;

import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class VisitValidator {
    private final VisitRepository visitRepository;
    public final static int MIN_VISIT_DURATION_IN_MINUTES = 15;
    public final static int MAX_VISIT_DURATION_IN_HOURS = 1;

    public void validateVisitCreation(Visit visit, Long doctorID) {
        validateVisit(visit);
        visitIsNotPast(visit);
        visitEndIsNotBeforeStart(visit);
        visitReservedInFullQuarterOfAnHour(visit);
        visitIsLongerThanMinDuration(visit);
        visitIsShorterThanMaxDuration(visit);
        visitIsNotOverlappingWithExistingOne(visit, doctorID);
    }

    public void validateVisitAssignment(Visit visit) {
        visitIsNotPast(visit);
    }

    public Visit visitExists(Long visitID) {
        var existingVisit = visitRepository.findById(visitID);
        if (existingVisit.isEmpty()) {
            throw new VisitDoesNotExistException("Visit with given ID does not exist.");
        }
        return existingVisit.get();
    }

    private void visitIsNotPast(Visit visit) {
        LocalDateTime startTime = visit.getAppointmentStart();
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new InvalidDateTimeException("Unable to create a visit for past dates.");
        }
    }

    private void visitEndIsNotBeforeStart(Visit visit) {
        LocalDateTime startTime = visit.getAppointmentStart();
        LocalDateTime endTime = visit.getAppointmentEnd();
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new InvalidDateTimeException("End time should be after start time.");
        }
    }

    private void visitReservedInFullQuarterOfAnHour(Visit visit) {
        LocalDateTime startTime = visit.getAppointmentStart();
        if (startTime.getMinute() % 15 != 0) {
            throw new InvalidDateTimeException("Visit creation is possible only for a full quarter of an hour.");
        }
    }

    private void visitIsLongerThanMinDuration(Visit visit) {
        LocalDateTime startTime = visit.getAppointmentStart();
        LocalDateTime endTime = visit.getAppointmentEnd();
        if (endTime.isBefore(startTime.plusMinutes(MIN_VISIT_DURATION_IN_MINUTES))) {
            throw new InvalidDateTimeException("Visit should have minimal duration of " + MIN_VISIT_DURATION_IN_MINUTES + " min.");
        }
    }

    private void visitIsShorterThanMaxDuration(Visit visit) {
        LocalDateTime startTime = visit.getAppointmentStart();
        LocalDateTime endTime = visit.getAppointmentEnd();
        if (endTime.isAfter(startTime.plusHours(MAX_VISIT_DURATION_IN_HOURS))) {
            throw new InvalidDateTimeException("Visit should have maximal duration of " + MAX_VISIT_DURATION_IN_HOURS + " hours.");
        }
    }

    private void visitIsNotOverlappingWithExistingOne(Visit visit, Long doctorID) {
        if (!visitRepository.findAllOverlapping(
                visit.getAppointmentStart(),
                visit.getAppointmentEnd(),
                doctorID).isEmpty()) {
            throw new VisitExistsException("Visit could not be created, there will be another visit at this time.");
        }
    }

    public void validateVisit(Visit visit) {
        if (Stream.of(visit.getAppointmentStart(), visit.getAppointmentEnd())
                .anyMatch(Objects::isNull)) {
            throw new EmptyFieldException("Visit has some null fields, please fill everything correctly.");
        }
    }
}
