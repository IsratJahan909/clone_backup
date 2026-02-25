import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../utils/app_theme.dart';

class AdminDashboardScreen extends StatefulWidget {
  const AdminDashboardScreen({super.key});

  @override
  State<AdminDashboardScreen> createState() => _AdminDashboardScreenState();
}

class _AdminDashboardScreenState extends State<AdminDashboardScreen> {
  final ApiService _apiService = ApiService();
  Map<String, dynamic>? _data;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    try {
      final res = await _apiService.getAdminDashboard();
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

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Center(child: CircularProgressIndicator());

    return RefreshIndicator(
      onRefresh: _loadData,
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text('Admin Dashboard', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
          const SizedBox(height: 20),
          
          GridView.count(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            crossAxisCount: 2,
            crossAxisSpacing: 10,
            mainAxisSpacing: 10,
            childAspectRatio: 1.2,
            children: [
              _statCard('Total Staff', _data?['totalEmployees']?.toString() ?? '0', Icons.people, Colors.blue),
              _statCard('Departments', _data?['activeDepartments']?.toString() ?? '0', Icons.business, Colors.green),
              _statCard('Present Today', _data?['presentToday']?.toString() ?? '0', Icons.check_circle, Colors.orange),
              _statCard('Pending Leaves', _data?['pendingLeaves']?.toString() ?? '0', Icons.event_note, Colors.red),
            ],
          ),
          
          const SizedBox(height: 30),
          const Text('Recent Requests', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          const SizedBox(height: 10),
          
          // Template for recent items
          Card(
            child: ListTile(
              leading: const CircleAvatar(child: Icon(Icons.person)),
              title: const Text('John Doe (Advance Salary)'),
              subtitle: const Text('Requested 5000 TK for medical reason'),
              trailing: const Text('Pending', style: TextStyle(color: Colors.orange, fontWeight: FontWeight.bold)),
            ),
          ),
          Card(
            child: ListTile(
              leading: const CircleAvatar(child: Icon(Icons.person)),
              title: const Text('Jane Smith (Leave Request)'),
              subtitle: const Text('Applied for 2 days sick leave'),
              trailing: const Text('Approved', style: TextStyle(color: Colors.green, fontWeight: FontWeight.bold)),
            ),
          ),
        ],
      ),
    );
  }

  Widget _statCard(String title, String value, IconData icon, Color color) {
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 30, color: color),
            const SizedBox(height: 4),
            Text(value, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            Flexible(
              child: Text(
                title, 
                style: const TextStyle(fontSize: 13, color: Colors.grey),
                textAlign: TextAlign.center,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
