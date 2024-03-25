package com.kustlik.medicalclinic.repository;

import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalFacilityRepository extends JpaRepository<MedicalFacility, Long> {
    Optional<MedicalFacility> findById(Long id);

    Optional<MedicalFacility> findByName(String name);
}
