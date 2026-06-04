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
| Android Compose History | 부분 완료 | 실제 기록 기반 요약/month/recent UI. 월 이동/상세 drill-down은 남은 polish |
| Android Compose Settings | 완료에 가까움 | 식사 시간 편집, 알림 기본값, 권한 안내, 초기화, 가이드/면책 고지 |
| Android notification/exact alarm | 부분 완료 | #23, permission controller, scheduler adapter, receiver. Android 13+/14 실기기 QA 필요 |
| Android haptics | 완료 | routine haptic intent adapter, 복용 완료/저장/destructive/validation intent |
| Android 디자인 시스템 적용 | 완료 | #44, warm white/berry/coral/mint/ink Compose theme token, edge-to-edge inset |
| iOS shared framework CI | 완료 | #42, `.github/workflows/ios_kmp_ci.yml`, `SupplementRoutineShared` framework build |

## Flutter 대비 기능별 상태

| Flutter 기능 | KMP/Android 상태 | 남은 gap |
| --- | --- | --- |
| 오늘 복용 목록 | Android 구현됨 | 여러 데이터 상태에서 device screenshot QA 필요 |
| 복용 체크/해제 | Android 구현됨 | 앱 재시작 후 유지 QA와 접근성 label 점검 필요 |
| 영양제 목록 | Android 구현됨 | 삭제/알림 toggle/empty state screenshot QA 필요 |
| 영양제 추가/수정 | Android 구현됨 | validation error, 저장 완료 햅틱, 시간 선택 UX QA 필요 |
| 기록 요약 | Android 부분 구현 | 월 이동, 상세 기록 확인, empty/partial completion 상태 QA |
| 설정 | Android 구현됨 | 권한 상태별 문구와 초기화 flow 실기기 QA |
| 알림 | Android 부분 구현 | Android 13+ notification runtime permission dialog, Android 12+/14 exact alarm 설정 이동, 실제 발화 QA |
| 로컬 저장 | Android 구현됨 | migration 호환성은 Flutter cutover 전 별도 판단 필요 |
| 디자인 시스템 | Android theme 적용됨 | 화면별 세부 polish와 expanded-width QA |
| iOS | 미완료 | #24 shared module integration, SwiftUI shell, iOS fallback |
| 접근성/스크린샷 QA | 미완료 | #25 device/emulator screenshot, expanded-width, semantic label 점검 |

## 남은 Gap Issue

| 우선순위 | Issue | 이유 |
| --- | --- | --- |
| P0 | #24 `[iOS] KMP shared module integration과 SwiftUI shell 구현` | Android/iOS 모두에서 사용 가능한 앱이라는 목표를 만족하려면 iOS 최소 shell이 필요하다. |
| P0 | #25 `[QA] KMP Android/iOS parity screenshot과 accessibility 검증` | Android 구현이 진척된 만큼 실제 기기/해상도/접근성 기준으로 남은 UI gap을 확정해야 한다. |
| P1 | #47 `[QA] Android notification permission/exact alarm 실기기 검증` | Android 13+ permission dialog와 Android 12+/14 exact alarm 설정/발화는 기기 또는 emulator별 확인이 필요하다. |
| P1 | #48 `[Android] History 화면 release polish 범위 결정` | 월 이동/상세 기록 확인이 release 전에 필요한지 PRD 기준으로 확정해야 한다. |
| P2 | #49 `[Release] Flutter 기준 구현 cutover/removal 결정` | KMP parity 후 Flutter를 유지할지 제거할지 결정해야 한다. |

## 닫힌 Gap Issue

| Issue | 반영 |
| --- | --- |
| #20 Settings 화면과 권한 상태 UI | `SettingsScreen.kt`, `SupplementRoutineKmpApp.kt` |
| #21 Android local persistence | `AndroidRoutineDataStore.kt`, `AndroidRepositories.kt` |
| #22 영양제 추가/수정 flow | `SupplementsScreen.kt`, `SupplementFormPolicy.kt` |
| #23 Notification/exact alarm adapter | `AndroidNotificationPermissionController.kt`, `AndroidReminderScheduler.kt`, `SupplementReminderReceiver.kt` |
| #42 iOS KMP framework CI | `.github/workflows/ios_kmp_ci.yml`, `SupplementRoutineShared` framework |
| #44 Android Compose design token | `SupplementRoutineTheme.kt`, `MainActivity.kt`, `SupplementRoutineKmpApp.kt` |

## 권장 진행 순서

1. #24 iOS shared integration과 SwiftUI shell을 만든다.
2. #25 Android/iOS screenshot/accessibility QA를 수행한다.
3. #47에서 Android 13+ emulator와 연결된 Android 실기기로 notification permission/exact alarm/실제 알림 발화를 검증한다.
4. #48에서 History 화면 release polish 범위를 확정한다.
5. Flutter 기준 구현과 KMP 구현의 cutover 조건을 정리한다.
6. 릴리즈 서명, store asset, privacy/disclaimer, 버전 정책을 마지막 release readiness 문서로 묶는다.

## 현재 제한 사항

- iOS는 아직 `kmp/iosApp/README.md`와 shared framework CI 수준이다. SwiftUI shell 구현은 #24에 남아 있다.
- Windows에서는 Xcode/iOS simulator를 직접 실행할 수 없다. 무료 검증은 GitHub-hosted macOS runner의 framework build까지이며, 실기기 iOS QA는 Mac/Xcode 또는 signing/provisioning 가능한 macOS runner가 필요하다.
- Android 12 실기기에서는 Android 13+ notification permission dialog를 검증할 수 없다. Android 13+ emulator가 필요하다.
- Flutter 구현은 아직 삭제 대상이 아니다. KMP parity와 QA가 끝날 때까지 기준 구현으로 유지한다.

## 검증 기준

Parity에 도달했다고 판단하려면 다음을 만족해야 한다.

- Flutter 기준 user flow의 주요 경로가 Android KMP 앱에서 동작한다.
- 앱 재시작 후 영양제, 기록, 식사 시간, 알림 기본값이 유지된다.
- Android notification runtime permission과 exact alarm permission이 분리되어 안내된다.
- iOS는 shared module을 import하고 최소 Today/Supplements/History/Settings shell을 제공한다.
- iOS는 Android-only 기능에 대해 명확한 fallback을 제공한다.
- Today checklist item과 History calendar tile은 상태를 색상만으로 전달하지 않는다.
- phone과 expanded-width 화면에서 navigation과 주요 목록이 겹치지 않는다.
- CI는 Flutter, KMP Android/shared, iOS shared framework를 모두 검증한다.
