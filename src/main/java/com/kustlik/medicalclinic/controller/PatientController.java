package com.kustlik.medicalclinic.controller;

import com.kustlik.medicalclinic.model.dto.patient.PatientCreationDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientPasswordDTO;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.model.mapper.PatientMapper;
import com.kustlik.medicalclinic.service.PatientService;
import com.kustlik.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final VisitService visitService;
    private final PatientMapper patientMapper;

    @GetMapping
    public List<PatientDTO> getPatients(@RequestParam(required = false) LocalDate visitDate, Pageable pageable) {
        if (visitDate == null) {
            return patientService.getPatients(pageable).stream()
                    .map(patientMapper::toDto)
                    .toList();
        }
        return visitService.getVisits(pageable, visitDate).stream()
                .map(Visit::getPatient)
                .map(patientMapper::toDto)
                .distinct()
                .toList();
    }

    @GetMapping("/{email}")
    public PatientDTO getPatient(@PathVariable("email") String email) {
        return patientMapper.toDto(patientService.getPatient(email));
    }

    @GetMapping("/id/{patientId}")
    public PatientDTO getPatient(@PathVariable("patientId") Long patientId) {
        return patientMapper.toDto(patientService.getPatient(patientId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDTO createPatient(@RequestBody PatientCreationDTO patientDTO) {
        Patient patient = patientService.createPatient(
                patientMapper.toPatient(patientDTO));
        return patientMapper.toDto(patient);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable("email") String email) {
        patientService.deletePatient(email);
    }

    @PutMapping("/{email}")
    public PatientDTO editPatient(@PathVariable("email") String email, @RequestBody PatientDTO newPatientDataDTO) {
        Patient patient = patientService.editPatient(email, patientMapper.toPatient(newPatientDataDTO));
        return patientMapper.toDto(patient);
    }

    @PatchMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editPatientPassword(@PathVariable("email") String email, @RequestBody PatientPasswordDTO newPatientDataDTO) {
        patientService.editPatientPassword(email, patientMapper.toPatient(newPatientDataDTO));
    }
}
