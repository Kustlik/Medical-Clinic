package com.kustlik.medicalclinic.controller;

import com.kustlik.medicalclinic.model.dto.visit.VisitCreationDTO;
import com.kustlik.medicalclinic.model.dto.visit.VisitDTO;
import com.kustlik.medicalclinic.model.entity.Visit;
import com.kustlik.medicalclinic.model.mapper.VisitMapper;
import com.kustlik.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/visits")
public class VisitController {
    private final VisitService visitService;
    private final VisitMapper visitMapper;

    @GetMapping("/available")
    public List<VisitDTO> getFreeVisits() {
        return visitService.getFreeVisits()
                .stream()
                .map(visitMapper::toDto)
                .toList();
    }

    @GetMapping("/available/doctor/{doctorId}")
    public List<VisitDTO> getFreeVisitsByDoctor(@PathVariable("doctorId") Long doctorID) {
        return visitService.getFreeVisitsByDoctor(doctorID)
                .stream()
                .map(visitMapper::toDto)
                .toList();
    }

    @GetMapping("/patient/{patientId}")
    public List<VisitDTO> getVisitsByPatient(@PathVariable("patientId") Long patientID) {
        return visitService.getVisitsByPatient(patientID)
                .stream()
                .map(visitMapper::toDto)
                .toList();
    }

    @PostMapping("/doctor/{doctorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDTO createVisit(@RequestBody VisitCreationDTO visitDTO, @PathVariable("doctorId") Long doctorID) {
        Visit visit = visitService.createVisit(
                visitMapper.toVisit(visitDTO), doctorID);
        return visitMapper.toDto(visit);
    }

    @PatchMapping("/patient/{patientId}")
    public VisitDTO assignVisitToPatient(@PathVariable("patientId") Long patientID, @RequestBody Long visitID) {
        Visit visit = visitService.assignVisitToPatient(visitID, patientID);
        return visitMapper.toDto(visit);
    }
}
