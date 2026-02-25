package Israt.example.Hr.and.payroll.management.system.backend.repository;

import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    User findByUserId(Long userId);
}
