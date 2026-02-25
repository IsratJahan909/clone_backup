import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../models/salary.dart';
import '../models/employee.dart';
import '../utils/app_theme.dart';
import 'add_salary_screen.dart';
import 'payslip_screen.dart';
import 'package:intl/intl.dart';

class SalaryManagementScreen extends StatefulWidget {
  const SalaryManagementScreen({super.key});

  @override
  State<SalaryManagementScreen> createState() => _SalaryManagementScreenState();
}

class _SalaryManagementScreenState extends State<SalaryManagementScreen> {
  final ApiService _apiService = ApiService();
  List<Salary> _salaries = [];
  List<Employee> _employees = [];
  bool _isLoading = true;
  late final bool _isAdmin;

  @override
  void initState() {
    super.initState();
    _isAdmin = ApiService.userRole?.toUpperCase() == 'ADMIN';
    _loadData();
  }

  Future<void> _loadData() async {
    if (!mounted) return;
    setState(() => _isLoading = true);
    try {
      int? effectiveEmployeeId = ApiService.userId;
      
      if (!_isAdmin && effectiveEmployeeId != null) {
        final emp = await _apiService.getEmployeeByUserId(effectiveEmployeeId);
        if (emp != null) {
          effectiveEmployeeId = emp.employeeId;
        }
      }

      final salaryData = _isAdmin
          ? await _apiService.getAllSalaries()
          : await _apiService.getSalariesByEmployee(effectiveEmployeeId!);

      if (_isAdmin) {
        _employees = await _apiService.getEmployees();
      } else if (effectiveEmployeeId != null) {
        final emp = await _apiService.getEmployeeById(effectiveEmployeeId);
        _employees = [emp];
      }

      if (mounted) {
        setState(() {
          _salaries = salaryData;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    }
  }

  String _getEmployeeName(int id) {
    try {
      return _employees.firstWhere((e) => e.employeeId == id).fullName;
    } catch (_) {
      return 'Employee #$id';
    }
  }

  Employee? _getEmployee(int id) {
    try {
      return _employees.firstWhere((e) => e.employeeId == id);
    } catch (_) {
      return null;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.scaffoldBg,
      appBar: AppBar(
        title: const Text('Payroll Management'),
        actions: [
          IconButton(onPressed: _loadData, icon: const Icon(Icons.refresh)),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Column(
              children: [
                _buildStatsHeader(),
                Expanded(
                  child: _salaries.isEmpty
                      ? const Center(child: Text('No salary records found.'))
                      : ListView.builder(
                          padding: const EdgeInsets.all(16),
                          itemCount: _salaries.length,
                          itemBuilder: (context, index) {
                            final sal = _salaries[index];
                            return _buildSalaryCard(sal);
                          },
                        ),
                ),
              ],
            ),
      floatingActionButton: _isAdmin
          ? FloatingActionButton.extended(
              onPressed: () => Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => AddSalaryScreen(employees: _employees)),
              ).then((v) { if (v == true) _loadData(); }),
              icon: const Icon(Icons.add),
              label: const Text('Add Salary'),
            )
          : null,
    );
  }

  Widget _buildStatsHeader() {
    double totalPaid = _salaries.fold(0, (sum, item) => sum + item.netSalary);
    return Container(
      padding: const EdgeInsets.all(20),
      color: AppTheme.primary.withOpacity(0.05),
      child: Row(
        children: [
          Expanded(child: _statItem('Total Payout', '৳${totalPaid.toStringAsFixed(0)}', AppTheme.primary)),
          Container(width: 1, height: 40, color: Colors.grey.withOpacity(0.3)),
          Expanded(child: _statItem('Records', _salaries.length.toString(), Colors.blue)),
        ],
      ),
    );
  }

  Widget _statItem(String label, String value, Color color) {
    return Column(
      children: [
        FittedBox( // Prevent text overflow in stats
          fit: BoxFit.scaleDown,
          child: Text(value, style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: color)),
        ),
        Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
      ],
    );
  }

  Widget _buildSalaryCard(Salary sal) {
    return GestureDetector(
      onTap: () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => PayslipScreen(
              salary: sal,
              employee: _getEmployee(sal.employeeId),
            ),
          ),
        );
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(15),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10)],
        ),
        child: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(color: Colors.green.withOpacity(0.1), shape: BoxShape.circle),
              child: const Icon(Icons.payments, color: Colors.green),
            ),
            const SizedBox(width: 16),
            Expanded( // Prevents name and details from overflowing
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    _getEmployeeName(sal.employeeId),
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '${DateFormat('MMMM yyyy').format(DateTime(sal.year, sal.month))} • ID: ${sal.employeeId}',
                    style: const TextStyle(color: Colors.grey, fontSize: 13),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
            const SizedBox(width: 8),
            Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(
                  '৳${sal.netSalary.toStringAsFixed(0)}',
                  style: const TextStyle(color: Colors.green, fontWeight: FontWeight.bold, fontSize: 16),
                ),
                const Icon(Icons.arrow_forward_ios, size: 12, color: Colors.grey),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
