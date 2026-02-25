import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../services/api_service.dart';
import '../utils/app_theme.dart';
import 'salary_management_screen.dart';

class EmployeeDashboardScreen extends StatefulWidget {
  const EmployeeDashboardScreen({super.key});

  @override
  State<EmployeeDashboardScreen> createState() => _EmployeeDashboardScreenState();
}

class _EmployeeDashboardScreenState extends State<EmployeeDashboardScreen> {
  final ApiService _apiService = ApiService();
  Map<String, dynamic>? _data;
  bool _isLoading = true;
  bool _isClocking = false;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    try {
      int? effectiveId = ApiService.userId;
      
      // Resolve employee ID from user ID
      final empProfile = await _apiService.getEmployeeByUserId(ApiService.userId!);
      if (empProfile != null) {
        effectiveId = empProfile.employeeId;
      }

      final res = await _apiService.getEmployeeDashboard(effectiveId!);
      setState(() {
        _data = res;
        _isLoading = false;
      });
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    }
  }

  void _handleClockAction() async {
    setState(() => _isClocking = true);
    try {
      // Resolve employee profile to get the correct ID for check-in
      final empProfile = await _apiService.getEmployeeByUserId(ApiService.userId!);
      await _apiService.checkIn(empProfile?.employeeId ?? ApiService.userId!);
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Action Successful!')));
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
    } finally {
      setState(() => _isClocking = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Center(child: CircularProgressIndicator());

    return RefreshIndicator(
      onRefresh: _loadData,
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _buildInfoCard(),
          const SizedBox(height: 20),
          _buildClockCard(),
          const SizedBox(height: 30),
          const Text('Your Stats', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          const SizedBox(height: 10),
          Row(
            children: [
              _statBox('Attendance', _data?['attendanceRate'] ?? '0%', Colors.blue),
              const SizedBox(width: 10),
              _statBox('Remaining Leave', _data?['remainingLeaves']?.toString() ?? '0', Colors.orange),
            ],
          ),
          const SizedBox(height: 20),
          _buildActionCard(
            'My Payslips', 
            'View and download your monthly salary slips', 
            Icons.payments_outlined, 
            Colors.green,
            () => Navigator.push(context, MaterialPageRoute(builder: (context) => const SalaryManagementScreen())),
          ),
        ],
      ),
    );
  }

  Widget _buildActionCard(String title, String subtitle, IconData icon, Color color, VoidCallback onTap) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
      child: ListTile(
        contentPadding: const EdgeInsets.all(16),
        leading: Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(color: color.withOpacity(0.1), borderRadius: BorderRadius.circular(12)),
          child: Icon(icon, color: color, size: 28),
        ),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
        subtitle: Text(subtitle, style: TextStyle(color: Colors.grey[600])),
        trailing: const Icon(Icons.arrow_forward_ios, size: 16),
        onTap: onTap,
      ),
    );
  }

  Widget _buildInfoCard() {
    // Backend returns 'employee' object. Mock might use 'employeeName' directly.
    final String name = _data?['employee']?['firstName'] != null 
        ? '${_data!['employee']['firstName']} ${_data!['employee']['lastName']}'
        : (_data?['employeeName'] ?? 'Employee');
    
    final String designation = _data?['employee']?['designation'] ?? 'Software Engineer';

    return Card(
      color: Colors.blue,
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            const CircleAvatar(
              radius: 40,
              backgroundColor: Colors.white,
              child: Icon(Icons.person, size: 50, color: Colors.blue),
            ),
            const SizedBox(height: 10),
            Text(
              name,
              style: const TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold),
            ),
            Text(designation, style: const TextStyle(color: Colors.white70)),
          ],
        ),
      ),
    );
  }

  Widget _buildClockCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            const Text('Attendance Status', style: TextStyle(fontSize: 16, color: Colors.grey)),
            const SizedBox(height: 10),
            Text(
              DateFormat('hh:mm a').format(DateTime.now()),
              style: const TextStyle(fontSize: 32, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 20),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _isClocking ? null : _handleClockAction,
                child: Text(_isClocking ? 'Processing...' : 'Clock In / Out'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _statBox(String label, String value, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: color.withOpacity(0.1),
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: color),
        ),
        child: Column(
          children: [
            Text(value, style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: color)),
            Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
          ],
        ),
      ),
    );
  }
}
