# Windows 지원

## 목표

Windows에서는 모바일 앱을 크게 늘린 화면보다, 켜 두고 현재 루틴을 빠르게 확인할 수 있는 작은 패널 경험을 우선합니다.

## 현재 적용한 동작

- Windows 데스크톱 타깃을 추가했습니다.
- 앱은 `420 x 720` 크기의 작은 창으로 시작합니다.
- 창은 기본적으로 다른 창 위에 유지되어, 루틴 확인 패널처럼 사용할 수 있습니다.
- 앱 제목은 `Supplement Routine`으로 표시됩니다.

## 빌드 메모

Windows 빌드는 Flutter 플러그인 symlink 생성을 사용하므로, 개발 환경에서 Windows Developer Mode가 필요합니다. Developer Mode를 켠 뒤 `flutter build windows` 또는 `flutter run -d windows`로 확인할 수 있습니다.

## 남은 결정

자동 시작은 사용자의 기기 시작 동작에 직접 영향을 주므로, 설정 화면에서 명시적으로 켜는 옵션으로 제공하는 편이 더 안전합니다. 현재 단계에서는 기본 동작에 포함하지 않고, Windows 설정 기능을 추가할 때 함께 구현합니다.
