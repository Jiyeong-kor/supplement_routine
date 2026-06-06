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

## Secret Setup Runbook

The commands below prefer input methods that do not leave secret values in terminal history. Replace paths and values with the local secure storage paths for the release assets.

### Android Upload Keystore

Windows PowerShell:

```powershell
$repo="Jiyeong-kor/supplement_routine"
$keystore="<local-upload-keystore-path>"
[Convert]::ToBase64String([IO.File]::ReadAllBytes($keystore)) | gh secret set ANDROID_KEYSTORE_BASE64 --repo $repo
gh secret set ANDROID_KEYSTORE_PASSWORD --repo $repo
gh secret set ANDROID_KEY_PASSWORD --repo $repo
gh secret set ANDROID_KEY_ALIAS --repo $repo
```

macOS/Linux:

```bash
repo="Jiyeong-kor/supplement_routine"
LOCAL_UPLOAD_KEYSTORE_PATH="<local-upload-keystore-path>"
base64 < "$LOCAL_UPLOAD_KEYSTORE_PATH" | tr -d '\n' | gh secret set ANDROID_KEYSTORE_BASE64 --repo "$repo"
gh secret set ANDROID_KEYSTORE_PASSWORD --repo "$repo"
gh secret set ANDROID_KEY_PASSWORD --repo "$repo"
gh secret set ANDROID_KEY_ALIAS --repo "$repo"
```

### iOS Distribution Signing

Windows PowerShell:

```powershell
$repo="Jiyeong-kor/supplement_routine"
$certificate="<local-ios-distribution-certificate-path>"
$profile="<local-ios-provisioning-profile-path>"
[Convert]::ToBase64String([IO.File]::ReadAllBytes($certificate)) | gh secret set IOS_CERTIFICATE_BASE64 --repo $repo
[Convert]::ToBase64String([IO.File]::ReadAllBytes($profile)) | gh secret set IOS_PROVISIONING_PROFILE_BASE64 --repo $repo
gh secret set IOS_CERTIFICATE_PASSWORD --repo $repo
gh secret set IOS_TEAM_ID --repo $repo
gh secret set IOS_BUNDLE_IDENTIFIER --repo $repo
```

macOS/Linux:

```bash
repo="Jiyeong-kor/supplement_routine"
LOCAL_IOS_DISTRIBUTION_CERTIFICATE_PATH="<local-ios-distribution-certificate-path>"
LOCAL_IOS_PROVISIONING_PROFILE_PATH="<local-ios-provisioning-profile-path>"
base64 < "$LOCAL_IOS_DISTRIBUTION_CERTIFICATE_PATH" | tr -d '\n' | gh secret set IOS_CERTIFICATE_BASE64 --repo "$repo"
base64 < "$LOCAL_IOS_PROVISIONING_PROFILE_PATH" | tr -d '\n' | gh secret set IOS_PROVISIONING_PROFILE_BASE64 --repo "$repo"
gh secret set IOS_CERTIFICATE_PASSWORD --repo "$repo"
gh secret set IOS_TEAM_ID --repo "$repo"
gh secret set IOS_BUNDLE_IDENTIFIER --repo "$repo"
```

### Verify Registration

```bash
gh secret list --repo Jiyeong-kor/supplement_routine
```

The list should show secret names only and must not print secret values.

### Run the Release Workflow

```bash
gh workflow run kmp_release.yml --repo Jiyeong-kor/supplement_routine --ref main -f platform=all
gh run list --repo Jiyeong-kor/supplement_routine --workflow "KMP Release" --limit 5
```

After finding the successful run id, download the artifacts.

Windows PowerShell:

```powershell
gh run download RUN_ID --repo Jiyeong-kor/supplement_routine --name kmp-android-release --dir <local-artifact-download-dir>
gh run download RUN_ID --repo Jiyeong-kor/supplement_routine --name kmp-ios-xcarchive-tar --dir <local-artifact-download-dir>
gh run download RUN_ID --repo Jiyeong-kor/supplement_routine --name kmp-ios-ipa --dir <local-artifact-download-dir>
```

macOS/Linux:

```bash
LOCAL_ARTIFACT_DOWNLOAD_DIR="<local-artifact-download-dir>"
mkdir -p "$LOCAL_ARTIFACT_DOWNLOAD_DIR"
gh run download RUN_ID --repo Jiyeong-kor/supplement_routine --name kmp-android-release --dir "$LOCAL_ARTIFACT_DOWNLOAD_DIR"
gh run download RUN_ID --repo Jiyeong-kor/supplement_routine --name kmp-ios-xcarchive-tar --dir "$LOCAL_ARTIFACT_DOWNLOAD_DIR"
gh run download RUN_ID --repo Jiyeong-kor/supplement_routine --name kmp-ios-ipa --dir "$LOCAL_ARTIFACT_DOWNLOAD_DIR"
```

Record the artifact verification result as a comment on #67.
