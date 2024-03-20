package com.kustlik.medicalclinic.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(unique = true)
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String specialisation;
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "DOCTOR_MEDICAL_FACILITY",
            joinColumns = @JoinColumn(name = "DOCTOR_ID"),
            inverseJoinColumns = @JoinColumn(name = "MEDICAL_FACILITY_ID")
    )
    private List<MedicalFacility> medicalFacilities;
    @OneToMany
    private List<Visit> visits;

    public boolean validateDoctor(){
        return Stream.of(email, firstName, lastName, password, specialisation)
                .noneMatch(Objects::isNull);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Doctor other))
            return false;

        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
