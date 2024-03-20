package com.kustlik.medicalclinic.controller;

import com.kustlik.medicalclinic.model.dto.doctor.DoctorCreationDTO;
import com.kustlik.medicalclinic.model.dto.doctor.DoctorDTO;
import com.kustlik.medicalclinic.model.dto.visit.VisitCreationDTO;
import com.kustlik.medicalclinic.model.dto.visit.VisitDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.model.mapper.DoctorMapper;
import com.kustlik.medicalclinic.model.mapper.VisitMapper;
import com.kustlik.medicalclinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final VisitMapper visitMapper;

    @GetMapping
    public List<DoctorDTO> getDoctors(){
        return doctorService.getDoctors()
                .stream()
                .map(doctorMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{email}")
    public DoctorDTO getDoctor(@PathVariable("email") String email){
        return doctorMapper.toDto(doctorService.getDoctor(email));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDTO createDoctor(@RequestBody DoctorCreationDTO doctorDTO){
        Doctor doctor = doctorService.createDoctor(
                doctorMapper.toDoctor(doctorDTO));
        return doctorMapper.toDto(doctor);
    }

    @PostMapping("/{doctorId}/assign")
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDTO createDoctorAssignment(@RequestBody Long medicalFacilityID, @PathVariable("doctorId") Long doctorID){
        Doctor doctor = doctorService.assignDoctorToMedicalFacility(doctorID, medicalFacilityID);
        return doctorMapper.toDto(doctor);
    }

    @PostMapping("/{doctorId}/visit")
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDTO createVisit(@RequestBody VisitCreationDTO visitDTO, @PathVariable("doctorId") Long doctorID){
        Visit visit = doctorService.createVisit(
                visitMapper.toVisit(visitDTO), doctorID);
        return visitMapper.toDto(visit);
    }
}
