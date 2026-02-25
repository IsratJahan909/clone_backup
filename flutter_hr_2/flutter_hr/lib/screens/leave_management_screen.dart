import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../services/api_service.dart';


import '../utils/app_theme.dart';
import 'apply_leave_screen.dart';
import 'package:intl/intl.dart';

class LeaveManagementScreen extends StatefulWidget {
  const LeaveManagementScreen({super.key});

  @override
  State<LeaveManagementScreen> createState() => _LeaveManagementScreenState();
}

class _LeaveManagementScreenState extends State<LeaveManagementScreen> {
  final ApiService _apiService = ApiService();
  List<dynamic> _requests = [];
  bool _isLoading = true;
  final bool _isAdmin = ApiService.userRole == 'ADMIN';

  @override
  void initState() {
    super.initState();
    _loadRequests();
  }

  Future<void> _loadRequests() async {
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

      final data = _isAdmin 
        ? await _apiService.getAllLeaveRequests()
        : await _apiService.getMyLeaveRequests(effectiveEmployeeId!);
        
      if (mounted) {
        setState(() {
          _requests = data;
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

  void _navigateToApplyLeave() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const ApplyLeaveScreen()),
    ).then((value) {
      if (value == true) _loadRequests();
    });
  }

  void _respondToLeave(int id, bool approve) async {
    final TextEditingController noteController = TextEditingController();
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(approve ? 'Approve Request' : 'Reject Request'),
        content: TextField(
          controller: noteController,
          decoration: InputDecoration(
            labelText: approve ? 'Approval Notes' : 'Rejection Reason',
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          ElevatedButton(
            onPressed: () async {
              try {
                if (approve) {
                  await _apiService.approveLeaveRequest(id, noteController.text);
                } else {
                  await _apiService.rejectLeaveRequest(id, noteController.text);
                }
                Navigator.pop(context);
                _loadRequests();
              } catch (e) {
                ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
              }
            },
            style: ElevatedButton.styleFrom(backgroundColor: approve ? AppTheme.success : AppTheme.error),
            child: Text(approve ? 'Approve' : 'Reject'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Leave Management'),
        elevation: 0,
        actions: [
          IconButton(onPressed: _loadRequests, icon: const Icon(Icons.refresh)),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _loadRequests,
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
                          Center(child: Text('No leave requests found')),
                        ],
                      )
                    : ListView.builder(
                        padding: const EdgeInsets.all(12),
                        itemCount: _requests.length,
                        itemBuilder: (context, index) {
                          final req = _requests[index];
                          final status = req['status']?.toString() ?? 'Pending';
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
                                child: Icon(Icons.calendar_month, color: statusColor),
                              ),
                              title: Text(
                                req['leaveType'] ?? 'Leave Request',
                                style: const TextStyle(fontWeight: FontWeight.bold),
                              ),
                              subtitle: Text(
                                '${req['totalDays']} Days (${_formatDate(req['startDate'])} - ${_formatDate(req['endDate'])})',
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
                                      _infoRow('Reason:', req['reason'] ?? 'N/A'),
                                      if (req['approvalNotes'] != null) ...[
                                        const SizedBox(height: 8),
                                        _infoRow('Admin Note:', req['approvalNotes']),
                                      ],
                                      if (req['rejectionReason'] != null) ...[
                                        const SizedBox(height: 8),
                                        _infoRow('Rejection Reason:', req['rejectionReason']),
                                      ],
                                      if (_isAdmin && status == 'Pending') ...[
                                        const Divider(height: 24),
                                        Row(
                                          mainAxisAlignment: MainAxisAlignment.end,
                                          children: [
                                            TextButton.icon(
                                              onPressed: () => _respondToLeave(req['id'], false),
                                              icon: const Icon(Icons.close, color: Colors.red, size: 18),
                                              label: const Text('Reject', style: TextStyle(color: Colors.red)),
                                            ),
                                            const SizedBox(width: 12),
                                            ElevatedButton.icon(
                                              onPressed: () => _respondToLeave(req['id'], true),
                                              icon: const Icon(Icons.done, size: 18),
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
        onPressed: _navigateToApplyLeave,
        icon: const Icon(Icons.add),
        label: const Text('Apply Leave'),
      ) : null,
    );
  }

  Widget _buildStatsHeader() {
    int pending = _requests.where((r) => r['status'] == 'Pending').length;
    int approved = _requests.where((r) => r['status'] == 'Approved').length;

    return Container(
      padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 16),
      color: AppTheme.primary.withOpacity(0.05),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _statItem('Pending', pending.toString(), Colors.orange),
          _statItem('Approved', approved.toString(), Colors.green),
          _statItem('Total', _requests.length.toString(), AppTheme.primary),
        ],
      ),
    );
  }

  Widget _statItem(String label, String value, Color color) {
    return Column(
      children: [
        Text(value, style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: color)),
        Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
      ],
    );
  }

  Widget _infoRow(String label, String value) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(width: 80, child: Text(label, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13))),
        Expanded(child: Text(value, style: const TextStyle(fontSize: 13))),
      ],
    );
  }

  String _formatDate(String? dateStr) {
    if (dateStr == null) return '';
    try {
      DateTime dt = DateTime.parse(dateStr);
      return DateFormat('MMM dd').format(dt);
    } catch (_) {
      return dateStr;
    }
  }

  Color _getStatusColor(String status) {
    switch (status.toLowerCase()) {
      case 'approved': return AppTheme.success;
      case 'pending': return Colors.orange;
      case 'rejected': return AppTheme.error;
      default: return Colors.grey;
    }
  }
}
