import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../models/bonus.dart';
import '../models/employee.dart';
import '../utils/app_theme.dart';

class AddBonusScreen extends StatefulWidget {
  final List<Employee> employees;
  const AddBonusScreen({super.key, required this.employees});

  @override
  State<AddBonusScreen> createState() => _AddBonusScreenState();
}

class _AddBonusScreenState extends State<AddBonusScreen> {
  final ApiService _apiService = ApiService();
  final _formKey = GlobalKey<FormState>();

  int? _selectedEmployeeId;
  String? _selectedType;
  final _amountController = TextEditingController();
  final _descriptionController = TextEditingController();
  
  final List<String> _bonusTypes = [
    'Festival Bonus',
    'Performance Bonus',
    'Yearly Bonus',
    'Eid Bonus',
    'Incentive',
    'Others'
  ];
  
  int _selectedMonth = DateTime.now().month;
  int _selectedYear = DateTime.now().year;

  List<Employee> _allEmployees = [];
  bool _isLoadingEmployees = false;

  @override
  void initState() {
    super.initState();
    _allEmployees = widget.employees;
    if (_allEmployees.isEmpty) {
      _fetchEmployees();
    }
  }

  Future<void> _fetchEmployees() async {
    setState(() => _isLoadingEmployees = true);
    try {
      final emps = await _apiService.getEmployees();
      setState(() {
        _allEmployees = emps;
        _isLoadingEmployees = false;
      });
    } catch (e) {
      setState(() => _isLoadingEmployees = false);
      print('Error fetching employees in AddBonusScreen: $e');
    }
  }

  Future<void> _saveBonus() async {
    if (_formKey.currentState!.validate() && _selectedEmployeeId != null && _selectedType != null) {
      try {
        final newBonus = Bonus(
          employeeId: _selectedEmployeeId!,
          bonusType: _selectedType!,
          bonusAmount: double.parse(_amountController.text),
          description: _descriptionController.text,
          status: 'PENDING',
          forMonth: _selectedMonth,
          forYear: _selectedYear,
          isPaid: false,
        );

        await _apiService.createBonus(newBonus);
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Bonus assigned successfully'), backgroundColor: Colors.green),
          );
          Navigator.pop(context, true);
        }
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e'), backgroundColor: Colors.red),
        );
      }
    } else if (_selectedEmployeeId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select an employee')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Assign New Bonus'),
        elevation: 0,
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(30),
              decoration: const BoxDecoration(
                color: AppTheme.primary,
                borderRadius: BorderRadius.only(
                  bottomLeft: Radius.circular(30),
                  bottomRight: Radius.circular(30),
                ),
              ),
              child: const Column(
                children: [
                  Icon(Icons.card_giftcard, size: 70, color: Colors.white),
                  SizedBox(height: 16),
                  Text(
                    'Employee Recognition',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Text(
                    'Reward excellence with bonuses',
                    style: TextStyle(color: Colors.white70, fontSize: 14),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(20.0),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildSectionLabel('Recipient Info'),
                    Card(
                      child: Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Column(
                          children: [
                            DropdownButtonFormField<int>(
                              isExpanded: true,
                              decoration: InputDecoration(
                                labelText: _isLoadingEmployees ? 'Loading Employees...' : 'Select Employee',
                                prefixIcon: _isLoadingEmployees 
                                  ? const SizedBox(width: 20, height: 20, child: Padding(padding: EdgeInsets.all(10), child: CircularProgressIndicator(strokeWidth: 2)))
                                  : const Icon(Icons.person),
                              ),
                              items: _allEmployees.map((e) => DropdownMenuItem<int>(
                                value: e.id,
                                child: Text(
                                  e.fullName,
                                  overflow: TextOverflow.ellipsis,
                                ),
                              )).toList(),
                              onChanged: (val) => setState(() => _selectedEmployeeId = val),
                              validator: (val) => val == null ? 'Selection required' : null,
                            ),
                            const SizedBox(height: 16),
                            Row(
                              children: [
                                Expanded(
                                  child: DropdownButtonFormField<int>(
                                    isExpanded: true,
                                    value: _selectedMonth,
                                    decoration: const InputDecoration(labelText: 'Month'),
                                    items: List.generate(12, (index) => DropdownMenuItem(
                                      value: index + 1,
                                      child: Text(
                                        _getMonthName(index + 1),
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                    )),
                                    onChanged: (val) => setState(() => _selectedMonth = val!),
                                  ),
                                ),
                                const SizedBox(width: 12),
                                Expanded(
                                  child: DropdownButtonFormField<int>(
                                    isExpanded: true,
                                    value: _selectedYear,
                                    decoration: const InputDecoration(labelText: 'Year'),
                                    items: [2024, 2025, 2026].map((y) => DropdownMenuItem(
                                      value: y,
                                      child: Text(y.toString()),
                                    )).toList(),
                                    onChanged: (val) => setState(() => _selectedYear = val!),
                                  ),
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 25),
                    _buildSectionLabel('Bonus Details'),
                    Card(
                      child: Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Column(
                          children: [
                            DropdownButtonFormField<String>(
                              isExpanded: true,
                              value: _selectedType,
                              decoration: const InputDecoration(
                                labelText: 'Bonus Type',
                                prefixIcon: Icon(Icons.category),
                              ),
                              items: _bonusTypes.map((type) => DropdownMenuItem(
                                value: type,
                                child: Text(
                                  type,
                                  overflow: TextOverflow.ellipsis,
                                ),
                              )).toList(),
                              onChanged: (val) => setState(() => _selectedType = val),
                              validator: (val) => val == null ? 'Selection required' : null,
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: _amountController,
                              keyboardType: TextInputType.number,
                              decoration: const InputDecoration(
                                labelText: 'Bonus Amount',
                                prefixIcon: Icon(Icons.payments),
                                suffixText: '৳',
                              ),
                              validator: (val) => val == null || val.isEmpty ? 'Amount required' : null,
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: _descriptionController,
                              maxLines: 3,
                              decoration: const InputDecoration(
                                labelText: 'Description/Criteria',
                                prefixIcon: Icon(Icons.description),
                                alignLabelWithHint: true,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 35),
                    SizedBox(
                      width: double.infinity,
                      height: 55,
                      child: ElevatedButton.icon(
                        onPressed: _saveBonus,
                        icon: const Icon(Icons.check_circle_outline),
                        label: const Text('Assign Bonus Now', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                        style: ElevatedButton.styleFrom(
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                          backgroundColor: AppTheme.primary,
                        ),
                      ),
                    ),
                    const SizedBox(height: 25),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionLabel(String label) {
    return Padding(
      padding: const EdgeInsets.only(left: 8.0, bottom: 8.0),
      child: Text(
        label.toUpperCase(),
        style: const TextStyle(
          fontSize: 13,
          fontWeight: FontWeight.bold,
          color: AppTheme.primary,
          letterSpacing: 1.2,
        ),
      ),
    );
  }

  String _getMonthName(int month) {
    const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
    return months[month - 1];
  }
}
