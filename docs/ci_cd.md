# CI/CD

## 현재 구성

현재 GitHub Actions는 `main` 브랜치 push, `main` 대상 pull request, 수동 실행에서 다음 작업을 수행합니다.

1. 저장소 checkout
2. Flutter stable 설치
3. 의존성 설치
4. 정적 분석
5. 테스트
6. Android debug APK 빌드

워크플로 파일:

- `.github/workflows/flutter_ci.yml`

## 왜 먼저 CI만 구성했는가

현재 앱은 아직 제품 기능을 다듬는 단계이므로, 우선 모든 변경이 항상 빌드 가능하고 테스트를 통과하는지 보장하는 것이 가장 중요합니다.

릴리즈 자동 배포는 다음 전제가 준비된 뒤 분리하는 편이 안전합니다.

- Play Console 앱 등록
- 업로드 keystore
- Play Console service account JSON
- 릴리즈 버전 정책
- GitHub Secrets 구성

## 다음 단계

릴리즈 준비가 끝나면 별도 workflow를 추가합니다.

1. `release` 태그 또는 수동 실행으로 트리거
2. GitHub Secrets에서 keystore와 비밀번호 복원
3. `flutter build appbundle --release`
4. 서명된 AAB를 artifact로 저장
5. 필요 시 fastlane 또는 Google Play 배포 단계 추가

## 필요한 GitHub Secrets 예시

릴리즈 자동화를 시작할 때는 다음 값을 저장소 Secrets로 관리합니다.

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `PLAY_STORE_SERVICE_ACCOUNT_JSON`

민감한 값은 workflow 파일이나 저장소에 직접 커밋하지 않습니다.
