package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.Visit;

import java.util.List;

public interface VisitService {
    List<Visit> getVisits();

    List<Visit> getFreeVisits();

    List<Visit> getVisitsByDoctor(Long doctorID);

    List<Visit> getFreeVisitsByDoctor(Long doctorID);

    List<Visit> getVisitsByPatient(Long patientID);

    Visit createVisit(Visit visit, Long doctorID);

    Visit assignVisitToPatient(Long visitID, Long patientID);
}
