import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'app/app_bootstrap.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  runApp(const AppBootstrap());

  WidgetsBinding.instance.addPostFrameCallback((_) {
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        systemNavigationBarColor: Colors.transparent,
        statusBarColor: Colors.transparent,
        systemNavigationBarDividerColor: Colors.transparent,
      ),
    );
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);
  });
}
