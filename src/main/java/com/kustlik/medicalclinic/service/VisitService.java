package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.model.entity.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VisitService {
    Page<Visit> getVisits(Pageable pageable);

    Page<Visit> getFreeVisits(Pageable pageable);

    List<Visit> getVisitsByDoctor(Long doctorID);

    List<Visit> getFreeVisitsByDoctor(Long doctorID);

    List<Visit> getVisitsByPatient(Long patientID);

    Visit createVisit(Visit visit, Long doctorID);

    Visit assignVisitToPatient(Long visitID, Long patientID);
}
