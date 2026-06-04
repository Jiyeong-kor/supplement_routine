# KMP Parity Check

작성일: 2026-06-03

이 문서는 `docs/prd.md`의 제품 요구사항을 기준으로 Flutter 구현과 KMP shared / Android Compose / iOS 전환 상태를 비교한다.

이 문서는 현재 Flutter 기준 앱과 KMP shared / Android Compose 전환 상태를 비교하고, 남은 gap을 추적 가능한 issue로 정리한다. Flutter 구현은 KMP parity가 검증되기 전까지 기준 구현으로 유지한다.

## 현재 기준

- Flutter 기준 source: `lib/`
- KMP shared source: `kmp/shared/`
- KMP Android source: `kmp/androidApp/`
- 제품 기준: `docs/prd.md`
- 제품 IA: `docs/information_architecture.md`
- 디자인 시스템: `docs/design_system.md`
- 현재 화면 참고: `docs/assets/screenshots/`
- 마이그레이션 로드맵: `docs/kmp_migration_roadmap.md`

## 완료된 범위

| 영역 | 상태 | 근거 |
| --- | --- | --- |
| KMP project scaffold | 완료 | `kmp/settings.gradle.kts`, `kmp/shared`, `kmp/androidApp`, `kmp/iosApp/README.md` |
| Shared domain model | 완료 | `Supplement`, `IntakeRecord`, `MealTimeSettings`, intake enum, schedule label |
| Shared scheduling logic | 완료 | `SchedulingService`, `TimeUtils`, shared tests |
| Shared history summary logic | 완료 | `HistorySummaryService`, recent/month summary tests |
| Shared persistence contract | 완료 | DTO/domain mapping, repository interface, mapping tests |
| Android Compose app shell | 부분 완료 | Bottom Navigation과 Today/Supplements/History route 연결 |
| Android Compose Today | 부분 완료 | shared scheduling 기반 목록/진행률/토글 UI. 실제 persistence는 미연결 |
| Android Compose Supplements | 부분 완료 | shared domain sample state 기반 목록 UI. form/persistence는 미연결 |
| Android Compose History | 부분 완료 | shared history summary 기반 요약/month/recent UI. 실제 persistence는 미연결 |

## Flutter 대비 기능별 상태

| Flutter 기능 | KMP/Android 상태 | 남은 gap |
| --- | --- | --- |
| 오늘 복용 목록 | 부분 완료 | sample state 제거, 저장된 기록 복원, 실제 add flow 연결 |
| 복용 체크/해제 | 부분 완료 | 앱 재시작 후 유지, repository mutation 연결 |
| 영양제 목록 | 부분 완료 | 실제 CRUD, 알림 toggle mutation, 삭제 확인 flow |
| 영양제 추가/수정 | 미완료 | Compose form, validation, 시간 선택 UI |
| 기록 요약 | 부분 완료 | 실제 기록 저장소 연결, 월 이동/상세 상호작용 |
| 설정 | 미완료 | 식사 시간 편집, 기본 알림, 권한 안내, 초기화, 가이드/면책 고지 |
| 알림 | 미완료 | Android notification permission, exact alarm permission, scheduler adapter |
| 로컬 저장 | 미완료 | Android persistence adapter, migration 호환성, 앱 재시작 복원 |
| iOS | 미완료 | shared module integration, SwiftUI shell, iOS fallback |
| 접근성/스크린샷 QA | 미완료 | device/emulator screenshot, expanded-width 확인, semantic label 점검 |

## 남은 Gap Issue

| 우선순위 | Issue | 이유 |
| --- | --- | --- |
| P0 | #21 `[Android] KMP shared repository 기반 local persistence 구현` | sample state를 제거하고 앱 재시작 후 상태 유지가 되어야 실제 parity 검증이 가능하다. |
| P0 | #22 `[Android] Compose 영양제 추가/수정 flow 구현` | 영양제 등록/수정은 Today와 Supplements parity의 핵심 입력 흐름이다. |
| P1 | #20 `[Android] Compose Settings 화면과 권한 상태 UI 구현` | 식사 시간 변경과 권한 안내가 Today schedule과 알림 UX에 영향을 준다. |
| P1 | #23 `[Android] Notification permission과 exact alarm adapter 구현` | Flutter Android의 알림 기능을 KMP Android로 옮기는 platform gap이다. |
| P1 | #24 `[iOS] KMP shared module integration과 SwiftUI shell 구현` | KMP 목표인 Android/iOS 사용 가능 상태를 만들기 위한 iOS 최소 통합이다. |
| P2 | #25 `[QA] KMP Android/iOS parity screenshot과 accessibility 검증` | 화면 구현 이후 실제 기기/해상도/접근성 기준으로 gap을 다시 확인해야 한다. |

## 권장 진행 순서

1. #21 Android local persistence를 먼저 연결한다.
2. #22 영양제 추가/수정 flow를 구현해 Today/Supplements/History 상태 변화를 실제 데이터로 확인한다.
3. #20 Settings를 구현하고 식사 시간 변경이 Today schedule에 반영되는지 검증한다.
4. #23 Android notification/exact alarm adapter를 붙인다.
5. #24 iOS shared integration과 SwiftUI shell을 만든다.
6. #25 screenshot/accessibility QA로 최종 parity gap을 재검토한다.

## 현재 제한 사항

- Android Compose 화면은 현재 sample state를 사용한다. production parity로 보려면 #21이 먼저 완료되어야 한다.
- Android device/emulator가 연결되어 있지 않아 이 단계에서는 screenshot 검증을 수행하지 못했다.
- iOS는 scaffold 문서 수준이며, shared module을 실제 SwiftUI 앱에서 호출하는 작업은 #24에 남아 있다.
- Flutter 구현은 아직 삭제 대상이 아니다. KMP parity와 QA가 끝날 때까지 기준 구현으로 유지한다.

## 검증 기준

Parity에 도달했다고 판단하려면 다음을 만족해야 한다.

- Flutter 기준 user flow의 주요 경로가 Android KMP 앱에서 동작한다.
- 앱 재시작 후 영양제, 기록, 식사 시간, 알림 기본값이 유지된다.
- Android notification runtime permission과 exact alarm permission이 분리되어 안내된다.
- iOS는 Android-only 기능에 대해 명확한 fallback을 제공한다.
- Today checklist item과 History calendar tile은 상태를 색상만으로 전달하지 않는다.
- phone과 expanded-width 화면에서 navigation과 주요 목록이 겹치지 않는다.
