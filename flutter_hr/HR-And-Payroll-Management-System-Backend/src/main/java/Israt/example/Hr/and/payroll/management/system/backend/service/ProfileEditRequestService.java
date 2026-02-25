package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Department;
import Israt.example.Hr.and.payroll.management.system.backend.entity.Profile;
import Israt.example.Hr.and.payroll.management.system.backend.entity.ProfileEditRequest;
import Israt.example.Hr.and.payroll.management.system.backend.repository.DepartmentRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.ProfileEditRequestRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileEditRequestService {

    @Autowired
    private ProfileEditRequestRepository profileEditRequestRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private DepartmentRepository departmentRepository;

    private final ObjectMapper mapper;

    public ProfileEditRequestService() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.registerModule(new JavaTimeModule());
    }

    public ProfileEditRequest createProfileEditRequest(ProfileEditRequest profileEditRequest) {
        return profileEditRequestRepository.save(profileEditRequest);
    }

    public ProfileEditRequest getProfileEditRequestById(Long id) {
        return profileEditRequestRepository.findById(id).orElse(null);
    }

    public List<ProfileEditRequest> getAllProfileEditRequests() {
        return profileEditRequestRepository.findAll();
    }

    public List<ProfileEditRequest> getProfileEditRequestsByUserId(Long userId) {
        return profileEditRequestRepository.findByUserId(userId);
    }

    public List<ProfileEditRequest> getProfileEditRequestsByStatus(String status) {
        return profileEditRequestRepository.findByStatus(status);
    }

    public ProfileEditRequest updateProfileEditRequest(Long id, ProfileEditRequest profileEditRequestDetails) {
        Optional<ProfileEditRequest> profileEditRequest = profileEditRequestRepository.findById(id);
        if (profileEditRequest.isPresent()) {
            ProfileEditRequest per = profileEditRequest.get();
            per.setUserId(profileEditRequestDetails.getUserId());
            per.setRequestData(profileEditRequestDetails.getRequestData());
            per.setStatus(profileEditRequestDetails.getStatus());
            per.setApprovedBy(profileEditRequestDetails.getApprovedBy());
            per.setRejectedBy(profileEditRequestDetails.getRejectedBy());
            per.setRejectionReason(profileEditRequestDetails.getRejectionReason());
            return profileEditRequestRepository.save(per);
        }
        return null;
    }

    public void deleteProfileEditRequest(Long id) {
        profileEditRequestRepository.deleteById(id);
    }

    @Transactional
    public ProfileEditRequest approveProfileEditRequest(Long id, Long approvedBy) {
        Optional<ProfileEditRequest> profileEditRequestOpt = profileEditRequestRepository.findById(id);
        if (profileEditRequestOpt.isPresent()) {
            ProfileEditRequest per = profileEditRequestOpt.get();

            // If already approved, just return it
            if ("Approved".equalsIgnoreCase(per.getStatus())) {
                return per;
            }

            per.setStatus("Approved");
            per.setApprovedBy(approvedBy);
            per.setApprovalDate(LocalDateTime.now());

            try {
                if (per.getRequestData() != null && !per.getRequestData().isEmpty()) {
                    JsonNode rootNode = mapper.readTree(per.getRequestData());

                    Profile existing = profileService.getProfileByUserId(per.getUserId());
                    if (existing == null) {
                        existing = new Profile();
                        existing.setUserId(per.getUserId());
                        // Set defaults or required fields if creating new
                    }

                    if (rootNode.has("firstName")) existing.setFirstName(rootNode.get("firstName").asText(null));
                    if (rootNode.has("lastName")) existing.setLastName(rootNode.get("lastName").asText(null));
                    if (rootNode.has("nid")) existing.setNid(rootNode.get("nid").asText(null));
                    if (rootNode.has("gender")) existing.setGender(rootNode.get("gender").asText(null));
                    if (rootNode.has("phoneNumber")) existing.setPhoneNumber(rootNode.get("phoneNumber").asText(null));
                    if (rootNode.has("address")) existing.setAddress(rootNode.get("address").asText(null));
                    if (rootNode.has("avatarUrl")) existing.setAvatarUrl(rootNode.get("avatarUrl").asText(null));
                    if (rootNode.has("email")) existing.setEmail(rootNode.get("email").asText(null));

                    if (rootNode.has("dateOfBirth")) {
                        String dobStr = rootNode.get("dateOfBirth").asText();
                        if (dobStr != null && !dobStr.isEmpty()) {
                            try {
                                existing.setDateOfBirth(LocalDate.parse(dobStr));
                            } catch (Exception e) {
                                // Try parsing as LocalDateTime if needed or log error
                                System.err.println("Error parsing dateOfBirth: " + e.getMessage());
                            }
                        }
                    }

                    // Handle Department
                    if (rootNode.has("departmentId")) {
                        String deptIdStr = rootNode.get("departmentId").asText();
                        if (deptIdStr != null && !deptIdStr.isEmpty()) {
                            try {
                                Long deptId = Long.parseLong(deptIdStr);
                                Department dept = departmentRepository.findById(deptId).orElse(null);
                                if (dept != null) {
                                    existing.setDepartment(dept);
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid departmentId format: " + deptIdStr);
                            }
                        }
                    }

                    if (existing.getId() != null) {
                        profileService.updateProfile(existing.getId(), existing);
                    } else {
                        profileService.createProfile(existing);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Failed to apply profile edit request: " + ex.getMessage());
                ex.printStackTrace();
                // Throwing exception to rollback transaction if needed, or handle gracefully
                throw new RuntimeException("Failed to apply profile changes: " + ex.getMessage());
            }

            return profileEditRequestRepository.save(per);
        }
        return null;
    }

    public ProfileEditRequest rejectProfileEditRequest(Long id, String reason, Long rejectedBy) {
        Optional<ProfileEditRequest> profileEditRequest = profileEditRequestRepository.findById(id);
        if (profileEditRequest.isPresent()) {
            ProfileEditRequest per = profileEditRequest.get();
            per.setStatus("Rejected");
            per.setRejectionReason(reason);
            per.setRejectedBy(rejectedBy);
            return profileEditRequestRepository.save(per);
        }
        return null;
    }
}
