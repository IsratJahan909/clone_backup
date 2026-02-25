package Israt.example.Hr.and.payroll.management.system.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile_edit_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "request_data", length = 4000)
    private String requestData;

    @Column(length = 50)
    private String status; // Pending, Approved, Rejected

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "rejection_reason", length = 2000)
    private String rejectionReason;

    @Column(name = "rejected_by")
    private Long rejectedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
