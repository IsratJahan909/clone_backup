package Israt.example.Hr.and.payroll.management.system.backend.enums;

import lombok.Getter;

@Getter
public enum AttendanceStatus {
  PRESENT("Present"),
  ABSENT("Absent"),
  LATE("Late"),
  EARLY("Early"),
  HALF_DAY("Half Day"),
  LEAVE("Leave"),
  HOLIDAY("Holiday"),
  WEEKEND("Weekend"),
  EXCUSED("Excused");

  private final String displayName;

  AttendanceStatus(String displayName) {
    this.displayName = displayName;
  }
}
