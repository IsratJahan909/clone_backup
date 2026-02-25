class Bonus {
  final int? id;
  final int employeeId;
  final String bonusType;
  final double bonusAmount;
  final double? percentageOfSalary;
  final int? forMonth;
  final int? forYear;
  final String? description;
  final String? criteria;
  final String? status;
  final int? approvedBy;
  final String? approvalDate;
  final String? rejectionReason;
  final bool isPaid;
  final String? paidDate;
  final String? paymentMethod;
  final String? paymentReference;
  final String? createdAt;

  Bonus({
    this.id,
    required this.employeeId,
    required this.bonusType,
    required this.bonusAmount,
    this.percentageOfSalary,
    this.forMonth,
    this.forYear,
    this.description,
    this.criteria,
    this.status,
    this.approvedBy,
    this.approvalDate,
    this.rejectionReason,
    this.isPaid = false,
    this.paidDate,
    this.paymentMethod,
    this.paymentReference,
    this.createdAt,
  });

  Bonus copyWith({
    int? id,
    int? employeeId,
    String? bonusType,
    double? bonusAmount,
    double? percentageOfSalary,
    int? forMonth,
    int? forYear,
    String? description,
    String? criteria,
    String? status,
    int? approvedBy,
    String? approvalDate,
    String? rejectionReason,
    bool? isPaid,
    String? paidDate,
    String? paymentMethod,
    String? paymentReference,
    String? createdAt,
  }) {
    return Bonus(
      id: id ?? this.id,
      employeeId: employeeId ?? this.employeeId,
      bonusType: bonusType ?? this.bonusType,
      bonusAmount: bonusAmount ?? this.bonusAmount,
      percentageOfSalary: percentageOfSalary ?? this.percentageOfSalary,
      forMonth: forMonth ?? this.forMonth,
      forYear: forYear ?? this.forYear,
      description: description ?? this.description,
      criteria: criteria ?? this.criteria,
      status: status ?? this.status,
      approvedBy: approvedBy ?? this.approvedBy,
      approvalDate: approvalDate ?? this.approvalDate,
      rejectionReason: rejectionReason ?? this.rejectionReason,
      isPaid: isPaid ?? this.isPaid,
      paidDate: paidDate ?? this.paidDate,
      paymentMethod: paymentMethod ?? this.paymentMethod,
      paymentReference: paymentReference ?? this.paymentReference,
      createdAt: createdAt ?? this.createdAt,
    );
  }

  factory Bonus.fromJson(Map<String, dynamic> json) {
    return Bonus(
      id: json['id'] as int?,
      employeeId: json['employeeId'] as int? ?? 0,
      bonusType: json['bonusType']?.toString() ?? '',
      bonusAmount: json['bonusAmount'] is String 
          ? double.parse(json['bonusAmount']) 
          : (json['bonusAmount'] as num?)?.toDouble() ?? 0.0,
      percentageOfSalary: (json['percentageOfSalary'] as num?)?.toDouble(),
      forMonth: json['forMonth'] as int?,
      forYear: json['forYear'] as int?,
      description: json['description']?.toString(),
      criteria: json['criteria']?.toString(),
      status: json['status']?.toString(),
      approvedBy: json['approvedBy'] as int?,
      approvalDate: json['approvalDate']?.toString(),
      rejectionReason: json['rejectionReason']?.toString(),
      isPaid: json['isPaid'] as bool? ?? false,
      paidDate: json['paidDate']?.toString(),
      paymentMethod: json['paymentMethod']?.toString(),
      paymentReference: json['paymentReference']?.toString(),
      createdAt: json['createdAt']?.toString(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'employeeId': employeeId,
      'bonusType': bonusType,
      'bonusAmount': bonusAmount,
      'percentageOfSalary': percentageOfSalary,
      'forMonth': forMonth,
      'forYear': forYear,
      'description': description,
      'criteria': criteria,
      'status': status,
      'approvedBy': approvedBy,
      'approvalDate': approvalDate,
      'rejectionReason': rejectionReason,
      'isPaid': isPaid,
      'paidDate': paidDate,
      'paymentMethod': paymentMethod,
      'paymentReference': paymentReference,
    };
  }
}
