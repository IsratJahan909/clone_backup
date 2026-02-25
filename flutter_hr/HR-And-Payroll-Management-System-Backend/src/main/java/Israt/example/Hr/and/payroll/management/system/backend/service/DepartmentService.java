package Israt.example.Hr.and.payroll.management.system.backend.service;

import Israt.example.Hr.and.payroll.management.system.backend.entity.Department;
import Israt.example.Hr.and.payroll.management.system.backend.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public Department getDepartmentById(Long departmentId) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        return department.orElse(null);
    }

    public Department getDepartmentByName(String departmentName) {
        return departmentRepository.findByDepartmentName(departmentName);
    }

    public Department findByDepartmentId(Long departmentId) {
      return departmentRepository.findByDepartmentId(departmentId);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department updateDepartment(Long departmentId, Department departmentDetails) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        if (department.isPresent()) {
            Department dept = department.get();
            dept.setDepartmentName(departmentDetails.getDepartmentName());
            dept.setDescription(departmentDetails.getDescription());
            dept.setDepartmentHead(departmentDetails.getDepartmentHead());
            dept.setIsActive(departmentDetails.getIsActive());
            return departmentRepository.save(dept);
        }
        return null;
    }

    public void deleteDepartment(Long departmentId) {
        departmentRepository.deleteById(departmentId);
    }
}
