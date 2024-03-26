package com.kustlik.medicalclinic.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
