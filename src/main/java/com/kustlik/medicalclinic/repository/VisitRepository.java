package com.kustlik.medicalclinic.repository;

import com.kustlik.medicalclinic.model.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    Optional<Visit> findById(Long id);

    List<Visit> findByDoctorId(Long id);

    List<Visit> findByDoctorIdAndPatientIdIsNull(Long doctor);

    List<Visit> findByPatientIdIsNull();

    List<Visit> findByPatientId(Long id);

    @Query(value = "SELECT * " +
            "FROM visit v " +
            "WHERE :doctorID = doctor_id " +
            "AND v.appointment_start  <= :endTime " +
            "and v.appointment_end >= :startTime", nativeQuery = true)
    List<Visit> findAllOverlapping(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("doctorID") Long doctorID);
}
