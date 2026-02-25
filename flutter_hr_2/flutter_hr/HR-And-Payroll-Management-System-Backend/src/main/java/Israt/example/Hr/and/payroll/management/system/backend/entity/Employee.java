package Israt.example.Hr.and.payroll.management.system.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import Israt.example.Hr.and.payroll.management.system.backend.enums.EmploymentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(nullable = false)
    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    @Column(nullable = false)
    private LocalDate dateOfJoining;

    @Column(nullable = false)
    private BigDecimal baseSalary;

//    @Column
//    private BigDecimal houseRentAllowance = BigDecimal.ZERO;

    @Column
    private BigDecimal medicalAllowance = BigDecimal.ZERO;

//    @Column
//    private BigDecimal dearnesAllowance = BigDecimal.ZERO;

    @Column(nullable = false)
    private String bankAccountNumber;

    @Column(nullable = false)
    private String bankName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
