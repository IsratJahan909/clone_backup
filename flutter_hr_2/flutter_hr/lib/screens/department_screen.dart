import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_slidable/flutter_slidable.dart';
import '../providers/hr_provider.dart';
import '../models/department.dart';
import '../utils/app_theme.dart';
import 'add_department_screen.dart';

class DepartmentScreen extends StatefulWidget {
  const DepartmentScreen({super.key});

  @override
  State<DepartmentScreen> createState() => _DepartmentScreenState();
}

class _DepartmentScreenState extends State<DepartmentScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(() => context.read<HrProvider>().fetchDepartments());
  }

  @override
  Widget build(BuildContext context) {
    final hrProvider = context.watch<HrProvider>();
    final departments = hrProvider.departments;

    return Scaffold(
      backgroundColor: AppTheme.scaffoldBg,
      body: hrProvider.isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: () => hrProvider.fetchDepartments(),
              child: ListView.builder(
                padding: const EdgeInsets.symmetric(vertical: 12),
                itemCount: departments.length,
                itemBuilder: (context, index) {
                  final dept = departments[index];
                  return _buildDepartmentCard(dept, hrProvider);
                },
              ),
            ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => const AddDepartmentScreen()),
          );
        },
        icon: const Icon(Icons.add),
        label: const Text('Add Department'),
      ),
    );
  }

  Widget _buildDepartmentCard(Department dept, HrProvider hrProvider) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: AppTheme.cardRadius,
        boxShadow: AppTheme.softShadow,
      ),
      child: ClipRRect(
        borderRadius: AppTheme.cardRadius,
        child: Slidable(
          key: ValueKey(dept.departmentId),
          endActionPane: ActionPane(
            motion: const ScrollMotion(),
            children: [
              SlidableAction(
                onPressed: (context) {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => AddDepartmentScreen(department: dept)),
                  );
                },
                backgroundColor: Colors.orange.shade400,
                foregroundColor: Colors.white,
                icon: Icons.edit,
                // label: 'Edit',
              ),
              SlidableAction(
                onPressed: (context) => _confirmDelete(dept, hrProvider),
                backgroundColor: AppTheme.error,
                foregroundColor: Colors.white,
                icon: Icons.delete,
                // label: 'Delete',
              ),
            ],
          ),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Row(
              children: [
                Container(
                  width: 50,
                  height: 50,
                  decoration: BoxDecoration(
                    color: AppTheme.primary.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: const Icon(Icons.business, color: AppTheme.primary, size: 28),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        dept.departmentName,
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: AppTheme.textPrimary,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Row(
                        children: [
                          const Icon(Icons.person_outline, size: 14, color: AppTheme.textSecondary),
                          const SizedBox(width: 4),
                          Text(
                            'Head: ${dept.departmentHead}',
                            style: const TextStyle(fontSize: 14, color: AppTheme.textSecondary),
                          ),
                        ],
                      ),
                      if (dept.description != null && dept.description!.isNotEmpty) ...[
                        const SizedBox(height: 4),
                        Text(
                          dept.description!,
                          style: const TextStyle(fontSize: 12, color: AppTheme.textMuted),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ],
                    ],
                  ),
                ),
                const Icon(Icons.swipe_left, color: AppTheme.textMuted, size: 16),
              ],
            ),
          ),
        ),
      ),
    );
  }

  void _confirmDelete(Department dept, HrProvider hrProvider) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Confirm Delete'),
        content: Text('Are you sure you want to delete ${dept.departmentName}?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          TextButton(
            onPressed: () {
              hrProvider.deleteDepartment(dept.departmentId!);
              Navigator.pop(context);
            },
            child: const Text('Delete', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }
}
