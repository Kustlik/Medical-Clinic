package com.kustlik.medicalclinic.controller;

import com.kustlik.medicalclinic.model.dto.doctor.DoctorCreationDTO;
import com.kustlik.medicalclinic.model.dto.doctor.DoctorDTO;
import com.kustlik.medicalclinic.model.entity.Doctor;
import com.kustlik.medicalclinic.model.mapper.DoctorMapper;
import com.kustlik.medicalclinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @GetMapping
    public List<DoctorDTO> getDoctors(Pageable pageable) {
        return doctorService.getDoctors(pageable).stream()
                .map(doctorMapper::toDto)
                .toList();
    }

    @GetMapping("/{email}")
    public DoctorDTO getDoctor(@PathVariable("email") String email) {
        return doctorMapper.toDto(doctorService.getDoctor(email));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDTO createDoctor(@RequestBody DoctorCreationDTO doctorDTO) {
        Doctor doctor = doctorService.createDoctor(
                doctorMapper.toDoctor(doctorDTO));
        return doctorMapper.toDto(doctor);
    }

    @PostMapping("/{doctorId}/assign")
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDTO createDoctorAssignment(@RequestBody Long medicalFacilityID, @PathVariable("doctorId") Long doctorID) {
        Doctor doctor = doctorService.assignDoctorToMedicalFacility(doctorID, medicalFacilityID);
        return doctorMapper.toDto(doctor);
    }
}
