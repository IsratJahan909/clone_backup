package Israt.example.Hr.and.payroll.management.system.backend.entity;

import Israt.example.Hr.and.payroll.management.system.backend.enums.Month;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "advance_salaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 1000)
    private String requestReason;

    @Column
    private LocalDateTime requestDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "for_month")
    private Month forMonth;

    @Column(name = "for_year")
    private Integer forYear;

    @Column(name = "repayment_months")
    private Integer repaymentMonths = 1;

    @Column(name = "monthly_deduction")
    private BigDecimal monthlyDeduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "repayment_start_month")
    private Month repaymentStartMonth;

    @Column(name = "repayment_start_year")
    private Integer repaymentStartYear;

    @Column(length = 50)
    private String status; // Pending, Approved, Rejected, Paid, Partially Paid

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(length = 1000)
    private String rejectionReason;

    @Column(name = "rejected_by")
    private Long rejectedBy;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(length = 1000)
    private String attachments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
