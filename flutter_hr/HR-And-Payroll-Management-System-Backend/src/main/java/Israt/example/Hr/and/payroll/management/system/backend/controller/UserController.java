package Israt.example.Hr.and.payroll.management.system.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Israt.example.Hr.and.payroll.management.system.backend.config.JwtUtil;
import Israt.example.Hr.and.payroll.management.system.backend.dto.LoginRequest;
import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import Israt.example.Hr.and.payroll.management.system.backend.enums.UserRole;
import Israt.example.Hr.and.payroll.management.system.backend.service.UserService;

@RestController
@RequestMapping("/api/users")
//@CrossOrigin(origins = "*")
@CrossOrigin(origins = "*")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtUtil jwtUtil;

  @Value("${app.admin.email:admin@gmail.com}")
  private String configuredAdminEmail;

  @Value("${app.admin.password:1234}")
  private String configuredAdminPassword;

  @PostMapping("/create")
  public ResponseEntity<?> createUser(@RequestBody User user) {
    try {

      if (!isCurrentUserAdmin()) {
        user.setRole(UserRole.EMPLOYEE);
      } else {

      }

      User newUser = userService.createUser(user);
      return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating user: " + e.getMessage());
    }
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<?> getUserById(@PathVariable Long id) {
    try {
      User user = userService.getUserById(id);
      if (user != null) {
        return ResponseEntity.ok(user);
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user");
    }
  }

  @GetMapping("/get-by-email/{email}")
  public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
    try {
      User user = userService.getUserByEmail(email);
      if (user != null) {
        return ResponseEntity.ok(user);
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user by email");
    }
  }

  @GetMapping("/get-all")
  public ResponseEntity<?> getAllUsers() {
    try {
      List<User> users = userService.getAllUsers();
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching users");
    }
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
    try {
      User updatedUser = userService.updateUser(id, user);
      if (updatedUser != null) {
        return ResponseEntity.ok(updatedUser);
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating user");
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    try {
      userService.deleteUser(id);
      return ResponseEntity.ok("User deleted successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user");
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginRequest credentials) {
    try {
      String email = credentials.getEmail();
      String password = credentials.getPassword();

      boolean isAuthenticated = userService.authenticateUser(email, password);

      if (!isAuthenticated) {

        if (configuredAdminEmail != null && configuredAdminPassword != null
          && configuredAdminEmail.equalsIgnoreCase(email)
          && configuredAdminPassword.equals(password)) {
          User adminUser = new User();
          adminUser.setEmail(configuredAdminEmail);
          adminUser.setFullName("Administrator");
          adminUser.setPassword("");

          adminUser.setRole(Israt.example.Hr.and.payroll.management.system.backend.enums.UserRole.ADMIN);
          String token = jwtUtil.generateToken(adminUser.getEmail(), adminUser.getRole().name(), adminUser.getUserId());
          Map<String, Object> response = new HashMap<>();
          response.put("success", true);
          response.put("message", "Login successful (static admin)");
          response.put("token", token);
          response.put("user", adminUser);
          return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
      }

      User user = userService.getUserByEmail(email);
      String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getUserId());
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Login successful");
      response.put("token", token);
      response.put("user", user);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during login");
    }
  }

  //  @PostMapping("/login")
//  public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginRequest credentials) {
//    try {
//      String email = credentials.getEmail().trim().toLowerCase();
//      String password = credentials.getPassword();
//
//      if (email.isBlank() || password.isBlank()) {
//        return ResponseEntity.badRequest()
//          .body(new ApiResponse(false, "Email and password are required", null, "Invalid input"));
//      }
//
//      User user = null;
//      String role = null;
//      Long userId = null;
//
//      // ১. Static Admin Check (in-memory)
//      if (configuredAdminEmail != null && configuredAdminEmail.equalsIgnoreCase(email)
//        && configuredAdminPassword != null && configuredAdminPassword.equals(password)) {
//
//        user = new User();
//        user.setEmail(configuredAdminEmail);
//        user.setFullName("System Administrator");
//        user.setRole(UserRole.ADMIN);
//        // userId null রাখলে token-এ null যাবে, কিন্তু চলবে
//        role = UserRole.ADMIN.name();
//        userId = null; // অথবা fake ID দিতে পারো, যেমন -1L
//      }
//      // ২. Database User Check
//      else {
//        user = userService.getUserByEmail(email);
//        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
//          return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//            .body(new ApiResponse(false, "Invalid email or password", null, "Authentication failed"));
//        }
//        role = user.getRole().name();
//        userId = user.getUserId();
//      }
//
//      // ৩. JWT Token Generate (role সবসময় পাঠানো হচ্ছে)
//      String token = jwtUtil.generateToken(email, role, userId);
//
//      // ৪. Response (consistent with frontend expectation)
//      LoginResponse loginRes = new LoginResponse(true, "Login successful", token, user);
//
//      return ResponseEntity.ok(new ApiResponse(true, "Login successful", loginRes, null));
//
//    } catch (Exception e) {
//      // Log the error in production
//      e.printStackTrace();
//      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//        .body(new ApiResponse(false, "Login failed due to server error", null, e.getMessage()));
//    }
//  }
//
  public boolean isCurrentUserAdmin() {
    try {
      String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
        .iterator().next().getAuthority();
      return "ADMIN".equals(role);
    } catch (Exception e) {
      return false;
    }
  }
}
