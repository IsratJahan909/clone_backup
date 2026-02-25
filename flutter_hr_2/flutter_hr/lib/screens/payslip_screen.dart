import 'package:flutter/material.dart';
import '../models/salary.dart';
import '../models/employee.dart';
import '../utils/app_theme.dart';
import 'package:intl/intl.dart';

class PayslipScreen extends StatelessWidget {
  final Salary salary;
  final Employee? employee;

  const PayslipScreen({super.key, required this.salary, this.employee});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        title: const Text('Digital Payslip'),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.download),
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Downloading Payslip PDF...')),
              );
            },
          ),
          IconButton(
            icon: const Icon(Icons.share),
            onPressed: () {},
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            _buildPayslipHeader(),
            const SizedBox(height: 20),
            _buildEmployeeInfo(),
            const SizedBox(height: 20),
            _buildSalaryTable(),
            const SizedBox(height: 20),
            _buildNetSalarySection(),
            const SizedBox(height: 30),
            const Text(
              'This is a computer-generated payslip and does not require a signature.',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 10, color: Colors.grey, fontStyle: FontStyle.italic),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPayslipHeader() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.primary,
        borderRadius: BorderRadius.circular(15),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded( // Added Expanded to handle long month/year text
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'PAYSLIP',
                  style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold, letterSpacing: 2),
                ),
                Text(
                  '${_getMonthName(salary.month)} ${salary.year}',
                  style: const TextStyle(color: Colors.white70, fontSize: 16),
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
          const Icon(Icons.account_balance, color: Colors.white, size: 40),
        ],
      ),
    );
  }

  Widget _buildEmployeeInfo() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(15),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10)],
      ),
      child: Column(
        children: [
          _infoRow('Employee Name', employee?.fullName ?? 'N/A'),
          const Divider(),
          _infoRow('Employee Code', employee?.employeeCode ?? 'N/A'),
          const Divider(),
          _infoRow('Designation', employee?.designation ?? 'N/A'),
          const Divider(),
          _infoRow('Bank Name', employee?.bankName ?? 'N/A'),
          const Divider(),
          _infoRow('Account Number', employee?.bankAccountNumber ?? 'N/A'),
        ],
      ),
    );
  }

  Widget _buildSalaryTable() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(15),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10)],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Earnings', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.green)),
          const SizedBox(height: 10),
          _itemRow('Basic Salary', salary.baseSalary),
          _itemRow('Bonus Amount', salary.bonusAmount),
          _itemRow('Allowances', salary.allowances),
          const Divider(height: 30),
          const Text('Deductions', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.red)),
          const SizedBox(height: 10),
          _itemRow('Advance Salary', salary.advanceSalary, isNegative: true),
          _itemRow('Tax (TDS)', salary.tax, isNegative: true),
          _itemRow('Other Deductions', salary.deductions, isNegative: true),
        ],
      ),
    );
  }

  Widget _buildNetSalarySection() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.green.withOpacity(0.1),
        borderRadius: BorderRadius.circular(15),
        border: Border.all(color: Colors.green.withOpacity(0.3)),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          const Expanded( // Added Expanded
            child: Text(
              'NET PAYABLE',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.green),
            ),
          ),
          Text(
            '৳${salary.netSalary.toStringAsFixed(2)}',
            style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: Colors.green),
          ),
        ],
      ),
    );
  }

  Widget _infoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start, // Align to top for multiline
        children: [
          SizedBox(
            width: 110, 
            child: Text(label, style: const TextStyle(color: Colors.grey, fontSize: 13))
          ),
          const SizedBox(width: 8),
          Expanded( // Key fix: Expanded for the value text
            child: Text(
              value, 
              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
              textAlign: TextAlign.right,
            )
          ),
        ],
      ),
    );
  }

  Widget _itemRow(String label, double amount, {bool isNegative = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded( // Added Expanded to handle long labels
            child: Text(label, style: const TextStyle(fontSize: 14))
          ),
          const SizedBox(width: 8),
          Text(
            '${isNegative ? "-" : ""} ৳${amount.toStringAsFixed(2)}',
            style: TextStyle(fontSize: 14, fontWeight: FontWeight.w500, color: isNegative ? Colors.red : Colors.black87),
          ),
        ],
      ),
    );
  }

  String _getMonthName(int month) {
    try {
      return DateFormat('MMMM').format(DateTime(2024, month));
    } catch (e) {
      return 'Month $month';
    }
  }
}
