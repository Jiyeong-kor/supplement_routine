---
name: supplement-routine-kmp-migration
description: Supplement Routine 저장소 안에서만 사용하는 KMP 마이그레이션 규칙. 기존 Flutter 기능을 제품 범위 손실 없이 KMP shared domain, data, scheduling, notification contract, Android UI, iOS integration으로 옮길 때 사용한다.
---

# Supplement Routine KMP 마이그레이션

이 skill은 이 저장소 안에서만 사용한다. KMP 구현이 parity에 도달하기 전까지 현재 Flutter 앱, 문서, 스크린샷, 테스트를 기준 구현으로 취급한다.

## 저장소 기준점

- Flutter source: `lib/`
- 제품 문서: `docs/information_architecture.md`, `docs/design_system.md`, `docs/user_flow.md`
- 현재 스크린샷: `docs/assets/screenshots/`
- CI 기준: `.github/workflows/flutter_ci.yml`

## 마이그레이션 순서

1. 현재 Flutter 앱을 동작 기준으로 보존한다.
2. Flutter 앱 옆에 KMP 구조를 병렬로 추가한다. 초기 마이그레이션 중에는 Flutter 코드를 삭제하지 않는다.
3. 공통 모델부터 옮긴다: `Supplement`, `IntakeRecord`, `MealTimeSettings`, intake method/condition/labels.
4. 순수 로직을 그다음에 옮긴다: schedule generation, date/time utilities, history summaries, form policies.
5. 플랫폼 저장소 구현보다 shared repository/interface를 먼저 정의한다.
6. shared logic 테스트가 생긴 뒤 shared state를 소비하는 Android Compose 화면을 만든다.
7. iOS shell과 shared module integration은 일찍 추가하되, Android parity가 안정된 뒤 UI를 확장한다.
8. notification, exact alarm, widget, startup behavior 같은 플랫폼 API는 마지막에 옮긴다.

## Shared Module 규칙

- 재사용 가능한 비즈니스 로직은 `commonMain`에 둔다.
- `expect/actual`은 notification permission, local storage, time zone, alarm scheduling처럼 실제 플랫폼 차이가 있는 경우에만 사용한다.
- 의료 조언, 영양제 추천, 복용량 가이드는 shared logic에 넣지 않는다. 이 제품은 루틴 관리 도구다.
- 플랫폼 persistence 타입을 domain model에 흘리지 말고 DTO/domain mapping을 명시한다.
- UI가 의존하기 전에 이식된 pure behavior에는 KMP unit test를 붙인다.

## Parity 체크리스트

마이그레이션한 기능을 완료로 보기 전에 확인한다:

- 한국어 문구와 포맷이 Flutter 동작 또는 문서화된 제품 의도와 맞는다.
- Empty, loading, error, disabled, success 상태가 표현되어 있다.
- checklist item, history calendar tile처럼 상태가 중요한 UI에는 accessibility label이 있다.
- 각 지원 플랫폼에서 앱 재시작 후 local persistence가 유지된다.
- Android는 notification runtime permission과 exact alarm permission을 분리해서 처리한다.
- Android-only 기능이 iOS에 없을 때 명확한 fallback이 있다.

## 하지 말 것

- 마이그레이션 중 제품 범위를 다시 쓰지 않는다.
- 대체 화면이 검증되기 전까지 Flutter 구현을 제거하지 않는다.
- 아키텍처가 Compose Multiplatform shared UI를 명시적으로 선택하기 전에는 플랫폼 UI 코드를 무리하게 공유하지 않는다.
- scaffolding, business logic, UI, platform API를 한 PR에 섞은 큰 migration PR을 만들지 않는다.
