class Salary {
  final int? id;
  final int employeeId;
  final int month;
  final int year;
  final double baseSalary;
  final double advanceSalary;
  final double bonusAmount;
  final double allowances;
  final double deductions;
  final double tax;
  final double netSalary;
  final String status;
  final String? paymentMethod;
  final String? paymentDate;

  Salary({
    this.id,
    required this.employeeId,
    required this.month,
    required this.year,
    required this.baseSalary,
    this.advanceSalary = 0,
    this.bonusAmount = 0,
    this.allowances = 0,
    this.deductions = 0,
    this.tax = 0,
    required this.netSalary,
    required this.status,
    this.paymentMethod,
    this.paymentDate,
  });

  factory Salary.fromJson(Map<String, dynamic> json) {
    return Salary(
      id: json['id'] as int?,
      employeeId: json['employeeId'] as int? ?? 0,
      month: json['month'] as int? ?? 0,
      year: json['year'] as int? ?? 0,
      baseSalary: (json['baseSalary'] as num?)?.toDouble() ?? 0.0,
      advanceSalary: (json['advanceSalary'] as num?)?.toDouble() ?? 0.0,
      bonusAmount: (json['bonusAmount'] as num?)?.toDouble() ?? 0.0,
      allowances: (json['allowances'] as num?)?.toDouble() ?? 0.0,
      deductions: (json['deductions'] as num?)?.toDouble() ?? 0.0,
      tax: (json['tax'] as num?)?.toDouble() ?? 0.0,
      netSalary: (json['netSalary'] as num?)?.toDouble() ?? 0.0,
      status: json['status']?.toString() ?? 'Draft',
      paymentMethod: json['paymentMethod']?.toString(),
      paymentDate: json['paymentDate']?.toString(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'employeeId': employeeId,
      'month': month,
      'year': year,
      'baseSalary': baseSalary,
      'advanceSalary': advanceSalary,
      'bonusAmount': bonusAmount,
      'allowances': allowances,
      'deductions': deductions,
      'tax': tax,
      'netSalary': netSalary,
      'status': status,
      'paymentMethod': paymentMethod,
      'paymentDate': paymentDate,
    };
  }
}
