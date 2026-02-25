import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/hr_provider.dart';
import '../models/department.dart';

class AddDepartmentScreen extends StatefulWidget {
  final Department? department;
  const AddDepartmentScreen({super.key, this.department});

  @override
  State<AddDepartmentScreen> createState() => _AddDepartmentScreenState();
}

class _AddDepartmentScreenState extends State<AddDepartmentScreen> {
  final _formKey = GlobalKey<FormState>();
  
  late final TextEditingController _nameController;
  late final TextEditingController _headController;
  late final TextEditingController _descController;

  @override
  void initState() {
    super.initState();
    _nameController = TextEditingController(text: widget.department?.departmentName ?? '');
    _headController = TextEditingController(text: widget.department?.departmentHead ?? '');
    _descController = TextEditingController(text: widget.department?.description ?? '');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.department == null ? 'Add New Department' : 'Edit Department'),
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Text(
                'Department Information',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.blue),
              ),
              const Divider(),
              const SizedBox(height: 20),
              TextFormField(
                controller: _nameController,
                decoration: const InputDecoration(
                  labelText: 'Department Name', 
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.business),
                ),
                validator: (v) => v!.isEmpty ? 'Please enter department name' : null,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _headController,
                decoration: const InputDecoration(
                  labelText: 'Department Head', 
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.person),
                ),
                validator: (v) => v!.isEmpty ? 'Please enter department head name' : null,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _descController,
                decoration: const InputDecoration(
                  labelText: 'Description', 
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.description),
                ),
                maxLines: 3,
              ),
              const SizedBox(height: 30),
              ElevatedButton(
                onPressed: _submitForm,
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
                child: Text(widget.department == null ? 'Save Department' : 'Update Department', style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _submitForm() {
    if (_formKey.currentState!.validate()) {
      final dept = Department(
        departmentId: widget.department?.departmentId,
        departmentName: _nameController.text,
        departmentHead: _headController.text,
        description: _descController.text,
        isActive: true,
      );
      
      if (widget.department == null) {
        context.read<HrProvider>().addDepartment(dept);
      } else {
        context.read<HrProvider>().updateDepartment(widget.department!.departmentId!, dept);
      }
      Navigator.pop(context);
    }
  }
}
