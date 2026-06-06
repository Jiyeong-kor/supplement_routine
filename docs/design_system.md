# Supplement Routine 디자인 시스템

이 문서는 `docs/prd.md`의 제품 범위와 UX 원칙을 시각 언어, 컴포넌트, 상태 표현으로 구체화합니다.

## 1. 디자인 방향

Supplement Routine은 의료 조언 앱이 아니라 사용자가 직접 정한 루틴을 매일 확인하고 기록하는 생활 앱입니다. 새 디자인 방향은 “귀엽고 친근하지만 유치하지 않은 데일리 루틴 도구”입니다.

사용자가 앱을 열었을 때 느껴야 하는 감정:

- 내 하루가 조금 더 잘 챙겨지고 있다.
- 작은 복용 체크가 부담스럽지 않고 기분 좋다.
- 앱이 귀엽지만 과하게 들뜨지 않는다.
- 중요한 정보는 빠르게 보이고, 반복 사용해도 피곤하지 않다.

피해야 하는 분위기:

- 의료 앱처럼 차갑고 딱딱한 느낌
- 영양제 효능을 암시하는 건강 마케팅 느낌
- 보라색 AI 앱처럼 보이는 generic gradient
- 캐릭터나 장식이 과해서 정보가 묻히는 느낌
- 너무 촘촘해서 관리 앱처럼 압박을 주는 느낌

## 2. 브랜드 은유

핵심 은유는 “작은 루틴 조각을 하루 보드에 붙이고 체크하는 느낌”입니다.

시각적으로는 다음 요소를 조합합니다.

- 루틴 보드: 오늘 해야 할 일을 안정적으로 정렬하는 화면 구조
- 스티커 카드: 복용 항목이 작고 친근한 조각처럼 느껴지는 카드
- 캡슐 체크: 영양제 맥락은 유지하되 의료적으로 보이지 않는 부드러운 capsule shape
- 작은 완료 신호: 완료 시 과한 축하 대신 짧은 색 변화, 체크 아이콘, 햅틱

로고와 앱 이름은 아직 확정하지 않습니다. 이 디자인 시스템은 `Supplement Routine`이라는 현재 이름을 지원하되, 나중에 한국어/영어 새 이름으로 바뀌어도 유지될 수 있는 시각 언어를 우선합니다.

## 3. UI 밀도

권장 밀도는 “편안한 중간 밀도”입니다.

- Today 화면은 복용 항목 3-5개가 한 화면에서 스캔될 수 있어야 합니다.
- 카드 안의 정보는 이름, 시간, 복용량, 상태가 빠르게 읽히는 수준으로 유지합니다.
- 헤더와 empty state는 따뜻해야 하지만 첫 화면의 할 일을 밀어내지 않아야 합니다.
- 설정과 기록 화면은 조용하고 정돈된 밀도를 유지합니다.

너무 여유로운 editorial layout은 매일 쓰는 도구로서 답답하고, 너무 촘촘한 dashboard layout은 귀엽고 가벼운 감정을 해칩니다.

## 4. 컬러 시스템

기존 보라/라벤더 중심 팔레트는 새 방향에서 제외합니다. 2026-06 `Supplement Routine Release UI Handoff 1.0.0` 기준 팔레트는 warm paper 배경, hand-made fruit/flower avatar, seed/leaf progress, 선명한 berry blue, leaf green, flower red, egg yellow 포인트를 사용합니다.

| 분류 | 토큰 | 값 | 용도 | 미리보기 |
| --- | --- | --- | --- | --- |
| Base | `GardenUi.WarmWhite` | `#F8F4EC` | 앱 배경 | ![Background](assets/colors/background.svg) |
| Base | `GardenUi.Paper` | `#F2EBDD` | 낮은 강조 배경 | |
| Base | `GardenUi.Surface` | `#FFFDF8` | 카드, sheet, dialog | |
| Base | `GardenUi.SurfaceSoft` | `#FAF3E6` | pill, 검색, 낮은 강조 표면 | |
| Brand | `GardenUi.MistBlue` | `#DDEAF1` | Today organic hero, 선택 indicator | |
| Brand | `GardenUi.PrimaryBlue` | `#496DFF` | 주요 CTA, 기록 hero, 선택 navigation | ![Seed](assets/colors/seed.svg) |
| Brand | `GardenUi.PrimaryBlueDark` | `#AEBBFF` | reverse/accent blue | |
| Brand | `GardenUi.LeafGreen` | `#B9DE65` | 완료 체크, leaf progress | ![Success](assets/colors/success.svg) |
| Brand | `GardenUi.LeafGreenDark` | `#5E7E2A` | active seed/leaf fill | |
| Brand | `GardenUi.FlowerRed` | `#FF4A3D` | 삭제, 위험 상태, flower avatar | ![Error](assets/colors/error.svg) |
| Brand | `GardenUi.Coral` | `#FF7A62` | 보조 경고 accent | |
| Brand | `GardenUi.EggYellow` | `#F4C052` | 주의, egg flower avatar, 낮은 완료율 | ![Warning](assets/colors/warning.svg) |
| Text | `GardenUi.Ink` | `#202124` | 주요 텍스트 | ![Ink](assets/colors/ink.svg) |
| Text | `GardenUi.InkMuted` | `#5F5B53` | 보조 텍스트 | |
| Line | `GardenUi.Line` | `#E7DDCC` | 카드/입력 outline | |

컬러 사용 규칙:

- Primary는 CTA와 중요한 완료 신호에만 사용합니다.
- 과일/정원 장식은 정보 구조를 밀어내지 않는 leading avatar, empty state, hero 보조 요소로 제한합니다.
- 완료 상태는 leaf green, check icon, text/semantics를 함께 사용합니다.
- 배경은 warm paper 계열로 유지하고, 카드와 bottom navigation은 `#FFFDF8` surface로 분리합니다.
- 상태 의미는 색상만으로 전달하지 않고 text, icon, semantics를 함께 사용합니다.

## 5. Typography

기본 폰트는 Pretendard를 유지합니다. 한국어 UI의 읽기 안정성과 Android/iOS parity를 우선합니다.

| 역할 | Weight | 용도 |
| --- | --- | --- |
| Display | 700 | Today headline, 큰 화면 제목 |
| Title | 600 | 카드 제목, section title |
| Body | 400 / 500 | 설명, 목록 정보 |
| Label | 600 | chip, badge, 작은 상태 텍스트 |

타이포그래피 규칙:

- 영웅형 headline을 남발하지 않습니다. Today 첫 문장과 화면 제목에만 제한합니다.
- 숫자, 시간, 완료율은 흔들림 없이 읽히도록 같은 위치와 계층을 유지합니다.
- 한국어 label이 버튼 안에서 잘리지 않게 버튼 최소 폭과 padding을 충분히 둡니다.
- 의료적 권위감을 주는 딱딱한 문구보다 짧고 직접적인 생활 언어를 사용합니다.

## 6. Spacing과 Layout

4pt 기반 스케일을 유지하되, “스티커 카드” 느낌이 나도록 카드 내부는 너무 빽빽하지 않게 둡니다.

| 토큰 | 값 | 용도 |
| --- | --- | --- |
| `xxs` | 4 | icon/text 내부 간격 |
| `xs` | 6 | chip 내부 간격 |
| `sm` | 8 | 작은 control 간격 |
| `md` | 12 | row 내부 간격 |
| `lg` | 16 | 카드 padding 기본 |
| `xl` | 20 | 화면 horizontal padding |
| `xxl` | 24 | section 간격 |
| `xxxl` | 32 | 큰 empty state 간격 |

Layout 규칙:

- Compact 화면의 horizontal padding은 20dp를 기본으로 합니다.
- 공통 top bar 높이는 64dp, bottom navigation 높이는 72dp를 기준으로 합니다.
- Compact phone은 bottom navigation을 기본으로 합니다.
- expanded width에서는 navigation rail과 2-column layout을 검토합니다.
- 본문 최대 폭은 일반 화면 `720dp`, 넓은 비교 화면 `1040dp`를 기준으로 합니다.
- FAB가 목록 마지막 항목을 가리지 않도록 bottom content padding을 항상 확보합니다.

## 7. Shape

귀여움은 모서리와 색에서 조금만 만듭니다. 모든 요소를 pill로 만들면 앱이 장난감처럼 보이므로 역할별 radius를 분리합니다.

| 토큰 | 값 | 용도 |
| --- | --- | --- |
| `xs` | 8 | 작은 chip, badge |
| `sm` | 12 | compact control |
| `md` | 20 | 입력, 일부 강조 surface |
| `lg` | 28 | 카드, 진행 카드, bottom sheet content |
| `xl` | 36 | dialog, modal, 큰 hero surface |
| `pill` | 999 | CTA, 완료 badge, capsule chip |

## 8. Elevation과 Surface

- 기본 카드는 0 elevation과 얇은 outline을 사용합니다.
- 중요한 카드만 아주 낮은 tonal surface를 사용합니다.
- shadow로 귀여움을 만들지 않습니다.
- surface 계층은 `background`, `surface`, `surfaceSoft`, `primaryContainer`, `successContainer` 순으로 구분합니다.

## 9. Component Rules

| 컴포넌트 | 사용 규칙 |
| --- | --- |
| AppBar | 큰 toolbar보다 content header를 우선합니다. 제목은 왼쪽 정렬합니다. |
| Navigation | compact는 label이 보이는 NavigationBar, expanded는 NavigationRail을 사용합니다. |
| Today Card | 시간, 이름, 복용량, 완료 상태가 한 번에 보이는 중간 밀도 카드로 만듭니다. |
| Supplement Card | 알림/수정/삭제 action 위계를 분리하고, destructive action은 error color로만 과하게 강조하지 않습니다. |
| FilledButton | 저장, 완료, 주요 CTA에만 사용합니다. |
| TextButton | dialog 보조 action과 low emphasis action에 사용합니다. |
| TextField | 16dp radius, 명확한 label, error text를 유지합니다. |
| Chip | capsule/sticker 느낌을 주되 상태 의미를 text로 포함합니다. |
| Dialog | destructive action은 확인 문구와 error color를 함께 사용합니다. |
| Empty State | 작은 스티커형 icon, 제목, 다음 행동 문구를 함께 제공합니다. |

## 10. 상태 표현

| 상태 | 표현 |
| --- | --- |
| Loading | primary 색상의 작은 progress indicator, 긴 설명 금지 |
| Empty | surfaceSoft 카드, 친근한 icon, 다음 행동 문구 |
| Success | mint 또는 primaryContainer + check icon + 완료 텍스트 |
| Warning | warningContainer + 안내 문구 + 다음 행동 |
| Error | errorContainer + 해결 방법이 있는 문구 |
| Disabled | Material disabled 처리와 비활성 이유가 드러나는 맥락 |

완료 상태는 “잘했어요” 같은 과한 칭찬보다 “완료됨”, “오늘 기록에 반영됨”처럼 짧고 정확하게 표현합니다.

## 11. Icon과 Illustration

아이콘은 Material outlined/filled icon을 기본으로 사용합니다.

- navigation selected state는 filled icon을 사용합니다.
- icon-only button은 content description을 반드시 둡니다.
- 스티커형 작은 illustration은 empty state나 onboarding에서만 제한적으로 사용합니다.
- 캐릭터 중심 시스템은 아직 도입하지 않습니다.
- 알약/캡슐 icon은 복용 맥락을 설명할 때만 쓰고, 의료 효능처럼 보이지 않게 합니다.

## 12. Motion과 Haptics

Motion은 상태 전환을 돕는 정도로만 사용합니다.

- 완료 체크: 색/아이콘 전환 + 가벼운 햅틱 1회
- 체크 해제: 햅틱 없음
- 저장 완료: 가벼운 햅틱
- 삭제/초기화 확인: 더 명확한 햅틱
- validation error: 짧은 warning성 햅틱

Android와 iOS는 같은 intent 이름을 공유하되, 플랫폼별 기본 햅틱 타입은 각 OS 권장 API를 따릅니다.

## 13. Copy

문구는 사용자를 밀어붙이지 않고, 작은 도움을 주는 톤으로 씁니다.

좋은 톤:

- “오늘 복용할 항목을 확인해보세요.”
- “복용 기록에 바로 반영됩니다.”
- “복용 타이밍을 하나 이상 선택해주세요.”
- “정해진 시간에 알림을 받을 수 있습니다.”

피하는 톤:

- “건강을 완벽하게 관리하세요.”
- “최적의 영양 루틴을 추천합니다.”
- “반드시 복용해야 합니다.”
- “몸이 좋아지는 습관을 만드세요.”

## 14. Accessibility

- 상태를 색상만으로 전달하지 않습니다.
- checkbox-like row는 대상 이름, 시간, 완료 상태가 함께 읽혀야 합니다.
- calendar tile은 날짜와 완료 상태를 읽을 수 있어야 합니다.
- 버튼은 Material 기본 touch target을 유지합니다.
- dynamic type/font scale에서 한국어 label이 잘리지 않게 합니다.
- primary/onPrimary, surface/onSurface처럼 대비가 검증된 ColorScheme pair를 우선 사용합니다.

## 15. Dark Mode

다크 모드는 단순히 색을 어둡게 뒤집지 않습니다. 밤에 루틴을 확인하는 상황을 고려해 눈부심을 줄이고, 귀여움은 채도보다 surface 계층과 작은 accent로 표현합니다.

권장 방향:

- background: `#17181C`
- surface: `#22242A`
- surfaceSoft: `#2C2F36`
- primary: `#FF8AA2`
- coral: `#FFB096`
- success: `#6DE0B5`
- text: `#F8F7F4`

## 16. 구현 파일

Flutter 기준:

- `lib/app/app_colors.dart`
- `lib/app/app_typography.dart`
- `lib/app/app_spacing.dart`
- `lib/app/app_radius.dart`
- `lib/app/app_components.dart`
- `lib/app/app_theme.dart`

Android Compose 기준:

- `kmp/androidApp/src/main/kotlin/.../ui/theme/`
- `kmp/androidApp/src/main/kotlin/.../ui/common/`

다음 UI 리팩터링 PR에서는 이 문서를 기준으로 Flutter theme와 Android Compose theme token을 순차적으로 맞춥니다.
