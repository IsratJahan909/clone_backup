import 'dart:io';
import 'package:flutter/foundation.dart';

class ApiConstants {
  // আপনার পিসির বর্তমান IP এখানে লিখুন (Real Device এর জন্য)
  static const String _pcIp = '10.32.112.3';
  static String get baseUrl {
    // Web can use localhost
    if (kIsWeb) {
      return 'http://localhost:8080/api';
    }

    // Android emulator (Android Studio emulator) uses 10.0.2.2 to reach host machine
    if (Platform.isAndroid) {
      return 'http://10.0.2.2:8080/api';
    }

    // iOS (real device) — set your PC IP in _pcIp above when testing on device
    if (Platform.isIOS) {
      return 'http://$_pcIp:8080/api';
    }

    // Desktop platforms (Windows/Linux/Mac) and others default to localhost
    if (Platform.isWindows || Platform.isLinux || Platform.isMacOS) {
      return 'http://localhost:8080/api';
    }

    // Fallback: use PC IP (useful for other/unexpected platforms)
    return 'http://$_pcIp:8080/api';
  }

  static const String departments = '/departments';
  static const String employees = '/employees';
}
