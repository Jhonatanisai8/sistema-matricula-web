package com.isai.demowebregistrationsystem.model.dtos.response;


import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicPeriodResponseDTO {
    private Integer periodId;
    private Integer academicYear;
    private String periodName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
}