package com.kustlik.medicalclinic.service;

import com.kustlik.medicalclinic.exception.*;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.repository.DoctorRepository;
import com.kustlik.medicalclinic.repository.MedicalFacilityRepository;
import com.kustlik.medicalclinic.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService{
    private final DoctorRepository doctorRepository;
    private final VisitRepository visitRepository;
    private final MedicalFacilityRepository medicalFacilityRepository;
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctor(String email) {
        return doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorDoesNotExistException("Doctor not found."));
    }

    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        if (doctor == null || !doctor.validateDoctor())
            throw new EmptyFieldException("No empty argument is allowed.");
        var existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
        if (existingDoctor.isPresent())
            throw new DoctorExistsException("Doctor with given email exists.");
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor assignDoctorToMedicalFacility(Long doctorID, Long medicalFacilityID) {
        if (doctorID == null || medicalFacilityID == null)
            throw new EmptyFieldException("No empty argument is allowed.");
        var existingDoctor = doctorRepository.findById(doctorID);
        if (existingDoctor.isEmpty())
            throw new DoctorDoesNotExistException("Doctor with given ID does not exist.");
        var existingMedicalFacility = medicalFacilityRepository.findById(medicalFacilityID);
        if (existingMedicalFacility.isEmpty())
            throw new MedicalFacilityDoesNotExistException("Medical facility with given ID does not exist.");
        var existingAssignment = existingDoctor.get().getMedicalFacilities().stream()
                .filter(x -> Objects.equals(x.getId(), medicalFacilityID))
                .findFirst();
        if (existingAssignment.isPresent())
            throw new DoctorToMedicalFacilityAssignmentExistsException("Doctor is already assigned to this facility.");
        existingDoctor.get().getMedicalFacilities().add(existingMedicalFacility.get());
        return doctorRepository.save(existingDoctor.get());
    }

    @Transactional
    public Visit createVisit(Visit visit, Long doctorID){
        if (visit.getAppointment().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Unable to create a visit for past dates");
        }
        if (visit.getAppointment().getMinute() % 15 != 0){
            throw new RuntimeException("Visit creation is possible only for a full quarter of an hour");
        }
        var existingDoctor = doctorRepository.findById(doctorID);
        if (existingDoctor.isEmpty())
            throw new DoctorDoesNotExistException("Doctor with given ID does not exist.");
        LocalDateTime visitStartTime = visit.getAppointment();
        LocalDateTime visitEndTime = visitStartTime.plusMinutes(visit.getDuration());
        if(!visitRepository.findAllVisitsForDoctorBetween(visitStartTime, visitEndTime, doctorID).isEmpty()){
            throw new RuntimeException("Visit could not be created, there will be another visit at this time");
        }
        existingDoctor.get().getVisits().add(visit);
        doctorRepository.save(existingDoctor.get());
        //Optional<Visit> savedVisit = existingDoctor.get().getVisits().stream()
        //        .filter(x -> x.getId().equals(visit.getId())).findFirst();
        //if(savedVisit.isEmpty()){
        //    throw new RuntimeException("Visit was not saved properly");
        //}
        return visit;
    }
}
