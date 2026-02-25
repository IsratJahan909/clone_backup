package Israt.example.Hr.and.payroll.management.system.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Israt.example.Hr.and.payroll.management.system.backend.entity.ProfileEditRequest;
import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import Israt.example.Hr.and.payroll.management.system.backend.enums.UserRole;
import Israt.example.Hr.and.payroll.management.system.backend.service.ProfileEditRequestService;
import Israt.example.Hr.and.payroll.management.system.backend.service.UserService;

@RestController
@RequestMapping("/api/profile-edit-requests")
@CrossOrigin(origins = "*")
public class ProfileEditRequestController {

    @Autowired
    private ProfileEditRequestService profileEditRequestService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createProfileEditRequest(@RequestBody ProfileEditRequest profileEditRequest) {
        try {
            User current = getCurrentUser();
            if (current == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            if (profileEditRequest.getUserId() == null) {
                profileEditRequest.setUserId(current.getUserId());
            } else {
                if (!UserRole.ADMIN.equals(current.getRole()) && !profileEditRequest.getUserId().equals(current.getUserId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot create request for other users");
                }
            }

            if (profileEditRequest.getStatus() == null) profileEditRequest.setStatus("Pending");

            ProfileEditRequest newProfileEditRequest = profileEditRequestService.createProfileEditRequest(profileEditRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newProfileEditRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating profile edit request: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileEditRequestById(@PathVariable Long id) {
        try {
            ProfileEditRequest profileEditRequest = profileEditRequestService.getProfileEditRequestById(id);
            if (profileEditRequest != null) {
                // allow admin or owner
                User current = getCurrentUser();
                if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
                if (UserRole.ADMIN.equals(current.getRole()) || profileEditRequest.getUserId().equals(current.getUserId())) {
                    return ResponseEntity.ok(profileEditRequest);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile edit request not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profile edit request");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProfileEditRequests(@RequestParam(required = false) Long userId, @RequestParam(required = false) String status) {
        try {
            User current = getCurrentUser();
            if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");

            List<ProfileEditRequest> profileEditRequests;
            // Non-admins may only see their own requests
            if (!UserRole.ADMIN.equals(current.getRole())) {
                profileEditRequests = profileEditRequestService.getProfileEditRequestsByUserId(current.getUserId());
            } else {
                if (userId != null) {
                    profileEditRequests = profileEditRequestService.getProfileEditRequestsByUserId(userId);
                } else if (status != null) {
                    profileEditRequests = profileEditRequestService.getProfileEditRequestsByStatus(status);
                } else {
                    profileEditRequests = profileEditRequestService.getAllProfileEditRequests();
                }
            }
            return ResponseEntity.ok(profileEditRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching profile edit requests");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfileEditRequest(@PathVariable Long id, @RequestBody ProfileEditRequest profileEditRequest) {
        try {
            User current = getCurrentUser();
            if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");

            ProfileEditRequest existing = profileEditRequestService.getProfileEditRequestById(id);
            if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile edit request not found");

            if (!UserRole.ADMIN.equals(current.getRole()) && !existing.getUserId().equals(current.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            if (!UserRole.ADMIN.equals(current.getRole())) {
                existing.setRequestData(profileEditRequest.getRequestData());
            } else {
                existing.setRequestData(profileEditRequest.getRequestData());
                existing.setStatus(profileEditRequest.getStatus());
                existing.setApprovedBy(profileEditRequest.getApprovedBy());
                existing.setRejectedBy(profileEditRequest.getRejectedBy());
                existing.setRejectionReason(profileEditRequest.getRejectionReason());
            }

            ProfileEditRequest updatedProfileEditRequest = profileEditRequestService.updateProfileEditRequest(id, existing);
            if (updatedProfileEditRequest != null) {
                return ResponseEntity.ok(updatedProfileEditRequest);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile edit request not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating profile edit request");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfileEditRequest(@PathVariable Long id) {
        try {
            User current = getCurrentUser();
            if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            if (!UserRole.ADMIN.equals(current.getRole())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can delete requests");

            profileEditRequestService.deleteProfileEditRequest(id);
            return ResponseEntity.ok("Profile edit request deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting profile edit request");
        }
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveProfileEditRequest(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> request) {
        try {
            User current = getCurrentUser();
            if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            if (!UserRole.ADMIN.equals(current.getRole())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can approve requests");

            Long approvedBy = null;
            try {
                if (request != null && request.get("approvedBy") != null) {
                    Object v = request.get("approvedBy");
                    if (v instanceof Number) approvedBy = ((Number) v).longValue();
                    else approvedBy = Long.valueOf(v.toString());
                }
            } catch (Exception ex) {
            }

            if (approvedBy == null) approvedBy = current.getUserId();
            ProfileEditRequest approvedProfileEditRequest = profileEditRequestService.approveProfileEditRequest(id, approvedBy);
            if (approvedProfileEditRequest != null) {
                return ResponseEntity.ok(approvedProfileEditRequest);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile edit request not found");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving profile edit request: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectProfileEditRequest(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> request) {
        try {
            User current = getCurrentUser();
            if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            if (!UserRole.ADMIN.equals(current.getRole())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can reject requests");

            String reason = null;
            Long rejectedBy = null;
            try {
                if (request != null) {
                    if (request.get("reason") != null) reason = request.get("reason").toString();
                    Object v = request.get("rejectedBy");
                    if (v != null) {
                        if (v instanceof Number) rejectedBy = ((Number) v).longValue();
                        else rejectedBy = Long.valueOf(v.toString());
                    }
                }
            } catch (Exception ex) {

            }

            if (rejectedBy == null) rejectedBy = current.getUserId();
            ProfileEditRequest rejectedProfileEditRequest = profileEditRequest_serviceWrapperReject(id, reason, rejectedBy);
            if (rejectedProfileEditRequest != null) {
                return ResponseEntity.ok(rejectedProfileEditRequest);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile edit request not found");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error rejecting profile edit request: " + e.getMessage());
        }
    }


    private ProfileEditRequest profileEditRequest_serviceWrapperReject(Long id, String reason, Long rejectedBy) {
        return profileEditRequestService.rejectProfileEditRequest(id, reason, rejectedBy);
    }

    private User getCurrentUser() {
        try {
            String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
            if (email == null) return null;
            return userService.getUserByEmail(email);
        } catch (Exception ex) {
            return null;
        }
    }
}
