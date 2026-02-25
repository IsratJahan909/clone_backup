package Israt.example.Hr.and.payroll.management.system.backend.enums;

import lombok.Getter;

@Getter
public enum EmploymentType {
    FULL_TIME("Full Time"),
    PART_TIME("Part Time"),
    CONTRACT("Contract"),
    TEMPORARY("Temporary"),
    PERMANENT("Permanent");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

}
