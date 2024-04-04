package com.kustlik.medicalclinic.remote.client;

import com.kustlik.medicalclinic.remote.model.dto.school.ParentBillingReportDto;
import com.kustlik.medicalclinic.remote.model.dto.school.SchoolBillingReportDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "schoolClient",
        url = "http://localhost:8080"
)
public interface SchoolClient {

    @GetMapping(value = "school/{id}/parent/{parentId}/billing")
    ParentBillingReportDto getSchoolBilling(
            @PathVariable("id") int id,
            @PathVariable("parentId") int parentId,
            @RequestParam("year") int year,
            @RequestParam("month") int month);

    @GetMapping(value = "school/{id}/billing")
    SchoolBillingReportDto getSchoolBilling_1(
            @PathVariable("id") int id,
            @RequestParam("year") int year,
            @RequestParam("month") int month);
}
