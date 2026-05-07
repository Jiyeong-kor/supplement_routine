enum AppFlavor { dev, prod }

class AppConfig {
  static const appName = String.fromEnvironment(
    'APP_NAME',
    defaultValue: 'Supplement Routine',
  );

  static const flavorName = String.fromEnvironment(
    'APP_FLAVOR',
    defaultValue: 'dev',
  );

  static const logLevel = String.fromEnvironment(
    'LOG_LEVEL',
    defaultValue: 'debug',
  );

  static const appVersion = String.fromEnvironment(
    'APP_VERSION',
    defaultValue: '1.0.0',
  );

  static AppFlavor get flavor {
    return flavorName == 'prod' ? AppFlavor.prod : AppFlavor.dev;
  }
}
