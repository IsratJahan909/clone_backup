import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../models/bonus.dart';
import '../models/employee.dart';
import '../utils/app_theme.dart';
import 'add_bonus_screen.dart';
import 'package:intl/intl.dart' as intl;

class BonusManagementScreen extends StatefulWidget {
  const BonusManagementScreen({super.key});

  @override
  State<BonusManagementScreen> createState() => _BonusManagementScreenState();
}

class _BonusManagementScreenState extends State<BonusManagementScreen> {
  final ApiService _apiService = ApiService();
  List<Bonus> _bonuses = [];
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
      // 1. Always load employees first to ensure names are available
      _employees = await _apiService.getEmployees();

      int? effectiveEmployeeId = ApiService.userId;
      if (!_isAdmin && effectiveEmployeeId != null) {
        final empProfile = await _apiService.getEmployeeByUserId(effectiveEmployeeId);
        if (empProfile != null) {
          effectiveEmployeeId = empProfile.employeeId;
        }
      }

      // 2. Fetch bonuses
      final bonusData = await _apiService.getBonuses();

      if (mounted) {
        setState(() {
          _bonuses = bonusData;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error loading data: $e')));
      }
    }
  }

  void _showAddBonusDialog() {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => AddBonusScreen(employees: _employees),
      ),
    ).then((value) {
      if (value == true) _loadData();
    });
  }

  void _approveBonus(int id) async {
    try {
      await _apiService.approveBonus(id);
      _loadData();
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Bonus Approved Successfully'), backgroundColor: Colors.green));
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e'), backgroundColor: Colors.red));
    }
  }

  void _rejectBonus(int id) async {
    try {
      await _apiService.rejectBonus(id);
      _loadData();
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Bonus Rejected Successfully'), backgroundColor: Colors.orange));
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e'), backgroundColor: Colors.red));
    }
  }

  String _getEmployeeName(int id) {
    try {
      // Look for employeeId match
      final emp = _employees.firstWhere((e) => e.employeeId == id);
      return emp.fullName;
    } catch (_) {
      return 'Employee #$id';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.scaffoldBg,
      appBar: AppBar(
        title: const Text('Bonus Management'),
        actions: [
          IconButton(onPressed: _loadData, icon: const Icon(Icons.refresh)),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadData,
        child: _isLoading
            ? const Center(child: CircularProgressIndicator())
            : _bonuses.isEmpty
            ? ListView(
                children: const [
                  SizedBox(height: 100),
                  Center(child: Text('No bonuses found', style: TextStyle(color: AppTheme.textSecondary))),
                ],
              )
            : ListView.builder(
          padding: const EdgeInsets.all(16),
          itemCount: _bonuses.length,
          itemBuilder: (context, index) {
            final bonus = _bonuses[index];
            final status = bonus.status?.toUpperCase() ?? 'PENDING';
            final statusColor = _getStatusColor(status);
            
            return Container(
              margin: const EdgeInsets.only(bottom: 16),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(15),
                boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10)],
              ),
              child: ExpansionTile(
                leading: CircleAvatar(
                  backgroundColor: statusColor.withOpacity(0.1),
                  child: Icon(Icons.military_tech, color: statusColor),
                ),
                title: Text(
                  _getEmployeeName(bonus.employeeId),
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                subtitle: Text('${bonus.bonusType} • ৳${bonus.bonusAmount.toStringAsFixed(0)}'),
                trailing: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                  decoration: BoxDecoration(
                    color: statusColor,
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    status,
                    style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.bold),
                  ),
                ),
                children: [
                  Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      children: [
                        _detailRow('Period', '${DateFormat.getMonthName(bonus.forMonth ?? 1)}, ${bonus.forYear}'),
                        const SizedBox(height: 8),
                        _detailRow('Description', bonus.description ?? 'N/A'),
                        const SizedBox(height: 8),
                        _detailRow('Payment', bonus.isPaid ? 'PAID' : 'UNPAID'),
                        if (_isAdmin && status == 'PENDING') ...[
                          const SizedBox(height: 16),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              TextButton(
                                onPressed: () => _rejectBonus(bonus.id!),
                                style: TextButton.styleFrom(foregroundColor: Colors.red),
                                child: const Text('Reject'),
                              ),
                              const SizedBox(width: 8),
                              ElevatedButton(
                                onPressed: () => _approveBonus(bonus.id!),
                                style: ElevatedButton.styleFrom(backgroundColor: Colors.green),
                                child: const Text('Approve', style: TextStyle(color: Colors.white)),
                              ),
                            ],
                          )
                        ]
                      ],
                    ),
                  )
                ],
              ),
            );
          },
        ),
      ),
      floatingActionButton: _isAdmin ? FloatingActionButton.extended(
        onPressed: _showAddBonusDialog,
        icon: const Icon(Icons.add),
        label: const Text('Assign Bonus'),
      ) : null,
    );
  }

  Widget _detailRow(String label, String value) {
    return Row(
      children: [
        SizedBox(width: 100, child: Text(label, style: const TextStyle(color: Colors.grey))),
        Expanded(child: Text(value, style: const TextStyle(fontWeight: FontWeight.bold))),
      ],
    );
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'APPROVED': return Colors.green;
      case 'REJECTED': return Colors.red;
      default: return Colors.orange;
    }
  }
}

class DateFormat {
  static String getMonthName(int month) {
    return intl.DateFormat('MMMM').format(DateTime(2024, month));
  }
}
