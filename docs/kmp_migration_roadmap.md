# KMP 마이그레이션 로드맵

이 문서는 `docs/prd.md`의 제품 범위를 바꾸지 않고 Flutter 기준 구현을 KMP shared, Android Compose, iOS shell로 옮기는 순서를 정의합니다.

## 목적

Supplement Routine의 현재 Flutter 앱을 안정 기준으로 유지하면서, Android는 Kotlin Native Jetpack Compose 앱으로 전환하고 iOS는 Kotlin Multiplatform shared module을 사용하는 앱으로 확장합니다.

이 전환의 목표는 제품 범위를 바꾸는 것이 아니라, 이미 정의된 루틴 확인, 복용 체크, 영양제 관리, 기록, 설정 흐름을 Android와 iOS에서 더 네이티브하게 유지보수할 수 있는 구조로 옮기는 것입니다.

## 기준 구현

마이그레이션 중에는 Flutter 앱이 기준 구현입니다.

- 제품 범위: `docs/information_architecture.md`
- 제품 기준: `docs/prd.md`
- 디자인 방향: `docs/design_system.md`
- 사용자 흐름: `docs/user_flow.md`
- 현재 UI 참고: `docs/assets/screenshots/`
- Flutter source: `lib/`

KMP 구현이 기능 parity에 도달하기 전까지 Flutter 코드를 삭제하지 않습니다.

## 아키텍처 방향

### Shared KMP

`commonMain`에 들어갈 항목:

- Domain model: Supplement, IntakeRecord, MealTimeSettings
- Enum/value type: intake method, intake condition, meal type, schedule label
- Pure logic: schedule generation, date grouping, history summary, form policy
- Repository contract: supplement, intake record, settings persistence interface
- View state contract: 화면이 소비하기 쉬운 상태 모델

플랫폼별로 둘 항목:

- Local storage actual implementation
- Notification permission and scheduling
- Android exact alarm permission
- iOS notification authorization and scheduling
- Android widget or startup integration

### Android

Android는 Jetpack Compose와 Material 3를 사용합니다.

- Flutter 화면을 1:1 복제하지 않고 `docs/design_system.md`의 의도를 Compose token/component로 재현합니다.
- Today, Supplements, History, Settings 순서로 화면을 이식합니다.
- Notification, exact alarm, widget처럼 플랫폼 API가 큰 기능은 shared logic parity 이후에 붙입니다.

### iOS

iOS는 먼저 shared module 연동이 되는 shell을 만듭니다.

- 초기 목표는 앱 실행, shared state 호출, 기본 화면 연결입니다.
- Android Compose parity가 안정된 뒤 SwiftUI 화면 확장 또는 Compose Multiplatform shared UI 여부를 다시 판단합니다.
- Android-only 기능은 iOS에서 명확한 fallback 또는 별도 구현을 둡니다.

## 작업 단계

### 1. Roadmap and Workflow

Branch: `kmp/roadmap-docs`

- 프로젝트 전용 Codex skill 추가
- KMP 마이그레이션 로드맵 추가
- GitHub issue template 추가
- PR template 추가

완료 기준:

- 이후 작업자가 같은 branch, issue, PR 기준을 따른다.
- Flutter 기준 구현과 KMP 전환 원칙이 문서화되어 있다.

### 2. KMP Scaffold

Branch: `kmp/scaffold`

- KMP 프로젝트 골격 추가
- Android app module과 shared module 구성
- iOS app shell 구성
- 기존 Flutter 앱과 병렬로 유지

완료 기준:

- Android KMP app이 기본 화면으로 빌드된다.
- iOS shell이 shared module을 참조할 수 있다.
- Flutter CI가 깨지지 않는다.

### 3. Shared Domain

Branch: `kmp/shared-domain`

- Flutter model을 Kotlin domain model로 이식
- 직렬화와 persistence DTO 경계 정의
- domain unit test 추가

완료 기준:

- Flutter 모델의 핵심 필드와 의미가 Kotlin 모델에 반영된다.
- pure domain test가 통과한다.

### 4. Shared Scheduling and History

Branch: `kmp/shared-scheduling`

- schedule generation 이식
- date/time utility 이식
- history summary 계산 이식
- form policy 이식

완료 기준:

- 기존 Flutter mock data 기준의 결과와 Kotlin test 결과가 일치한다.
- 시간대와 날짜 경계 테스트가 포함된다.

### 5. Android Compose Screens

Branches:

- `android/compose-today`
- `android/compose-supplements`
- `android/compose-history`
- `android/compose-settings`

화면 이식 순서:

1. Today
2. Supplements
3. History
4. Settings

완료 기준:

- 각 화면은 shared state를 소비한다.
- Empty, loading, error, success, disabled 상태가 있다.
- 한국어 텍스트와 접근성 라벨을 확인한다.
- visible UI 변경은 스크린샷으로 검증한다.

### 6. Platform APIs

Branches:

- `android/notifications`
- `android/exact-alarm`
- `ios/notifications`
- `android/widget`

완료 기준:

- permission flow가 화면에 명확히 드러난다.
- 실패 또는 권한 거부 상태에 fallback이 있다.
- 플랫폼 API는 shared contract 뒤에 숨긴다.

### 7. Parity and Cutover

Branch: `kmp/parity-check`

- Flutter와 KMP 기능 비교
- 화면별 parity checklist 작성
- 남은 기능 gap issue 생성
- Flutter 제거 또는 유지 정책 결정

완료 기준:

- Today, Supplements, History, Settings 핵심 흐름이 Android에서 동작한다.
- iOS에서 shared module 기반 핵심 흐름이 확인된다.
- Flutter 기준 구현을 제거할지, 별도 유지할지 결정되어 있다.

## UI/UX 개선 항목

KMP 전환 중 함께 반영할 개선:

- Today 화면에서 FAB가 마지막 카드의 체크 영역과 겹치지 않게 한다.
- 첫 화면에서 다음 복용 항목이 더 빨리 보이도록 여백과 헤드라인 밀도를 조정한다.
- Supplement 카드의 알림, 편집, 삭제 액션 위계를 정리한다.
- History calendar는 색상만으로 상태를 전달하지 않고 텍스트/아이콘/접근성 라벨을 함께 둔다.
- Settings permission 상태는 사용자가 다음 행동을 바로 이해하도록 문구를 구체화한다.

## PR 원칙

- 한 PR은 하나의 작업 흐름만 다룬다.
- Flutter 기준 구현은 parity 전까지 유지한다.
- shared logic PR은 테스트를 포함한다.
- UI PR은 가능하면 스크린샷을 포함한다.
- platform API PR은 권한 거부, 실패, fallback 상태를 명시한다.

## CI 전환 전략

현재 CI는 Flutter 기준 구현을 보호하는 역할을 유지합니다.

- 기존 `.github/workflows/flutter_ci.yml`은 KMP scaffold가 들어오기 전까지 변경하지 않는다.
- 문서만 변경하는 PR에서도 Flutter CI가 계속 동작하도록 둔다.
- `kmp/scaffold` PR에서 KMP/Gradle 전용 workflow를 별도로 추가한다.
- KMP CI는 처음에는 Android와 shared JVM test를 우선 검증한다.
- iOS CI는 무료 GitHub-hosted macOS runner에서 KMP shared iOS simulator framework build와 SwiftUI shell build를 검증한다.
- Flutter와 KMP가 병렬로 존재하는 동안 CI도 Flutter job과 KMP job을 분리한다.
- Flutter 제거 또는 유지 결정 전까지 Flutter CI를 삭제하지 않는다.

KMP CI가 생긴 뒤 기본 job 후보:

- Gradle wrapper 검증
- KMP shared unit test
- Android lint 또는 compile check
- Android debug build
- iOS simulator framework build
- iOS SwiftUI shell simulator build
- iOS signing/provisioning이 필요한 실기기 build는 release 준비 단계에서 별도 추가
