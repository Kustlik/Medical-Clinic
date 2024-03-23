package com.kustlik.medicalclinic.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private LocalDateTime appointmentStart;
    private LocalDateTime appointmentEnd;
    @ManyToOne()
    @JoinColumn(name = "DOCTOR_ID")
    private Doctor doctor;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "PATIENT_ID")
    private Patient patient;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Visit other))
            return false;

        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
