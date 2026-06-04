# Supplement Routine PRD

작성일: 2026-06-04

이 문서는 Supplement Routine의 제품 기준 문서입니다. 정보 구조, 사용자 흐름, 디자인 시스템, KMP 마이그레이션, 기술 결정은 이 PRD의 제품 범위를 기준으로 해석합니다.

## 1. 제품 요약

Supplement Routine은 사용자가 직접 정한 영양제 복용 루틴을 확인하고, 복용 여부를 기록하고, 최근 수행 상태를 되돌아볼 수 있게 돕는 local-first 루틴 관리 앱입니다.

이 앱은 의료 조언, 영양제 추천, 복용량 판단을 제공하지 않습니다. 사용자가 이미 정한 루틴을 잊지 않고 반복할 수 있게 만드는 것이 핵심입니다.

## 2. 문제 정의

사용자는 영양제를 여러 개 복용할수록 다음 문제를 겪습니다.

- 오늘 어떤 영양제를 언제 먹어야 하는지 매번 기억하기 어렵다.
- 먹었는지 안 먹었는지 헷갈려 중복 복용하거나 누락할 수 있다.
- 복용 규칙이 식사 기준, 특정 시각, 일정 간격처럼 서로 다르면 한 화면에서 파악하기 어렵다.
- 최근에 루틴을 얼마나 지켰는지 확인하기 어렵다.
- 알림과 식사 시간 기준이 실제 루틴에 어떻게 반영되는지 알기 어렵다.

## 3. 제품 목표

1. 사용자가 앱을 열고 3초 안에 오늘 할 일을 이해한다.
2. 복용 체크는 한 번의 탭으로 끝난다.
3. 영양제 등록/수정 flow는 건강 지식이 없어도 완료할 수 있다.
4. 일정, 기록, 설정은 같은 source of truth에서 파생되어 화면 간 상태가 어긋나지 않는다.
5. Android와 iOS에서 KMP shared domain/data/scheduling 로직을 공유해 기능 parity를 유지한다.

## 4. 대상 사용자

주요 사용자는 영양제 복용을 개인적으로 관리하려는 사용자입니다.

- 하루에 하나 이상의 영양제를 반복 복용한다.
- 식사 전후, 특정 시각, 일정 간격 등 복용 규칙이 있다.
- 알림을 받고 싶지만 앱이 과하게 개입하는 것은 원하지 않는다.
- 의료적 추천보다 자신이 정한 루틴을 정확히 기록하는 것을 원한다.

## 5. 범위

### 포함

- 영양제 등록, 수정, 삭제
- 복용량, 단위, 메모, 알림 여부 관리
- 식사 기준 복용 규칙
- 특정 시각 복용 규칙
- 일정 간격 복용 규칙
- 오늘 복용 일정 생성
- 복용 체크와 체크 해제
- 오늘 진행률
- 최근 기록과 월간 완료 상태
- 식사 시간 설정
- 알림 기본값 설정
- Android 알림 권한과 exact alarm 안내
- 데이터 초기화
- 사용 가이드와 면책 고지
- Android Compose 앱
- KMP shared module 기반 iOS 확장

### 제외

- 영양제 추천
- 성분 분석
- 복용량 처방 또는 의료 판단
- 약물 상호작용 판단
- 병원, 약국, 커머스 연동
- 서버 계정, 클라우드 동기화, 소셜 기능
- 광고 또는 구독 결제

제외 범위가 필요해지면 별도 제품 결정 문서와 issue에서 목적, 위험, 사용자 가치, 법적/의료적 표현 한계를 먼저 검토합니다.

## 6. 핵심 사용자 흐름

### 오늘 복용 체크

1. 사용자가 앱을 실행한다.
2. 오늘 화면에서 날짜, 진행률, 복용 목록을 본다.
3. 미완료 항목을 탭해 완료로 체크한다.
4. 앱은 진행률과 기록을 즉시 갱신한다.
5. 완료 체크에는 가벼운 햅틱을 1회 제공한다.

### 영양제 등록

1. 사용자가 오늘 또는 영양제 화면에서 추가 버튼을 누른다.
2. 이름, 복용량, 단위를 입력한다.
3. 복용 방식을 선택한다.
4. 식사 기준, 특정 시각, 일정 간격 중 필요한 세부 규칙을 입력한다.
5. 알림 여부와 메모를 설정한다.
6. 저장하면 오늘 일정과 기록 계산에 반영된다.

### 설정 변경

1. 사용자가 설정 화면으로 이동한다.
2. 식사 시간, 알림 기본값, 권한 상태, 데이터 관리 항목을 확인한다.
3. 식사 시간을 바꾸면 식사 기준 일정에 반영된다.
4. 데이터 초기화는 확인 dialog를 거친다.

## 7. 기능 요구사항

| 영역 | 요구사항 | 우선순위 |
| --- | --- | --- |
| Today | 오늘 날짜, 진행률, 복용 목록을 표시한다. | P0 |
| Today | 복용 항목을 한 번의 탭으로 체크/해제한다. | P0 |
| Today | 일정이 없으면 등록 행동으로 이어지는 empty state를 보여준다. | P0 |
| Supplements | 영양제 목록에서 이름, 복용 방식, 하루 횟수, 복용량, 알림 상태를 보여준다. | P0 |
| Supplements | 영양제 추가/수정/삭제가 실제 저장소에 반영된다. | P0 |
| Supplements | form validation 오류는 저장 시점에 명확한 한국어 문구로 보여준다. | P0 |
| History | 오늘 완료율, 월간 완료 상태, 최근 기록을 보여준다. | P1 |
| Settings | 식사 시간 변경이 Today schedule에 반영된다. | P0 |
| Settings | 알림 기본값과 실제 권한 상태를 분리해서 보여준다. | P1 |
| Settings | 데이터 초기화는 확인 dialog와 명확한 destructive action으로 처리한다. | P1 |
| Notification | Android notification permission과 exact alarm permission을 분리해 안내한다. | P1 |
| Persistence | 앱 재시작 후 영양제, 기록, 설정이 유지된다. | P0 |
| iOS | KMP shared module을 사용하는 iOS shell을 제공한다. | P1 |

## 8. 비기능 요구사항

- Local-first로 동작한다.
- 개인 루틴 데이터의 source of truth는 repository에 둔다.
- domain model과 scheduling/history logic은 KMP shared에 둔다.
- Android UI는 Jetpack Compose와 Material 3를 사용한다.
- Android UI는 MVVM, state hoisting, SSOT, Clean Architecture 원칙을 따른다.
- iOS는 KMP shared module을 사용하되 platform UI와 permission 정책을 iOS에 맞게 구현한다.
- 알림, exact alarm, iOS notification은 platform adapter 뒤에 둔다.
- 사용자가 시스템 알림/햅틱 설정을 꺼둔 경우 이를 존중한다.

## 9. UX 원칙

- 반복 사용 앱이므로 장식보다 스캔성과 안정감을 우선한다.
- 첫 화면의 primary task는 “오늘 먹을 것 확인”과 “복용 체크”다.
- 상태는 색상만으로 전달하지 않고 텍스트, 아이콘, 접근성 label을 함께 사용한다.
- destructive action은 confirmation flow 안에 둔다.
- empty state는 다음 행동을 알려준다.
- 한국어 문구는 짧고 직접적으로 쓴다.
- 의료 효능이나 건강 개선을 암시하는 과장 문구를 쓰지 않는다.
- 햅틱은 복용 완료, 저장 완료, destructive confirm, validation error처럼 중요한 상태 변화에만 짧게 사용한다.

## 10. 플랫폼 요구사항

### Android

- Kotlin, Jetpack Compose, Material 3 기반으로 구현한다.
- `POST_NOTIFICATIONS` runtime permission과 exact alarm permission을 분리해서 다룬다.
- 알림 발화와 예약은 Android platform adapter에서 처리한다.
- Compose 화면은 가능한 stateless로 유지하고 state/event를 hoist한다.
- `View.performHapticFeedback` 기반 햅틱을 사용해 시스템 설정을 존중한다.

### iOS

- KMP shared framework를 사용한다.
- SwiftUI shell에서 shared domain/scheduling/data contract를 호출한다.
- iOS notification 권한과 scheduling은 iOS adapter에서 처리한다.
- Android-only 권한이나 동작은 iOS에서 명확한 fallback을 제공한다.

## 11. 성공 기준

제품 parity에 도달했다고 보려면 다음을 만족해야 합니다.

- Android KMP 앱에서 Today, Supplements, History, Settings의 핵심 흐름이 동작한다.
- 앱 재시작 후 영양제, 복용 기록, 식사 시간, 알림 기본값이 유지된다.
- Flutter 기준 구현의 주요 사용자 흐름이 Android KMP 앱과 비교 가능하다.
- Android 알림 권한, exact alarm 설정 이동, 실제 알림 발화가 실제 기기에서 확인된다.
- iOS 앱이 KMP shared module을 호출해 최소 핵심 흐름을 표시한다.
- screenshot/accessibility QA에서 주요 화면의 겹침, 잘림, color-only state 전달 문제가 없다.

## 12. 현재 상태

현재 프로젝트는 Flutter 기준 구현을 유지하면서 KMP shared와 Android Compose 앱으로 전환 중입니다.

- Flutter 앱은 parity 전까지 제품 기준 구현입니다.
- KMP shared domain, scheduling, history, persistence contract는 단계적으로 구현되고 있습니다.
- Android Compose는 Today, Supplements, History, Settings, persistence, notification, haptics를 확장 중입니다.
- iOS는 KMP shared integration과 SwiftUI shell이 남아 있습니다.
- QA는 Android 실기기 일부 확인이 진행되었고, Android 13+ runtime notification dialog와 iOS 실기기 검증은 추가 환경이 필요합니다.

## 13. 관련 문서

- 정보 구조: `docs/information_architecture.md`
- 사용자 흐름: `docs/user_flow.md`
- 디자인 시스템: `docs/design_system.md`
- 기술 스택: `docs/tech_stack.md`
- KMP 마이그레이션 로드맵: `docs/kmp_migration_roadmap.md`
- KMP parity check: `docs/kmp_parity_check.md`
