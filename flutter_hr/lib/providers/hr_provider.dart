import 'package:flutter/material.dart';
import '../models/department.dart';
import '../models/employee.dart';
import '../services/api_service.dart';

class HrProvider with ChangeNotifier {
  final ApiService _apiService = ApiService();
  
  List<Department> _departments = [];
  List<Employee> _employees = [];
  Map<String, dynamic> _adminDashboardData = {};
  Map<String, dynamic> _employeeDashboardData = {};
  bool _isLoading = false;

  List<Department> get departments => _departments;
  List<Employee> get employees => _employees;
  Map<String, dynamic> get adminDashboardData => _adminDashboardData;
  Map<String, dynamic> get employeeDashboardData => _employeeDashboardData;
  bool get isLoading => _isLoading;

  // --- Dashboard ---
  
  Future<void> fetchAdminDashboard() async {
    _isLoading = true;
    notifyListeners();
    try {
      _adminDashboardData = await _apiService.getAdminDashboard();
    } catch (e) {
      debugPrint('Admin Dashboard Error: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> fetchEmployeeDashboard(int id) async {
    _isLoading = true;
    notifyListeners();
    try {
      _employeeDashboardData = await _apiService.getEmployeeDashboard(id);
    } catch (e) {
      debugPrint('Employee Dashboard Error: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // --- Department Methods ---
  
  Future<void> fetchDepartments() async {
    _isLoading = true;
    notifyListeners();
    try {
      _departments = await _apiService.getDepartments();
    } catch (e) {
      debugPrint('Fetch Dept Error: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> addDepartment(Department dept) async {
    try {
      await _apiService.createDepartment(dept);
      await fetchDepartments();
    } catch (e) {
      debugPrint('Add Dept Error: $e');
    }
  }

  Future<void> updateDepartment(int id, Department dept) async {
    try {
      await _apiService.updateDepartment(id, dept);
      await fetchDepartments();
    } catch (e) {
      debugPrint('Update Dept Error: $e');
    }
  }

  Future<void> deleteDepartment(int id) async {
    try {
      await _apiService.deleteDepartment(id);
      _departments.removeWhere((dept) => dept.departmentId == id);
      notifyListeners();
    } catch (e) {
      debugPrint('Delete Dept Error: $e');
    }
  }

  // --- Employee Methods ---

  Future<void> fetchEmployees() async {
    _isLoading = true;
    notifyListeners();
    try {
      _employees = await _apiService.getEmployees();
    } catch (e) {
      debugPrint('Fetch Emp Error: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> addEmployee(Employee emp) async {
    try {
      await _apiService.createEmployee(emp);
      await fetchEmployees();
    } catch (e) {
      debugPrint('Add Emp Error: $e');
    }
  }

  Future<void> updateEmployee(int id, Employee emp) async {
    try {
      await _apiService.updateEmployee(id, emp);
      await fetchEmployees();
    } catch (e) {
      debugPrint('Update Emp Error: $e');
    }
  }

  Future<void> deleteEmployee(int id) async {
    try {
      await _apiService.deleteEmployee(id);
      _employees.removeWhere((emp) => emp.employeeId == id);
      notifyListeners();
    } catch (e) {
      debugPrint('Delete Emp Error: $e');
    }
  }
}
