package com.kustlik.medicalclinic.repository;

import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalFacilityRepository extends JpaRepository<MedicalFacility, Long> {
    @NonNull Optional<MedicalFacility> findById(@NonNull Long id);
    Optional<MedicalFacility> findByName(String name);
}
