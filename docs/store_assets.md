# 스토어 에셋

## 목적

Supplement Routine의 스토어 에셋은 의료 앱처럼 보이기보다, 사용자가 직접 정한 루틴을 관리하는 앱이라는 인상을 주어야 합니다.

## 준비할 에셋

| 에셋 | 권장 내용 |
| --- | --- |
| 앱 아이콘 | 현재 런처 아이콘 유지 |
| feature graphic | 앱 이름, 오늘 루틴 진행률, 기록 중심 메시지 |
| 휴대폰 스크린샷 | 오늘, 영양제 등록, 기록, 설정 |
| 태블릿 스크린샷 | NavigationRail이 보이는 넓은 화면 구성 |
| README 미디어 | 대표 스크린샷 2~4장, 짧은 사용 흐름 GIF 1개 |

## 카피 방향

- 사용 가능: `오늘 루틴`, `복용 기록`, `직접 입력한 일정`
- 피해야 함: `치료`, `처방`, `효능`, `추천`

## 산출물 경로

- 스토어 그래픽: `docs/assets/store/`
- README 스크린샷: `docs/assets/screenshots/`
- README GIF: `docs/assets/demos/`

현재 포함된 에셋:

- `docs/assets/store/feature_graphic.svg`
- `docs/assets/screenshots/today.png`
- `docs/assets/screenshots/today_dark.png`
- `docs/assets/screenshots/supplements.png`
- `docs/assets/screenshots/history.png`
- `docs/assets/screenshots/settings.png`
- `docs/assets/screenshots/widget.png`
- `docs/assets/screenshots/notification.png`

## 캡처 체크리스트

1. 라이트 모드와 다크 모드 모두 확인합니다.
2. mock 데이터가 충분히 들어간 상태에서 캡처합니다.
3. 개인정보처럼 보이는 문구는 사용하지 않습니다.
4. 알림과 홈 위젯은 앱 본문과 별도 캡처합니다.
