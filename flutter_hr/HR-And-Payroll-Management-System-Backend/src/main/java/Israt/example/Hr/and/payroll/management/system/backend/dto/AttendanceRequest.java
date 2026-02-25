package Israt.example.Hr.and.payroll.management.system.backend.dto;

import Israt.example.Hr.and.payroll.management.system.backend.enums.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttendanceRequest {
    private Long employeeId;
    private String email;
    private LocalDate date;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private AttendanceStatus status;
    private String remarks;
    private Boolean isOvertime;
    private Double overtimeHours;
    private String overtimeReason;
}
