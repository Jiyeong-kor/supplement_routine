# KMP Parity Check

작성일: 2026-06-04

이 문서는 `docs/prd.md`의 제품 요구사항을 기준으로 Flutter 구현과 KMP shared / Android Compose / iOS 전환 상태를 비교한다. Flutter 구현은 KMP parity와 QA가 끝나기 전까지 기준 구현으로 유지한다.

## 현재 기준

- Flutter 기준 source: `lib/`
- KMP shared source: `kmp/shared/`
- KMP Android source: `kmp/androidApp/`
- iOS 준비 위치: `kmp/iosApp/`
- 제품 기준: `docs/prd.md`
- 제품 IA: `docs/information_architecture.md`
- 디자인 시스템: `docs/design_system.md`
- 마이그레이션 로드맵: `docs/kmp_migration_roadmap.md`
- 현재 GitHub gap issue: #24, #25

## 완료된 범위

| 영역 | 상태 | 근거 |
| --- | --- | --- |
| KMP project scaffold | 완료 | `kmp/settings.gradle.kts`, `kmp/shared`, `kmp/androidApp`, `kmp/iosApp/README.md` |
| Shared domain model | 완료 | `Supplement`, `IntakeRecord`, `MealTimeSettings`, intake enum, schedule label |
| Shared scheduling logic | 완료 | `SchedulingService`, `TimeUtils`, shared tests |
| Shared history summary logic | 완료 | `HistorySummaryService`, recent/month summary tests |
| Shared persistence contract | 완료 | DTO/domain mapping, repository interface, mapping tests |
| Shared form validation policy | 완료 | `SupplementFormPolicy`, form policy tests |
| Android local persistence | 완료 | #21, `AndroidRoutineDataStore`, Android repository implementations |
| Android Compose app shell | 완료 | `SupplementRoutineKmpApp`, Bottom Navigation, Today/Supplements/History/Settings route |
| Android Compose Today | 완료에 가까움 | stored supplements/records 기반 목록, 진행률, 복용 체크/해제, empty/error state |
| Android Compose Supplements | 완료에 가까움 | add/edit form, shared validation, notification toggle, delete confirmation |
| Android Compose History | 릴리스 범위 충족 | PRD P1 범위인 오늘 완료율, 월간 완료 상태, 최근 기록 표시를 구현. 월 이동/상세 drill-down은 릴리스 필수에서 제외 |
| Android Compose Settings | 완료에 가까움 | 식사 시간 편집, 알림 기본값, 권한 안내, 초기화, 가이드/면책 고지 |
| Android notification/exact alarm | 완료 | #23, #47, permission controller, scheduler adapter, receiver. API 36 emulator에서 Android 13+ runtime permission dialog, 정확 알림 설정 화면, 즉시/예약 알림 발화 확인 |
| Android haptics | 완료 | routine haptic intent adapter, 복용 완료/저장/destructive/validation intent |
| Android 디자인 시스템 적용 | 완료 | #44, warm white/berry/coral/mint/ink Compose theme token, edge-to-edge inset |
| iOS shared framework CI | 완료 | #42, `.github/workflows/ios_kmp_ci.yml`, `SupplementRoutineShared` framework build |
| iOS SwiftUI shell | 완료 | #24, `kmp/iosApp/SupplementRoutineIos.xcodeproj`, shared import/call smoke path |
| iOS local persistence | 완료에 가까움 | #51, `UserDefaultsRoutineStore`, SwiftUI shell snapshot 저장/복원 path |
| iOS notification adapter | 완료에 가까움 | #52, `UserNotificationReminderScheduler`, 권한 요청, daily reminder 예약/취소 path |

## Flutter 대비 기능별 상태

| Flutter 기능 | KMP/Android 상태 | 남은 gap |
| --- | --- | --- |
| 오늘 복용 목록 | Android 구현됨 | API 36 emulator phone/expanded-width screenshot QA 완료. 현재 QA 데이터는 empty state 기준 |
| 복용 체크/해제 | Android 구현됨 | Today row는 완료됨/미완료 semantics 제공. 현재 QA 데이터는 empty state라 실데이터 row screenshot은 후속 polish에서 확대 가능 |
| 영양제 목록 | Android 구현됨 | API 36 emulator phone screenshot QA 완료. 삭제/알림 toggle 세부 상태는 후속 polish에서 확대 가능 |
| 영양제 추가/수정 | Android 구현됨 | shared validation과 저장 햅틱 구현. validation error 세부 screenshot은 후속 polish에서 확대 가능 |
| 기록 요약 | Android 구현됨 | 오늘 완료율, 월간 완료 상태, 최근 기록 표시 제공. 월 이동/상세 기록 drill-down은 릴리스 필수에서 제외 |
| 설정 | Android 구현됨 | 권한 상태별 문구와 알림 테스트 flow QA 완료. 초기화 flow 세부 screenshot은 후속 polish에서 확대 가능 |
| 알림 | Android 구현됨 | Android 13+ notification runtime permission dialog, 정확 알림 설정 이동, 권한 off fallback, 즉시/예약 알림 발화 QA 완료 |
| 로컬 저장 | Android 구현됨 | migration 호환성은 Flutter cutover 전 별도 판단 필요 |
| 디자인 시스템 | Android theme 적용됨 | phone/expanded-width QA 중 History bottom navigation overlap을 발견해 하단 bar background를 불투명하게 보강 |
| iOS | 부분 구현 | SwiftUI shell, shared import/call smoke path, UserDefaults local snapshot, UserNotifications adapter. screenshot QA는 남음 |
| 접근성/스크린샷 QA | 완료 | #25 Android phone/expanded-width screenshot, Today/History semantics 점검 완료. iOS screenshot은 Windows/Xcode 제약으로 캡처 불가 사유 문서화 |

## 남은 Gap Issue

| 우선순위 | Issue | 이유 |
| --- | --- | --- |
| P0 | #67 `[release] Android/iOS 서명 secret 등록과 signed artifact 검증` | Android signed artifact는 통과했지만, iOS distribution certificate/provisioning profile secret이 아직 없어 iOS signed archive/IPA 검증이 막혀 있다. |

## 닫힌 Gap Issue

| Issue | 반영 |
| --- | --- |
| #20 Settings 화면과 권한 상태 UI | `SettingsScreen.kt`, `SupplementRoutineKmpApp.kt` |
| #21 Android local persistence | `AndroidRoutineDataStore.kt`, `AndroidRepositories.kt` |
| #22 영양제 추가/수정 flow | `SupplementsScreen.kt`, `SupplementFormPolicy.kt` |
| #23 Notification/exact alarm adapter | `AndroidNotificationPermissionController.kt`, `AndroidReminderScheduler.kt`, `SupplementReminderReceiver.kt` |
| #24 iOS SwiftUI shell | `kmp/iosApp/SupplementRoutineIos.xcodeproj`, `RootView.swift`, `SharedRoutineViewModel.swift` |
| #51 iOS local persistence adapter | `IosRoutineStore.swift`, `SharedRoutineViewModel.swift`, `RootView.swift` |
| #52 iOS notification adapter | `IosNotificationScheduler.swift`, `SharedRoutineViewModel.swift`, `RootView.swift` |
| #42 iOS KMP framework CI | `.github/workflows/ios_kmp_ci.yml`, `SupplementRoutineShared` framework |
| #44 Android Compose design token | `SupplementRoutineTheme.kt`, `MainActivity.kt`, `SupplementRoutineKmpApp.kt` |
| #25 KMP Android/iOS parity screenshot과 accessibility 검증 | Android phone/expanded-width screenshot과 Today/History semantics 확인. iOS screenshot은 Windows/Xcode 제약으로 캡처 불가 사유를 release readiness에 기록 |
| #47 Android notification permission/exact alarm 실기기 검증 | API 36 emulator에서 runtime permission dialog 거부/허용, `POST_NOTIFICATION` app-op, 정확 알림 설정 화면, 즉시/예약 알림 발화 확인 |
| #48 Android History release polish 범위 결정 | PRD P1 기준에서 오늘 완료율, 월간 완료 상태, 최근 기록 표시를 릴리스 필수 범위로 확정. 월 이동/상세 drill-down은 후속 개선으로 보류 |
| #49 Flutter 기준 구현 cutover/removal 결정 | KMP parity, iOS signing, store cutover 전까지 Flutter 기준 구현 유지. 즉시 제거하지 않음 |

## 권장 진행 순서

1. #67에서 iOS signing/provisioning secret을 등록하고 signed archive/IPA artifact를 검증한다.
2. store asset, privacy/disclaimer, 버전 정책을 마지막 release readiness 문서로 묶는다.

## 현재 제한 사항

- iOS는 SwiftUI shell, shared import/call smoke path, local snapshot persistence, notification adapter 수준이다. simulator screenshot QA는 남아 있다.
- Windows에서는 Xcode/iOS simulator를 직접 실행할 수 없다. 무료 검증은 GitHub-hosted macOS runner의 framework와 SwiftUI shell simulator build까지이며, 실기기 iOS QA는 Mac/Xcode 또는 signing/provisioning 가능한 macOS runner가 필요하다.
- Android 13+ notification permission dialog는 API 36 emulator에서 검증 완료했다. Android 12 실기기는 runtime notification permission 대상이 아니므로 package/launch/exact alarm smoke만 확인했다.
- Flutter 구현은 아직 삭제 대상이 아니다. KMP parity, iOS signing, store cutover가 끝날 때까지 기준 구현과 rollback reference로 유지한다.

## 검증 기준

Parity에 도달했다고 판단하려면 다음을 만족해야 한다.

- Flutter 기준 user flow의 주요 경로가 Android KMP 앱에서 동작한다.
- 앱 재시작 후 영양제, 기록, 식사 시간, 알림 기본값이 유지된다.
- Android notification runtime permission과 exact alarm permission이 분리되어 안내된다.
- iOS는 shared module을 import하고 최소 Today/Supplements/History/Settings shell을 제공한다.
- iOS는 플랫폼별 저장소와 알림 adapter를 제공하고, 아직 parity가 아닌 화면에는 명확한 fallback을 제공한다.
- Today checklist item과 History calendar tile은 상태를 색상만으로 전달하지 않는다.
- phone과 expanded-width 화면에서 navigation과 주요 목록이 겹치지 않는다.
- CI는 Flutter, KMP Android/shared, iOS shared framework를 모두 검증한다.
