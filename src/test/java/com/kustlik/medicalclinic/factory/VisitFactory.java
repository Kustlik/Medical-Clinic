package com.kustlik.medicalclinic.factory;

import com.kustlik.medicalclinic.model.dto.visit.VisitCreationDTO;
import com.kustlik.medicalclinic.model.dto.visit.VisitDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.model.entity.Visit;

import java.time.LocalDateTime;
import java.time.Year;

public class VisitFactory {
    private static int YEAR = 2029;

    public static Visit getVisit(){
        return getVisit(
                1L,
                LocalDateTime.of(YEAR, 1, 1, 12, 0),
                LocalDateTime.of(YEAR, 1, 1, 12, 30),
                null,
                null);
    }
    public static Visit getVisit(Long id, LocalDateTime appointmentStart, LocalDateTime appointmentEnd, Doctor doctor, Patient patient){
        return Visit.builder()
                .id(id)
                .appointmentStart(appointmentStart)
                .appointmentEnd(appointmentEnd)
                .doctor(doctor)
                .patient(patient)
                .build();
    }

    public static VisitDTO getVisitDTO(){
        return getVisitDTO(
                LocalDateTime.of(YEAR, 1, 1, 12, 0),
                LocalDateTime.of(YEAR, 1, 1, 12, 30),
                null,
                null);
    }
    public static VisitDTO getVisitDTO(LocalDateTime appointmentStart, LocalDateTime appointmentEnd, Long doctorId, Long patientId){
        return VisitDTO.builder()
                .appointmentStart(appointmentStart)
                .appointmentEnd(appointmentEnd)
                .doctorId(doctorId)
                .patientId(patientId)
                .build();
    }

    public static VisitCreationDTO getVisitCreationDTO(){
        return getVisitCreationDTO(
                LocalDateTime.of(YEAR, 1, 1, 12, 0),
                LocalDateTime.of(YEAR, 1, 1, 12, 30));
    }
    public static VisitCreationDTO getVisitCreationDTO(LocalDateTime appointmentStart, LocalDateTime appointmentEnd){
        return VisitCreationDTO.builder()
                .appointmentStart(appointmentStart)
                .appointmentEnd(appointmentEnd)
                .build();
    }
}
