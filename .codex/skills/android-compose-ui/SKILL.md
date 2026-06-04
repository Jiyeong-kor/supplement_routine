---
name: supplement-routine-android-compose-ui
description: Supplement Routine 저장소 안에서만 사용하는 Android Jetpack Compose UI 규칙. 기존 Flutter 디자인 시스템, 스크린샷, IA를 Material 3 Compose 화면으로 옮길 때 사용한다.
---

# Supplement Routine Android Compose UI

이 skill은 이 저장소 안에서만 사용한다. Android UI는 Flutter widget을 1:1 복사한 느낌이 아니라, 네이티브 Material 3 루틴 도구처럼 느껴져야 한다. 아키텍처, SSOT, MVVM, state hoisting 판단은 `supplement-routine-android-architecture` skill을 함께 따르고, 화면 UX 판단은 `supplement-routine-ux-review` skill을 함께 따른다.

## 기준 자료

- 제품 기준: `docs/prd.md`
- 디자인 토큰과 규칙: `docs/design_system.md`
- 정보 구조: `docs/information_architecture.md`
- 사용자 흐름: `docs/user_flow.md`
- 시각 참고 자료: `docs/assets/screenshots/`
- Flutter theme/components: `lib/app/`
- Flutter feature screens: `lib/features/`

## Compose 디자인 규칙

- Material 3 Compose component와 theme token을 사용한다.
- 앱은 차분하고, 스캔하기 쉽고, 반복 사용에 적합해야 한다.
- 최상위 destination 4개를 유지한다: Today, Supplements, History, Settings.
- phone에서는 compact bottom navigation, expanded width에서는 가능한 경우 navigation rail을 사용한다.
- 목록에는 `LazyColumn` 또는 `LazyVerticalGrid`를 우선 사용하고, 사용자 데이터에는 stable item key를 둔다.
- card는 가볍게 유지한다: surface color, outline, 명확한 hierarchy, 최소 shadow.
- action density를 안전하게 유지한다. destructive action은 보통 overflow/menu 또는 confirmation flow 안에 둔다.
- 한국어 기준으로 text sizing을 확인한다. 긴 한국어 label이 icon/button과 충돌하면 안 된다.

## 화면 우선순위

1. Today: 다음 행동, 완료 상태, 체크 ergonomics, FAB/list overlap 방지.
2. Supplements: 읽기 쉬운 복용 규칙, notification state, edit flow, 안전한 삭제.
3. History: 완료 개요, 월간 calendar 명확성, 접근 가능한 상태 label.
4. Settings: permission state 명확성, meal time editing, data reset confirmation.

## UI 개선 목표

- content padding, inset handling, 또는 다른 add action placement를 사용해 Today 마지막 item과 FAB가 겹치지 않게 한다.
- 오늘의 첫 task를 보는 데 방해가 된다면 과하게 큰 hero spacing을 줄인다.
- supplement card의 primary action을 더 명확히 한다. 같은 무게의 trailing icon 3개를 피한다.
- checked/unchecked intake row와 history calendar status에 semantic label을 추가한다.
- color 의미는 text 또는 icon으로 보강하고, color만으로 전달하지 않는다.

## 검증

눈에 보이는 UI 변경이 있을 때:

- 가능한 Android/Compose test를 실행한다.
- 실행 가능한 앱이 있으면 phone과 expanded-width screenshot을 캡처한다.
- pixel-perfect Flutter parity가 아니라 `docs/assets/screenshots/`의 의도와 비교한다.
- Compose theme이 생기면 dark mode를 확인한다.
