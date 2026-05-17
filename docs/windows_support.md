# Windows 지원

## 목표

Windows에서는 모바일 앱을 크게 늘린 화면보다, 켜 두고 현재 루틴을 빠르게 확인할 수 있는 작은 패널 경험을 우선합니다.

## 현재 적용한 동작

- Windows 데스크톱 타깃을 추가했습니다.
- 앱은 `420 x 720` 크기의 작은 창으로 시작합니다.
- 창은 기본적으로 다른 창 위에 유지되어, 루틴 확인 패널처럼 사용할 수 있습니다.
- 앱 제목은 `Supplement Routine`으로 표시됩니다.
- 설정에서 `Windows 시작 시 실행`을 직접 켜거나 끌 수 있습니다.

## 빌드 메모

Windows 빌드는 Flutter 플러그인 symlink 생성을 사용하므로, 개발 환경에서 Windows Developer Mode가 필요합니다. Developer Mode를 켠 뒤 `flutter build windows` 또는 `flutter run -d windows`로 확인할 수 있습니다.

## 자동 시작

자동 시작은 기본값으로 강제하지 않고, 사용자가 설정 화면에서 직접 선택하도록 구성합니다. 사용자가 켜면 Windows 로그인 후 앱이 자동으로 실행되고, 끄면 다음 로그인부터 자동 실행되지 않습니다.
