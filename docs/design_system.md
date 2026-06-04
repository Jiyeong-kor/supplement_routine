# Supplement Routine 디자인 시스템

이 문서는 `docs/prd.md`의 제품 범위와 UX 원칙을 시각 언어, 컴포넌트, 상태 표현으로 구체화합니다.

## UI/UX 방향

Supplement Routine은 의료 조언 앱이 아니라 루틴과 기록을 관리하는 앱입니다. UI는 차분하고 예측 가능하며, 사용자가 반복적으로 확인하기 쉬워야 합니다. 주요 사용자 흐름은 다음과 같습니다.

1. 오늘 예정된 복용 일정을 확인합니다.
2. 완료한 복용 항목을 체크합니다.
3. 영양제 복용 규칙을 등록하거나 수정합니다.
4. 완료 기록을 확인합니다.
5. 루틴 관련 설정을 조정합니다.

## Material Design 3 결정 사항

- 색상 체계: 흰 표면, 선명한 보라색 액센트, 연한 라벤더 컨테이너를 사용해 복용 루틴 정보를 가볍고 또렷하게 구분합니다. 성공, 경고, 오류 색상은 앱 토큰으로 명시합니다.
- 타이포그래피: Pretendard 정적 폰트 파일을 사용하며 400, 500, 600, 700 weight만 등록합니다. Material 3 타입 스케일을 바탕으로 하되, 반복 확인 화면의 스캔성을 위해 제목 계층은 700 weight로 더 또렷하게 강조합니다.
- 간격: 4pt 기반 스케일을 사용하고 화면 padding과 카드 padding은 공통 토큰으로 관리합니다.
- 형태: 작은 컨트롤은 8px, 카드와 입력창은 16px, 주요 액션은 pill shape, 큰 컨테이너는 28px radius를 사용합니다.
- Elevation: 대부분 0 elevation을 사용하고 밝은 표면, 연한 outline, 라벤더 캡슐 상태로 계층을 구분합니다.
- 아이콘: 기본적으로 Material outlined icon을 사용하고, 선택된 navigation 상태에는 filled icon을 사용합니다.
- 버튼: 폼 저장 같은 주요 액션은 FilledButton, 다이얼로그 보조 액션은 TextButton을 사용합니다.
- TextField: 채워진 Material 3 입력 필드를 사용하고 focused/error border를 명확히 구분합니다.
- Card: 흰 표면과 얇은 outlineVariant border를 사용합니다.
- Dialog와 BottomSheet: Material 3 surface container와 24px radius를 사용합니다.
- Navigation: compact 폭에서는 label이 항상 보이는 NavigationBar, expanded 폭에서는 NavigationRail을 사용합니다.
- Layout: 본문은 읽기 쉬운 최대 폭 안에 배치하고, expanded 폭에서는 목록과 기록 화면이 공간을 더 효율적으로 사용합니다.
- Haptics: 반복 사용 피로도를 낮추기 위해 햅틱은 중요한 상태 변화에만 짧게 사용합니다. Android는 `View.performHapticFeedback`/Compose `LocalView` 기반으로 시스템 햅틱 설정을 존중하고, 별도 진동 권한을 요구하지 않습니다.

## 토큰

| 분류 | 토큰 | 값 | 미리보기 |
| --- | --- | --- | --- |
| Color | `AppColors.seed` | `#6B36F6` | ![Seed](assets/colors/seed.svg) |
| Color | `AppColors.success` | `#20B486` | ![Success](assets/colors/success.svg) |
| Color | `AppColors.warning` | `#FFB020` | ![Warning](assets/colors/warning.svg) |
| Color | `AppColors.error` | `#E5484D` | ![Error](assets/colors/error.svg) |
| Spacing | `xxs / xs / sm / md / lg / xl / xxl / xxxl` | `4 / 6 / 8 / 12 / 16 / 20 / 24 / 32` |
| Radius | `sm / md / lg / xl / xxl / pill` | `8 / 12 / 16 / 24 / 28 / 999` |
| Typography | `Pretendard` | `400 / 500 / 600 / 700` |

## 컴포넌트 규칙

| 컴포넌트 | 사용 규칙 |
| --- | --- |
| AppBar | `centerTitle: false`, 표면색 배경, 낮은 scrolled-under elevation |
| Navigation | compact 폭은 NavigationBar, expanded 폭은 NavigationRail |
| Layout | 일반 본문은 `720px`, 넓은 콘텐츠는 `1040px` 안에서 정렬 |
| Card | 흰 표면, 0 elevation, 연한 outline variant border |
| FilledButton | 저장/완료 같은 주요 액션 |
| TextButton | 다이얼로그 보조 액션 |
| TextField | filled input, 16px radius, focus/error border 분리 |
| BottomSheet | 상단 28px radius, drag handle 사용 |
| Dialog | 28px radius, 명확한 CTA |
| Empty State | 아이콘 + 제목 + 짧은 회복 문구 |

## 햅틱 원칙

햅틱은 시각 피드백을 대체하지 않고, 사용자가 손 안에서 상태 변화를 더 확실히 느끼도록 돕는 보조 신호로만 사용합니다.

| Intent | 강도 | 적용 |
| --- | --- | --- |
| 복용 체크 완료 | 가벼움 | 미완료 항목을 완료로 바꿀 때 1회 |
| 체크 해제 | 없음 | 반복 피로도를 줄이기 위해 기본적으로 생략 |
| 저장 완료 | 가벼움 | 영양제 추가/수정 저장 성공 |
| 삭제/초기화 확인 | 명확함 | 확인 dialog에서 최종 destructive action을 실행할 때 |
| Validation error | 짧은 warning | 저장 시 필수 입력이 누락되었을 때만 사용 |

Android와 iOS는 같은 intent 이름을 공유하되, 플랫폼별 기본 햅틱 타입은 각 OS 권장 API를 따릅니다.

## 구현 파일

- `lib/app/app_colors.dart`
- `lib/app/app_typography.dart`
- `lib/app/app_spacing.dart`
- `lib/app/app_radius.dart`
- `lib/app/app_components.dart`
- `lib/app/app_theme.dart`

## 상태

- Loading: ColorScheme primary 색상을 사용하는 Material progress indicator를 사용합니다.
- Empty: 낮은 surface 카드, 중립적인 outlined icon, 제목, 짧은 회복 액션 문구를 함께 제공합니다.
- Error: ColorScheme.error와 사용자가 다음 행동을 이해할 수 있는 짧은 문구를 사용합니다.
- Disabled: ThemeData의 기본 Material disabled 처리를 따릅니다.
- Success: 별도의 성공 신호가 필요할 때는 `AppColors.success`를 사용합니다. 복용 완료 체크는 주로 ColorScheme.primary를 사용합니다.

## 접근성

- 상태를 색상만으로 전달하지 않고 아이콘과 텍스트를 함께 사용합니다.
- 주요 터치 영역은 Material 기본 크기를 유지합니다.
- 고정 font size보다 TextTheme 역할을 우선 사용해 사용자 글자 크기 설정에 대응합니다.
- 대비를 위해 `primary/onPrimary`, `surface/onSurface`처럼 ColorScheme 쌍을 우선 사용합니다.

## 다크 모드

다크 모드는 같은 seed color 계열을 사용하되, `surface`, `surfaceContainer*`, `onSurface*`, `primaryContainer` 값을 별도로 조정해 계층과 대비가 무너지지 않도록 합니다. 컴포넌트는 하드코딩된 흰색/검은색 대신 ColorScheme의 surface 계열을 기준으로 표시합니다.
