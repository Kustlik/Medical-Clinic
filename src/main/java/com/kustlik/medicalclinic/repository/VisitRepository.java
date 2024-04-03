package com.kustlik.medicalclinic.repository;

import com.kustlik.medicalclinic.model.entity.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Visit> findByPatientIdIsNull(Pageable pageable);

    List<Visit> findByPatientId(Long id);

    @Query("SELECT v " +
            "FROM Visit v " +
            "WHERE v.doctor.id = :doctorID " +
            "AND v.appointmentStart <= :endTime AND v.appointmentEnd >= :startTime")
    List<Visit> findAllOverlapping(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("doctorID") Long doctorID);
}
