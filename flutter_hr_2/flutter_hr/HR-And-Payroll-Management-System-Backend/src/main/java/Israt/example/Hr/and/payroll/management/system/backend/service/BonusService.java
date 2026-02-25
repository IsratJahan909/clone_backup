package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Bonus;
import Israt.example.Hr.and.payroll.management.system.backend.entity.Employee;
import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import Israt.example.Hr.and.payroll.management.system.backend.enums.UserRole;
import Israt.example.Hr.and.payroll.management.system.backend.repository.BonusRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.EmployeeRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BonusService {

    @Autowired
    private BonusRepository bonusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Bonus createBonus(Bonus bonus) {
        return bonusRepository.save(bonus);
    }

    public Bonus getBonusById(Long id) {
        Optional<Bonus> bonus = bonusRepository.findById(id);
        return bonus.orElse(null);
    }

    public List<Bonus> getAllBonuses() {
        return bonusRepository.findAll();
    }

    public List<Bonus> getBonusesByEmployeeId(Long employeeId) {
        return bonusRepository.findByEmployeeId(employeeId);
    }

    public List<Bonus> getBonusesByStatus(String status) {
        return bonusRepository.findByStatus(status);
    }

    public Bonus updateBonus(Long id, Bonus bonusDetails) {
        Optional<Bonus> bonus = bonusRepository.findById(id);
        if (bonus.isPresent()) {
            Bonus b = bonus.get();
            b.setEmployeeId(bonusDetails.getEmployeeId());
            b.setBonusAmount(bonusDetails.getBonusAmount());
            b.setDescription(bonusDetails.getDescription());
            b.setStatus(bonusDetails.getStatus());
            b.setApprovedBy(bonusDetails.getApprovedBy());
            return bonusRepository.save(b);
        }
        return null;
    }

    public void deleteBonus(Long id) {
        bonusRepository.deleteById(id);
    }

    public Bonus approveBonus(Long id, Long approvedBy) {
        Optional<Bonus> bonus = bonusRepository.findById(id);
        if (bonus.isPresent()) {
            Bonus b = bonus.get();
            b.setStatus("Approved");
            b.setApprovedBy(approvedBy);
            return bonusRepository.save(b);
        }
        return null;
    }

    public Bonus rejectBonus(Long id, Long rejectedBy) {
        Optional<Bonus> bonus = bonusRepository.findById(id);
        if (bonus.isPresent()) {
            Bonus b = bonus.get();
            b.setStatus("Rejected");
            // আপনি চাইলে এখানে b.setRejectedBy(rejectedBy) ও যোগ করতে পারেন যদি আপনার মডেলে থাকে
            return bonusRepository.save(b);
        }
        return null;
    }

    public boolean isOwnerOfBonus(Long bonusId, String userEmail) {
        Optional<Bonus> bonus = bonusRepository.findById(bonusId);
        if (bonus.isPresent()) {
            Long employeeId = bonus.get().getEmployeeId();
            Optional<Employee> employee = employeeRepository.findById(employeeId);
            if (employee.isPresent()) {
                return employee.get().getEmail().equals(userEmail);
            }
        }
        return false;
    }

    public boolean isUserAdminOrOwner(String userEmail, Long employeeId) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()) {
            if (user.get().getRole() == UserRole.ADMIN) {
                return true;
            }
            Optional<Employee> employee = employeeRepository.findById(employeeId);
            if (employee.isPresent()) {
                return employee.get().getEmail().equals(userEmail);
            }
        }
        return false;
    }

    public boolean isUserAdmin(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent() && user.get().getRole() == UserRole.ADMIN) return true;
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        }
        return false;
    }

    public Long getEmployeeIdByEmail(String userEmail) {
        Optional<Employee> employee = employeeRepository.findByEmail(userEmail);
        return employee.map(Employee::getEmployeeId).orElse(null);
    }
}
