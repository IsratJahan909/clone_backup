package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Profile;
import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import Israt.example.Hr.and.payroll.management.system.backend.repository.ProfileRepository;
import Israt.example.Hr.and.payroll.management.system.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public Profile getProfileById(Long profileId) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        return profile.orElse(null);
    }

    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    public Profile getProfileByEmail(String email) {
        return profileRepository.findByEmail(email);
    }

    public Profile getProfileByNid(String nid) {
        return profileRepository.findByNid(nid);
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile updateProfile(Long profileId, Profile profileDetails) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            Profile prof = profile.get();
            prof.setFirstName(profileDetails.getFirstName());
            prof.setLastName(profileDetails.getLastName());
            prof.setDateOfBirth(profileDetails.getDateOfBirth());
            prof.setNid(profileDetails.getNid());
            prof.setGender(profileDetails.getGender());
            prof.setPhoneNumber(profileDetails.getPhoneNumber());
            prof.setAddress(profileDetails.getAddress());
            prof.setAvatarUrl(profileDetails.getAvatarUrl());
            prof.setDepartment(profileDetails.getDepartment());
            prof.setEmail(profileDetails.getEmail());
            return profileRepository.save(prof);
        }
        return null;
    }

    public void deleteProfile(Long profileId) {
        profileRepository.deleteById(profileId);
    }

    public List<Profile> getProfilesByDepartment(Long departmentId) {
        return profileRepository.findAll().stream()
            .filter(prof -> prof.getDepartment() != null && prof.getDepartment().getDepartmentId().equals(departmentId))
            .toList();
    }

    public boolean isOwnerOfProfile(Long profileId, String userEmail) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            return profile.get().getEmail().equals(userEmail);
        }
        return false;
    }

    public boolean isUserOwnerOfProfile(Long userId, String userEmail) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getEmail().equals(userEmail);
        }
        return false;
    }
}
