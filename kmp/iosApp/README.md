# iOS Shell

이 폴더는 KMP shared module을 사용하는 SwiftUI iOS shell입니다.

## 현재 범위

- `SupplementRoutineIos.xcodeproj`는 signing 없이 iOS simulator debug build를 검증하기 위한 최소 Xcode project입니다.
- Xcode project는 KMP Gradle이 생성한 `kmp/shared/build/XCFrameworks/release/SupplementRoutineShared.xcframework`를 Frameworks phase에서 참조합니다.
- SwiftUI shell은 Today, Supplements, History, Settings tab 구조를 제공합니다.
- `SharedRoutineViewModel`은 `SupplementRoutineShared` framework의 `SharedAppSummary`를 import/call 해서 shared module integration을 smoke-test합니다.
- `UserDefaultsRoutineStore`는 iOS shell의 supplement/record/settings snapshot을 local-first로 저장하고 복원합니다.
- `UserNotificationReminderScheduler`는 `UNUserNotificationCenter` 기반으로 알림 권한 요청, daily reminder 예약, 예약 취소를 처리합니다.
- `Assets.xcassets`는 AppIcon과 AccentColor를 포함하며 Xcode target Resources phase에 연결되어 있습니다.
- Today는 저장된 영양제와 오늘 기록을 표시하고 항목별 체크/해제를 처리합니다.
- Supplements는 영양제 추가, 수정, 삭제, 시간 입력 검증, 알림 여부 관리를 제공합니다.
- History는 오늘 완료율, 월간 상태, 최근 2주 기록을 표시합니다.
- Settings는 식사 시간 입력 검증/저장, iOS 알림 권한/예약/취소, 로컬 데이터 초기화, 면책 안내를 제공합니다.
- SwiftUI shell은 glass material surface와 warm gradient background를 사용해 Android KMP glass UI와 같은 제품 톤을 맞춥니다.
- Settings는 iOS 알림 표시 QA를 위해 즉시 테스트 알림과 15초 뒤 예약 테스트 알림 액션을 제공합니다.
- `PrivacyInfo.xcprivacy`는 Xcode target Resources phase에 포함되어 있으며, 현재 범위는 tracking/data collection 없음과 iOS local store의 UserDefaults required-reason API(`CA92.1`) 선언으로 둡니다.

## 로컬 macOS 검증

Windows에서는 Xcode/iOS simulator를 실행할 수 없습니다. macOS에서는 저장소 루트에서 다음 순서로 확인합니다.

```bash
gradle -p kmp :shared:linkDebugFrameworkIosSimulatorArm64 --build-cache --no-daemon
gradle -p kmp :shared:assembleSupplementRoutineSharedReleaseXCFramework --build-cache --no-daemon
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
Release XCFramework 산출은 macOS runner에서 확인합니다. Windows에서는 Kotlin/Native iOS framework link 단계가 host 제약으로 skip될 수 있습니다.

## 남은 범위

- 실제 iOS simulator screenshot QA
- iOS 테스트 알림 표시와 15초 예약 발화 QA
- iPhone 실기기 실행, signing, provisioning
