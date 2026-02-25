package Israt.example.Hr.and.payroll.management.system.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Employee;
import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import Israt.example.Hr.and.payroll.management.system.backend.enums.UserRole;
import Israt.example.Hr.and.payroll.management.system.backend.service.EmployeeService;
import Israt.example.Hr.and.payroll.management.system.backend.service.UserService;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {
        try {
            Employee newEmployee = employeeService.createEmployee(employee);


            try {
                User existing = userService.getUserByEmail(employee.getEmail());
                if (existing == null) {
                    User u = new User();
                    u.setEmail(employee.getEmail());

                    u.setPassword("1234");
                    u.setFullName(employee.getFirstName() + " " + employee.getLastName());
                    u.setRole(UserRole.EMPLOYEE);
                    userService.createUser(u);
                }
            } catch (Exception e) {

                System.err.println("Warning: failed to create user for employee: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(newEmployee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating employee: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
            
            Employee employee = employeeService.getEmployeeById(id);
            if (employee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
            }

            if (!isAdmin && !employee.getEmail().equalsIgnoreCase(email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching employee");
        }
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching employees");
        }
    }


    @GetMapping("")
    public ResponseEntity<?> getEmployeesByUserId(@RequestParam(name = "userId", required = false) Long userId) {
        try {
            if (userId == null) {
                List<Employee> employees = employeeService.getAllEmployees();
                return ResponseEntity.ok(employees);
            }


            try {
                String callerRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
                String callerName = SecurityContextHolder.getContext().getAuthentication().getName();
                if (!"ADMIN".equals(callerRole)) {
                    if (userService.getUserById(userId) == null) {
                        return ResponseEntity.ok(List.of());
                    }
                    User requestedUser = userService.getUserById(userId);
                    if (!requestedUser.getEmail().equalsIgnoreCase(callerName)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
                    }
                }
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
            }

            User user = userService.getUserById(userId);
            if (user == null) return ResponseEntity.ok(List.of());
            Employee emp = employeeService.getEmployeeByEmail(user.getEmail());
            if (emp == null) return ResponseEntity.ok(List.of());
            return ResponseEntity.ok(List.of(emp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching employee by userId");
        }
    }

//    @GetMapping("/get-by-code/{code}")
//    public ResponseEntity<?> getEmployeeByCode(@PathVariable String code) {
//        try {
//            Employee employee = employeeService.getEmployeeByCode(code);
//            if (employee != null) {
//                return ResponseEntity.ok(employee);
//            }
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching employee");
//        }
//    }

    @GetMapping("/get-by-department/{departmentId}")
    public ResponseEntity<?> getEmployeesByDepartment(@PathVariable Long departmentId) {
        try {
            List<Employee> employees = employeeService.getEmployeesByDepartment(departmentId);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching employees");
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employee);
            if (updatedEmployee != null) {
                return ResponseEntity.ok(updatedEmployee);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating employee");
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Employee deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting employee");
        }
    }


    @PostMapping("/sync-users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> syncEmployeeUsers() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            int created = 0;
            for (Employee emp : employees) {
                try {
                    if (userService.getUserByEmail(emp.getEmail()) == null) {
                        User u = new User();
                        u.setEmail(emp.getEmail());
                        u.setPassword("1234");
                        u.setFullName(emp.getFirstName() + " " + emp.getLastName());
                        u.setRole(UserRole.EMPLOYEE);
                        userService.createUser(u);
                        created++;
                    }
                } catch (Exception ex) {

                }
            }
            return ResponseEntity.ok("Created user accounts for " + created + " employees");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error syncing users: " + e.getMessage());
        }
    }
}
