# Android Release Signing

Supplement Routine uses `android/key.properties` for release signing when the file exists. If the file is missing, the release build falls back to the debug signing config so development release builds can still be verified.

## Setup

Create `android/key.properties` locally by following `android/key.properties.example`.

```properties
storePassword=your-store-password
keyPassword=your-key-password
keyAlias=your-key-alias
storeFile=C:/path/to/upload-keystore.jks
```

## Security

- Do not commit `android/key.properties`.
- Do not commit `.jks`, `.keystore`, or `.env` files.
- Store Play Store upload keys and passwords in a separate secret storage.

## Verify Release Build

```bash
flutter build apk --release --dart-define=APP_FLAVOR=prod --dart-define=MOCK_DATA=false
```

Use the following command for a Play Store App Bundle.

```bash
flutter build appbundle --release --dart-define=APP_FLAVOR=prod --dart-define=MOCK_DATA=false
```
