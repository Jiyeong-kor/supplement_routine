# Supplement Routine 디자인 시스템

## UI/UX 방향

Supplement Routine은 의료 조언 앱이 아니라 루틴과 기록을 관리하는 앱입니다. UI는 차분하고 예측 가능하며, 사용자가 반복적으로 확인하기 쉬워야 합니다. 주요 사용자 흐름은 다음과 같습니다.

1. 오늘 예정된 복용 일정을 확인합니다.
2. 완료한 복용 항목을 체크합니다.
3. 영양제 복용 규칙을 등록하거나 수정합니다.
4. 완료 기록을 확인합니다.
5. 루틴 관련 설정을 조정합니다.

## Material Design 3 결정 사항

- 색상 체계: 신뢰감과 루틴 관리에 어울리는 indigo seed color를 사용합니다. 성공, 경고, 오류 색상은 앱 토큰으로 명시합니다.
- 타이포그래피: Pretendard 정적 폰트 파일을 사용하며 400, 500, 600, 700 weight만 등록합니다. Material 3 텍스트 역할은 유지하고 제목과 라벨은 조금 더 또렷하게 설정합니다.
- 간격: 4pt 기반 스케일을 사용하고 화면 padding과 카드 padding은 공통 토큰으로 관리합니다.
- 형태: 작은 컨트롤은 8px, 카드와 입력창은 12px, 주요 액션은 16px, 다이얼로그와 bottom sheet는 24px radius를 사용합니다.
- Elevation: 대부분 0 elevation을 사용하고 outline 또는 tonal surface로 구분합니다. 매일 반복해서 쓰는 앱에 맞게 조용한 시각 구조를 유지합니다.
- 아이콘: 기본적으로 Material outlined icon을 사용하고, 선택된 navigation 상태에는 filled icon을 사용합니다.
- 버튼: 폼 저장 같은 주요 액션은 FilledButton, 다이얼로그 보조 액션은 TextButton을 사용합니다.
- TextField: 채워진 Material 3 입력 필드를 사용하고 focused/error border를 명확히 구분합니다.
- Card: 낮은 tonal surface와 outlineVariant border를 사용합니다.
- Dialog와 BottomSheet: Material 3 surface container와 24px radius를 사용합니다.
- Navigation: 4개의 주요 목적지는 label이 항상 보이는 하단 NavigationBar로 제공합니다.

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

다크 모드는 같은 seed color를 dark brightness로 생성해 사용합니다. 컴포넌트는 하드코딩된 흰색/검은색 대신 ColorScheme의 surface container를 기준으로 표시합니다.
