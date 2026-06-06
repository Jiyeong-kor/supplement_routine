# 릴리스 준비 상태

작성일: 2026-06-05

연결 issue:

- [#60 KMP Android 릴리스 APK/AAB 패키징과 알림 QA 게이트](https://github.com/Jiyeong-kor/supplement_routine/issues/60)
- [#62 KMP iOS SwiftUI 셸 릴리스 메타데이터와 App Store 패키징 경로](https://github.com/Jiyeong-kor/supplement_routine/issues/62)
- [#63 KMP 수동 릴리스 워크플로와 Android/iOS 서명 문서화](https://github.com/Jiyeong-kor/supplement_routine/issues/63)
- [#67 Android/iOS 서명 secret 등록과 signed artifact 검증](https://github.com/Jiyeong-kor/supplement_routine/issues/67)

이 문서는 Android 우선 출시를 위한 현재 검증 상태와 남은 외부 환경 작업을 정리한다. iOS 출시는 제품 결정에 따라 후속으로 보류한다. Flutter 기준 구현은 Android store cutover와 iOS 재개 결정 전까지 reference/rollback 기준으로 유지한다.

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
| KMP Android glass UI screenshot QA | 통과 | API 36 emulator에서 재조정된 glass surface screenshot 증거 확인됨 |
| KMP Android phone screenshot QA | 통과 | API 36 emulator phone size에서 Today/Supplements/History/Settings 캡처 확인. Today empty state, FAB, bottom navigation 겹침 없음. 스크린샷 증거 확인됨 |
| KMP Android expanded-width screenshot QA | 통과 | API 36 emulator `wm size 1920x1280`, density `320`에서 Today/History/Settings 캡처 확인. History 월간 기록과 bottom navigation 겹침을 발견해 bottom navigation background를 불투명 surface로 보강. 수정 후 스크린샷 증거 확인됨 |
| KMP Android accessibility label QA | 통과 | Today checklist row는 코드상 `${supplement.name}, ${formatTime(record.scheduledTime)}, 완료됨/미완료` semantics 제공. 현재 QA 데이터는 empty state라 row 미노출. History calendar tile은 UI dump에서 `1일, 일정 없음`, `5일, 일정 없음`처럼 날짜와 상태를 content description으로 제공 |
| KMP Android physical install | 통과 | Galaxy A31 `SM-A315N`, Android 12(API 31)에 `com.jiyeong.supplement_routine` release APK 설치 성공 |
| KMP Android physical launch smoke | 통과 | Galaxy A31에서 `com.jiyeong.supplement_routine/com.jiyeong.supplementroutine.kmp.android.MainActivity` foreground 실행 확인 |
| KMP Android package/version/permission declaration | 확인됨 | Galaxy A31 설치 package에서 `versionCode=1`, `versionName=1.0.0`, `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM` 확인 |
| KMP Android launcher/notification icon | 구성됨 | KMP Android manifest가 `@drawable/ic_launcher`를 launcher/round icon으로 사용하고 reminder notification이 `@drawable/ic_stat_notification` 사용 |
| KMP Android app label | 구성됨 | 사용자 표시명은 `영양제 루틴`으로 설정. 내부 KMP 표기는 launcher label에 노출하지 않음 |
| KMP Android backup/data extraction rules | 구성됨 | `backup_rules.xml`, `data_extraction_rules.xml`을 manifest에 연결해 shared preferences와 DataStore 루틴 데이터를 cloud backup/device transfer 대상으로 명시 |
| KMP Android notification app-op | 확인됨 | API 36 emulator에서 `POST_NOTIFICATION` default mode allow, package permission granted |
| KMP Android notification runtime permission dialog | 통과 | API 36 emulator에서 `POST_NOTIFICATIONS` 사용자 플래그 초기화 후 Settings `권한 요청` 실행. 시스템 `GrantPermissionsActivity`가 `Allow 영양제 루틴 to send you notifications?` 다이얼로그와 `Allow`/`Don’t allow` 버튼 표시. 거부 후 `POST_NOTIFICATION: ignore`, 허용 후 `granted=true` 확인. 스크린샷 증거 확인됨 |
| KMP Android notification display smoke | 통과 | API 36 emulator Settings 테스트 알림 전송 후 `dumpsys notification`에서 `id=4901`, channel `supplement_reminders`, title `테스트 알림 복용 시간` 확인. 스크린샷 증거 확인됨 |
| KMP Android scheduled notification smoke | 통과 | API 36 emulator Settings 예약 테스트 알림 실행 후 15초 뒤 `dumpsys notification`에서 `id=44588649`, channel `supplement_reminders` 확인. 즉시 알림 `id=4901`과 함께 app group에 묶임 |
| KMP Android exact alarm permission | 확인됨 | Galaxy A31 Android 12에서 `SCHEDULE_EXACT_ALARM` granted, app-op default |
| KMP Android exact alarm settings/fallback | 통과 | API 36 emulator에서 `Alarms & reminders` 시스템 설정 화면 이동 확인. 앱 `영양제 루틴`의 `Allow setting alarms and reminders` 토글은 unchecked. 권한이 없어도 Settings 예약 테스트 알림이 `SupplementReminderReceiver`를 통해 발화했고, `AlarmManager.setAndAllowWhileIdle`/`set` fallback으로 근처 시간 알림 예약 확인. 스크린샷 증거 확인됨 |
| iOS SwiftUI form validation | 구현됨 | 영양제 이름/복용량 empty state와 `HH:mm` 시간 입력 검증 |
| iOS shared framework CI path | 구성됨 | `.github/workflows/ios_kmp_ci.yml` |
| iOS shared release XCFramework CI path | 구성됨 | `.github/workflows/ios_kmp_ci.yml`에서 `:shared:assembleSupplementRoutineSharedReleaseXCFramework` 실행 |
| iOS SwiftUI simulator build CI path | 구성됨 | `.github/workflows/ios_kmp_ci.yml` |
| KMP CI | 통과 | #64, #65, #66 head에서 shared check, Android debug/release APK, release AAB 검증 성공. #66 수동 run `26999304231` 성공 |
| iOS KMP CI | 통과 | #64, #65, #66 head에서 iOS simulator framework, release XCFramework, SwiftUI shell build 검증 성공. #66 수동 run `26999298276` 성공 |
| iOS privacy manifest | 구성됨 | `PrivacyInfo.xcprivacy`가 Xcode target Resources phase에 포함됨. `UserDefaultsRoutineStore` 사용에 맞춰 `NSPrivacyAccessedAPICategoryUserDefaults` / `CA92.1` 선언 |
| iOS AppIcon asset catalog | 구성됨 | `Assets.xcassets/AppIcon.appiconset`이 Xcode target Resources phase에 포함됨. iPhone/iPad/App Store marketing icon PNG 포함 |
| Android package/version metadata | 구성됨 | Flutter 기준 `applicationId=com.jiyeong.supplement_routine`, `versionName=1.0.0`, `versionCode=1` 승계 |
| iOS bundle/version metadata | 구성됨 | 기본 bundle id는 `com.jiyeong.supplementroutine.ios`, `MARKETING_VERSION=1.0.0`, `CURRENT_PROJECT_VERSION=1`, generated Info.plist의 `CFBundleShortVersionString`/`CFBundleVersion` 명시. 실제 App Store bundle id는 release workflow secret으로 override 가능 |
| Android/iOS resource parse | 통과 | Android manifest/backup/data extraction XML, iOS `PrivacyInfo.xcprivacy`, iOS asset catalog `Contents.json` 파싱 성공 |
| KMP manual release workflow | 구성됨 | `.github/workflows/kmp_release.yml`에서 Android signed APK/AAB와 iOS signed archive tarball/IPA artifact 생성 경로 제공. 현재 출시 범위는 `platform=android` |
| Android release signing secrets | 등록됨 | 2026-06-05 `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_PASSWORD`, `ANDROID_KEY_ALIAS` 등록 완료 |
| iOS release signing secrets | 보류 | 2026-06-05 제품 결정으로 iOS 출시는 후속으로 미룬다. iOS signed archive/IPA 생성은 이번 Android-only 출시 범위에서 제외 |
| Release secret setup runbook | 구성됨 | `docs/release_signing.md`에 Android/iOS secret base64 변환, `gh secret set`, `KMP Release` 실행, artifact 다운로드 명령 정리 |
| Android KMP Release workflow | 통과 | 최신 `main` 기준 `KMP Release` run `27008729353`, `platform=android`, Android signed release artifacts job 성공. iOS job은 의도적으로 skipped |
| Android signed release artifacts | 생성됨 | `kmp-android-release` artifact 다운로드 확인. `androidApp-release.apk` 13,686,333 bytes, `androidApp-release.aab` 13,252,323 bytes |
| Android signed APK verification | 통과 | `apksigner verify --verbose --print-certs` 결과 `Verifies`, v1/v2 signing true. 인증서 SHA-256 digest `f90457e364fc0c14d30abdd841efadc8d7ba69b55cff9455cfcf63cb6afe83c2` |
| Android signed AAB verification | 통과 | `jarsigner -verify` 결과 `jar verified`. 자체 서명 upload certificate라 PKIX chain warning은 예상됨 |

Windows 로컬 검증에는 Android SDK 경로가 필요하다.

```powershell
$env:ANDROID_HOME="$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
android\gradlew.bat -p kmp :shared:check :androidApp:assembleDebug :androidApp:assembleRelease :androidApp:bundleRelease --no-daemon
```

## Android 릴리스 직전 체크

| 항목 | 현재 상태 | 릴리스 전 필요 증거 |
| --- | --- | --- |
| Today/Supplements/History/Settings 핵심 flow | 통과 | API 36 emulator phone/expanded-width screenshot 확인. expanded History bottom navigation overlap은 수정 후 재캡처 |
| Local persistence | 구현됨 | 앱 재시작 후 영양제, 기록, 식사 시간, 알림 기본값 유지 확인 |
| Notification runtime permission | 통과 | API 36 emulator에서 Android 13+ runtime permission dialog, 거부/허용 결과, app-op/package permission 상태 확인 |
| Exact alarm permission | 통과 | Galaxy A31 Android 12 권한 granted 확인. API 36 emulator에서 `Alarms & reminders` 설정 이동과 권한 off 상태 fallback 예약/발화 확인 |
| 실제 알림 표시/예약 발화 | 통과 | Settings 테스트 알림과 15초 예약 테스트 알림으로 notification channel, 표시, `AlarmManager` 발화 smoke 확인. 실제 사용자 루틴 시간대 장시간 QA는 릴리스 후보에서 추가 확인 |
| Release APK install/launch smoke | 통과 | API 36 emulator와 Galaxy A31 Android 12 실기기 설치/foreground Activity 확인 |
| Release APK assemble | 통과 | `:androidApp:assembleRelease` |
| Release AAB bundle | 통과 | `:androidApp:bundleRelease` |
| Release lint | 통과 | `:androidApp:lintRelease` |
| App display name | 구성됨 | Launcher label은 `영양제 루틴` |
| Application id/version | 구성됨 | 기존 Flutter Android 앱 승계를 위해 `com.jiyeong.supplement_routine`, `1.0.0(1)`로 설정 |
| Backup/device transfer | 구성됨 | `android:fullBackupContent`, `android:dataExtractionRules`가 KMP Android manifest에 명시됨 |
| Launcher/notification icon | 구성됨 | 기존 Flutter 제품 아이콘 톤을 KMP Android launcher icon과 reminder notification small icon에 반영 |
| Upload signing | 통과 | 새 Android upload keystore로 로컬 `:shared:check :androidApp:assembleRelease :androidApp:bundleRelease` 성공. GitHub Secrets 기반 `KMP Release` Android job 성공 |
| Manual release artifact workflow | 통과 | `.github/workflows/kmp_release.yml` `platform=android` run `27008729353` 성공 및 최신 signed APK/AAB artifact 생성 |
| Store screenshots | Android KMP QA 캡처 있음 | 릴리스 QA용 phone/expanded screenshot은 수집됨. Play Store 제출용 최종 마케팅 screenshot은 스토어 등록 시 별도 선별 |
| Play Console 제출 | 사용자 작업 필요 | repository에는 Play Console service account secret이 없다. 최신 AAB를 Play Console에 업로드하고 심사 제출하는 단계는 계정 소유자가 진행해야 한다 |

## iOS 후속 보류 범위

| 항목 | 현재 상태 | 재개 시 필요 증거 |
| --- | --- | --- |
| KMP shared import/call | 구현됨 | macOS CI 또는 Xcode build log |
| SwiftUI product shell | 구현됨 | Today/Supplements/History/Settings simulator screenshot |
| SwiftUI glass material UI | 구현됨 | Windows에서는 iOS simulator screenshot 캡처 불가. macOS/Xcode에서 material surface와 tab별 layout 확인 필요 |
| Supplement form validation | 구현됨 | 이름/복용량 empty state와 00:00-23:59 시간 입력 오류 표시 |
| Meal time validation | 구현됨 | 식사 시간 저장 전 00:00-23:59 입력 오류 표시 |
| Local persistence | 구현됨 | simulator 재실행 후 UserDefaults snapshot 복원 확인 |
| UserNotifications adapter | 구현됨 | 권한 요청, daily reminder 예약/취소, 실제 발화 확인 |
| UserNotifications smoke actions | 구현됨 | Settings에서 즉시 테스트 알림과 15초 뒤 예약 테스트 알림 실행 가능. macOS/iOS simulator 또는 실기기에서 표시 확인 필요 |
| Privacy manifest | 구성됨 | `PrivacyInfo.xcprivacy` 포함. UserDefaults required-reason API는 `CA92.1`로 선언됨. App Store Connect 제출 전 실제 데이터 수집/추적 사용 여부 재확인 필요 |
| AppIcon asset catalog | 구성됨 | `Assets.xcassets`가 target resource로 연결됨. macOS/Xcode에서 asset catalog compile 확인 필요 |
| Bundle/version metadata | 구성됨 | 기본 bundle id는 `com.jiyeong.supplementroutine.ios`, marketing/build version은 `1.0.0(1)` |
| Simulator build | 통과 | #65/#66 iOS KMP CI에서 SwiftUI iOS shell simulator build 성공 |
| Release shared artifact | 통과 | #65/#66 iOS KMP CI에서 `:shared:assembleSupplementRoutineSharedReleaseXCFramework` 성공. Windows에서는 iOS framework link task가 host 제약으로 skip됨 |
| Signing/provisioning | 후속 보류 | Apple team, bundle id, certificate, provisioning profile secrets 구성 필요 |
| TestFlight/App Store path | 후속 보류 | `.github/workflows/kmp_release.yml`은 `.xcarchive.tar.gz`와 App Store Connect export option 기반 `.ipa` artifact까지 생성. TestFlight upload 단계는 별도 추가 필요 |

Windows에서는 Xcode/iOS simulator를 직접 실행할 수 없다. iOS 릴리스 재개 시 증거는 macOS/Xcode 환경 또는 GitHub-hosted macOS runner에서 수집한다.

## Cutover 결정

KMP 앱을 실제 배포 대상으로 전환하기 전에 다음 결정을 완료한다.

| 항목 | 결정 |
| --- | --- |
| Android package | 기존 Flutter Android 앱 업데이트 배포를 기준으로 `com.jiyeong.supplement_routine` 승계 |
| Android version | Flutter `pubspec.yaml`의 `1.0.0+1`과 맞춰 `versionName=1.0.0`, `versionCode=1` |
| iOS 기본 bundle id | 신규 iOS 앱 기본값으로 `com.jiyeong.supplementroutine.ios` 사용. 실제 App Store Connect bundle id는 release workflow의 `IOS_BUNDLE_IDENTIFIER` secret으로 override 가능 |
| iOS version | Flutter `pubspec.yaml`의 `1.0.0+1`과 맞춰 `MARKETING_VERSION=1.0.0`, `CURRENT_PROJECT_VERSION=1` |
| Flutter 기준 구현 | Android store cutover와 iOS 재개 결정 전까지 유지 |
| Store screenshots | Android KMP 화면 기준 screenshot은 QA용으로 수집됨. Play Store 제출용 최종 마케팅 screenshot은 스토어 등록 시 선별 |
| Android History release scope | PRD P1 기준으로 오늘 완료율, 월간 완료 상태, 최근 기록 표시를 릴리스 필수 범위로 확정. 월 이동/상세 기록 drill-down은 후속 개선으로 보류 |
| Flutter cutover/removal | Android Play Store 제출/배포 확인 전까지 제거하지 않음. iOS는 후속 출시 재개 전까지 reference로 유지 |
| iOS 출시 | 이번 출시 범위에서 보류. Android 출시를 먼저 진행 |

## 남은 차단 조건

현재 로컬/GitHub Actions에서 더 진행할 수 없는 항목은 외부 환경 의존이다.

- 실제 사용자 루틴 시간대 장시간 알림 QA
- Play Console 앱 등록, 콘텐츠 등급, 데이터 보안, 스토어 등록정보, 심사 제출
- Play Console 업로드용 service account secret이 없으므로 자동 제출 불가
- iOS simulator screenshot/accessibility QA, iPhone signing/provisioning, iOS signed archive/IPA 검증은 후속 출시로 보류
