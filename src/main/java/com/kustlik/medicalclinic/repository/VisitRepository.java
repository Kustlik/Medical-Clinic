package com.kustlik.medicalclinic.repository;

import com.kustlik.medicalclinic.model.entity.Visit;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    @NonNull Optional<Visit> findById(@NonNull Long id);
    @Query(value = "SELECT *, DATEADD(MINUTE, DURATION, APPOINTMENT) as ESTIMATED_TIME " +
            "FROM VISIT " +
            "WHERE DOCTOR_ID = :doctorID " +
            "AND NOT (:startTime < APPOINTMENT AND :endTime > DATEADD(MINUTE, DURATION, APPOINTMENT))" +
            "AND NOT (:startTime < APPOINTMENT AND :endTime < DATEADD(MINUTE, DURATION, APPOINTMENT) AND :endTime > APPOINTMENT)" +
            "AND NOT (:startTime > APPOINTMENT AND :endTime > DATEADD(MINUTE, DURATION, APPOINTMENT) AND :startTime < DATEADD(MINUTE, DURATION, APPOINTMENT))" +
            "AND NOT (:startTime > APPOINTMENT AND :endTime < DATEADD(MINUTE, DURATION, APPOINTMENT))", nativeQuery = true)
    List<Visit> findAllVisitsForDoctorBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("doctorID") Long doctorID);
}
