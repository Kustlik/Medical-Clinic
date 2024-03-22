package com.kustlik.medicalclinic.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String password;
    private LocalDate birthday;
    @OneToMany(mappedBy = "patient")
    private List<Visit> visits;

    public void update(Patient newPatientData) {
        this.email = newPatientData.getEmail();
        this.firstName = newPatientData.getFirstName();
        this.lastName = newPatientData.getLastName();
        this.birthday = newPatientData.getBirthday();
    }

    public boolean validatePatient() {
        return Stream.of(email, idCardNo, firstName, lastName, password, birthday)
                .noneMatch(Objects::isNull);
    }

    public boolean validateEdit() {
        return Stream.of(email, firstName, lastName, birthday)
                .noneMatch(Objects::isNull);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Patient other))
            return false;

        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
