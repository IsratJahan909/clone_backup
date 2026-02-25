package Israt.example.Hr.and.payroll.management.system.backend.controller;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Bonus;
import Israt.example.Hr.and.payroll.management.system.backend.service.BonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bonus")
@CrossOrigin(origins = "*")
public class BonusController {

    @Autowired
    private BonusService bonusService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createBonus(@RequestBody Bonus bonus) {
        try {
            Bonus newBonus = bonusService.createBonus(bonus);
            return ResponseEntity.status(HttpStatus.CREATED).body(newBonus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating bonus: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getBonusById(@PathVariable Long id) {
        try {
            Bonus bonus = bonusService.getBonusById(id);
            if (bonus != null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String currentUserEmail = auth.getName();
                if (!bonusService.isUserAdmin(currentUserEmail) &&
                    !bonusService.isOwnerOfBonus(id, currentUserEmail)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
                return ResponseEntity.ok(bonus);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bonus not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching bonus");
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getAllBonuses(@RequestParam(required = false) Long employeeId) {
        try {
            List<Bonus> bonuses;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = auth.getName();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));

            if (isAdmin) {
                if (employeeId != null) {
                    bonuses = bonusService.getBonusesByEmployeeId(employeeId);
                } else {
                    bonuses = bonusService.getAllBonuses();
                }
            } else {
                if (employeeId != null) {
                    if (!bonusService.isUserAdminOrOwner(currentUserEmail, employeeId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                    }
                    bonuses = bonusService.getBonusesByEmployeeId(employeeId);
                } else {
                    Long currentEmployeeId = bonusService.getEmployeeIdByEmail(currentUserEmail);
                    if (currentEmployeeId == null) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Employee not found for current user");
                    }
                    bonuses = bonusService.getBonusesByEmployeeId(currentEmployeeId);
                }
            }
            return ResponseEntity.ok(bonuses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching bonuses");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateBonus(@PathVariable Long id, @RequestBody Bonus bonus) {
        try {
            Bonus updatedBonus = bonusService.updateBonus(id, bonus);
            if (updatedBonus != null) {
                return ResponseEntity.ok(updatedBonus);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bonus not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating bonus");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteBonus(@PathVariable Long id) {
        try {
            bonusService.deleteBonus(id);
            return ResponseEntity.ok("Bonus deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting bonus");
        }
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> approveBonus(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        try {
            Long approvedBy = request.get("approvedBy");
            Bonus approvedBonus = bonusService.approveBonus(id, approvedBy);
            if (approvedBonus != null) {
                return ResponseEntity.ok(approvedBonus);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bonus not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error approving bonus");
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> rejectBonus(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        try {
            Long rejectedBy = request.get("rejectedBy");
            Bonus rejectedBonus = bonusService.rejectBonus(id, rejectedBy);
            if (rejectedBonus != null) {
                return ResponseEntity.ok(rejectedBonus);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bonus not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error rejecting bonus");
        }
    }
}
