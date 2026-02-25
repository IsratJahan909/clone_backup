package Israt.example.Hr.and.payroll.management.system.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_leaves")
    private Integer totalLeaves = 0;

    @Column(name = "used_leaves")
    private Integer usedLeaves = 0;

    @Column(name = "earned_leaves")
    private Integer earnedLeaves = 0;

    @Column(name = "sick_leaves")
    private Integer sickLeaves = 0;

    @Column(name = "casual_leaves")
    private Integer casualLeaves = 0;

    @Column(name = "remaining_leaves")
    private Integer remainingLeaves = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
