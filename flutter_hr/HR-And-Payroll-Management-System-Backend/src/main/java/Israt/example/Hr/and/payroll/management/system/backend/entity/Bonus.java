package Israt.example.Hr.and.payroll.management.system.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bonuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bonus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "bonus_type", length = 100)
    private String bonusType;
    @Column(name = "bonus_amount", nullable = false)
    private BigDecimal bonusAmount;

    @Column(name = "percentage_of_salary")
    private Double percentageOfSalary;

    @Column(name = "for_month")
    private Integer forMonth;

    @Column(name = "for_year")
    private Integer forYear;

    @Column(length = 2000)
    private String description;

    @Column(length = 1000)
    private String criteria;

    @Column(length = 50)
    private String status;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(length = 1000)
    private String rejectionReason;

    @Column(name = "is_paid")
    private Boolean isPaid = false;

    @Column(name = "paid_date")
    private LocalDateTime paidDate;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Column(name = "payment_reference", length = 255)
    private String paymentReference;

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
