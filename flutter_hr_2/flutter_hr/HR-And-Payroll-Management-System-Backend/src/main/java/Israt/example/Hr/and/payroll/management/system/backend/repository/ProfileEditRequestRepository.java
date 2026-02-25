package Israt.example.Hr.and.payroll.management.system.backend.repository;

import Israt.example.Hr.and.payroll.management.system.backend.entity.ProfileEditRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfileEditRequestRepository extends JpaRepository<ProfileEditRequest, Long> {
    List<ProfileEditRequest> findByUserId(Long userId);
    List<ProfileEditRequest> findByStatus(String status);
}