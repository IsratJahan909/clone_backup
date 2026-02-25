package Israt.example.Hr.and.payroll.management.system.backend.repository;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    @Query("SELECT p FROM Profile p WHERE p.userId = :userId")
    Profile findByUserId(@Param("userId") Long userId);

    Profile findByEmail(String email);
    Profile findByNid(String nid);
}
