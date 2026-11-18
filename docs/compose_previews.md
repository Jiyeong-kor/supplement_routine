# Compose Preview 화면 목록

안드로이드 앱의 등록, 복용 체크, 기록 확인, 설정 흐름을 48개 `@Preview`에서 확인할 수 있습니다. 실제 화면 컴포넌트와 앱 테마를 사용하며, 예제 데이터는 `debug` 소스에만 포함합니다.

## Android Studio에서 열기

1. `kmp` 프로젝트의 `androidApp` 모듈에서 `debug` 변형을 선택합니다.
2. `androidApp/src/debug/kotlin/com/jiyeong/supplementroutine/kmp/android/ui/preview/RoutineFlowPreviews.kt`를 엽니다.
3. Design 또는 Split에서 Preview 그룹을 선택합니다.

흰 배경과 승인된 파랑, 하늘색, 노랑 팔레트, Pretendard를 공통으로 적용합니다. 날짜는 2026년 11월 18일로 고정합니다. 식사 기준, 정해진 시간, 일정 간격의 세 복용 방식과 하루 네 건의 일정을 예제로 사용합니다.

## 사용자 흐름별 범위

| 흐름 | Preview에서 확인하는 상태 | 개수 |
| --- | --- | ---: |
| 시작 | 불러오는 중 | 1 |
| 오늘 | 첫 등록 전, 복용 전, 일부 완료, 모두 완료, 체크 해제, 등록 완료, 알림 권한 안내, 기록 저장 오류 | 8 |
| 영양제 관리 | 빈 목록, 복용 방식별 목록, 삭제 확인 | 3 |
| 등록과 수정 | 세 복용 방식, 기존 영양제 수정, 이름과 복용량과 조건의 입력 오류, 저장 중, 저장 실패, 추가 단위, 알림과 메모 | 11 |
| 기록 | 빈 기록, 월간 기록, 이전 날짜 선택, 최근 기록과 복용 패턴 | 4 |
| 설정 | 기본 설정, 알림 기본값 끔, 식사 시간 편집, 권한 허용과 미허용, 앱 정보, 초기화 확인과 완료, 사용 가이드, 면책 고지, 테스트 알림 결과 세 가지 | 13 |
| 좁은 화면 | 오늘, 등록, 기록, 식사 시간 편집의 360dp 너비와 1.3배 글자 | 4 |
| 확장 화면 | 오늘, 영양제 목록, 기록, 설정의 1280 × 800dp 화면 | 4 |

기본 크기는 400 × 900dp입니다. 긴 등록 폼과 설정은 1100dp 또는 1350dp 높이로 확인하며, 아래쪽 항목은 스크롤 위치를 고정한 Preview도 제공합니다. 높이를 늘린 이미지는 전체 내용을 검토하기 위한 화면이며 실제 기기 캡처가 아닙니다.

확장 화면에서는 본문과 하단 메뉴의 너비를 최대 720dp로 제한하고 가운데에 배치합니다. 식사 시간 편집은 이름과 시간을 두 줄로 배치해 큰 글자에서도 조절 버튼과 겹치지 않도록 구성합니다.

앱 내부 권한 안내와 대화상자를 포함합니다. 운영체제의 권한 요청 창과 실제 알림 전송은 기기에서 별도로 확인해야 합니다. Preview의 저장, 삭제, 권한 요청 콜백은 실제 데이터를 변경하거나 시스템 작업을 실행하지 않습니다.

## PNG 생성과 비교

JDK 17 이상, Gradle 8.14, 안드로이드 SDK가 있는 환경에서 저장소 루트를 기준으로 실행합니다.

```shell
gradle -p kmp :androidApp:recordRoborazziDebug --tests '*RoborazziPreviewParameterizedTests*'
gradle -p kmp :androidApp:verifyRoborazziDebug --tests '*RoborazziPreviewParameterizedTests*'
```

로컬에 Flutter 프로젝트의 Gradle wrapper가 있으면 `gradle` 대신 `android/gradlew.bat`을 사용할 수 있습니다. 생성한 PNG는 `docs/assets/screenshots/compose-previews`에 저장하며 Git 추적에서 제외합니다. PR에는 생성한 PNG를 파일로 첨부합니다. 비교 검사는 로컬에 기준 이미지를 먼저 생성한 뒤 같은 환경에서 실행합니다.

Roborazzi가 `@Preview`의 크기, 글자 배율과 언어 설정을 읽고 Robolectric API 35에서 실제 Compose 화면을 렌더링합니다. 대화상자는 배경 화면과 함께 캡처합니다. 화면 상태에 필요한 예제만 주입하며 ViewModel, 저장소와 알림 스케줄러는 생성하지 않습니다.

초기화 확인 대화상자는 `ManualClockOptions`로 렌더링 시점을 500ms로 고정합니다. 앱에서의 동작이나 입력 가능 여부는 바꾸지 않습니다.

Preview 도구와 UI 테스트는 앱의 Compose 1.11 계열에 맞춥니다. 이미지 비교에 사용하는 Robolectric은 4.16.1로 고정하며, 버전을 올릴 때 캡처를 다시 확인합니다. Preview Scanner는 Roborazzi 1.79.0의 호출 API와 호환되는 0.9.1을 사용합니다. 이 의존성의 새 버전 안내만 각 선언에서 제외하며, 화면 코드와 나머지 lint 검사는 유지합니다.

## 상태 변경 회귀 테스트

```shell
gradle -p kmp :androidApp:testDebugUnitTest --tests '*RoutineScreenStateTest*'
```

이름 누락 시 저장 차단, 저장 실패 후 입력값을 유지한 재시도, 식사 시간 변경 취소, 저장 시점의 변경 전달을 검사합니다. 정지된 Preview 이미지와 실제 상호작용 검사를 구분합니다.
