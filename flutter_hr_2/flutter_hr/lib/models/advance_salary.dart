class AdvanceSalary {
  final int? id;
  final int employeeId;
  final double amount;
  final String requestReason;
  final String? forMonth;
  final int? forYear;
  final int repaymentMonths;
  final double? monthlyDeduction;
  final String status; // Pending, Approved, Rejected, Paid, Partially Paid
  final String? requestDate;
  final String? approvalDate;
  final String? rejectionReason;
  final double? amountPaid;

  AdvanceSalary({
    this.id,
    required this.employeeId,
    required this.amount,
    required this.requestReason,
    this.forMonth,
    this.forYear,
    this.repaymentMonths = 1,
    this.monthlyDeduction,
    required this.status,
    this.requestDate,
    this.approvalDate,
    this.rejectionReason,
    this.amountPaid,
  });

  factory AdvanceSalary.fromJson(Map<String, dynamic> json) {
    return AdvanceSalary(
      id: (json['id'] as num?)?.toInt(),
      employeeId: (json['employeeId'] as num?)?.toInt() ?? 0,
      amount: (json['amount'] as num?)?.toDouble() ?? 0.0,
      requestReason: json['requestReason']?.toString() ?? json['reason']?.toString() ?? '',
      forMonth: json['forMonth']?.toString(),
      forYear: (json['forYear'] as num?)?.toInt(),
      repaymentMonths: (json['repaymentMonths'] as num?)?.toInt() ?? 1,
      monthlyDeduction: (json['monthlyDeduction'] as num?)?.toDouble(),
      status: json['status']?.toString() ?? 'Pending',
      requestDate: json['requestDate']?.toString(),
      approvalDate: json['approvalDate']?.toString(),
      rejectionReason: json['rejectionReason']?.toString(),
      amountPaid: (json['amountPaid'] as num?)?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'employeeId': employeeId,
      'amount': amount,
      'requestReason': requestReason,
      'forMonth': forMonth,
      'forYear': forYear,
      'repaymentMonths': repaymentMonths,
      if (monthlyDeduction != null) 'monthlyDeduction': monthlyDeduction,
      'status': status,
      if (requestDate != null) 'requestDate': requestDate,
      if (approvalDate != null) 'approvalDate': approvalDate,
      if (rejectionReason != null) 'rejectionReason': rejectionReason,
      if (amountPaid != null) 'amountPaid': amountPaid,
    };
  }
}
