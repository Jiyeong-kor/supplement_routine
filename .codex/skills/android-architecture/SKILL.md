---
name: supplement-routine-android-architecture
description: Supplement Routine 저장소 안에서만 사용하는 Android/KMP 아키텍처 규칙. Android Jetpack Compose, KMP shared, 멀티 모듈, SSOT, Clean Architecture, SOLID, MVVM, state hoisting, Kotlin convention, Android Developers 공식 문서 기준을 지켜 구현/리팩터링/리뷰할 때 사용한다.
---

# Supplement Routine Android Architecture

이 skill은 이 저장소 안에서만 사용한다. Android/KMP 구현을 추가하거나 리팩터링할 때 항상 SSOT, Clean Architecture, SOLID, MVVM, state hoisting, 멀티 모듈 구조를 기준으로 판단한다.

## 공식 기준

작업 전에 관련 Android Developers / Kotlin 공식 문서를 확인한다. PR 설명에는 참고한 공식 문서나 그 문서의 원칙을 간단히 적는다.

- Android app architecture: `https://developer.android.com/topic/architecture`
- UI layer and state holders: `https://developer.android.com/topic/architecture/ui-layer/stateholders`
- ViewModel: `https://developer.android.com/topic/libraries/architecture/viewmodel`
- Compose architecture: `https://developer.android.com/develop/ui/compose/architecture`
- Compose state: `https://developer.android.com/develop/ui/compose/state`
- Compose state hoisting: `https://developer.android.com/develop/ui/compose/state-hoisting`
- Kotlin coding conventions: `https://kotlinlang.org/docs/coding-conventions.html`

## 아키텍처 선택

- 이 프로젝트의 Android UI 패턴은 MVVM을 기본으로 한다.
- 근거: Android Developers 문서에서 Compose 화면의 screen UI state 노출 수단으로 `ViewModel`을 권장하고, 현재 제품은 폼/목록/설정 중심의 CRUD 루틴 앱이라 MVI reducer/effect 체계보다 MVVM + unidirectional data flow가 더 단순하고 충분하다.
- MVI는 복잡한 one-off effect, event sourcing, 강한 action/reducer audit trail이 필요할 때만 별도 issue에서 근거를 남기고 도입한다.
- ViewModel은 UI state를 만들고 user intent를 application/domain use case로 위임한다. ViewModel에 Android `Context`, persistence detail, notification scheduling detail을 직접 넣지 않는다.
- Android 의존성 주입은 Hilt를 기준으로 한다. `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, `@Module/@InstallIn`을 사용하고, Compose route 안에서 repository/store/scheduler를 직접 생성하지 않는다.

## SSOT

- domain model의 진실은 KMP shared domain에 둔다.
- 저장된 데이터의 진실은 repository 구현에 둔다. 화면별 local copy나 sample data를 production path의 source of truth로 만들지 않는다.
- UI는 `UiState`를 렌더링하고 event callback을 올린다. UI 내부의 `remember` state는 text field editing, dialog open 여부, selected tab 같은 UI-only state에 제한한다.
- 같은 데이터를 Today, Supplements, History, Settings가 함께 쓰면 같은 repository/use case 흐름에서 파생한다.
- DTO, database entity, platform model은 domain model과 분리하고 mapper를 명시한다.

## Clean Architecture

의존 방향은 바깥에서 안쪽으로만 흐른다.

1. `domain`: entity, value object, repository interface, use case, pure policy.
2. `data`: repository implementation, DTO/entity mapper, local storage adapter.
3. `presentation`: ViewModel, UiState, UI event contract.
4. `ui`: Compose stateless screen/component.
5. `platform`: notification, permission, alarm, Android/iOS adapter.
6. `di`: Hilt module과 binding. concrete implementation 선택은 이 계층에서 한다.

규칙:

- domain은 Android/Compose/SQL/SharedPreferences/Room/DataStore에 의존하지 않는다.
- data는 domain interface를 구현한다.
- presentation은 use case/repository interface에 의존하고 concrete storage에 의존하지 않는다.
- UI는 ViewModel 타입 또는 hoisted state/event만 알고, repository를 직접 호출하지 않는다.
- platform API는 interface 뒤에 숨기고 Android-only 동작은 iOS fallback을 문서화한다.
- Android DI graph는 Hilt module에 두고, ViewModel은 interface에 의존한다. `Context`가 필요한 구현은 `@ApplicationContext` provider에서만 생성한다.

## 멀티 모듈 방향

새 구현은 한 모듈에 계속 쌓지 않는다. 현재 scaffold가 단순하더라도 다음 방향으로 쪼갤 수 있게 package와 dependency를 설계한다.

- `:shared`: KMP domain/data contract/pure logic.
- `:androidApp`: Android entry point와 app-level navigation.
- 향후 Android 모듈 후보:
  - `:android:core:designsystem`
  - `:android:core:data`
  - `:android:core:domain` 또는 shared domain facade
  - `:android:feature:today`
  - `:android:feature:supplements`
  - `:android:feature:history`
  - `:android:feature:settings`
  - `:android:platform:notification`
- 모듈 분리는 PR 하나의 목적이 명확하고 CI가 안정될 때 진행한다. 기능 구현과 대규모 모듈 이동을 같은 PR에 섞지 않는다.

## SOLID

- SRP: 화면, ViewModel, use case, repository, mapper, platform adapter의 책임을 분리한다.
- OCP: 새 복용 방식/알림 adapter/저장소 구현을 추가할 때 기존 domain 호출부 변경을 최소화한다.
- LSP: fake/in-memory repository와 production repository가 같은 contract를 만족해야 한다.
- ISP: repository interface는 화면 편의 메서드 덩어리가 아니라 aggregate별 작은 contract로 유지한다.
- DIP: presentation/domain은 concrete Android storage나 scheduler가 아니라 interface에 의존한다.

## Compose와 State Hoisting

- 화면 함수는 가능하면 stateless로 작성한다: `Screen(uiState, onEvent, modifier)`.
- route 함수에서 ViewModel을 얻고 state collection을 처리한다.
- leaf composable은 값과 callback만 받는다.
- `remember`는 UI-only transient state에만 사용하고, business state는 ViewModel/repository에서 온다.
- state는 필요한 가장 낮은 공통 조상으로 hoist한다. 여러 화면이 공유하면 repository/use case/ViewModel graph로 올린다.
- composable이 repository, scheduler, permission manager를 직접 호출하지 않는다.
- list/grid에는 stable key를 사용한다.
- 상태는 색상만으로 전달하지 말고 text, icon, semantics를 함께 둔다.

## Kotlin Convention

- Kotlin 공식 coding convention을 따른다.
- package path와 file path를 맞춘다.
- public API는 의도가 드러나는 이름을 쓴다.
- `const val`/불변 top-level 상수는 screaming snake case를 사용한다.
- nullable은 domain 의미가 있을 때만 사용하고, 빈 문자열/빈 list와 null의 의미를 섞지 않는다.
- extension 함수는 domain 언어를 명확히 만들 때만 추가한다.
- 복잡한 `when`/mapper는 test를 붙인다.

## 검증 체크리스트

Android/KMP PR 전 확인한다.

- SSOT가 어디인지 PR 설명에 드러난다.
- domain/data/presentation/ui/platform 책임이 섞이지 않는다.
- ViewModel 또는 state holder가 UI state를 만들고, composable은 hoisted state를 렌더링한다.
- shared pure logic에는 KMP test가 있다.
- Android UI는 Compose 기반이고 Android Developers 공식 원칙과 어긋나지 않는다.
- Android dependency graph는 Hilt를 사용하고, manual ViewModel factory나 composable 내부 concrete repository 생성이 없다.
- Kotlin convention 위반이 없다.
- 실제 persistence나 platform API를 붙이면 fake/test double도 같은 contract로 검증한다.
- KMP/Flutter CI를 모두 확인한다.
