# KMP Scaffold

이 폴더는 Flutter 기준 구현을 유지한 채 KMP 전환을 시작하기 위한 병렬 프로젝트입니다.

## 구조

- `shared`: Android와 iOS가 함께 사용할 domain, state, pure logic을 둘 KMP module입니다.
- `androidApp`: Jetpack Compose 기반 Android native app shell입니다.
- `iosApp`: SwiftUI 기반 iOS shell입니다. KMP shared framework를 import해서 최소 tab 구조를 표시합니다.

## 현재 범위

이 scaffold는 기능 이식이 아니라 빌드 가능한 시작점입니다.

- Flutter 앱은 그대로 유지합니다.
- shared module에는 앱 이름과 최상위 destination 정보를 제공하는 최소 모델만 둡니다.
- Android app은 shared module을 읽어 Compose 화면에 표시합니다.
- iOS는 SwiftUI shell에서 shared framework를 import/call 하는 smoke path를 제공합니다.

## 검증

Gradle이 설치되어 있으면 저장소 루트에서 다음 명령을 실행합니다.

```bash
gradle -p kmp clean :shared:check :androidApp:assembleDebug --no-daemon
```

현재 Flutter Android wrapper가 로컬에 있는 개발 환경에서는 다음 명령도 사용할 수 있습니다.

```powershell
android\gradlew.bat -p kmp clean :shared:check :androidApp:assembleDebug --no-daemon
```

로컬에서 Android SDK를 찾지 못하면 `ANDROID_HOME` 또는 `ANDROID_SDK_ROOT`를 설정합니다.

```powershell
$env:ANDROID_HOME="$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
```

## 다음 단계

1. `kmp/shared-domain`에서 Flutter model을 Kotlin domain model로 옮깁니다.
2. `kmp/shared-scheduling`에서 일정 계산과 기록 요약 로직을 옮깁니다.
3. `android/compose-today`부터 실제 화면을 이식합니다.
4. iOS는 local persistence adapter와 notification adapter를 후속 PR에서 연결합니다.
