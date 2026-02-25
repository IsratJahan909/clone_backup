package Israt.example.Hr.and.payroll.management.system.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "leave_type", length = 100)
    private String leaveType; // Annual, Sick, Casual, etc.

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_days")
    private Integer totalDays;

    @Column(length = 2000)
    private String reason;

    @Column(name = "request_date")
    private LocalDateTime requestDate = LocalDateTime.now();

    @Column(name = "contact_number", length = 50)
    private String contactNumber;

    @Column(name = "handover_notes", length = 2000)
    private String handoverNotes;

    @Column(length = 50)
    private String status; // Pending, Approved, Rejected, Cancelled, Completed

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(length = 2000)
    private String approvalNotes;

    @Column(length = 1000)
    private String rejectionReason;

    @ManyToOne
    @JoinColumn(name = "rejected_by")
    private User rejectedBy;

    @Column(length = 2000)
    private String attachments;

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
