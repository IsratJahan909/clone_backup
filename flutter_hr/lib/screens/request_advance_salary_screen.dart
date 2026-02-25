import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../models/advance_salary.dart';
import '../utils/app_theme.dart';
import 'package:intl/intl.dart';

class RequestAdvanceSalaryScreen extends StatefulWidget {
  const RequestAdvanceSalaryScreen({super.key});

  @override
  State<RequestAdvanceSalaryScreen> createState() => _RequestAdvanceSalaryScreenState();
}

class _RequestAdvanceSalaryScreenState extends State<RequestAdvanceSalaryScreen> {
  final ApiService _apiService = ApiService();
  final _formKey = GlobalKey<FormState>();

  final _amountController = TextEditingController();
  final _reasonController = TextEditingController();
  final _repaymentMonthsController = TextEditingController(text: '1');
  
  String _selectedMonth = DateFormat('MMMM').format(DateTime.now()).toUpperCase();
  int _selectedYear = DateTime.now().year;

  final List<String> _months = [
    'JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE',
    'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER'
  ];

  Future<void> _submitRequest() async {
    if (_formKey.currentState!.validate()) {
      try {
        int? effectiveEmployeeId = ApiService.userId;
        
        // Resolve employee profile to get the correct employeeId
        final empProfile = await _apiService.getEmployeeByUserId(ApiService.userId!);
        if (empProfile != null) {
          effectiveEmployeeId = empProfile.employeeId;
        }

        final newRequest = AdvanceSalary(
          employeeId: effectiveEmployeeId!,
          amount: double.parse(_amountController.text),
          requestReason: _reasonController.text,
          forMonth: _selectedMonth,
          forYear: _selectedYear,
          repaymentMonths: int.parse(_repaymentMonthsController.text),
          status: 'Pending',
        );

        await _apiService.requestAdvanceSalary(newRequest.toJson());
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Request submitted successfully'), backgroundColor: AppTheme.success),
          );
          Navigator.pop(context, true);
        }
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e'), backgroundColor: AppTheme.error),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Request Advance'),
        elevation: 0,
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 30, horizontal: 20),
              decoration: const BoxDecoration(
                color: AppTheme.primary,
                borderRadius: BorderRadius.only(
                  bottomLeft: Radius.circular(30),
                  bottomRight: Radius.circular(30),
                ),
              ),
              child: const Column(
                children: [
                  Icon(Icons.request_quote_outlined, size: 60, color: Colors.white),
                  SizedBox(height: 16),
                  Text(
                    'Advance Salary Request',
                    style: TextStyle(color: Colors.white, fontSize: 22, fontWeight: FontWeight.bold),
                  ),
                  SizedBox(height: 8),
                  Text(
                    'Request funds ahead of your next paycheck',
                    style: TextStyle(color: Colors.white70, fontSize: 13),
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
                    _sectionHeader('Salary Period', Icons.calendar_today),
                    Card(
                      child: Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Row(
                          children: [
                            Expanded(
                              flex: 2,
                              child: DropdownButtonFormField<String>(
                                value: _selectedMonth,
                                decoration: const InputDecoration(labelText: 'Month'),
                                items: _months.map((m) => DropdownMenuItem(value: m, child: Text(m))).toList(),
                                onChanged: (val) => setState(() => _selectedMonth = val!),
                              ),
                            ),
                            const SizedBox(width: 16),
                            Expanded(
                              child: TextFormField(
                                initialValue: _selectedYear.toString(),
                                decoration: const InputDecoration(labelText: 'Year'),
                                keyboardType: TextInputType.number,
                                onChanged: (val) => _selectedYear = int.tryParse(val) ?? _selectedYear,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 24),
                    _sectionHeader('Request Details', Icons.monetization_on_outlined),
                    Card(
                      child: Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Column(
                          children: [
                            TextFormField(
                              controller: _amountController,
                              keyboardType: TextInputType.number,
                              decoration: const InputDecoration(
                                labelText: 'Amount (৳)',
                                prefixIcon: Icon(Icons.account_balance_wallet_outlined),
                              ),
                              validator: (val) => val == null || val.isEmpty ? 'Amount required' : null,
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: _repaymentMonthsController,
                              keyboardType: TextInputType.number,
                              decoration: const InputDecoration(
                                labelText: 'Repayment Duration (Months)',
                                prefixIcon: Icon(Icons.history_outlined),
                              ),
                              validator: (val) {
                                if (val == null || val.isEmpty) return 'Required';
                                final n = int.tryParse(val);
                                if (n == null || n < 1) return 'Min 1 month';
                                return null;
                              },
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: _reasonController,
                              maxLines: 3,
                              decoration: const InputDecoration(
                                labelText: 'Reason for Advance',
                                prefixIcon: Icon(Icons.notes),
                                alignLabelWithHint: true,
                              ),
                              validator: (val) => val == null || val.isEmpty ? 'Reason required' : null,
                            ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 40),
                    SizedBox(
                      width: double.infinity,
                      height: 55,
                      child: ElevatedButton.icon(
                        onPressed: _submitRequest,
                        icon: const Icon(Icons.send_rounded),
                        label: const Text('Submit Request', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                        style: ElevatedButton.styleFrom(
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                        ),
                      ),
                    ),
                    const SizedBox(height: 30),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _sectionHeader(String title, IconData icon) {
    return Padding(
      padding: const EdgeInsets.only(left: 4.0, bottom: 8.0),
      child: Row(
        children: [
          Icon(icon, size: 18, color: AppTheme.primary),
          const SizedBox(width: 8),
          Text(
            title.toUpperCase(),
            style: const TextStyle(
              fontSize: 13,
              fontWeight: FontWeight.bold,
              color: AppTheme.primary,
              letterSpacing: 1.1,
            ),
          ),
        ],
      ),
    );
  }
}
