package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.exception.DoctorDoesNotExistException;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import com.kustlik.medicalclinic.repository.PatientRepository;
import com.kustlik.medicalclinic.repository.VisitRepository;
import com.kustlik.medicalclinic.service.validator.VisitValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final VisitValidator visitValidator;

    public List<Visit> getFreeVisits() {
        return visitRepository.findByPatientIdIsNull();
    }

    public List<Visit> getFreeVisitsByDoctor(Long doctorID) {
        return visitRepository.findByDoctorIdAndPatientIdIsNull(doctorID);
    }

    public List<Visit> getVisitsByPatient(Long patientID) {
        return visitRepository.findByPatientId(patientID);
    }

    @Transactional
    public Visit createVisit(Visit visit, Long doctorID) {
        var existingDoctor = doctorRepository.findById(doctorID);
        if (existingDoctor.isEmpty())
            throw new DoctorDoesNotExistException("Doctor with given ID does not exist.");
        visitValidator.validateVisitCreation(visit, doctorID);
        visit.setDoctor(existingDoctor.get());
        return visitRepository.save(visit);
    }

    @Transactional
    public Visit assignVisitToPatient(Long visitID, Long patientID) {
        var existingPatient = patientRepository.findById(patientID);
        if (existingPatient.isEmpty()) {
            throw new DoctorDoesNotExistException("Patient with given ID does not exist.");
        }
        var existingVisit = visitValidator.getExistingVisit(visitID);
        visitValidator.validateVisitAssignment(existingVisit);
        existingVisit.setPatient(existingPatient.get());
        return existingVisit;
    }
}
