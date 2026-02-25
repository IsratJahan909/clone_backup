class Department {
  final int? departmentId;
  final String departmentName;
  final String? description;
  final String departmentHead;
  final bool isActive;

  Department({
    this.departmentId,
    required this.departmentName,
    this.description,
    required this.departmentHead,
    this.isActive = true,
  });

  factory Department.fromJson(Map<String, dynamic> json) {
    return Department(
      departmentId: json['departmentId'],
      departmentName: json['departmentName'],
      description: json['description'],
      departmentHead: json['departmentHead'],
      isActive: json['isActive'] ?? true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'departmentId': departmentId,
      'departmentName': departmentName,
      'description': description,
      'departmentHead': departmentHead,
      'isActive': isActive,
    };
  }
}
