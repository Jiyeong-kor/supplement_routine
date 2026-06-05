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
