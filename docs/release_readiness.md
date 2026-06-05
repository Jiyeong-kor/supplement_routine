# 릴리스 준비 상태

작성일: 2026-06-05

연결 issue:

- [#60 KMP Android 릴리스 APK/AAB 패키징과 알림 QA 게이트](https://github.com/Jiyeong-kor/supplement_routine/issues/60)
- [#62 KMP iOS SwiftUI 셸 릴리스 메타데이터와 App Store 패키징 경로](https://github.com/Jiyeong-kor/supplement_routine/issues/62)
- [#63 KMP 수동 릴리스 워크플로와 Android/iOS 서명 문서화](https://github.com/Jiyeong-kor/supplement_routine/issues/63)

이 문서는 Android와 iOS를 릴리스 직전까지 끌어올리기 위한 현재 검증 상태와 남은 외부 환경 QA를 정리한다. Flutter 기준 구현은 KMP parity와 cutover 결정 전까지 유지한다.

## 현재 로컬 검증

| 영역 | 상태 | 증거 |
| --- | --- | --- |
| KMP shared check | 통과 | `android\gradlew.bat -p kmp :shared:check :androidApp:assembleRelease --no-daemon` |
| KMP Android debug build | 통과 | `android\gradlew.bat -p kmp :shared:check :androidApp:assembleDebug :androidApp:assembleRelease --no-daemon` |
| KMP Android release assemble | 통과 | `android\gradlew.bat -p kmp :shared:check :androidApp:assembleRelease --no-daemon` |
| KMP Android release bundle | 통과 | `android\gradlew.bat -p kmp :androidApp:bundleRelease --no-daemon` |
| KMP Android release lint | 통과 | `android\gradlew.bat -p kmp :androidApp:lintRelease --no-daemon` |
| KMP Android/KMP release gate | 통과 | `android\gradlew.bat -p kmp :shared:check :androidApp:assembleDebug :androidApp:assembleRelease :androidApp:bundleRelease :androidApp:lintRelease --no-daemon` |
| KMP Android release signing fallback | 통과 | `kmp/key.properties`가 없을 때 debug signing config로 `:androidApp:assembleRelease` 성공 |
| KMP Android release APK install | 통과 | Galaxy A31 `SM-A315N`, Android 12(API 31)에 기존 Flutter 앱 승계 package `com.jiyeong.supplement_routine`으로 `androidApp-release.apk` 설치 성공 |
| KMP Android release launch smoke | 통과 | Galaxy A31에서 `com.jiyeong.supplement_routine/com.jiyeong.supplementroutine.kmp.android.MainActivity` foreground 실행 확인 |
| KMP Android glass UI screenshot QA | 통과 | API 36 emulator에서 재조정된 glass surface screenshot 확인: `<local-path>` |
| KMP Android physical install | 통과 | Galaxy A31 `SM-A315N`, Android 12(API 31)에 `com.jiyeong.supplement_routine` release APK 설치 성공 |
| KMP Android physical launch smoke | 통과 | Galaxy A31에서 `com.jiyeong.supplement_routine/com.jiyeong.supplementroutine.kmp.android.MainActivity` foreground 실행 확인 |
| KMP Android package/version/permission declaration | 확인됨 | Galaxy A31 설치 package에서 `versionCode=1`, `versionName=1.0.0`, `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM` 확인 |
| KMP Android launcher/notification icon | 구성됨 | KMP Android manifest가 `@drawable/ic_launcher`를 launcher/round icon으로 사용하고 reminder notification이 `@drawable/ic_stat_notification` 사용 |
| KMP Android app label | 구성됨 | 사용자 표시명은 `영양제 루틴`으로 설정. 내부 KMP 표기는 launcher label에 노출하지 않음 |
| KMP Android backup/data extraction rules | 구성됨 | `backup_rules.xml`, `data_extraction_rules.xml`을 manifest에 연결해 shared preferences와 DataStore 루틴 데이터를 cloud backup/device transfer 대상으로 명시 |
| KMP Android notification app-op | 확인됨 | API 36 emulator에서 `POST_NOTIFICATION` default mode allow, package permission granted |
| KMP Android notification display smoke | 통과 | API 36 emulator Settings 테스트 알림 전송 후 `dumpsys notification`에서 `id=4901`, channel `supplement_reminders`, title `테스트 알림 복용 시간` 확인. 화면: `<local-path>` |
| KMP Android scheduled notification smoke | 통과 | API 36 emulator Settings 예약 테스트 알림 실행 후 15초 뒤 `dumpsys notification`에서 channel `supplement_reminders`, title `예약 테스트 알림 복용 시간` 확인 |
| KMP Android exact alarm permission | 확인됨 | Galaxy A31 Android 12에서 `SCHEDULE_EXACT_ALARM` granted, app-op default |
| KMP Android exact alarm fallback | 확인됨 | API 36 emulator에서 `SCHEDULE_EXACT_ALARM: deny` 상태로 설정 화면 fallback 안내 확인. 정확 알림 권한이 없어도 `AlarmManager.setAndAllowWhileIdle`/`set`으로 근처 시간 알림 예약 |
| iOS SwiftUI form validation | 구현됨 | 영양제 이름/복용량 empty state와 `HH:mm` 시간 입력 검증 |
| iOS shared framework CI path | 구성됨 | `.github/workflows/ios_kmp_ci.yml` |
| iOS shared release XCFramework CI path | 구성됨 | `.github/workflows/ios_kmp_ci.yml`에서 `:shared:assembleSupplementRoutineSharedReleaseXCFramework` 실행 |
| iOS SwiftUI simulator build CI path | 구성됨 | `.github/workflows/ios_kmp_ci.yml` |
| KMP CI | 통과 | PR #61 최신 head checks: shared check, Android debug/release APK, release AAB |
| iOS KMP CI | 통과 | PR #61 최신 head checks: iOS simulator framework, release XCFramework, SwiftUI shell build |
| iOS privacy manifest | 구성됨 | `PrivacyInfo.xcprivacy`가 Xcode target Resources phase에 포함됨. `UserDefaultsRoutineStore` 사용에 맞춰 `NSPrivacyAccessedAPICategoryUserDefaults` / `CA92.1` 선언 |
| iOS AppIcon asset catalog | 구성됨 | `Assets.xcassets/AppIcon.appiconset`이 Xcode target Resources phase에 포함됨. iPhone/iPad/App Store marketing icon PNG 포함 |
| Android package/version metadata | 구성됨 | Flutter 기준 `applicationId=com.jiyeong.supplement_routine`, `versionName=1.0.0`, `versionCode=1` 승계 |
| iOS bundle/version metadata | 구성됨 | 기본 bundle id는 `com.jiyeong.supplementroutine.ios`, `MARKETING_VERSION=1.0.0`, `CURRENT_PROJECT_VERSION=1`, generated Info.plist의 `CFBundleShortVersionString`/`CFBundleVersion` 명시. 실제 App Store bundle id는 release workflow secret으로 override 가능 |
| Android/iOS resource parse | 통과 | Android manifest/backup/data extraction XML, iOS `PrivacyInfo.xcprivacy`, iOS asset catalog `Contents.json` 파싱 성공 |
| KMP manual release workflow | 구성됨 | `.github/workflows/kmp_release.yml`에서 Android signed APK/AAB와 iOS signed archive tarball/IPA artifact 생성 경로 제공. GitHub `workflow_dispatch` 제약상 workflow 파일이 default branch에 병합된 뒤 수동 실행 가능. 실제 signing secrets 필요 |
| Release signing secrets | 미등록 | 2026-06-05 `gh secret list --repo Jiyeong-kor/supplement_routine` 결과 release signing secrets 없음. Android/iOS signed artifact 생성 전 등록 필요 |

Windows 로컬 검증에는 Android SDK 경로가 필요하다.

```powershell
$env:ANDROID_HOME="$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
android\gradlew.bat -p kmp :shared:check :androidApp:assembleDebug :androidApp:assembleRelease :androidApp:bundleRelease --no-daemon
```

## Android 릴리스 직전 체크

| 항목 | 현재 상태 | 릴리스 전 필요 증거 |
| --- | --- | --- |
| Today/Supplements/History/Settings 핵심 flow | 구현됨 | API 36 emulator에서 glass UI 기준 주요 tab screenshot 확인 |
| Local persistence | 구현됨 | 앱 재시작 후 영양제, 기록, 식사 시간, 알림 기본값 유지 확인 |
| Notification runtime permission | 구현됨 | API 36 emulator package permission granted 확인. Android 13+ permission dialog 거부/허용 UX는 별도 시나리오 필요 |
| Exact alarm permission | 구현됨 | Galaxy A31 Android 12에서 권한 granted 확인. 정확 알림 권한이 없으면 inexact alarm으로 fallback. Android 14 설정 이동 화면은 별도 실기기/에뮬레이터 시나리오 필요 |
| 실제 알림 표시/예약 발화 | 통과 | Settings 테스트 알림과 15초 예약 테스트 알림으로 notification channel, 표시, `AlarmManager` 발화 smoke 확인. 실제 사용자 루틴 시간대 장시간 QA는 릴리스 후보에서 추가 확인 |
| Release APK install/launch smoke | 통과 | API 36 emulator와 Galaxy A31 Android 12 실기기 설치/foreground Activity 확인 |
| Release APK assemble | 통과 | `:androidApp:assembleRelease` |
| Release AAB bundle | 통과 | `:androidApp:bundleRelease` |
| Release lint | 통과 | `:androidApp:lintRelease` |
| App display name | 구성됨 | Launcher label은 `영양제 루틴` |
| Application id/version | 구성됨 | 기존 Flutter Android 앱 승계를 위해 `com.jiyeong.supplement_routine`, `1.0.0(1)`로 설정 |
| Backup/device transfer | 구성됨 | `android:fullBackupContent`, `android:dataExtractionRules`가 KMP Android manifest에 명시됨 |
| Launcher/notification icon | 구성됨 | 기존 Flutter 제품 아이콘 톤을 KMP Android launcher icon과 reminder notification small icon에 반영 |
| Upload signing | 준비됨 | 실제 upload keystore를 `kmp/key.properties`에 설정한 release build |
| Manual release artifact workflow | 구성됨 | `.github/workflows/kmp_release.yml` 실행 전 Android signing secrets 구성 필요 |
| Store screenshots | 기존 Flutter asset 있음 | KMP Android 화면 기준 phone/expanded screenshot 갱신 여부 결정 |

## iOS 릴리스 직전 체크

| 항목 | 현재 상태 | 릴리스 전 필요 증거 |
| --- | --- | --- |
| KMP shared import/call | 구현됨 | macOS CI 또는 Xcode build log |
| SwiftUI product shell | 구현됨 | Today/Supplements/History/Settings simulator screenshot |
| SwiftUI glass material UI | 구현됨 | Windows에서는 simulator screenshot 미검증. macOS/Xcode에서 material surface와 tab별 layout 확인 필요 |
| Supplement form validation | 구현됨 | 이름/복용량 empty state와 00:00-23:59 시간 입력 오류 표시 |
| Meal time validation | 구현됨 | 식사 시간 저장 전 00:00-23:59 입력 오류 표시 |
| Local persistence | 구현됨 | simulator 재실행 후 UserDefaults snapshot 복원 확인 |
| UserNotifications adapter | 구현됨 | 권한 요청, daily reminder 예약/취소, 실제 발화 확인 |
| UserNotifications smoke actions | 구현됨 | Settings에서 즉시 테스트 알림과 15초 뒤 예약 테스트 알림 실행 가능. macOS/iOS simulator 또는 실기기에서 표시 확인 필요 |
| Privacy manifest | 구성됨 | `PrivacyInfo.xcprivacy` 포함. UserDefaults required-reason API는 `CA92.1`로 선언됨. App Store Connect 제출 전 실제 데이터 수집/추적 사용 여부 재확인 필요 |
| AppIcon asset catalog | 구성됨 | `Assets.xcassets`가 target resource로 연결됨. macOS/Xcode에서 asset catalog compile 확인 필요 |
| Bundle/version metadata | 구성됨 | 기본 bundle id는 `com.jiyeong.supplementroutine.ios`, marketing/build version은 `1.0.0(1)` |
| Simulator build | 통과 | PR #61 최신 head checks에서 SwiftUI iOS shell simulator build 성공 |
| Release shared artifact | 통과 | PR #61 최신 head checks에서 `:shared:assembleSupplementRoutineSharedReleaseXCFramework` 성공. Windows에서는 iOS framework link task가 host 제약으로 skip됨 |
| Signing/provisioning | workflow 구성됨 | Apple team, bundle id, certificate, provisioning profile secrets 구성 필요 |
| TestFlight/App Store path | archive/IPA 구성됨 | `.github/workflows/kmp_release.yml`은 `.xcarchive.tar.gz`와 App Store Connect export option 기반 `.ipa` artifact까지 생성. TestFlight upload 단계는 별도 추가 필요 |

Windows에서는 Xcode/iOS simulator를 직접 실행할 수 없다. iOS 릴리스 직전 증거는 macOS/Xcode 환경 또는 GitHub-hosted macOS runner에서 수집한다.

## Cutover 결정

KMP 앱을 실제 배포 대상으로 전환하기 전에 다음 결정을 완료한다.

| 항목 | 결정 |
| --- | --- |
| Android package | 기존 Flutter Android 앱 업데이트 배포를 기준으로 `com.jiyeong.supplement_routine` 승계 |
| Android version | Flutter `pubspec.yaml`의 `1.0.0+1`과 맞춰 `versionName=1.0.0`, `versionCode=1` |
| iOS 기본 bundle id | 신규 iOS 앱 기본값으로 `com.jiyeong.supplementroutine.ios` 사용. 실제 App Store Connect bundle id는 release workflow의 `IOS_BUNDLE_IDENTIFIER` secret으로 override 가능 |
| iOS version | Flutter `pubspec.yaml`의 `1.0.0+1`과 맞춰 `MARKETING_VERSION=1.0.0`, `CURRENT_PROJECT_VERSION=1` |
| Flutter 기준 구현 | KMP parity와 store cutover 전까지 유지 |
| Store screenshots | KMP 화면 기준 screenshot 갱신 여부는 스토어 등록 시 결정 |

## 남은 차단 조건

현재 로컬에서 더 진행할 수 없는 항목은 외부 환경 의존이다.

- Android 13+ notification runtime permission dialog 거부/허용 UX QA
- Android 14 exact alarm 설정 이동 화면 QA
- 실제 사용자 루틴 시간대 장시간 알림 QA
- iOS simulator screenshot/accessibility QA
- iPhone 실기기 signing/provisioning
- Play Console/App Store Connect 등록과 GitHub Actions release signing secret 구성
- KMP manual release workflow 실제 실행 결과. GitHub `workflow_dispatch`는 workflow 파일이 default branch에 있어야 수동 트리거할 수 있으므로 PR 병합 뒤 실행한다.
