package com.kustlik.medicalclinic.mapper;

import com.kustlik.medicalclinic.model.dto.medical_facility.MedicalFacilityDTO;
import com.kustlik.medicalclinic.model.entity.MedicalFacility;
import com.kustlik.medicalclinic.model.mapper.MedicalFacilityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class MedicalFacilityMapperTest {
    MedicalFacilityMapper medicalFacilityMapper;

    @BeforeEach
    void setup(){
        this.medicalFacilityMapper = Mappers.getMapper(MedicalFacilityMapper.class);
    }

    @Test
    void toDto_MedicalFacilityIsMappedToDTO_MedicalFacilityDTOReturned(){
        //Given
        MedicalFacility medicalFacility = MedicalFacility.builder()
                .id(1L)
                .city("Łódź")
                .zipCode("42-234")
                .buildingNumber("23")
                .street("Ormiańska")
                .name("MediSun")
                .build();
        MedicalFacilityDTO medicalFacilityDTO = MedicalFacilityDTO.builder()
                .city("Łódź")
                .zipCode("42-234")
                .buildingNumber("23")
                .street("Ormiańska")
                .name("MediSun")
                .build();
        //When
        var result = medicalFacilityMapper.toDto(medicalFacility);

        //Then
        Assertions.assertInstanceOf(MedicalFacilityDTO.class, result);
        Assertions.assertEquals(medicalFacility.getCity(), medicalFacilityDTO.getCity());
        Assertions.assertEquals(medicalFacility.getZipCode(), medicalFacilityDTO.getZipCode());
        Assertions.assertEquals(medicalFacility.getBuildingNumber(), medicalFacilityDTO.getBuildingNumber());
        Assertions.assertEquals(medicalFacility.getStreet(), medicalFacilityDTO.getStreet());
        Assertions.assertEquals(medicalFacility.getName(), medicalFacilityDTO.getName());
    }

    @Test
    void toMedicalFacility_MedicalFacilityCreationDTOIsMappedToMedicalFacility_MedicalFacilityReturned(){
        //Given
        MedicalFacilityDTO medicalFacilityDTO = MedicalFacilityDTO.builder()
                .city("Łódź")
                .zipCode("42-234")
                .buildingNumber("23")
                .street("Ormiańska")
                .name("MediSun")
                .build();
        MedicalFacility medicalFacility = MedicalFacility.builder()
                .city("Łódź")
                .zipCode("42-234")
                .buildingNumber("23")
                .street("Ormiańska")
                .name("MediSun")
                .build();
        //When
        var result = medicalFacilityMapper.toMedicalFacility(medicalFacilityDTO);

        //Then
        Assertions.assertInstanceOf(MedicalFacility.class, result);
        Assertions.assertEquals(medicalFacility.getCity(), medicalFacilityDTO.getCity());
        Assertions.assertEquals(medicalFacility.getZipCode(), medicalFacilityDTO.getZipCode());
        Assertions.assertEquals(medicalFacility.getBuildingNumber(), medicalFacilityDTO.getBuildingNumber());
        Assertions.assertEquals(medicalFacility.getStreet(), medicalFacilityDTO.getStreet());
        Assertions.assertEquals(medicalFacility.getName(), medicalFacilityDTO.getName());
    }
}
