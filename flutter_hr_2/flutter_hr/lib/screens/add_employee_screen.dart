import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/hr_provider.dart';
import '../models/employee.dart';
import '../models/department.dart';

class AddEmployeeScreen extends StatefulWidget {
  final Employee? employee;
  const AddEmployeeScreen({super.key, this.employee});

  @override
  State<AddEmployeeScreen> createState() => _AddEmployeeScreenState();
}

class _AddEmployeeScreenState extends State<AddEmployeeScreen> {
  final _formKey = GlobalKey<FormState>();

  late final TextEditingController _fNameController;
  late final TextEditingController _lNameController;
  late final TextEditingController _emailController;
  late final TextEditingController _codeController;
  late final TextEditingController _salaryController;
  late final TextEditingController _bankAccController;
  late final TextEditingController _bankNameController;
  late final TextEditingController _imageUrlController;

  int? _selectedDepartmentId;
  String? _selectedDesignation;
  EmploymentType _selectedType = EmploymentType.FULL_TIME;
  DateTime _joiningDate = DateTime.now();

  // Demo Designations List
  final List<String> _designations = [
    'Software Engineer',
    'Senior Software Engineer',
    'Lead Developer',
    'HR Manager',
    'Finance Manager',
    'Operations Manager',
    'Product Manager',
    'Sales Executive',
    'Project Manager',
    'Business Analyst',
    'QA Engineer',
    'DevOps Engineer',
    'Manager',
    'Executive',
    'Intern',
  ];

  @override
  void initState() {
    super.initState();
    _fNameController = TextEditingController(
      text: widget.employee?.firstName ?? '',
    );
    _lNameController = TextEditingController(
      text: widget.employee?.lastName ?? '',
    );
    _emailController = TextEditingController(
      text: widget.employee?.email ?? '',
    );
    _codeController = TextEditingController(
      text: widget.employee?.employeeCode ?? '',
    );
    _salaryController = TextEditingController(
      text: widget.employee?.baseSalary?.toString() ?? '',
    );
    _bankAccController = TextEditingController(
      text: widget.employee?.bankAccountNumber ?? '',
    );
    _bankNameController = TextEditingController(
      text: widget.employee?.bankName ?? '',
    );
    _imageUrlController = TextEditingController(
      text: widget.employee?.profileImageUrl ?? '',
    );

    _selectedDepartmentId = widget.employee?.departmentId;
    _selectedDesignation = widget.employee?.designation;
    // If editing and the existing designation is not in the demo list, insert it
    if (_selectedDesignation != null &&
        !_designations.contains(_selectedDesignation)) {
      _designations.insert(0, _selectedDesignation!);
    }
    if (widget.employee != null) {
      _selectedType = widget.employee!.employmentType;
      if (widget.employee!.dateOfJoining != null) {
        _joiningDate = DateTime.parse(widget.employee!.dateOfJoining!);
      }
    }

    Future.microtask(() => context.read<HrProvider>().fetchDepartments());
  }

  @override
  Widget build(BuildContext context) {
    final hrProvider = context.watch<HrProvider>();
    final departments = hrProvider.departments;

    return Scaffold(
      appBar: AppBar(
        title: Text(
          widget.employee == null ? 'Add New Employee' : 'Edit Employee',
        ),
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Profile Image Section
              Center(
                child: Stack(
                  children: [
                    CircleAvatar(
                      radius: 50,
                      backgroundColor: Colors.blue.withOpacity(0.1),
                      backgroundImage: _imageUrlController.text.isNotEmpty
                          ? NetworkImage(_imageUrlController.text)
                          : const NetworkImage(
                              'https://cdn-icons-png.flaticon.com/512/3135/3135715.png',
                            ),
                    ),
                    Positioned(
                      bottom: 0,
                      right: 0,
                      child: Container(
                        padding: const EdgeInsets.all(4),
                        decoration: const BoxDecoration(
                          color: Colors.blue,
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(
                          Icons.add_a_photo,
                          color: Colors.white,
                          size: 20,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 20),
              TextFormField(
                controller: _imageUrlController,
                onChanged: (v) => setState(() {}),
                decoration: const InputDecoration(
                  labelText: 'Profile Image URL',
                  hintText: 'https://example.com/image.jpg',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.link),
                  helperText: 'Leave empty for default profile picture',
                ),
              ),
              const SizedBox(height: 30),
              const Text(
                'Personal Information',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  color: Colors.blue,
                ),
              ),
              const Divider(),
              const SizedBox(height: 10),
              Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: _fNameController,
                      decoration: const InputDecoration(
                        labelText: 'First Name',
                        border: OutlineInputBorder(),
                      ),
                      validator: (v) => v!.isEmpty ? 'Required' : null,
                    ),
                  ),
                  const SizedBox(width: 10),
                  Expanded(
                    child: TextFormField(
                      controller: _lNameController,
                      decoration: const InputDecoration(
                        labelText: 'Last Name',
                        border: OutlineInputBorder(),
                      ),
                      validator: (v) => v!.isEmpty ? 'Required' : null,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _emailController,
                decoration: const InputDecoration(
                  labelText: 'Email Address',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.email),
                ),
                keyboardType: TextInputType.emailAddress,
                validator: (v) => v!.isEmpty ? 'Required' : null,
              ),
              const SizedBox(height: 24),
              const Text(
                'Employment Details',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  color: Colors.blue,
                ),
              ),
              const Divider(),
              const SizedBox(height: 10),
              TextFormField(
                controller: _codeController,
                decoration: const InputDecoration(
                  labelText: 'Employee Code',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.badge),
                ),
                validator: (v) => v!.isEmpty ? 'Required' : null,
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<int>(
                value: _selectedDepartmentId,
                decoration: const InputDecoration(
                  labelText: 'Department',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.business),
                ),
                items: departments
                    .map(
                      (d) => DropdownMenuItem(
                        value: d.departmentId,
                        child: Text(d.departmentName),
                      ),
                    )
                    .toList(),
                onChanged: (v) => setState(() => _selectedDepartmentId = v),
                validator: (v) =>
                    v == null ? 'Please select a department' : null,
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _selectedDesignation,
                decoration: const InputDecoration(
                  labelText: 'Designation',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.work),
                ),
                items: _designations
                    .map((d) => DropdownMenuItem(value: d, child: Text(d)))
                    .toList(),
                onChanged: (v) => setState(() => _selectedDesignation = v),
                validator: (v) => v == null || v.isEmpty
                    ? 'Please select a designation'
                    : null,
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<EmploymentType>(
                value: _selectedType,
                decoration: const InputDecoration(
                  labelText: 'Employment Type',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.assignment_ind),
                ),
                items: EmploymentType.values
                    .map(
                      (t) => DropdownMenuItem(
                        value: t,
                        child: Text(t.toString().split('.').last),
                      ),
                    )
                    .toList(),
                onChanged: (v) => setState(() => _selectedType = v!),
              ),
              const SizedBox(height: 16),
              ListTile(
                title: const Text('Date of Joining'),
                subtitle: Text(
                  '${_joiningDate.day}/${_joiningDate.month}/${_joiningDate.year}',
                ),
                leading: const Icon(Icons.calendar_today),
                trailing: const Icon(Icons.edit),
                onTap: () async {
                  final picked = await showDatePicker(
                    context: context,
                    initialDate: _joiningDate,
                    firstDate: DateTime(2000),
                    lastDate: DateTime(2100),
                  );
                  if (picked != null) setState(() => _joiningDate = picked);
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _salaryController,
                decoration: const InputDecoration(
                  labelText: 'Base Salary (TK)',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.money),
                ),
                keyboardType: TextInputType.number,
                validator: (v) => v!.isEmpty ? 'Required' : null,
              ),
              const SizedBox(height: 24),
              const Text(
                'Bank Information',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  color: Colors.blue,
                ),
              ),
              const Divider(),
              const SizedBox(height: 10),
              TextFormField(
                controller: _bankNameController,
                decoration: const InputDecoration(
                  labelText: 'Bank Name',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.account_balance),
                ),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _bankAccController,
                decoration: const InputDecoration(
                  labelText: 'Bank Account Number',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.numbers),
                ),
              ),
              const SizedBox(height: 30),
              ElevatedButton(
                onPressed: _submitForm,
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                child: Text(
                  widget.employee == null ? 'Save Employee' : 'Update Employee',
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              const SizedBox(height: 40),
            ],
          ),
        ),
      ),
    );
  }

  void _submitForm() {
    if (_formKey.currentState!.validate()) {
      final emp = Employee(
        employeeId: widget.employee?.employeeId,
        firstName: _fNameController.text,
        lastName: _lNameController.text,
        email: _emailController.text,
        employeeCode: _codeController.text,
        departmentId: _selectedDepartmentId ?? 1,
        designation: _selectedDesignation ?? 'Manager',
        employmentType: _selectedType,
        dateOfJoining: _joiningDate.toIso8601String().split('T').first,
        baseSalary: double.tryParse(_salaryController.text) ?? 0.0,
        bankAccountNumber: _bankAccController.text.isEmpty
            ? 'N/A'
            : _bankAccController.text,
        bankName: _bankNameController.text.isEmpty
            ? 'N/A'
            : _bankNameController.text,
        isActive: true,
        profileImageUrl: _imageUrlController.text.trim(),
      );

      if (widget.employee == null) {
        context.read<HrProvider>().addEmployee(emp);
      } else {
        context.read<HrProvider>().updateEmployee(
          widget.employee!.employeeId!,
          emp,
        );
      }
      Navigator.pop(context);
    }
  }
}
