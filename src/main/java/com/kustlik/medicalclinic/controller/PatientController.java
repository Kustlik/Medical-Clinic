package com.kustlik.medicalclinic.controller;

import com.kustlik.medicalclinic.model.dto.patient.PatientCreationDTO;
import com.kustlik.medicalclinic.model.dto.patient.PatientDTO;
import com.kustlik.medicalclinic.model.mapper.PatientMapper;
import com.kustlik.medicalclinic.model.dto.patient.PatientPasswordDTO;
import com.kustlik.medicalclinic.model.entity.Patient;
import com.kustlik.medicalclinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @GetMapping
    public List<PatientDTO> getPatients(){
        return patientService.getPatients()
                .stream()
                .map(patientMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{email}")
    public PatientDTO getPatient(@PathVariable("email") String email){
        return patientMapper.toDto(patientService.getPatient(email));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDTO createPatient(@RequestBody PatientCreationDTO patientDTO){
        Patient patient = patientService.createPatient(
                patientMapper.toPatient(patientDTO));
        return patientMapper.toDto(patient);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable("email") String email){
        patientService.deletePatient(email);
    }

    @PutMapping("/{email}")
    public PatientDTO editPatient(@PathVariable("email") String email, @RequestBody PatientDTO newPatientDataDTO){
        Patient patient = patientService.editPatient(email, patientMapper.toPatientEdit(newPatientDataDTO));
        return patientMapper.toDto(patient);
    }

    @PatchMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editPatientPassword(@PathVariable("email") String email, @RequestBody PatientPasswordDTO newPatientDataDTO){
        patientService.editPatientPassword(email, patientMapper.toPatientPasswordEdit(newPatientDataDTO));
    }
}
