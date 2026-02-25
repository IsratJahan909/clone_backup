class LeaveRequest {
  final int? id;
  final int employeeId;
  final String leaveType;
  final String startDate;
  final String endDate;
  final int totalDays;
  final String reason;
  final String status;
  final String? approvalNotes;
  final String? rejectionReason;

  LeaveRequest({
    this.id,
    required this.employeeId,
    required this.leaveType,
    required this.startDate,
    required this.endDate,
    required this.totalDays,
    required this.reason,
    required this.status,
    this.approvalNotes,
    this.rejectionReason,
  });

  factory LeaveRequest.fromJson(Map<String, dynamic> json) {
    return LeaveRequest(
      id: json['id'],
      employeeId: json['employeeId'],
      leaveType: json['leaveType'],
      startDate: json['startDate'],
      endDate: json['endDate'],
      totalDays: json['totalDays'],
      reason: json['reason'],
      status: json['status'],
      approvalNotes: json['approvalNotes'],
      rejectionReason: json['rejectionReason'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'employeeId': employeeId,
      'leaveType': leaveType,
      'startDate': startDate,
      'endDate': endDate,
      'totalDays': totalDays,
      'reason': reason,
      'status': status,
      'approvalNotes': approvalNotes,
      'rejectionReason': rejectionReason,
    };
  }
}
