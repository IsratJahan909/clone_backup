package Israt.example.Hr.and.payroll.management.system.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import Israt.example.Hr.and.payroll.management.system.backend.enums.UserRole;
import Israt.example.Hr.and.payroll.management.system.backend.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
      
        if (user.getRole() == null) {
            user.setRole(UserRole.EMPLOYEE);
        }
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long userId, User userDetails) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User u = user.get();
            u.setFullName(userDetails.getFullName());
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                u.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            u.setRole(userDetails.getRole());
            u.setIsActive(userDetails.getIsActive());
            return userRepository.save(u);
        }
        return null;
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public boolean authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword()) && user.getIsActive()) {
            return true;
        }
        return false;
    }
}
