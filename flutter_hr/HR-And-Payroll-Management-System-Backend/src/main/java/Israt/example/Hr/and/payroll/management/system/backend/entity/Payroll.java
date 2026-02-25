package Israt.example.Hr.and.payroll.management.system.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payrolls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payrollId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private Integer payrollMonth;

    @Column(nullable = false)
    private Integer payrollYear;

    @Column(nullable = false)
    private BigDecimal baseSalary;

//    @Column
//    private BigDecimal houseRentAllowance = BigDecimal.ZERO;

    @Column
    private BigDecimal medicalAllowance = BigDecimal.ZERO;

//    @Column
//    private BigDecimal dearnesAllowance = BigDecimal.ZERO;

    @Column
    private BigDecimal overtime = BigDecimal.ZERO;

    @Column
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Column
    private BigDecimal incomeTax = BigDecimal.ZERO;

    @Column
    private BigDecimal providentFund = BigDecimal.ZERO;

    @Column
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    @Column
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    @Column
    private BigDecimal netSalary = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
