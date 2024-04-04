package com.kustlik.medicalclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MedicalClinicApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalClinicApplication.class, args);
    }

}
