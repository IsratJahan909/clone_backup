package Israt.example.Hr.and.payroll.management.system.backend.controller;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Profile;
import Israt.example.Hr.and.payroll.management.system.backend.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createProfile(@RequestBody Profile profile) {
        try {
            Profile newProfile = profileService.createProfile(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body(newProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating profile: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('EMPLOYEE') and @profileService.isOwnerOfProfile(#id, authentication.name))")
    public ResponseEntity<?> getProfileById(@PathVariable Long id) {
        try {
            Profile profile = profileService.getProfileById(id);
            if (profile != null) {
                return ResponseEntity.ok(profile);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profile");
        }
    }

    @GetMapping("/get-by-user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('EMPLOYEE') and @profileService.isUserOwnerOfProfile(#userId, authentication.name))")
    public ResponseEntity<?> getProfileByUserId(@PathVariable Long userId) {
        try {
            Profile profile = profileService.getProfileByUserId(userId);
            if (profile != null) {
                return ResponseEntity.ok(profile);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found for user");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profile");
        }
    }

    @GetMapping("/get-by-email/{email}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('EMPLOYEE') and #email == authentication.name)")
    public ResponseEntity<?> getProfileByEmail(@PathVariable String email) {
        try {
            Profile profile = profileService.getProfileByEmail(email);
            if (profile != null) {
                return ResponseEntity.ok(profile);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profile");
        }
    }

    @GetMapping("/get-by-nid/{nid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getProfileByNid(@PathVariable String nid) {
        try {
            Profile profile = profileService.getProfileByNid(nid);
            if (profile != null) {
                return ResponseEntity.ok(profile);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profile");
        }
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllProfiles() {
        try {
            List<Profile> profiles = profileService.getAllProfiles();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profiles");
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('EMPLOYEE') and @profileService.isOwnerOfProfile(#id, authentication.name))")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Profile profile) {
        try {
            Profile updatedProfile = profileService.updateProfile(id, profile);
            if (updatedProfile != null) {
                return ResponseEntity.ok(updatedProfile);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating profile");
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteProfile(@PathVariable Long id) {
        try {
            profileService.deleteProfile(id);
            return ResponseEntity.ok("Profile deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting profile");
        }
    }

    @GetMapping("/get-by-department/{departmentId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getProfilesByDepartment(@PathVariable Long departmentId) {
        try {
            List<Profile> profiles = profileService.getProfilesByDepartment(departmentId);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profiles");
        }
    }
}
