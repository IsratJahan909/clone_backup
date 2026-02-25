import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../models/salary.dart';
import '../models/employee.dart';
import '../utils/app_theme.dart';
import 'package:intl/intl.dart';

class AddSalaryScreen extends StatefulWidget {
  final List<Employee> employees;
  const AddSalaryScreen({super.key, required this.employees});

  @override
  State<AddSalaryScreen> createState() => _AddSalaryScreenState();
}

class _AddSalaryScreenState extends State<AddSalaryScreen> {
  final ApiService _apiService = ApiService();
  final _formKey = GlobalKey<FormState>();

  int? _selectedEmployeeId;
  final _baseSalaryController = TextEditingController();
  final _allowancesController = TextEditingController(text: '0');
  final _bonusController = TextEditingController(text: '0');
  final _deductionsController = TextEditingController(text: '0');
  final _taxController = TextEditingController(text: '0');
  
  int _selectedMonth = DateTime.now().month;
  int _selectedYear = DateTime.now().year;

  double _netSalary = 0.0;

  @override
  void initState() {
    super.initState();
    _baseSalaryController.addListener(_calculateNetSalary);
    _allowancesController.addListener(_calculateNetSalary);
    _bonusController.addListener(_calculateNetSalary);
    _deductionsController.addListener(_calculateNetSalary);
    _taxController.addListener(_calculateNetSalary);
  }

  void _calculateNetSalary() {
    double base = double.tryParse(_baseSalaryController.text) ?? 0.0;
    double allow = double.tryParse(_allowancesController.text) ?? 0.0;
    double bonus = double.tryParse(_bonusController.text) ?? 0.0;
    double deduct = double.tryParse(_deductionsController.text) ?? 0.0;
    double tax = double.tryParse(_taxController.text) ?? 0.0;

    setState(() {
      _netSalary = base + allow + bonus - deduct - tax;
    });
  }

  Future<void> _saveSalary() async {
    if (_formKey.currentState!.validate() && _selectedEmployeeId != null) {
      try {
        final newSalary = Salary(
          employeeId: _selectedEmployeeId!,
          month: _selectedMonth,
          year: _selectedYear,
          baseSalary: double.parse(_baseSalaryController.text),
          allowances: double.parse(_allowancesController.text),
          bonusAmount: double.parse(_bonusController.text),
          deductions: double.parse(_deductionsController.text),
          tax: double.parse(_taxController.text),
          netSalary: _netSalary,
          status: 'Pending',
        );

        await _apiService.createSalaryRecord(newSalary);
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Salary record added successfully'), backgroundColor: Colors.green),
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
        title: const Text('New Salary Record'),
        elevation: 0,
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(24),
              decoration: const BoxDecoration(
                color: AppTheme.primary,
                borderRadius: BorderRadius.only(
                  bottomLeft: Radius.circular(30),
                  bottomRight: Radius.circular(30),
                ),
              ),
              child: Column(
                children: [
                  const Icon(Icons.account_balance, size: 60, color: Colors.white),
                  const SizedBox(height: 16),
                  FittedBox( 
                    fit: BoxFit.scaleDown,
                    child: Text(
                      'Net Salary: ৳${_netSalary.toStringAsFixed(2)}',
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 28,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const Text(
                    'Real-time calculation',
                    style: TextStyle(color: Colors.white70, fontSize: 14),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(12.0), // Reduced outer padding
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildSectionHeader('Employee & Period', Icons.person_outline),
                    Card(
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                      child: Padding(
                        padding: const EdgeInsets.all(10.0), // Reduced inner padding
                        child: Column(
                          children: [
                            DropdownButtonFormField<int>(
                              decoration: const InputDecoration(
                                labelText: 'Select Employee',
                                prefixIcon: Icon(Icons.person),
                                contentPadding: EdgeInsets.symmetric(horizontal: 10, vertical: 10),
                              ),
                              isExpanded: true, 
                              isDense: true, // Makes dropdown more compact
                              items: widget.employees.map((e) => DropdownMenuItem<int>(
                                value: e.employeeId,
                                child: Text(e.fullName, overflow: TextOverflow.ellipsis, style: const TextStyle(fontSize: 14)),
                              )).toList(),
                              onChanged: (val) {
                                setState(() => _selectedEmployeeId = val);
                                final emp = widget.employees.firstWhere((e) => e.employeeId == val);
                                _baseSalaryController.text = emp.baseSalary.toString();
                              },
                            ),
                            const SizedBox(height: 12),
                            Row(
                              children: [
                                Expanded(
                                  flex: 3, // Month takes more space
                                  child: DropdownButtonFormField<int>(
                                    value: _selectedMonth,
                                    isDense: true,
                                    decoration: const InputDecoration(
                                      labelText: 'Month',
                                      contentPadding: EdgeInsets.symmetric(horizontal: 8, vertical: 10),
                                    ),
                                    items: List.generate(12, (index) => DropdownMenuItem(
                                      value: index + 1,
                                      child: Text(DateFormat('MMM').format(DateTime(2024, index + 1)), style: const TextStyle(fontSize: 13)),
                                    )),
                                    onChanged: (val) => setState(() => _selectedMonth = val!),
                                  ),
                                ),
                                const SizedBox(width: 8),
                                Expanded(
                                  flex: 2, // Year takes less space
                                  child: DropdownButtonFormField<int>(
                                    value: _selectedYear,
                                    isDense: true,
                                    decoration: const InputDecoration(
                                      labelText: 'Year',
                                      contentPadding: EdgeInsets.symmetric(horizontal: 8, vertical: 10),
                                    ),
                                    items: [2024, 2025, 2026].map((y) => DropdownMenuItem(
                                      value: y,
                                      child: Text(y.toString(), style: const TextStyle(fontSize: 13)),
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
                    const SizedBox(height: 16),
                    _buildSectionHeader('Earnings', Icons.add_circle_outline),
                    Card(
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                      child: Padding(
                        padding: const EdgeInsets.all(10.0),
                        child: Column(
                          children: [
                            _buildTextField(_baseSalaryController, 'Base Salary', Icons.money),
                            const SizedBox(height: 10),
                            _buildTextField(_allowancesController, 'Allowances', Icons.add_moderator),
                            const SizedBox(height: 10),
                            _buildTextField(_bonusController, 'Bonus Amount', Icons.star_outline),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 16),
                    _buildSectionHeader('Deductions', Icons.remove_circle_outline),
                    Card(
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                      child: Padding(
                        padding: const EdgeInsets.all(10.0),
                        child: Column(
                          children: [
                            _buildTextField(_taxController, 'Income Tax', Icons.account_balance_wallet),
                            const SizedBox(height: 10),
                            _buildTextField(_deductionsController, 'Other Deductions', Icons.money_off),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 24),
                    SizedBox(
                      width: double.infinity,
                      height: 50,
                      child: ElevatedButton(
                        onPressed: _saveSalary,
                        style: ElevatedButton.styleFrom(
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                          backgroundColor: AppTheme.primary,
                        ),
                        child: const Text('Generate Salary Record', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white)),
                      ),
                    ),
                    const SizedBox(height: 20),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title, IconData icon) {
    return Padding(
      padding: const EdgeInsets.only(left: 4.0, bottom: 8.0),
      child: Row(
        children: [
          Icon(icon, size: 18, color: AppTheme.primary),
          const SizedBox(width: 8),
          Expanded( 
            child: Text(
              title,
              style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: AppTheme.primary),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTextField(TextEditingController controller, String label, IconData icon) {
    return TextFormField(
      controller: controller,
      keyboardType: TextInputType.number,
      style: const TextStyle(fontSize: 14),
      decoration: InputDecoration(
        labelText: label,
        prefixIcon: Icon(icon, size: 18),
        suffixText: '৳',
        contentPadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
      ),
      validator: (val) => val == null || val.isEmpty ? 'Required' : null,
    );
  }
}
