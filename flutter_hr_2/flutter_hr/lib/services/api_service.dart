import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/department.dart';
import '../models/employee.dart';
import '../models/attendance_record.dart';
import '../models/leave_request.dart';
import '../models/bonus.dart';
import '../models/salary.dart';
import '../utils/api_constants.dart';

class ApiService {
  static String? _token;
  static String? _userRole;
  static String? _userEmail;
  static String? _userName;
  static int? _userId;

  static bool useMock = false;

  static String? get userRole => _userRole;
  static String? get userEmail => _userEmail;
  static String? get userName => _userName;
  static int? get userId => _userId;

  // Mock data storage
  static List<AttendanceRecord> _mockAttendance = [];
  static List<LeaveRequest> _mockLeaveRequests = [];
  static List<Map<String, dynamic>> _mockAdvanceSalaries = [];
  static List<Bonus> _mockBonuses = [];

  Future<List<dynamic>> getAdvanceSalaries({int? employeeId}) async {
    if (useMock) {
      if (employeeId != null) {
        return _mockAdvanceSalaries
            .where((e) => e['employeeId'].toString() == employeeId.toString())
            .toList();
      }
      return _mockAdvanceSalaries;
    }
    final url = employeeId != null
        ? '${ApiConstants.baseUrl}/advanceSalary?employeeId=$employeeId'
        : '${ApiConstants.baseUrl}/advanceSalary';
    final response = await http.get(
      Uri.parse(url),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) return json.decode(response.body);
    throw Exception('Failed to load advance salaries');
  }

  Future<void> requestAdvanceSalary(Map<String, dynamic> data) async {
    if (useMock) {
      _mockAdvanceSalaries.add({
        ...data,
        'id': _mockAdvanceSalaries.length + 1,
        'status': 'Pending',
        'requestDate': DateTime.now().toIso8601String(),
      });
      return;
    }
    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/advanceSalary'),
      headers: await _getHeaders(),
      body: jsonEncode(data),
    );
    if (response.statusCode != 201 && response.statusCode != 200)
      throw Exception('Request failed: ${response.body}');
  }

  Future<void> approveAdvanceSalary(int id) async {
    if (useMock) {
      final idx = _mockAdvanceSalaries.indexWhere((e) => e['id'] == id);
      if (idx != -1) _mockAdvanceSalaries[idx]['status'] = 'Approved';
      return;
    }
    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/advanceSalary/$id/approve'),
      headers: await _getHeaders(),
      body: jsonEncode({'approvedBy': _userId}),
    );
    if (response.statusCode != 200) throw Exception('Approval failed');
  }

  Future<void> rejectAdvanceSalary(int id) async {
    if (useMock) {
      final idx = _mockAdvanceSalaries.indexWhere((e) => e['id'] == id);
      if (idx != -1) _mockAdvanceSalaries[idx]['status'] = 'Rejected';
      return;
    }
    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/advanceSalary/$id/reject'),
      headers: await _getHeaders(),
      body: jsonEncode({'rejectedBy': _userId}),
    );
    if (response.statusCode != 200) throw Exception('Rejection failed');
  }

  // ── Auth APIs ──
  Future<Map<String, dynamic>> login(String email, String password) async {
    if (useMock) {
      _token = "mock_token";
      _userRole = email.contains('admin') ? 'ADMIN' : 'EMPLOYEE';
      _userEmail = email;
      _userName = email.split('@')[0];
      _userId = 1;
      await _saveUserInfo();
      return {
        "success": true,
        "token": _token,
        "user": {
          "userId": _userId,
          "role": _userRole,
          "email": _userEmail,
          "fullName": _userName,
        },
      };
    }

    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/users/login'),
      headers: {"Content-Type": "application/json"},
      body: jsonEncode({"email": email, "password": password}),
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      _token = data['token'];
      if (data['user'] != null) {
        _userRole = data['user']['role']?.toString();
        _userEmail = data['user']['email']?.toString();
        _userName = data['user']['fullName']?.toString();
        _userId = data['user']['userId'];
      }
      await _saveUserInfo();
      return data;
    } else {
      throw Exception('Login failed: ${response.body}');
    }
  }

  Future<void> _saveUserInfo() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    if (_token != null) await prefs.setString('jwt_token', _token!);
    if (_userRole != null) await prefs.setString('user_role', _userRole!);
    if (_userEmail != null) await prefs.setString('user_email', _userEmail!);
    if (_userName != null) await prefs.setString('user_name', _userName!);
    if (_userId != null) await prefs.setInt('user_id', _userId!);
  }

  static Future<void> loadSavedUserInfo() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    _token = prefs.getString('jwt_token');
    _userRole = prefs.getString('user_role');
    _userEmail = prefs.getString('user_email');
    _userName = prefs.getString('user_name');
    _userId = prefs.getInt('user_id');
  }

  static Future<void> logout() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.clear();
    _token = null;
    _userRole = null;
    _userEmail = null;
    _userName = null;
    _userId = null;
  }

  Future<Map<String, String>> _getHeaders() async {
    return {
      "Content-Type": "application/json",
      "Authorization": "Bearer $_token",
    };
  }

  // ── Attendance APIs ──
  Future<List<dynamic>> getMyAttendance(int employeeId) async {
    if (useMock) return _mockAttendance.map((e) => e.toJson()).toList();

    // Using the same parameters as the backend Controller expects
    final url =
        '${ApiConstants.baseUrl}/attendance?employeeId=$employeeId&_limit=100';

    final response = await http.get(
      Uri.parse(url),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) return json.decode(response.body);
    throw Exception('Failed to load attendance');
  }

  Future<List<dynamic>> getAllAttendance() async {
    if (useMock) return _mockAttendance.map((e) => e.toJson()).toList();
    final url = '${ApiConstants.baseUrl}/attendance?_limit=100';
    final response = await http.get(
      Uri.parse(url),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) return json.decode(response.body);
    throw Exception('Failed to load all attendance');
  }

  Future<Map<String, dynamic>> checkIn(int employeeId) async {
    if (useMock) {
      final record = AttendanceRecord(
        id: _mockAttendance.length + 1,
        employeeId: employeeId,
        date: DateTime.now().toString().split(' ')[0],
        clockInTime: DateTime.now().toIso8601String(),
        status: 'PRESENT',
      );
      _mockAttendance.insert(0, record);
      return record.toJson();
    }
    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/attendance'),
      headers: await _getHeaders(),
      body: jsonEncode({
        "employeeId": employeeId,
        "date": DateTime.now().toIso8601String().split('T')[0],
        "clockInTime": DateTime.now().toIso8601String(),
        "status": "PRESENT",
        "email": _userEmail, // Send email for verification as per backend
      }),
    );
    if (response.statusCode == 201 || response.statusCode == 200) {
      return json.decode(response.body);
    }
    throw Exception('Check-in failed: ${response.body}');
  }

  Future<void> checkOut(int recordId, int employeeId) async {
    if (useMock) {
      final index = _mockAttendance.indexWhere((e) => e.id == recordId);
      if (index != -1) {
        final record = _mockAttendance[index];
        _mockAttendance[index] = AttendanceRecord(
          id: record.id,
          employeeId: record.employeeId,
          date: record.date,
          clockInTime: record.clockInTime,
          clockOutTime: DateTime.now().toIso8601String(),
          status: record.status,
        );
      }
      return;
    }

    final response = await http.put(
      Uri.parse('${ApiConstants.baseUrl}/attendance/$recordId'),
      headers: await _getHeaders(),
      body: jsonEncode({
        "employeeId": employeeId,
        "clockOutTime": DateTime.now().toIso8601String(),
        "email": _userEmail, // Required for backend verifyOwner
      }),
    );
    if (response.statusCode != 200)
      throw Exception('Check-out failed: ${response.body}');
  }

  // ── Leave Request APIs ──
  Future<List<dynamic>> getAllLeaveRequests() async {
    if (useMock) return _mockLeaveRequests.map((e) => e.toJson()).toList();
    final response = await http.get(
      Uri.parse('${ApiConstants.baseUrl}/leave-requests/get-all'),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) return json.decode(response.body);
    throw Exception('Failed to load leaves');
  }

  Future<List<dynamic>> getMyLeaveRequests(int employeeId) async {
    if (useMock)
      return _mockLeaveRequests
          .where((e) => e.employeeId == employeeId)
          .map((e) => e.toJson())
          .toList();
    final response = await http.get(
      Uri.parse(
        '${ApiConstants.baseUrl}/leave-requests/get-by-employee/$employeeId',
      ),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) return json.decode(response.body);
    throw Exception('Failed to load my leaves');
  }

  Future<void> createLeaveRequest(Map<String, dynamic> data) async {
    if (useMock) {
      _mockLeaveRequests.add(
        LeaveRequest.fromJson({...data, 'id': 1, 'status': 'Pending'}),
      );
      return;
    }
    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/leave-requests/create'),
      headers: await _getHeaders(),
      body: jsonEncode(data),
    );
    if (response.statusCode != 201 && response.statusCode != 200)
      throw Exception('Leave request failed');
  }

  Future<void> approveLeaveRequest(int id, String notes) async {
    if (useMock) return;
    final response = await http.patch(
      Uri.parse('${ApiConstants.baseUrl}/leave-requests/$id/approve'),
      headers: await _getHeaders(),
      body: jsonEncode({"approvalNotes": notes}),
    );
    if (response.statusCode != 200) throw Exception('Approve failed');
  }

  Future<void> rejectLeaveRequest(int id, String reason) async {
    if (useMock) return;
    final response = await http.patch(
      Uri.parse('${ApiConstants.baseUrl}/leave-requests/$id/reject'),
      headers: await _getHeaders(),
      body: jsonEncode({"rejectionReason": reason}),
    );
    if (response.statusCode != 200) throw Exception('Reject failed');
  }

  // ── Dashboard APIs ──
  Future<Map<String, dynamic>> getAdminDashboard() async {
    if (useMock)
      return {
        "totalEmployees": 12,
        "activeDepartments": 4,
        "presentToday": 8,
        "pendingLeaves": 3,
      };
    final response = await http.get(
      Uri.parse('${ApiConstants.baseUrl}/admin-dashboard'),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) return json.decode(response.body);
    throw Exception('Failed to load dashboard');
  }

  Future<Map<String, dynamic>> getEmployeeDashboard(int id) async {
    if (useMock)
      return {
        "employeeName": _userName ?? "User",
        "attendanceRate": "95%",
        "remainingLeaves": 12,
        "monthlySalary": "45000",
      };
    final response = await http.get(
      Uri.parse('${ApiConstants.baseUrl}/employee-dashboard/$id'),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) return json.decode(response.body);
    throw Exception('Failed to load dashboard');
  }

  Future<List<Department>> getDepartments() async {
    if (useMock) return [];
    final response = await http.get(
      Uri.parse('${ApiConstants.baseUrl}/departments/get-all'),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) {
      List jsonResponse = json.decode(response.body);
      return jsonResponse.map((data) => Department.fromJson(data)).toList();
    }
    throw Exception('Failed to load departments');
  }

  Future<Department> createDepartment(Department dept) async {
    if (useMock) return dept;
    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/departments/create'),
      headers: await _getHeaders(),
      body: jsonEncode(dept.toJson()),
    );
    return Department.fromJson(json.decode(response.body));
  }

  Future<void> deleteDepartment(int id) async {
    if (useMock) return;
    await http.delete(
      Uri.parse('${ApiConstants.baseUrl}/departments/delete/$id'),
      headers: await _getHeaders(),
    );
  }

  Future<Department> updateDepartment(int id, Department dept) async {
    if (useMock) return dept;
    final response = await http.put(
      Uri.parse('${ApiConstants.baseUrl}/departments/update/$id'),
      headers: await _getHeaders(),
      body: jsonEncode(dept.toJson()),
    );
    if (response.statusCode == 200)
      return Department.fromJson(json.decode(response.body));
    throw Exception('Failed to update department');
  }

  Future<List<Employee>> getEmployees() async {
    if (useMock) {
      return [
        Employee(
          employeeId: 1,
          firstName: "John",
          lastName: "Doe",
          email: "john@example.com",
          employeeCode: "EMP001",
          departmentId: 1,
          designation: "Software Engineer",
          employmentType: EmploymentType.FULL_TIME,
          dateOfJoining: "2023-01-01",
          baseSalary: 50000,
          bankAccountNumber: "123456",
          bankName: "Bank A",
        ),
        Employee(
          employeeId: 2,
          firstName: "Jane",
          lastName: "Smith",
          email: "jane@example.com",
          employeeCode: "EMP002",
          departmentId: 2,
          designation: "Product Manager",
          employmentType: EmploymentType.FULL_TIME,
          dateOfJoining: "2023-02-01",
          baseSalary: 60000,
          bankAccountNumber: "654321",
          bankName: "Bank B",
        ),
      ];
    }
    final response = await http.get(
      Uri.parse('${ApiConstants.baseUrl}/employees/get-all'),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) {
      List jsonResponse = json.decode(response.body);
      return jsonResponse.map((data) => Employee.fromJson(data)).toList();
    }
    throw Exception('Failed to load employees');
  }

  Future<Employee?> getEmployeeByUserId(int userId) async {
    if (useMock) return null;
    final response = await http.get(
      Uri.parse('${ApiConstants.baseUrl}/employees?userId=$userId'),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) {
      List jsonResponse = json.decode(response.body);
      if (jsonResponse.isNotEmpty) {
        return Employee.fromJson(jsonResponse[0]);
      }
    }
    return null;
  }

  Future<Employee> getEmployeeById(int id) async {
    if (useMock) {
      return Employee(
        employeeId: id,
        firstName: "Demo",
        lastName: "User",
        email: "demo@gmail.com",
        employeeCode: "DEMO-001",
        departmentId: 1,
        designation: "Software Engineer",
        employmentType: EmploymentType.FULL_TIME,
        dateOfJoining: "2024-01-01",
        baseSalary: 50000.0,
        bankAccountNumber: "123456789",
        bankName: "Demo Bank",
      );
    }
    final response = await http.get(
      Uri.parse('${ApiConstants.baseUrl}/employees/get/$id'),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200)
      return Employee.fromJson(json.decode(response.body));
    throw Exception('Failed to load employee details');
  }

  Future<Employee> createEmployee(Employee emp) async {
    if (useMock) return emp;
    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/employees/create'),
      headers: await _getHeaders(),
      body: jsonEncode(emp.toJson()),
    );

    if (response.statusCode == 201 || response.statusCode == 200) {
      if (response.body != null && response.body.trim().isNotEmpty) {
        return Employee.fromJson(json.decode(response.body));
      }
      // Backend returned empty body — return the sent object as a fallback
      return emp;
    }
    throw Exception(
      'Failed to create employee: ${response.statusCode} ${response.body}',
    );
  }

  Future<Employee> updateEmployee(int id, Employee emp) async {
    if (useMock) return emp;
    final response = await http.put(
      Uri.parse('${ApiConstants.baseUrl}/employees/update/$id'),
      headers: await _getHeaders(),
      body: jsonEncode(emp.toJson()),
    );
    if (response.statusCode == 200) {
      if (response.body != null && response.body.trim().isNotEmpty) {
        return Employee.fromJson(json.decode(response.body));
      }
      // No body returned — assume update succeeded and return local object
      return emp;
    }
    throw Exception(
      'Failed to update employee: ${response.statusCode} ${response.body}',
    );
  }

  Future<void> deleteEmployee(int id) async {
    if (useMock) return;
    await http.delete(
      Uri.parse('${ApiConstants.baseUrl}/employees/delete/$id'),
      headers: await _getHeaders(),
    );
  }

  // ── Bonus APIs ──
  Future<List<Bonus>> getBonuses({int? employeeId}) async {
    if (useMock) return _mockBonuses;
    final url = employeeId != null
        ? '${ApiConstants.baseUrl}/bonus?employeeId=$employeeId'
        : '${ApiConstants.baseUrl}/bonus';

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: await _getHeaders(),
      );
      if (response.statusCode == 200) {
        List jsonResponse = json.decode(response.body);
        return jsonResponse.map((data) => Bonus.fromJson(data)).toList();
      }
      throw Exception('Server returned ${response.statusCode}');
    } catch (e) {
      print('Bonus Error: $e');
      throw Exception('Failed to load bonuses: $e');
    }
  }

  Future<void> createBonus(Bonus bonus) async {
    if (useMock) {
      _mockBonuses.add(bonus);
      return;
    }
    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/bonus'),
      headers: await _getHeaders(),
      body: jsonEncode(bonus.toJson()),
    );
    if (response.statusCode != 201 && response.statusCode != 200)
      throw Exception('Bonus creation failed');
  }

  Future<void> approveBonus(int id) async {
    if (useMock) {
      final idx = _mockBonuses.indexWhere((e) => e.id == id);
      if (idx != -1)
        _mockBonuses[idx] = _mockBonuses[idx].copyWith(status: 'APPROVED');
      return;
    }
    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/bonus/$id/approve'),
      headers: await _getHeaders(),
      body: jsonEncode({"approvedBy": _userId}),
    );
    if (response.statusCode != 200) throw Exception('Bonus approval failed');
  }

  Future<void> rejectBonus(int id) async {
    if (useMock) {
      final idx = _mockBonuses.indexWhere((e) => e.id == id);
      if (idx != -1)
        _mockBonuses[idx] = _mockBonuses[idx].copyWith(status: 'REJECTED');
      return;
    }

    // Check if the backend has a /reject endpoint.
    // If not, we use the regular Update (PUT) endpoint to change status.
    final url = '${ApiConstants.baseUrl}/bonus/$id';

    // We fetch the current bonus first to keep other fields intact
    final getRes = await http.get(Uri.parse(url), headers: await _getHeaders());
    if (getRes.statusCode == 200) {
      Map<String, dynamic> bonusData = jsonDecode(getRes.body);
      bonusData['status'] = 'Rejected';

      final response = await http.put(
        Uri.parse(url),
        headers: await _getHeaders(),
        body: jsonEncode(bonusData),
      );

      if (response.statusCode == 200) return;
    }

    throw Exception('Bonus rejection failed');
  }

  // ── Salary APIs ──
  Future<List<Salary>> getAllSalaries() async {
    if (useMock) return [];
    final response = await http.get(
      Uri.parse('${ApiConstants.baseUrl}/salary'),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) {
      List jsonResponse = json.decode(response.body);
      return jsonResponse.map((data) => Salary.fromJson(data)).toList();
    }
    throw Exception('Failed to load all salaries');
  }

  Future<List<Salary>> getSalariesByEmployee(int employeeId) async {
    if (useMock) return [];
    final response = await http.get(
      Uri.parse('${ApiConstants.baseUrl}/salary/employee/$employeeId'),
      headers: await _getHeaders(),
    );
    if (response.statusCode == 200) {
      List jsonResponse = json.decode(response.body);
      return jsonResponse.map((data) => Salary.fromJson(data)).toList();
    }
    throw Exception('Failed to load salaries');
  }

  Future<void> createSalaryRecord(Salary salary) async {
    if (useMock) return;
    final response = await http.post(
      Uri.parse('${ApiConstants.baseUrl}/salary'),
      headers: await _getHeaders(),
      body: jsonEncode(salary.toJson()),
    );
    if (response.statusCode != 201 && response.statusCode != 200)
      throw Exception('Salary record creation failed');
  }
}
