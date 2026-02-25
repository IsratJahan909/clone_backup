import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../utils/app_theme.dart';
import 'package:intl/intl.dart';

class AttendanceScreen extends StatefulWidget {
  const AttendanceScreen({super.key});

  @override
  State<AttendanceScreen> createState() => _AttendanceScreenState();
}

class _AttendanceScreenState extends State<AttendanceScreen> {
  final ApiService _apiService = ApiService();
  List<dynamic> _history = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadAttendance();
  }

  Future<void> _loadAttendance() async {
    if (ApiService.userId == null) {
      if (mounted) {
        setState(() => _isLoading = false);
      }
      return;
    }

    try {
      final isAdmin = ApiService.userRole?.toUpperCase() == 'ADMIN';
      int? effectiveEmployeeId = ApiService.userId;

      if (!isAdmin && effectiveEmployeeId != null) {
        // Resolve employee profile to get the correct employeeId
        final empProfile = await _apiService.getEmployeeByUserId(effectiveEmployeeId);
        if (empProfile != null) {
          effectiveEmployeeId = empProfile.employeeId;
        }
      }

      final data = isAdmin 
          ? await _apiService.getAllAttendance()
          : await _apiService.getMyAttendance(effectiveEmployeeId!);
          
      if (mounted) {
        setState(() {
          _history = data;
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

  Future<void> _handleCheckInOut() async {
    final today = DateFormat('yyyy-MM-dd').format(DateTime.now());
    
    // Find today's record from history
    dynamic todaysRecord;
    try {
      todaysRecord = _history.firstWhere((r) => r['date'] != null && r['date'].toString().startsWith(today));
    } catch (_) {
      todaysRecord = null;
    }

    setState(() => _isLoading = true);
    try {
      int? effectiveEmployeeId = ApiService.userId;
      
      // Resolve employee profile to get the correct employeeId
      final empProfile = await _apiService.getEmployeeByUserId(ApiService.userId!);
      if (empProfile != null) {
        effectiveEmployeeId = empProfile.employeeId;
      }

      if (todaysRecord == null) {
        await _apiService.checkIn(effectiveEmployeeId!);
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Checked In Successfully!'), backgroundColor: Colors.green));
      } else if (todaysRecord['clockOutTime'] == null) {
        await _apiService.checkOut(todaysRecord['id'], effectiveEmployeeId!);
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Checked Out Successfully!'), backgroundColor: Colors.blue));
      } else {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Day already completed.')));
      }
      _loadAttendance();
    } catch (e) {
      setState(() => _isLoading = false);
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Action failed: $e'), backgroundColor: Colors.red));
    }
  }

  String _formatTime(String? timeStr) {
    if (timeStr == null || timeStr.isEmpty) return '--:--';
    try {
      if (timeStr.contains('T')) {
        String timePart = timeStr.split('T').last;
        return timePart.length >= 5 ? timePart.substring(0, 5) : timePart;
      }
      return timeStr.length >= 5 ? timeStr.substring(0, 5) : timeStr;
    } catch (e) {
      return timeStr;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.scaffoldBg,
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadAttendance,
              child: CustomScrollView(
                slivers: [
                  SliverToBoxAdapter(child: _buildHeader()),
                  SliverToBoxAdapter(child: _buildStatsRow()),
                  const SliverToBoxAdapter(
                    child: Padding(
                      padding: EdgeInsets.fromLTRB(16, 24, 16, 8),
                      child: Text(
                        'Recent History',
                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                      ),
                    ),
                  ),
                  if (_history.isEmpty)
                    const SliverFillRemaining(
                      child: Center(child: Text('No attendance records found.')),
                    )
                  else
                    SliverPadding(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      sliver: SliverList(
                        delegate: SliverChildBuilderDelegate(
                          (context, index) {
                            final record = _history[index];
                            return _buildAttendanceCard(record);
                          },
                          childCount: _history.length,
                        ),
                      ),
                    ),
                  const SliverToBoxAdapter(child: SizedBox(height: 20)),
                ],
              ),
            ),
    );
  }

  Widget _buildHeader() {
    final today = DateFormat('yyyy-MM-dd').format(DateTime.now());
    dynamic todaysRecord;
    try {
      todaysRecord = _history.firstWhere((r) => r['date'] != null && r['date'].toString().startsWith(today));
    } catch (_) {
      todaysRecord = null;
    }
    
    bool canCheckIn = todaysRecord == null;
    bool canCheckOut = todaysRecord != null && todaysRecord['clockOutTime'] == null;
    bool isCompleted = todaysRecord != null && todaysRecord['clockOutTime'] != null;

    final isAdmin = ApiService.userRole?.toUpperCase() == 'ADMIN';

    return Stack(
      children: [
        Container(
          width: double.infinity,
          height: isAdmin ? 140 : 220,
          padding: const EdgeInsets.fromLTRB(24, 20, 24, 0),
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              colors: [AppTheme.primary, AppTheme.primaryDark],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            borderRadius: BorderRadius.only(
              bottomLeft: Radius.circular(40),
              bottomRight: Radius.circular(40),
            ),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 10),
              Text(
                isAdmin ? 'Attendance Management' : 'My Attendance',
                style: const TextStyle(color: Colors.white, fontSize: 26, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 4),
              Text(
                DateFormat('EEEE, d MMMM yyyy').format(DateTime.now()),
                style: const TextStyle(color: Colors.white70, fontSize: 14),
              ),
            ],
          ),
        ),
        if (!isAdmin)
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 100, 16, 0),
          child: Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(24),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.1),
                  blurRadius: 20,
                  offset: const Offset(0, 10),
                ),
              ],
            ),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Expanded( // Added Expanded to prevent overflow
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            DateFormat('hh:mm a').format(DateTime.now()),
                            style: const TextStyle(fontSize: 32, fontWeight: FontWeight.bold, color: AppTheme.textPrimary),
                          ),
                          const SizedBox(height: 4),
                          const Text('Current Time', style: TextStyle(color: AppTheme.textSecondary, fontSize: 14)),
                        ],
                      ),
                    ),
                    Icon(
                      isCompleted ? Icons.verified : Icons.timer_outlined,
                      size: 48,
                      color: isCompleted ? Colors.green : AppTheme.primary.withOpacity(0.2),
                    ),
                  ],
                ),
                const SizedBox(height: 24),
                if (isCompleted)
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    decoration: BoxDecoration(
                      color: Colors.green.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: const Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.check_circle, color: Colors.green),
                        SizedBox(width: 8),
                        Text('Day Completed', style: TextStyle(color: Colors.green, fontWeight: FontWeight.bold)),
                      ],
                    ),
                  )
                else
                  SizedBox(
                    width: double.infinity,
                    height: 56,
                    child: ElevatedButton(
                      onPressed: _handleCheckInOut,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: canCheckIn ? AppTheme.primary : Colors.orange,
                        foregroundColor: Colors.white,
                        elevation: 0,
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                      ),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(canCheckIn ? Icons.login : Icons.logout),
                          const SizedBox(width: 12),
                          Text(
                            canCheckIn ? 'Check In Now' : 'Check Out Now',
                            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                          ),
                        ],
                      ),
                    ),
                  ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildStatsRow() {
    int present = _history.where((r) => r['status'] == 'PRESENT').length;
    int lateCount = _history.where((r) => r['status'] == 'LATE').length;
    int absent = _history.where((r) => r['status'] == 'ABSENT').length;

    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 20, 16, 0),
      child: Row(
        children: [
          _statItem('Present', present.toString(), Colors.green),
          const SizedBox(width: 12),
          _statItem('Late', lateCount.toString(), Colors.orange),
          const SizedBox(width: 12),
          _statItem('Absent', absent.toString(), Colors.red),
        ],
      ),
    );
  }

  Widget _statItem(String label, String value, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 16),
        decoration: BoxDecoration(
          color: color.withOpacity(0.1),
          borderRadius: BorderRadius.circular(15),
          border: Border.all(color: color.withOpacity(0.3)),
        ),
        child: Column(
          children: [
            Text(value, style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: color)),
            const SizedBox(height: 4),
            Text(label, style: const TextStyle(fontSize: 12, color: AppTheme.textSecondary)),
          ],
        ),
      ),
    );
  }

  Widget _buildAttendanceCard(dynamic record) {
    final status = record['status']?.toString() ?? 'PRESENT';
    final statusColor = _getStatusColor(status);
    
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(15),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: IntrinsicHeight(
        child: Row(
          children: [
            Container(
              width: 6,
              decoration: BoxDecoration(
                color: statusColor,
                borderRadius: const BorderRadius.only(
                  topLeft: Radius.circular(15),
                  bottomLeft: Radius.circular(15),
                ),
              ),
            ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Row(
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            record['date'] ?? 'N/A',
                            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                          ),
                          const SizedBox(height: 8),
                          Row(
                            children: [
                              // Added Expanded/Flexible to time info to prevent overflow
                              Expanded(child: _timeInfo(Icons.login, 'In', _formatTime(record['clockInTime']))),
                              const SizedBox(width: 10),
                              Expanded(child: _timeInfo(Icons.logout, 'Out', _formatTime(record['clockOutTime']))),
                            ],
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(width: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                      decoration: BoxDecoration(
                        color: statusColor.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Text(
                        status,
                        style: TextStyle(
                          color: statusColor,
                          fontWeight: FontWeight.bold,
                          fontSize: 11,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _timeInfo(IconData icon, String label, String time) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 14, color: AppTheme.textSecondary),
        const SizedBox(width: 4),
        Flexible( // Added Flexible to text
          child: Text(
            '$label: $time',
            style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w500),
            overflow: TextOverflow.ellipsis,
          ),
        ),
      ],
    );
  }

  Color _getStatusColor(String status) {
    switch (status.toUpperCase()) {
      case 'PRESENT': return Colors.green;
      case 'LATE': return Colors.orange;
      case 'ABSENT': return Colors.red;
      case 'LEAVE': return Colors.blue;
      default: return Colors.grey;
    }
  }
}
