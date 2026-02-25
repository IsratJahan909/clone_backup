package Israt.example.Hr.and.payroll.management.system.backend.enums;

import lombok.Getter;

@Getter
public enum ApprovalStatus {
  PENDING("Pending"),
  APPROVED("Approved"),
  REJECTED("Rejected");

  private final String displayName;

  ApprovalStatus(String displayName) {
    this.displayName = displayName;
  }
}
