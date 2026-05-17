import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class WindowsStartupService {
  WindowsStartupService._();

  static const _channel = MethodChannel('supplement_routine/windows_startup');

  static bool get isSupported =>
      !kIsWeb && defaultTargetPlatform == TargetPlatform.windows;

  static Future<bool> isEnabled() async {
    if (!isSupported) {
      return false;
    }

    return await _channel.invokeMethod<bool>('isEnabled') ?? false;
  }

  static Future<bool> setEnabled(bool enabled) async {
    if (!isSupported) {
      return false;
    }

    return await _channel.invokeMethod<bool>('setEnabled', enabled) ?? false;
  }
}
