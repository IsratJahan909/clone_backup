import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../models/advance_salary.dart';
import '../utils/app_theme.dart';
import 'package:intl/intl.dart';
import 'request_advance_salary_screen.dart';
import '../models/employee.dart';

class AdvanceSalaryScreen extends StatefulWidget {
  const AdvanceSalaryScreen({super.key});

  @override
  State<AdvanceSalaryScreen> createState() => _AdvanceSalaryScreenState();
}


class _AdvanceSalaryScreenState extends State<AdvanceSalaryScreen> {
  final ApiService _apiService = ApiService();
  List<AdvanceSalary> _requests = [];
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
        // Resolve employee profile to get the correct employeeId
        final empProfile = await _apiService.getEmployeeByUserId(effectiveEmployeeId);
        if (empProfile != null) {
          effectiveEmployeeId = empProfile.employeeId;
        }
      }

      if (_isAdmin) {
        _employees = await _apiService.getEmployees();
      } else if (effectiveEmployeeId != null) {
        // For employee, fetch their own details to show name
        final emp = await _apiService.getEmployeeById(effectiveEmployeeId);
        _employees = [emp];
      }

      final List<dynamic> data = await _apiService.getAdvanceSalaries(
        employeeId: _isAdmin ? null : effectiveEmployeeId,
      );
      
      if (mounted) {
        setState(() {
          _requests = data.map((json) => AdvanceSalary.fromJson(json)).toList();
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        WidgetsBinding.instance.addPostFrameCallback((_) {
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
          }
        });
      }
    }
  }

  String _getEmployeeName(int id) {
    try {
      return _employees.firstWhere((e) => e.employeeId == id).fullName;
    } catch (_) {
      return 'ID: $id';
    }
  }

  void _navigateToRequest() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const RequestAdvanceSalaryScreen()),
    ).then((value) {
      if (value == true) _loadData();
    });
  }

  Future<void> _approveRequest(int id) async {
    try {
      await _apiService.approveAdvanceSalary(id);
      await _loadData();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Request Approved'), backgroundColor: Colors.green));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e'), backgroundColor: Colors.red));
      }
    }
  }

  Future<void> _rejectRequest(int id) async {
    try {
      await _apiService.rejectAdvanceSalary(id);
      await _loadData();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Request Rejected'), backgroundColor: Colors.red));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e'), backgroundColor: Colors.red));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Advance Salary'),
        elevation: 0,
        actions: [
          IconButton(onPressed: _loadData, icon: const Icon(Icons.refresh)),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadData,
        child: _isLoading
            ? const Center(child: CircularProgressIndicator())
            : Column(
                children: [
                  _buildStatsHeader(),
                  Expanded(
                    child: _requests.isEmpty
                        ? ListView(
                            children: const [
                              SizedBox(height: 100),
                              Center(child: Text('No advance salary requests found')),
                            ],
                          )
                        : ListView.builder(
                            padding: const EdgeInsets.all(12),
                            itemCount: _requests.length,
                            itemBuilder: (context, index) {
                              final req = _requests[index];
                              final status = req.status;
                              final statusColor = _getStatusColor(status);

                              return Card(
                                elevation: 2,
                                margin: const EdgeInsets.only(bottom: 12),
                                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                                child: ExpansionTile(
                                  leading: Container(
                                    padding: const EdgeInsets.all(8),
                                    decoration: BoxDecoration(
                                      color: statusColor.withOpacity(0.1),
                                      shape: BoxShape.circle,
                                    ),
                                    child: Icon(Icons.payments_outlined, color: statusColor),
                                  ),
                                  title: Text(
                                    _isAdmin ? _getEmployeeName(req.employeeId) : '৳${req.amount}',
                                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                                  ),
                                  subtitle: Text(
                                    _isAdmin 
                                      ? '৳${req.amount} • ${req.forMonth ?? ''} ${req.forYear ?? ''}'
                                      : '${req.forMonth ?? ''} ${req.forYear ?? ''}',
                                    style: const TextStyle(fontSize: 12),
                                  ),
                                  trailing: Chip(
                                    label: Text(
                                      status.toUpperCase(),
                                      style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.bold),
                                    ),
                                    backgroundColor: statusColor,
                                    padding: EdgeInsets.zero,
                                    visualDensity: VisualDensity.compact,
                                  ),
                                  children: [
                                    Padding(
                                      padding: const EdgeInsets.all(16.0),
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.start,
                                        children: [
                                          _infoRow('Reason:', req.requestReason),
                                          _infoRow('Repayment:', '${req.repaymentMonths} Months'),
                                          if (req.monthlyDeduction != null)
                                            _infoRow('Monthly Deduction:', '৳${req.monthlyDeduction}'),
                                          if (req.id != null && _isAdmin && status.toLowerCase() == 'pending') ...[
                                            const Divider(height: 24),
                                            Row(
                                              mainAxisAlignment: MainAxisAlignment.end,
                                              children: [
                                                OutlinedButton.icon(
                                                  onPressed: () => _rejectRequest(req.id!),
                                                  icon: const Icon(Icons.close, size: 18),
                                                  label: const Text('Reject'),
                                                  style: OutlinedButton.styleFrom(
                                                    foregroundColor: AppTheme.error,
                                                    side: const BorderSide(color: AppTheme.error),
                                                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                                                  ),
                                                ),
                                                const SizedBox(width: 12),
                                                ElevatedButton.icon(
                                                  onPressed: () => _approveRequest(req.id!),
                                                  icon: const Icon(Icons.check, size: 18),
                                                  label: const Text('Approve'),
                                                  style: ElevatedButton.styleFrom(
                                                    backgroundColor: AppTheme.success,
                                                    foregroundColor: Colors.white,
                                                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                                                  ),
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
                ],
              ),
      ),
      floatingActionButton: !_isAdmin ? FloatingActionButton.extended(
        onPressed: _navigateToRequest,
        icon: const Icon(Icons.add),
        label: const Text('Request Advance'),
      ) : null,
    );
  }

  Widget _buildStatsHeader() {
    int pending = _requests.where((r) => r.status == 'Pending').length;
    double totalAmount = _requests.fold(0, (sum, item) => sum + item.amount);

    return Container(
      padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 16),
      color: AppTheme.primary.withOpacity(0.05),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _statItem('Pending', pending.toString(), Colors.orange),
          _statItem('Total Amount', '৳${totalAmount.toStringAsFixed(0)}', AppTheme.primary),
          _statItem('Requests', _requests.length.toString(), Colors.blue),
        ],
      ),
    );
  }

  Widget _statItem(String label, String value, Color color) {
    return Column(
      children: [
        Text(value, style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: color)),
        Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
      ],
    );
  }

  Widget _infoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 4.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(width: 120, child: Text(label, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13))),
          Expanded(child: Text(value, style: const TextStyle(fontSize: 13))),
        ],
      ),
    );
  }

  Color _getStatusColor(String status) {
    switch (status.toLowerCase()) {
      case 'approved': return AppTheme.success;
      case 'pending': return Colors.orange;
      case 'rejected': return AppTheme.error;
      case 'paid': return Colors.blue;
      default: return Colors.grey;
    }
  }
}
