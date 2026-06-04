# Tech Stack

작성일: 2026-06-04

이 문서는 `docs/prd.md`의 제품 목표와 플랫폼 요구사항을 구현하기 위한 기술 선택을 설명한다.

이 문서는 Supplement Routine 프로젝트가 어떤 기술을 어디에, 왜 사용하는지 설명한다. 현재 앱은 Flutter 구현을 기준 구현으로 유지하면서, KMP shared와 Android Jetpack Compose 앱으로 단계적으로 전환 중이다.

## 한눈에 보는 구조

| 영역 | 기술 | 위치 | 상태 | 선택 이유 |
| --- | --- | --- | --- | --- |
| 기준 앱 | Flutter, Dart | `lib/`, `android/`, `ios/`, `windows/` | 운영 기준 | 이미 완성된 사용자 흐름과 테스트가 있고, KMP parity 전까지 제품 기준 역할을 한다. |
| Flutter 상태 관리 | Riverpod | `lib/features/*/*_provider.dart` | 사용 중 | feature별 상태를 테스트하기 쉽고, provider 단위로 UI와 repository를 연결하기 좋다. |
| Flutter UI | Material 3, Custom theme, Pretendard | `lib/app/`, `lib/features/` | 사용 중 | 루틴 앱에 맞는 차분한 디자인 시스템과 한국어 가독성을 유지한다. |
| Flutter local persistence | SharedPreferences | `lib/features/*/data/local_*_repository.dart` | 사용 중 | 현재 데이터 규모가 작고 JSON 기반 로컬 저장으로 충분하다. |
| Flutter notification | `flutter_local_notifications`, `timezone`, `flutter_timezone` | `lib/core/services/intake_notification_service.dart`, `lib/core/services/intake_notification_copy.dart`, Android manifest/config | 사용 중 | 로컬 알림과 시간대 기반 예약 알림을 Flutter 기준 구현에서 처리한다. |
| Shared logic | Kotlin Multiplatform | `kmp/shared/` | 전환 중 | Android/iOS가 같은 domain, scheduling, history logic을 사용하게 한다. |
| Android native UI | Jetpack Compose, Material 3 | `kmp/androidApp/` | 전환 중 | Android 공식 UI toolkit 기반으로 네이티브 UX와 state hoisting을 적용한다. |
| Android app architecture | MVVM, SSOT, Clean Architecture | `.codex/skills/android-architecture/SKILL.md`, 향후 Android feature packages/modules | 기준 확정 | ViewModel + UiState + hoisted state가 이 앱의 CRUD/루틴 흐름에 단순하고 적합하다. |
| iOS native UI | SwiftUI + KMP shared | `kmp/iosApp/README.md` | 계획 | shared module을 iOS에서도 사용해 Android/iOS parity를 만든다. |
| CI | GitHub Actions | `.github/workflows/` | 사용 중 | Flutter 기준 구현과 KMP scaffold를 PR마다 검증한다. |

## 현재 기준 구현: Flutter

Flutter 앱은 KMP 전환이 끝나기 전까지 제품 동작의 기준이다.

| 기술 | 어디에 쓰나 | 왜 쓰나 |
| --- | --- | --- |
| Flutter `3.41.9` | 전체 기존 앱 UI와 Android debug build CI | 이미 앱 주요 화면과 테스트가 Flutter로 구현되어 있고, migration 중에도 사용자 흐름을 보존해야 한다. |
| Dart SDK `^3.11.5` | Flutter 앱 언어 | Flutter stable toolchain과 함께 사용한다. |
| Material 3 | `lib/app/app_theme.dart`, feature screens | Android/iOS에서 일관된 루틴 관리 UI를 제공한다. |
| Pretendard font | `assets/fonts/`, `pubspec.yaml` | 한국어 UI에서 가독성과 무게 계층을 안정적으로 맞춘다. |
| Riverpod `flutter_riverpod` | `today_provider`, `supplement_provider`, `settings_provider`, history providers | feature별 상태를 분리하고 widget/provider 테스트를 쉽게 한다. |
| SharedPreferences | local repository 구현 | 현재 데이터는 영양제 목록, 복용 기록, 식사 시간 설정처럼 작은 로컬 데이터라 단순 key-value 저장으로 충분하다. |
| `flutter_local_notifications` | `lib/core/services/intake_notification_service.dart` | Flutter 기준 앱에서 복용 알림을 예약하고 표시한다. |
| `timezone`, `flutter_timezone` | 알림 예약 시간대 처리 | 복용 알림이 사용자의 현지 시간 기준으로 동작해야 한다. |
| `intl`, Flutter localization | `lib/l10n/` | 한국어 문구와 날짜/시간 표시를 관리한다. |
| `flutter_lints` | `analysis_options.yaml` | Dart 코드 스타일과 기본 정적 분석 기준을 유지한다. |

## KMP Shared

KMP shared는 Android/iOS 공통 비즈니스 로직의 중심이다.

| 기술 | 어디에 쓰나 | 왜 쓰나 |
| --- | --- | --- |
| Kotlin Multiplatform `2.2.20` | `kmp/shared` | domain model, scheduling, history summary, DTO mapping을 Android/iOS에서 공유한다. |
| `commonMain` | `kmp/shared/src/commonMain` | 플랫폼과 무관한 순수 로직을 둔다. |
| Android target | `androidTarget()` | Android Compose 앱에서 shared domain/logic을 직접 사용한다. |
| iOS targets | `iosX64`, `iosArm64`, `iosSimulatorArm64` | iOS shell이 같은 shared module을 사용할 준비를 한다. |
| Kotlin test | `commonTest.dependencies { kotlin("test") }` | shared scheduling/history/DTO mapping을 플랫폼 독립적으로 검증한다. |

KMP shared에 두는 것:

- `Supplement`, `IntakeRecord`, `MealTimeSettings` 같은 domain model
- intake method, condition, meal type, schedule label
- 일정 계산 `SchedulingService`
- 기록 요약 `HistorySummaryService`
- DTO/domain mapper와 repository contract

KMP shared에 두지 않는 것:

- Compose UI
- Android permission, exact alarm, notification scheduler
- iOS-specific notification/storage API
- 영양제 추천, 복용량 조언 같은 의료 판단

## Android Native App

Android native 앱은 Jetpack Compose 기반으로 전환 중이다.

| 기술 | 어디에 쓰나 | 왜 쓰나 |
| --- | --- | --- |
| Android Gradle Plugin `8.11.1` | `kmp/build.gradle.kts` | Android application/library 빌드를 관리한다. |
| Kotlin Android `2.2.20` | `kmp/androidApp` | Android native 구현 언어를 Kotlin으로 통일한다. |
| Jetpack Compose compiler plugin | `org.jetbrains.kotlin.plugin.compose` | Kotlin 2.x 기반 Compose 컴파일을 처리한다. |
| JetBrains Compose plugin `1.11.0` | `kmp/androidApp` | Compose Material/UI dependency를 Gradle에서 사용한다. |
| Activity Compose `1.11.0` | Android entry point | `ComponentActivity`에서 Compose content를 렌더링한다. |
| Compose Material 3 | `kmp/androidApp/src/main/.../ui` | Android 공식 Material 3 기반 UI를 만든다. |
| Compose Material Icons Extended | navigation/action/status icons | 버튼과 상태 표현에 familiar icon을 사용한다. |
| Java 17 / JVM target 17 | `compileOptions`, `kotlinOptions` | AGP/Kotlin/CI toolchain과 맞춘다. |
| compileSdk/targetSdk `36`, minSdk `23` | `kmp/androidApp/build.gradle.kts` | 최신 Android API 기준으로 빌드하면서 지원 범위를 minSdk 23으로 유지한다. |

Android UI 구현 원칙:

- Jetpack Compose만 사용한다.
- 화면은 가능한 stateless composable로 두고, route/ViewModel에서 state를 공급한다.
- state hoisting을 적용해 `Screen(uiState, onEvent)` 형태를 우선한다.
- list/grid에는 stable key를 둔다.
- 상태는 색상만으로 전달하지 않고 text, icon, semantics를 함께 사용한다.
- Android Developers 공식 문서의 app architecture, ViewModel, Compose state/state hoisting 원칙을 따른다.

## Architecture

프로젝트의 Android/KMP 아키텍처 기준은 `.codex/skills/android-architecture/SKILL.md`에 고정했다.

| 원칙 | 적용 방식 | 이유 |
| --- | --- | --- |
| SSOT | domain truth는 KMP shared domain, 저장 truth는 repository implementation | 화면마다 다른 복사본이 생기면 Today/Supplements/History/Settings가 서로 어긋난다. |
| Clean Architecture | domain -> data -> presentation -> ui/platform 방향으로 의존성 관리 | 플랫폼 API와 UI가 domain 로직을 오염시키지 않게 한다. |
| SOLID | repository, use case, mapper, ViewModel, UI 책임 분리 | 기능 추가와 테스트 double 교체가 쉬워진다. |
| MVVM | ViewModel이 UiState를 만들고 Compose가 렌더링 | 이 앱은 목록/폼/설정 중심이라 MVI보다 단순하고 Android 공식 권장 흐름과 잘 맞는다. |
| State hoisting | composable 내부 business state 금지, UI-only state만 `remember` | 상태 추적과 테스트를 쉽게 한다. |
| Multi-module | shared와 Android app을 분리하고, 향후 feature/core/platform 모듈로 확장 | migration 중 큰 PR을 피하면서 의존 방향을 관리한다. |

MVI를 기본으로 선택하지 않은 이유:

- 현재 앱은 복잡한 event sourcing이나 reducer/effect audit trail보다 명확한 CRUD state와 form state가 중요하다.
- MVVM + unidirectional data flow + state hoisting으로 충분히 예측 가능한 구조를 만들 수 있다.
- MVI는 필요성이 생기면 별도 issue에서 이유와 범위를 정하고 도입한다.

## Persistence

| 단계 | 기술 | 상태 | 이유 |
| --- | --- | --- | --- |
| Flutter 기준 | SharedPreferences + JSON mapper | 사용 중 | 작은 로컬 데이터에는 단순하고 테스트하기 쉽다. |
| KMP shared | Repository interface + DTO mapper | 구현됨 | Android/iOS storage 구현이 같은 domain contract를 따르게 한다. |
| Android KMP | 아직 미구현 | 남은 gap | sample state를 제거하고 앱 재시작 후 데이터를 복원하려면 Android local adapter가 필요하다. |
| iOS KMP | 아직 미구현 | 남은 gap | iOS에서도 같은 repository contract를 구현해야 한다. |

다음 Android persistence 구현에서 선택할 후보:

- DataStore: key-value/protobuf 기반 설정과 작은 데이터 저장에 적합하다.
- Room: 관계형 query, 복잡한 history query, migration이 커질 때 적합하다.

현재 데이터 규모만 보면 DataStore가 먼저 검토할 만하지만, history query와 장기 migration 요구가 커지면 Room이 더 적합할 수 있다. 이 선택은 #21 구현 시 공식 Android storage guidance와 실제 query 요구를 기준으로 결정한다.

## Notification

| 단계 | 기술 | 상태 | 이유 |
| --- | --- | --- | --- |
| Flutter 기준 | `flutter_local_notifications`, `timezone`, `flutter_timezone` | 사용 중 | 기존 앱의 복용 알림 기준 구현이다. |
| KMP shared | 알림 대상 schedule 계산 contract | 부분 준비 | 알림 스케줄 자체는 shared schedule logic에서 파생할 수 있다. |
| Android native | Notification permission, exact alarm, scheduler adapter | 미구현 | Android 13+ notification permission과 exact alarm permission을 분리해야 한다. |
| iOS native | iOS notification adapter | 미구현 | iOS 권한/스케줄링 정책에 맞는 별도 adapter가 필요하다. |

Notification은 platform API 차이가 크므로 shared domain에 직접 넣지 않는다. shared는 “언제 알림이 필요하다”까지만 계산하고, 실제 권한/예약은 platform adapter가 담당한다.

## iOS 계획

현재 iOS KMP 쪽은 `kmp/iosApp/README.md` 수준이다.

목표 stack:

- SwiftUI: iOS native UI
- KMP shared framework: domain/scheduling/history/data contract 공유
- iOS platform adapter: local persistence, notification permission/scheduler

iOS에서 Android-only 기능이 없거나 정책이 다르면 UI에 fallback을 명확히 보여준다.

## CI/CD

| Workflow | 위치 | 하는 일 | 이유 |
| --- | --- | --- | --- |
| Flutter CI | `.github/workflows/flutter_ci.yml` | `flutter pub get --enforce-lockfile`, `flutter analyze`, `flutter test`, `flutter build apk --debug` | Flutter 기준 구현이 migration 중 깨지지 않게 한다. |
| KMP CI | `.github/workflows/kmp_ci.yml` | `gradle -p kmp clean :shared:check :androidApp:assembleDebug --no-daemon` | shared logic과 Android Compose scaffold가 빌드되는지 확인한다. |

CI toolchain:

- Ubuntu latest
- Temurin Java 17
- Flutter stable `3.41.9`
- Gradle `8.14`

## 문서와 Skill

| 문서/skill | 역할 |
| --- | --- |
| `docs/kmp_migration_roadmap.md` | Flutter에서 KMP로 옮기는 큰 순서 |
| `docs/kmp_parity_check.md` | 현재 parity 상태와 남은 gap |
| `.codex/skills/kmp-migration/SKILL.md` | KMP migration 작업 규칙 |
| `.codex/skills/android-compose-ui/SKILL.md` | Android Compose UI 구현 규칙 |
| `.codex/skills/android-architecture/SKILL.md` | SSOT, Clean Architecture, SOLID, MVVM, state hoisting, multi-module 규칙 |
| `.codex/skills/github-branch-pr/SKILL.md` | issue, branch, PR 운영 규칙 |

## 다음 기술 결정이 필요한 곳

| Issue | 결정할 것 |
| --- | --- |
| #21 | Android local persistence를 DataStore로 할지 Room으로 할지 결정 |
| #22 | form validation을 shared use case로 올릴지 Android presentation에 둘지 결정 |
| #23 | Android notification scheduler adapter 경계와 exact alarm fallback 결정 |
| #24 | iOS SwiftUI shell과 shared framework integration 방식 결정 |
| #25 | screenshot/accessibility QA 방식 결정 |

## 공식 참고 기준

- Android app architecture: `https://developer.android.com/topic/architecture`
- UI layer state holders: `https://developer.android.com/topic/architecture/ui-layer/stateholders`
- ViewModel: `https://developer.android.com/topic/libraries/architecture/viewmodel`
- Compose architecture: `https://developer.android.com/develop/ui/compose/architecture`
- Compose state: `https://developer.android.com/develop/ui/compose/state`
- Compose state hoisting: `https://developer.android.com/develop/ui/compose/state-hoisting`
- Kotlin coding conventions: `https://kotlinlang.org/docs/coding-conventions.html`
