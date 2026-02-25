package Israt.example.Hr.and.payroll.management.system.backend.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("Admin"),
    EMPLOYEE("Employee"),
    HR("HR");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

}
