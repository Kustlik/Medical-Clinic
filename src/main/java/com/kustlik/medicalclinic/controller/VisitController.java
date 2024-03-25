package com.kustlik.medicalclinic.controller;

import com.kustlik.medicalclinic.controller.status.VisitStatus;
import com.kustlik.medicalclinic.exception.NoSuchOptionException;
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

    @GetMapping
    public List<VisitDTO> getVisits(@RequestParam("status") VisitStatus status) {
        switch (status) {
            case ALL -> {
                return visitService.getVisits().stream()
                        .map(visitMapper::toDto)
                        .toList();
            }
            case AVAILABLE -> {
                return visitService.getFreeVisits().stream()
                        .map(visitMapper::toDto)
                        .toList();
            }
            default -> throw new NoSuchOptionException("No such option.");
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public List<VisitDTO> getVisitsByDoctor(@PathVariable("doctorId") Long doctorId,
                                            @RequestParam("status") VisitStatus status) {
        switch (status) {
            case ALL -> {
                return visitService.getVisitsByDoctor(doctorId).stream()
                        .map(visitMapper::toDto)
                        .toList();
            }
            case AVAILABLE -> {
                return visitService.getFreeVisitsByDoctor(doctorId).stream()
                        .map(visitMapper::toDto)
                        .toList();
            }
            default -> throw new NoSuchOptionException("No such option.");
        }
    }

    @GetMapping("/patient/{patientId}")
    public List<VisitDTO> getVisitsByPatient(@PathVariable("patientId") Long patientID) {
        return visitService.getVisitsByPatient(patientID).stream()
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
