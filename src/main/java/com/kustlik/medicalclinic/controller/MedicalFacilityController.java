package com.kustlik.medicalclinic.controller;

import com.kustlik.medicalclinic.model.dto.medical_facility.MedicalFacilityDTO;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.model.mapper.MedicalFacilityMapper;
import com.kustlik.medicalclinic.service.MedicalFacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/medical_facilities")
public class MedicalFacilityController {
    private final MedicalFacilityService medicalFacilityService;
    private final MedicalFacilityMapper medicalFacilityMapper;

    @GetMapping
    public List<MedicalFacilityDTO> getMedicalFacilities(Pageable pageable) {
        return medicalFacilityService.getMedicalFacilities(pageable).stream()
                .map(medicalFacilityMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public MedicalFacilityDTO getMedicalFacility(@PathVariable("id") Long id) {
        return medicalFacilityMapper.toDto(medicalFacilityService.getMedicalFacility(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalFacilityDTO createMedicalFacility(@RequestBody MedicalFacilityDTO medicalFacilityDTO) {
        MedicalFacility medicalFacility = medicalFacilityService.createMedicalFacility(
                medicalFacilityMapper.toMedicalFacility(medicalFacilityDTO));
        return medicalFacilityMapper.toDto(medicalFacility);
    }

    @PostMapping("/{medicalFacilityId}/assign")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalFacilityDTO createMedicalFacilityAssignment(@RequestBody Long doctorID, @PathVariable("medicalFacilityId") Long medicalFacilityID) {
        MedicalFacility medicalFacility = medicalFacilityService.assignMedicalFacilityToDoctor(medicalFacilityID, doctorID);
        return medicalFacilityMapper.toDto(medicalFacility);
    }
}
