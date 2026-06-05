# CI/CD

## 현재 구성

현재 GitHub Actions는 Flutter 기준 구현, KMP Android/shared, iOS KMP shell을 분리해서 검증합니다.

Flutter CI는 `main` 브랜치 push, `main` 대상 pull request, 수동 실행에서 다음 작업을 수행합니다.

1. 저장소 checkout
2. Flutter stable 설치
3. 의존성 설치
4. 정적 분석
5. 테스트
6. Android debug APK 빌드

워크플로 파일:

- `.github/workflows/flutter_ci.yml`

KMP CI는 `main` 브랜치 push, `main` 대상 pull request, 수동 실행에서 다음 작업을 수행합니다.

1. 저장소 checkout
2. Temurin Java 17 설치
3. Gradle 8.14 설치
4. shared check 실행
5. KMP Android debug APK 빌드
6. KMP Android release APK assemble 검증
7. KMP Android release AAB bundle 검증

워크플로 파일:

- `.github/workflows/kmp_ci.yml`

iOS KMP CI는 `main` 대상 pull request와 수동 실행에서 다음 작업을 수행합니다.

1. macOS runner에서 저장소 checkout
2. Temurin Java 17 설치
3. Gradle 8.14 설치
4. iOS simulator용 shared framework 빌드
5. iOS release XCFramework 산출 검증
6. signing 없이 SwiftUI iOS shell simulator build 검증

워크플로 파일:

- `.github/workflows/ios_kmp_ci.yml`

수동 릴리즈 artifact workflow는 `workflow_dispatch`로 Android signed APK/AAB와 iOS signed archive/IPA를 생성합니다. 이 workflow는 실제 signing secrets가 없으면 실패하도록 구성되어 있습니다.

워크플로 파일:

- `.github/workflows/kmp_release.yml`

## 왜 먼저 CI만 구성했는가

현재 앱은 아직 제품 기능을 다듬는 단계이므로, 우선 모든 변경이 항상 빌드 가능하고 테스트를 통과하는지 보장하는 것이 가장 중요합니다.

릴리즈 자동 배포는 다음 전제가 준비된 뒤 분리하는 편이 안전합니다.

- Play Console 앱 등록
- 업로드 keystore
- Play Console service account JSON
- 릴리즈 버전 정책
- GitHub Secrets 구성
- iOS signing team, bundle id, provisioning profile
- iOS 배포 대상을 TestFlight/App Store 중 어디로 둘지에 대한 결정

## 릴리즈 workflow

KMP 릴리즈 artifact는 별도 수동 workflow에서 생성합니다.

1. 수동 실행에서 `all`, `android`, `ios` 중 대상 선택
2. GitHub Secrets에서 keystore와 비밀번호 복원
3. Android는 `gradle -p kmp :shared:check :androidApp:assembleRelease :androidApp:bundleRelease` 실행
4. iOS는 shared release framework/XCFramework를 빌드한 뒤 signing/provisioning을 복원해 archive 생성
5. iOS archive를 App Store Connect export option으로 `.ipa`로 export
6. Android APK/AAB와 iOS `.xcarchive.tar.gz`/`.ipa`를 artifact로 저장
7. 필요 시 fastlane, Google Play, TestFlight 업로드 단계 추가

## 필요한 GitHub Secrets 예시

릴리즈 자동화를 시작할 때는 다음 값을 저장소 Secrets로 관리합니다.

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `PLAY_STORE_SERVICE_ACCOUNT_JSON`
- `IOS_TEAM_ID`
- `IOS_BUNDLE_IDENTIFIER`
- `IOS_PROVISIONING_PROFILE_BASE64`
- `IOS_CERTIFICATE_BASE64`
- `IOS_CERTIFICATE_PASSWORD`

민감한 값은 workflow 파일이나 저장소에 직접 커밋하지 않습니다.
