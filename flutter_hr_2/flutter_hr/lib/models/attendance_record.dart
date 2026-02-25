class AttendanceRecord {
  final int? id;
  final int employeeId;
  final String date;
  final String? clockInTime;
  final String? clockOutTime;
  final double? workHours;
  final String status;
  final String? remarks;

  AttendanceRecord({
    this.id,
    required this.employeeId,
    required this.date,
    this.clockInTime,
    this.clockOutTime,
    this.workHours,
    required this.status,
    this.remarks,
  });

  factory AttendanceRecord.fromJson(Map<String, dynamic> json) {
    return AttendanceRecord(
      id: json['id'],
      employeeId: json['employeeId'],
      date: json['date'],
      clockInTime: json['clockInTime'],
      clockOutTime: json['clockOutTime'],
      workHours: json['workHours']?.toDouble(),
      status: json['status'],
      remarks: json['remarks'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'employeeId': employeeId,
      'date': date,
      'clockInTime': clockInTime,
      'clockOutTime': clockOutTime,
      'workHours': workHours,
      'status': status,
      'remarks': remarks,
    };
  }
}
