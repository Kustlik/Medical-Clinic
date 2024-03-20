package com.kustlik.medicalclinic.repository;

import com.kustlik.medicalclinic.model.entity.Doctor;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    @NonNull Optional<Doctor> findById(@NonNull Long id);
}
