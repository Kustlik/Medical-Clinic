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
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "patient_id")
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
