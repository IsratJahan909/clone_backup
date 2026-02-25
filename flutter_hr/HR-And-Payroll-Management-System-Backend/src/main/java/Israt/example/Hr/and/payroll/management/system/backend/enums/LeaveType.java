package Israt.example.Hr.and.payroll.management.system.backend.enums;

import lombok.Getter;

@Getter
public enum LeaveType {
    SICK_LEAVE("Sick Leave"),
    CASUAL_LEAVE("Casual Leave"),
    EARNED_LEAVE("Earned Leave"),
    MATERNITY_LEAVE("Maternity Leave"),
    PATERNITY_LEAVE("Paternity Leave"),
    UNPAID_LEAVE("Unpaid Leave");

    private final String displayName;

    LeaveType(String displayName) {
        this.displayName = displayName;
    }

}
