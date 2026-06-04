# iOS Shell

이 폴더는 KMP shared module을 사용하는 SwiftUI iOS shell입니다.

## 현재 범위

- `SupplementRoutineIos.xcodeproj`는 signing 없이 iOS simulator debug build를 검증하기 위한 최소 Xcode project입니다.
- SwiftUI shell은 Today, Supplements, History, Settings tab 구조를 제공합니다.
- `SharedRoutineViewModel`은 `SupplementRoutineShared` framework의 `SharedAppSummary`를 import/call 해서 shared module integration을 smoke-test합니다.
- Android-only 알림/저장소 기능은 iOS fallback 문구로 표시합니다.

## 로컬 macOS 검증

Windows에서는 Xcode/iOS simulator를 실행할 수 없습니다. macOS에서는 저장소 루트에서 다음 순서로 확인합니다.

```bash
gradle -p kmp :shared:linkDebugFrameworkIosSimulatorArm64 --build-cache --no-daemon
xcodebuild \
  -project kmp/iosApp/SupplementRoutineIos.xcodeproj \
  -scheme SupplementRoutineIos \
  -configuration Debug \
  -sdk iphonesimulator \
  -destination "generic/platform=iOS Simulator" \
  CODE_SIGNING_ALLOWED=NO \
  build
```

GitHub Actions에서는 `.github/workflows/ios_kmp_ci.yml`이 같은 순서로 shared framework와 SwiftUI shell을 빌드합니다.

## 남은 범위

- iOS local persistence adapter
- iOS notification permission/scheduler adapter
- 실제 iOS simulator screenshot QA
- iPhone 실기기 실행, signing, provisioning
