import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:intl/intl.dart';
import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';
import 'package:sqflite/sqflite.dart';
import 'api_service.dart';

// Simple in-memory store used when running on web, because
// sqflite + path_provider are not supported there.
List<Map<String, dynamic>> _webStore = []; // shared across instances

class AttendanceLocalDb {
  /// Singleton instance
  AttendanceLocalDb._privateConstructor();
  static final AttendanceLocalDb instance =
      AttendanceLocalDb._privateConstructor();

  static Database? _database;

  Future<Database> get database async {
    if (_database != null) return _database!;
    if (kIsWeb) {
      // database not used on web, callers should avoid invoking it
      throw Exception('SQLite not available on web');
    }
    _database = await _initDatabase();
    return _database!;
  }

  Future<Database> _initDatabase() async {
    Directory documentsDirectory = await getApplicationDocumentsDirectory();
    final path = join(documentsDirectory.path, 'attendance.db');
    return await openDatabase(
      path,
      version: 2,
      onCreate: _onCreate,
      onUpgrade: _onUpgrade,
    );
  }

  Future<void> _onCreate(Database db, int version) async {
    // nothing to do here; version column creation already handles
    await db.execute('''
      CREATE TABLE attendance (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        employeeId INTEGER NOT NULL,
        employeeName TEXT,
        date TEXT NOT NULL,
        clockInTime TEXT,
        clockOutTime TEXT,
        workHours REAL,
        status TEXT NOT NULL,
        remarks TEXT
      )
    ''');

    // seed with a couple of rows so the screen isn't empty
    final today = DateFormat('yyyy-MM-dd').format(DateTime.now());
    await db.insert('attendance', {
      'employeeId': 1,
      'employeeName': 'Employee 1',
      'date': today,
      'clockInTime': null,
      'clockOutTime': null,
      'workHours': null,
      'status': 'ABSENT',
      'remarks': 'Seed record',
    });
  }

  Future<void> _onUpgrade(Database db, int oldVersion, int newVersion) async {
    if (oldVersion < 2) {
      try {
        await db.execute('ALTER TABLE attendance ADD COLUMN employeeName TEXT');
      } catch (_) {}
    }
  }

  Future<List<Map<String, dynamic>>> getAllRecords() async {
    if (kIsWeb) {
      return List<Map<String, dynamic>>.from(_webStore);
    }
    final db = await database;
    return await db.query('attendance', orderBy: 'date DESC');
  }

  Future<List<Map<String, dynamic>>> getRecordsForEmployee(
    int employeeId,
  ) async {
    if (kIsWeb) {
      return _webStore.where((e) => e['employeeId'] == employeeId).toList();
    }
    final db = await database;
    return await db.query(
      'attendance',
      where: 'employeeId = ?',
      whereArgs: [employeeId],
      orderBy: 'date DESC',
    );
  }

  Future<int> checkIn(int employeeId) async {
    final date = DateFormat('yyyy-MM-dd').format(DateTime.now());
    final name = ApiService.userName ?? '';
    if (kIsWeb) {
      // check if already exists
      final existing = _webStore.firstWhere(
        (e) => e['employeeId'] == employeeId && e['date'] == date,
        orElse: () => {},
      );
      if (existing.isNotEmpty) return existing['id'] as int;
      final id = _webStore.length + 1;
      final newRec = {
        'id': id,
        'employeeId': employeeId,
        'employeeName': name,
        'date': date,
        'clockInTime': DateFormat('HH:mm').format(DateTime.now()),
        'status': 'PRESENT',
      };
      _webStore.insert(0, newRec);
      return id;
    }
    final db = await database;
    // ensure no entry already exists for today
    final existing = await db.query(
      'attendance',
      where: 'employeeId = ? AND date = ?',
      whereArgs: [employeeId, date],
    );
    if (existing.isNotEmpty) {
      return existing.first['id'] as int;
    }
    return await db.insert('attendance', {
      'employeeId': employeeId,
      'employeeName': name,
      'date': date,
      'clockInTime': DateFormat('HH:mm').format(DateTime.now()),
      'status': 'PRESENT',
    });
  }

  Future<int> checkOut(int recordId) async {
    final time = DateFormat('HH:mm').format(DateTime.now());
    if (kIsWeb) {
      for (var rec in _webStore) {
        if (rec['id'] == recordId) {
          rec['clockOutTime'] = time;
          return 1;
        }
      }
      return 0;
    }
    final db = await database;
    return await db.update(
      'attendance',
      {
        'clockOutTime': time,
        // optionally compute workHours but keep simple
      },
      where: 'id = ?',
      whereArgs: [recordId],
    );
  }
}
