---
name: supplement-routine-ux-review
description: Supplement Routine 저장소 안에서만 사용하는 UX review 규칙. Flutter 기준 화면이나 KMP Android Compose/iOS 화면을 설계, 리뷰, 리팩터링할 때 루틴 관리 앱에 맞는 UX audit, 정보 구조, 상태, 접근성, 카피, interaction polish를 점검한다.
---

# Supplement Routine UX Review

이 skill은 이 저장소 안에서만 사용한다. 웹 랜딩 페이지식 화려함이 아니라, 매일 반복해서 쓰는 루틴 관리 앱의 명확성, 안정감, 접근성, 빠른 체크 경험을 우선한다.

## Design Read

Supplement Routine은 의료 조언 앱이 아니라 영양제 복용 루틴을 관리하고 기록하는 앱이다.

- Audience: 반복적으로 복용 일정을 확인하고 완료 여부를 기록하는 개인 사용자.
- Vibe: 귀엽고 친근한 생활 앱, 신뢰, 빠른 스캔, 낮은 피로도.
- Density: phone 기준은 편안한 중간 밀도, tablet/expanded width는 정보 비교가 쉬울 만큼 효율적으로.
- Motion: 상태 전환과 터치 피드백은 명확하게, 장식적 motion은 최소화.

## 적용 범위

- Flutter 기준 화면 UX 평가
- Android Jetpack Compose 화면 설계/리팩터링
- iOS SwiftUI shell/화면 설계
- parity check, screenshot QA, accessibility QA
- PR review에서 UI/UX regression 검토

## 기준 자료

- 제품 기준: `docs/prd.md`
- 정보 구조: `docs/information_architecture.md`
- 사용자 흐름: `docs/user_flow.md`
- 디자인 시스템: `docs/design_system.md`
- Flutter 기준 화면: `lib/features/`
- 시각 기준: `docs/assets/screenshots/`
- KMP parity: `docs/kmp_parity_check.md`
- Tech stack: `docs/tech_stack.md`

## UX 원칙

- 사용자가 가장 빨리 해야 할 일은 “오늘 먹을 것 확인”과 “먹은 항목 체크”다.
- 화면마다 primary task를 하나로 분명히 한다.
- 사용자가 같은 정보를 여러 탭에서 보더라도 숫자, 상태, 문구가 서로 어긋나면 안 된다.
- 중요한 상태는 color만으로 전달하지 않고 text, icon, semantics를 함께 쓴다.
- destructive action은 우발 실행을 막는다. 보통 overflow/menu 또는 confirmation flow 안에 둔다.
- empty state는 사용자가 다음 행동을 알 수 있어야 한다.
- loading/error/disabled/success 상태를 실제 흐름에 맞게 제공한다.
- 한국어 문구는 짧고 직접적으로 쓴다. 과장된 마케팅 문구를 넣지 않는다.
- 햅틱은 매일 반복 사용 피로도를 낮추기 위해 중요한 상태 변화에만 짧고 일관되게 사용한다.
- 브랜드 은유는 작은 루틴 조각을 하루 보드에 붙이고 체크하는 느낌을 우선한다.

## 화면별 UX 체크

### Today

- 오늘 날짜와 루틴 문구가 첫 화면에서 과하게 공간을 차지하지 않는다.
- 다음 복용 항목과 미완료 항목이 빠르게 보인다.
- 체크/해제 터치 영역이 충분히 크다.
- 완료 상태는 check icon, text/semantics, surface 변화로 함께 전달한다.
- FAB 또는 추가 버튼이 마지막 복용 항목을 가리지 않는다.
- 복용 시간이 정렬되고 오전/오후 표기가 한국어 사용자에게 명확하다.

### Supplements

- 카드에서 이름, 복용 방식, 하루 횟수, 복용량, 알림 상태가 빠르게 스캔된다.
- 알림 on/off는 icon만이 아니라 text 또는 tooltip/semantics로 설명한다.
- 수정/삭제/알림 toggle의 시각적 무게가 같아서 primary action이 흐려지지 않게 한다.
- 삭제는 확인 흐름이 있어야 한다.
- 영양제 추가 flow는 저장 버튼이 항상 찾기 쉬워야 한다.

### History

- 오늘 완료율과 최근 기록이 숫자만으로 끝나지 않고 “몇 개 중 몇 개 완료”로 설명된다.
- 월간 calendar tile은 낮음/보통/높음/없음을 color와 icon/semantics로 함께 표현한다.
- 오늘 날짜가 구분된다.
- empty state는 기록할 일정이 없는 상태와 아직 기록이 없는 상태를 혼동하지 않는다.
- percent 숫자는 tabular 또는 정렬이 흔들리지 않는 표현을 우선한다.

### Settings

- 알림 기본값과 실제 권한 상태를 분리해서 보여준다.
- Android notification permission과 exact alarm permission을 혼동하지 않는다.
- 식사 시간 편집은 Today schedule에 영향을 준다는 맥락이 드러난다.
- 데이터 초기화는 confirmation과 결과 피드백이 필요하다.
- 면책 고지는 찾을 수 있어야 하지만 앱 사용을 방해하지 않는다.

## Product UI Anti-Slop

이 프로젝트에서 피한다.

- AI-purple gradient 장식, 의미 없는 glow, bokeh/orb 배경.
- 모든 정보를 같은 카드 weight로 나열하는 화면.
- 세 개 이상의 trailing icon을 같은 무게로 붙이는 카드.
- “완벽한 건강 관리”, “최적의 루틴” 같은 의료/효능을 암시하는 과장 문구.
- 색상 하나만으로 완료/실패/주의 상태를 전달하는 calendar나 checklist.
- placeholder action이 실제 동작처럼 보이는 dead button.
- empty state가 단순히 “없음”으로 끝나는 화면.
- 화면마다 다른 radius, shadow, chip style을 쓰는 불안정한 디자인.

## Interaction과 State

- 터치 가능한 요소는 Material 기본 touch target을 유지한다.
- press/selected/disabled/focused state가 있어야 한다.
- form validation은 저장 후가 아니라 입력 중 또는 저장 시점에 사용자가 이해할 수 있게 표시한다.
- snackbar/toast는 transient feedback에만 사용하고, 중요한 오류는 화면 안에 남긴다.
- 네트워크 의존이 없는 local-first 앱이라도 persistence failure/error path는 UI state로 고려한다.
- placeholder action은 disabled, “준비 중”, 또는 별도 issue 링크로 명확히 표현한다.
- 복용 체크 완료, 저장 완료, destructive confirm, validation error에만 햅틱 intent를 부여한다. 체크 해제와 단순 탭/탭 전환은 기본적으로 햅틱을 생략한다.
- Android는 `View.performHapticFeedback` 또는 Compose의 view 기반 feedback을 우선하고, iOS는 같은 intent를 SwiftUI/UIKit 햅틱으로 매핑한다.

## Accessibility

- Compose에서는 `contentDescription`, `semantics`, role을 필요한 곳에 명시한다.
- checkbox-like row는 상태와 대상 이름이 함께 읽혀야 한다.
- icon-only button은 content description 또는 tooltip을 가진다.
- calendar tile은 날짜와 상태를 읽을 수 있어야 한다.
- 색 대비는 Material color pair를 우선 사용한다.
- dynamic type/font scale에서 한국어 label이 잘리지 않게 한다.

## Layout

- phone은 bottom navigation을 우선한다.
- expanded width에서는 가능한 경우 navigation rail과 2-column layout을 검토한다.
- 목록은 `LazyColumn`/`LazyVerticalGrid`를 사용하고 stable key를 둔다.
- 본문은 지나치게 넓어지지 않게 max width를 둔다.
- FAB가 있는 화면은 bottom padding/inset을 확보한다.
- 카드 안에 카드가 들어가는 구조를 피한다.

## Copy

- 한국어는 짧고 직접적으로 쓴다.
- 버튼은 동사 중심으로 쓴다: `저장`, `삭제`, `수정`, `알림 켜기`.
- 오류는 사용자가 다음에 할 일을 알려준다.
- 성공 문구는 과장하지 않는다.
- 의료 조언처럼 보이는 표현을 피한다.

## UX Review 절차

UI 작업 전/후에 다음 순서로 확인한다.

1. 이 화면의 primary task를 한 문장으로 말한다.
2. Flutter 기준 화면, IA, user flow를 확인한다.
3. empty/loading/error/disabled/success 상태를 확인한다.
4. color-only state 전달이 없는지 확인한다.
5. text overflow, 한국어 label, touch target, FAB overlap을 확인한다.
6. 접근성 label과 icon-only action 설명을 확인한다.
7. screenshot을 캡처할 수 있으면 phone과 expanded width를 비교한다.
8. PR 설명에 UX 기준과 의도적으로 남긴 gap을 적는다.

## 다른 Skill과의 관계

- 아키텍처와 state ownership은 `supplement-routine-android-architecture`를 따른다.
- Compose 구현 세부 규칙은 `supplement-routine-android-compose-ui`를 따른다.
- migration 순서와 parity 판단은 `supplement-routine-kmp-migration`을 따른다.
- issue/branch/PR 운영은 `supplement-routine-github-branch-pr`를 따른다.
