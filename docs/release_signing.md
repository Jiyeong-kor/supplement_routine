# Android 릴리즈 서명

Supplement Routine은 Flutter 기준 앱과 KMP Android 앱을 병렬로 유지하고 있습니다.

- Flutter Android 앱은 `android/key.properties` 파일이 있으면 릴리즈 빌드에 해당 서명 설정을 사용합니다.
- KMP Android 앱은 `kmp/key.properties` 파일이 있으면 릴리즈 빌드에 해당 서명 설정을 사용합니다.
- 각 파일이 없으면 개발 중 릴리즈 빌드 확인을 위해 debug signing config로 빌드됩니다.

## 준비

Flutter Android 앱은 `android/key.properties.example`을 참고해 `android/key.properties`를 로컬에 생성합니다.

KMP Android 앱은 `kmp/key.properties.example`을 참고해 `kmp/key.properties`를 로컬에 생성합니다.

```properties
storePassword=실제_스토어_비밀번호
keyPassword=실제_키_비밀번호
keyAlias=실제_키_별칭
storeFile=<local-upload-keystore-path>
```

## 보안

- `android/key.properties`는 Git에 커밋하지 않습니다.
- `kmp/key.properties`는 Git에 커밋하지 않습니다.
- `.jks`, `.keystore`, `.env` 파일은 Git에 포함하지 않습니다.
- Play Store 업로드 키와 비밀번호는 별도 비밀 저장소에 보관합니다.

## Flutter Android 릴리즈 빌드 확인

```bash
flutter build apk --release --dart-define=APP_FLAVOR=prod --dart-define=MOCK_DATA=false
```

Play Store 배포용 App Bundle은 다음 명령으로 생성합니다.

```bash
flutter build appbundle --release --dart-define=APP_FLAVOR=prod --dart-define=MOCK_DATA=false
```

## KMP Android 릴리즈 빌드 확인

Windows 개발 환경에서는 저장소 루트에서 Android SDK 경로를 지정한 뒤 다음 명령을 실행합니다.

```powershell
$env:ANDROID_HOME="$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
android\gradlew.bat -p kmp :androidApp:assembleRelease :androidApp:bundleRelease --no-daemon
```

CI 또는 macOS/Linux 개발 환경에서는 Gradle이 설치되어 있을 때 다음 명령을 사용할 수 있습니다.

```bash
gradle -p kmp :androidApp:assembleRelease :androidApp:bundleRelease --no-daemon
```

Play Store 업로드 전에는 실제 upload keystore가 설정된 `kmp/key.properties`로 `assembleRelease`와 `bundleRelease`를 다시 확인합니다.

## KMP 수동 릴리즈 workflow

`.github/workflows/kmp_release.yml`은 수동 실행으로 KMP Android signed APK/AAB와 iOS signed archive/IPA를 생성합니다.

GitHub Actions `workflow_dispatch`는 workflow 파일이 default branch에 있어야 수동 실행할 수 있습니다. `KMP Release` workflow는 현재 `main`에 있으므로 signing secrets를 등록한 뒤 수동 실행할 수 있습니다.

2026-06-05 기준 `gh secret list --repo Jiyeong-kor/supplement_routine` 결과, 아래 release signing secrets는 아직 저장소에 등록되어 있지 않습니다. 실제 signed artifact 생성 전 모두 등록해야 합니다.

Android release job은 다음 Secrets를 요구합니다.

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_PASSWORD`
- `ANDROID_KEY_ALIAS`

iOS release job은 다음 Secrets를 요구합니다.

- `IOS_TEAM_ID`
- `IOS_BUNDLE_IDENTIFIER`
- `IOS_PROVISIONING_PROFILE_BASE64`
- `IOS_CERTIFICATE_BASE64`
- `IOS_CERTIFICATE_PASSWORD`

Secrets가 없으면 workflow는 릴리즈 artifact를 만들지 않고 실패합니다. 남은 signed artifact 검증은 [#67](https://github.com/Jiyeong-kor/supplement_routine/issues/67)에서 추적합니다.

## Secret 등록 runbook

아래 명령은 secret 값을 터미널 history에 남기지 않는 방식을 우선합니다. 실제 파일 경로와 값은 로컬 보안 저장소 기준으로 바꿉니다.

### Android upload keystore

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

### iOS distribution signing

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

### 등록 확인

```bash
gh secret list --repo Jiyeong-kor/supplement_routine
```

목록에는 secret 이름만 보여야 하며 값은 출력되지 않아야 합니다.

### 릴리즈 workflow 실행

```bash
gh workflow run kmp_release.yml --repo Jiyeong-kor/supplement_routine --ref main -f platform=all
gh run list --repo Jiyeong-kor/supplement_routine --workflow "KMP Release" --limit 5
```

성공한 run id를 확인한 뒤 artifact를 내려받습니다.

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

artifact 확인 결과는 #67에 comment로 남깁니다.
