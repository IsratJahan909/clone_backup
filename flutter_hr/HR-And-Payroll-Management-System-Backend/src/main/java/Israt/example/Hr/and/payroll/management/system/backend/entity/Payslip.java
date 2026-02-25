package Israt.example.Hr.and.payroll.management.system.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payslips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

//    @Column(name = "pay_period_start")
//    private LocalDate payPeriodStart;
//
//    @Column(name = "pay_period_end")
//    private LocalDate payPeriodEnd;

    @Column(name = "base_salary")
    private BigDecimal baseSalary = BigDecimal.ZERO;

//    @Column(name = "house_rent_allowance")
//    private BigDecimal houseRentAllowance = BigDecimal.ZERO;

    @Column(name = "medical_allowance")
    private BigDecimal medicalAllowance = BigDecimal.ZERO;

//    @Column(name = "travel_allowance")
//    private BigDecimal travelAllowance = BigDecimal.ZERO;

//    @Column(name = "dearness_allowance")
//    private BigDecimal dearnessAllowance = BigDecimal.ZERO;

    @Column(name = "other_allowances")
    private BigDecimal otherAllowances = BigDecimal.ZERO;

    @Column(name = "overtime_hours")
    private Double overtimeHours = 0.0;

    @Column(name = "overtime_rate")
    private BigDecimal overtimeRate = BigDecimal.ZERO;

    @Column(name = "bonus_amount")
    private BigDecimal bonusAmount = BigDecimal.ZERO;

//    @Column(name = "performance_bonuses")
//    private BigDecimal performanceBonuses = BigDecimal.ZERO;

//    @Column(name = "incentives")
//    private BigDecimal incentives = BigDecimal.ZERO;

//    @Column(name = "total_working_days")
//    private Integer totalWorkingDays;
//
//    @Column(name = "actual_working_days")
//    private Integer actualWorkingDays;

    @Column(name = "leaves_days")
    private Integer leavesDays;

//    @Column(name = "sick_leave_taken")
//    private Integer sickLeaveTaken;
//
//    @Column(name = "casual_leave_taken")
//    private Integer casualLeaveTaken;

    @Column(name = "total_leave_day")
    private Integer totalLeaveDay;

    @Column(name = "advance_salary")
    private BigDecimal advanceSalary = BigDecimal.ZERO;
//
//    @Column(name = "income_tax")
//    private BigDecimal incomeTax = BigDecimal.ZERO;
//
//    @Column
//    private BigDecimal insurance = BigDecimal.ZERO;

    @Column
    private BigDecimal medicare = BigDecimal.ZERO;

    @Column(name = "provident_fund")
    private BigDecimal providentFund = BigDecimal.ZERO;

//    @Column
//    private BigDecimal fund = BigDecimal.ZERO;
//
//    @Column(name = "employee_contribution")
//    private BigDecimal employeeContribution = BigDecimal.ZERO;

    @Column(name = "loan_deduction")
    private BigDecimal loanDeduction = BigDecimal.ZERO;

    @Column
    private BigDecimal deductions = BigDecimal.ZERO;

//    @Column(name = "other_deductions")
//    private BigDecimal otherDeductions = BigDecimal.ZERO;

    @Column(name = "gross_salary")
    private BigDecimal grossSalary = BigDecimal.ZERO;

    @Column(name = "net_salary")
    private BigDecimal netSalary = BigDecimal.ZERO;

    @Column(name = "total_pay")
    private BigDecimal totalPay = BigDecimal.ZERO;

    @Column(length = 50)
    private String status; // Draft, Pending, Processing, Paid, Failed, Hold

//    @Column(name = "is_over_time")
//    private Boolean overTime = false;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

//    @Column(name = "pay_date")
//    private LocalDate payDate;

    @Column(name = "payment_reference", length = 255)
    private String paymentReference;

    @Column(name = "payslip_create_date")
    private LocalDate payslipCreateDate;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
