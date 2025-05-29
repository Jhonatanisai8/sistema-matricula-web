package com.isai.demowebregistrationsystem.model.dtos.response;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO { // AsistenciaResponseDTO -> AttendanceResponseDTO
    private Integer attendanceId;
    private LocalDate attendanceDate;
    private String attendanceStatus;
    private String observations;
    private StudentResponseDTO student;
    private ScheduleResponseDTO schedule;
}