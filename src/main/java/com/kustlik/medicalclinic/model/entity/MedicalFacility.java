package com.kustlik.medicalclinic.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MedicalFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(unique = true)
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private String buildingNumber;
    @ToString.Exclude
    @ManyToMany(mappedBy = "medicalFacilities", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Doctor> doctors;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MedicalFacility other))
            return false;

        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
