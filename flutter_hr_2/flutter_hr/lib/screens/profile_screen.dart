import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../models/employee.dart';
import '../utils/app_theme.dart';

class ProfileScreen extends StatefulWidget {
  final int? employeeId;
  const ProfileScreen({super.key, this.employeeId});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final ApiService _apiService = ApiService();
  Employee? _employee;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  Future<void> _loadProfile() async {
    final targetId = widget.employeeId ?? ApiService.userId;
    if (targetId == null) return;
    try {
      final emp = await _apiService.getEmployeeById(targetId);
      setState(() {
        _employee = emp;
        _isLoading = false;
      });
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Scaffold(body: Center(child: CircularProgressIndicator()));
    if (_employee == null) return const Scaffold(body: Center(child: Text('Profile not found')));

    return Scaffold(
      backgroundColor: AppTheme.scaffoldBg,
      body: CustomScrollView(
        slivers: [
          _buildAppBar(),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  _infoSection('Basic Information', [
                    _infoTile('Full Name', _employee!.fullName, Icons.person_outline),
                    _infoTile('Email', _employee!.email, Icons.email_outlined),
                    _infoTile('Employee Code', _employee!.employeeCode, Icons.badge_outlined),
                  ]),
                  _infoSection('Employment Details', [
                    _infoTile('Designation', _employee!.designation, Icons.work_outline),
                    _infoTile('Employment Type', _employee!.employmentType.toString().split('.').last, Icons.assignment_ind_outlined),
                    _infoTile('Joining Date', _employee!.dateOfJoining, Icons.calendar_today_outlined),
                  ]),
                  _infoSection('Financial Information', [
                    _infoTile('Base Salary', '৳${_employee!.baseSalary}', Icons.monetization_on_outlined),
                    _infoTile('Bank Name', _employee!.bankName, Icons.account_balance_outlined),
                    _infoTile('Account No', _employee!.bankAccountNumber, Icons.numbers_outlined),
                  ]),
                  const SizedBox(height: 30),
                  ElevatedButton.icon(
                    onPressed: () {
                      // Logic for edit request can be added later
                      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Profile edit request functionality coming soon!')));
                    },
                    icon: const Icon(Icons.edit_note),
                    label: const Text('Request Information Update'),
                    style: ElevatedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildAppBar() {
    return SliverAppBar(
      expandedHeight: 280,
      pinned: true,
      flexibleSpace: FlexibleSpaceBar(
        background: Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: [AppTheme.primary, Colors.blueAccent],
            ),
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const SizedBox(height: 40),
              Container(
                padding: const EdgeInsets.all(4),
                decoration: const BoxDecoration(color: Colors.white, shape: BoxShape.circle),
                child: CircleAvatar(
                  radius: 60,
                  backgroundImage: NetworkImage(_employee!.profileImage),
                ),
              ),
              const SizedBox(height: 12),
              Text(
                _employee!.fullName,
                style: const TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold),
              ),
              Text(
                _employee!.designation,
                style: const TextStyle(color: Colors.white70, fontSize: 16),
              ),
              const SizedBox(height: 8),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Text(
                  _employee!.isActive ? 'ACTIVE' : 'INACTIVE',
                  style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _infoSection(String title, List<Widget> children) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 4),
          child: Text(
            title.toUpperCase(),
            style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: Colors.blueGrey, letterSpacing: 1.1),
          ),
        ),
        Card(
          elevation: 0,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15), side: BorderSide(color: Colors.grey.withOpacity(0.2))),
          child: Column(children: children),
        ),
      ],
    );
  }

  Widget _infoTile(String label, String value, IconData icon) {
    return ListTile(
      leading: Icon(icon, color: AppTheme.primary, size: 22),
      title: Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
      subtitle: Text(value, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w500, color: AppTheme.textPrimary)),
    );
  }
}
