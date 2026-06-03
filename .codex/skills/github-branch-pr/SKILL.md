---
name: supplement-routine-github-branch-pr
description: Supplement Routine 저장소 안에서만 사용하는 GitHub issue, branch, commit, pull request 운영 규칙. Flutter에서 KMP와 Android Compose로 전환하는 작업을 작게 나누고 검토 가능하게 유지할 때 사용한다.
---

# Supplement Routine GitHub Branch PR Workflow

이 skill은 이 저장소 안에서만 사용한다. 마이그레이션 작업은 검토 가능하고, 되돌릴 수 있고, 범위가 분명해야 한다.

## 브랜치 전략

- KMP parity가 증명되기 전까지 `main`은 안정적인 Flutter 기준 구현으로 유지한다.
- 작성자 prefix가 아니라 제품/작업 흐름 prefix를 사용한다. 브랜치 이름은 누가 만들었는지가 아니라 어떤 마이그레이션 영역인지 설명해야 한다.
- 브랜치 하나에는 concern 하나만 담는 것을 우선한다:
  - `kmp/roadmap-docs`
  - `kmp/scaffold`
  - `kmp/shared-domain`
  - `kmp/shared-scheduling`
  - `android/compose-today`
  - `android/compose-supplements`
  - `android/compose-history`
  - `android/compose-settings`
  - `ios/shared-integration`
- docs, scaffolding, shared logic, Android UI, iOS UI, platform API를 한 PR에 섞지 않는다.

## Issue 전략

작업이 작은 파일 하나를 넘거나 아키텍처에 영향을 주면 구현 전에 issue를 만든다.

좋은 issue 카테고리:

- UI/UX audit and design token mapping
- KMP project scaffold
- Shared domain model port
- Shared scheduling and history logic
- Shared persistence contract
- Android Compose screen port
- Android notification/exact alarm integration
- iOS app shell and shared module integration
- Localization/accessibility QA
- Flutter parity verification

각 issue에는 다음을 포함한다:

- 목표
- 범위
- 제외 범위
- 완료 기준
- 검증 방법
- 관련 Flutter 파일 또는 문서

## PR 전략

모든 PR은 다음 질문에 답해야 한다:

- 무엇이 바뀌었는가?
- 왜 지금 필요한가?
- 어떤 Flutter 동작 또는 문서를 기준으로 삼았는가?
- 의도적으로 제외한 것은 무엇인가?
- 어떻게 검증했는가?

마이그레이션 PR은 한 번에 검토할 수 있을 정도로 작게 유지한다. generated files, build files, UI를 한 PR에서 모두 건드린다면 강한 이유가 없는 한 나눈다.

## Commit 규칙

- commit message는 구체적이고 명령형으로 작성한다.
- 관련 없는 사용자 변경을 rewrite/revert하지 않는다.
- 현재 브랜치와 관련 있는 파일만 stage한다.
- 시작 전에 worktree가 dirty라면 먼저 확인하고, 관련 없는 변경은 보존한다.

## PR 준비 체크리스트

- 사소하지 않은 작업에는 연결된 issue가 있다.
- scope가 branch name과 맞다.
- test 또는 문서화된 verification을 수행했다.
- Flutter reference behavior가 여전히 남아 있다.
- 플랫폼별 limitation을 명시했다.
- visible UI 변경에는 가능한 경우 screenshot을 포함했다.
