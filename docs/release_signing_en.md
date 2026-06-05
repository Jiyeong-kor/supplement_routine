# Android Release Signing

Supplement Routine currently keeps the Flutter reference app and KMP Android app side by side.

- The Flutter Android app uses `android/key.properties` for release signing when the file exists.
- The KMP Android app uses `kmp/key.properties` for release signing when the file exists.
- If the matching file is missing, the release build falls back to the debug signing config so development release builds can still be verified.

## Setup

For the Flutter Android app, create `android/key.properties` locally by following `android/key.properties.example`.

For the KMP Android app, create `kmp/key.properties` locally by following `kmp/key.properties.example`.

```properties
storePassword=your-store-password
keyPassword=your-key-password
keyAlias=your-key-alias
storeFile=<local-upload-keystore-path>
```

## Security

- Do not commit `android/key.properties`.
- Do not commit `kmp/key.properties`.
- Do not commit `.jks`, `.keystore`, or `.env` files.
- Store Play Store upload keys and passwords in a separate secret storage.

## Verify Flutter Android Release Build

```bash
flutter build apk --release --dart-define=APP_FLAVOR=prod --dart-define=MOCK_DATA=false
```

Use the following command for a Play Store App Bundle.

```bash
flutter build appbundle --release --dart-define=APP_FLAVOR=prod --dart-define=MOCK_DATA=false
```

## Verify KMP Android Release Build

On Windows, run the following from the repository root after setting the Android SDK path.

```powershell
$env:ANDROID_HOME="$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
android\gradlew.bat -p kmp :androidApp:assembleRelease :androidApp:bundleRelease --no-daemon
```

On CI or macOS/Linux machines with Gradle installed, use:

```bash
gradle -p kmp :androidApp:assembleRelease :androidApp:bundleRelease --no-daemon
```

Before Play Store upload, verify `assembleRelease` and `bundleRelease` again with a real upload keystore configured in `kmp/key.properties`.

## KMP Manual Release Workflow

`.github/workflows/kmp_release.yml` creates KMP Android signed APK/AAB artifacts and iOS signed archive/IPA artifacts from manual dispatch.

GitHub Actions `workflow_dispatch` can be manually triggered only after the workflow file exists on the default branch. `KMP Release` now exists on `main`, so it can be manually dispatched after the signing secrets are configured.

As of 2026-06-05, `gh secret list --repo Jiyeong-kor/supplement_routine` returns no configured repository secrets for release signing. Add all of the following secrets before generating signed store artifacts.

The Android release job requires these secrets:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_PASSWORD`
- `ANDROID_KEY_ALIAS`

The iOS release job requires these secrets:

- `IOS_TEAM_ID`
- `IOS_BUNDLE_IDENTIFIER`
- `IOS_PROVISIONING_PROFILE_BASE64`
- `IOS_CERTIFICATE_BASE64`
- `IOS_CERTIFICATE_PASSWORD`

If the secrets are missing, the workflow fails instead of creating release artifacts. Track the remaining signed artifact verification in [#67](https://github.com/Jiyeong-kor/supplement_routine/issues/67).
