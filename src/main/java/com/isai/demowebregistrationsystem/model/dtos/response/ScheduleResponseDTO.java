package com.isai.demowebregistrationsystem.model.dtos.response;


import lombok.*;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponseDTO { // HorarioResponseDTO -> ScheduleResponseDTO
    private Integer scheduleId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private CourseResponseDTO course;
    private TeacherResponseDTO teacher;
    private ClassroomResponseDTO classroom;
    private SectionResponseDTO section;
}