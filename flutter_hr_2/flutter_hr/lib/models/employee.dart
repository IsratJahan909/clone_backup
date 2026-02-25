import 'dart:convert';

enum EmploymentType {
  FULL_TIME,
  PART_TIME,
  CONTRACT,
  INTERN
}

class Employee {
  final int? employeeId;
  final String firstName;
  final String lastName;
  final String email;
  final String? phoneNumber;
  final String employeeCode;
  final int departmentId;
  final String designation;
  final EmploymentType employmentType;
  final String dateOfJoining; // backend uses LocalDate (ISO 8601 string)
  final double baseSalary;
  final double medicalAllowance;
  final String bankAccountNumber;
  final String bankName;
  final bool isActive;
  final String? profileImageUrl;

  int? get id => employeeId;
  String get fullName => '$firstName $lastName';
  // Logic: if image URL is provided, use it, else return a default avatar
  String get profileImage => (profileImageUrl != null && profileImageUrl!.isNotEmpty) 
      ? profileImageUrl! 
      : 'https://cdn-icons-png.flaticon.com/512/3135/3135715.png';

  Employee({
    this.employeeId,
    required this.firstName,
    required this.lastName,
    required this.email,
    this.phoneNumber,
    required this.employeeCode,
    required this.departmentId,
    required this.designation,
    required this.employmentType,
    required this.dateOfJoining,
    required this.baseSalary,
    this.medicalAllowance = 0.0,
    required this.bankAccountNumber,
    required this.bankName,
    this.isActive = true,
    this.profileImageUrl,
  });

  factory Employee.fromJson(Map<String, dynamic> json) {
    return Employee(
      employeeId: json['employeeId'],
      firstName: json['firstName'],
      lastName: json['lastName'],
      email: json['email'],
      phoneNumber: json['phoneNumber'],
      employeeCode: json['employeeCode'],
      departmentId: json['departmentId'],
      designation: json['designation'],
      employmentType: EmploymentType.values.firstWhere(
          (e) => e.toString().split('.').last == json['employmentType']),
      dateOfJoining: json['dateOfJoining'],
      baseSalary: (json['baseSalary'] as num).toDouble(),
      medicalAllowance: (json['medicalAllowance'] as num).toDouble(),
      bankAccountNumber: json['bankAccountNumber'],
      bankName: json['bankName'],
      isActive: json['isActive'] ?? true,
      profileImageUrl: json['profileImageUrl'] ?? json['imageUrl'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (employeeId != null) 'employeeId': employeeId,
      'firstName': firstName,
      'lastName': lastName,
      'email': email,
      'phoneNumber': phoneNumber,
      'employeeCode': employeeCode,
      'departmentId': departmentId,
      'designation': designation,
      'employmentType': employmentType.toString().split('.').last,
      'dateOfJoining': dateOfJoining,
      'baseSalary': baseSalary,
      'medicalAllowance': medicalAllowance,
      'bankAccountNumber': bankAccountNumber,
      'bankName': bankName,
      'isActive': isActive,
      'profileImageUrl': profileImageUrl,
    };
  }
}
