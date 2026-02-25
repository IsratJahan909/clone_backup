package Israt.example.Hr.and.payroll.management.system.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "salaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "base_salary", nullable = false)
    private BigDecimal baseSalary = BigDecimal.ZERO;

    @Column(name = "advance_salary")
    private BigDecimal advanceSalary = BigDecimal.ZERO;

    @Column(name = "bonus_amount")
    private BigDecimal bonusAmount = BigDecimal.ZERO;

    @Column
    private BigDecimal allowances = BigDecimal.ZERO;

    @Column
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column
    private BigDecimal insurance = BigDecimal.ZERO;

    @Column
    private BigDecimal medicare = BigDecimal.ZERO;

    @Column
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "provident_fund")
    private BigDecimal providentFund = BigDecimal.ZERO;

    @Column(name = "food_deduction")
    private BigDecimal foodDeduction = BigDecimal.ZERO;

    @Column(name = "other_deduction")
    private BigDecimal otherDeduction = BigDecimal.ZERO;

    @Column(name = "overtime_hours")
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "overtime_rate")
    private BigDecimal overtimeRate = BigDecimal.ZERO;

    @Column(name = "overtime_pay")
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Column(name = "gross_salary")
    private BigDecimal grossSalary = BigDecimal.ZERO;

    @Column(name = "net_salary")
    private BigDecimal netSalary = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column
    private SalaryStatus status = SalaryStatus.Draft;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_date")
    private LocalDate approvedDate;

    public enum SalaryStatus {
        Draft, Pending, Processing, Paid, Failed
    }

    public enum PaymentMethod {
        Bank_Transfer, Check, Cash
    }
}