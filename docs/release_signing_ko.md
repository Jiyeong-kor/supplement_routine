# Android 릴리즈 서명

Supplement Routine은 `android/key.properties` 파일이 있으면 릴리즈 빌드에 해당 서명 설정을 사용합니다. 파일이 없으면 개발 중 릴리즈 빌드 확인을 위해 debug signing config로 빌드됩니다.

## 준비

`android/key.properties.example`을 참고해 `android/key.properties`를 로컬에 생성합니다.

```properties
storePassword=실제_스토어_비밀번호
keyPassword=실제_키_비밀번호
keyAlias=실제_키_별칭
storeFile=C:/path/to/upload-keystore.jks
```

## 보안

- `android/key.properties`는 Git에 커밋하지 않습니다.
- `.jks`, `.keystore`, `.env` 파일은 Git에 포함하지 않습니다.
- Play Store 업로드 키와 비밀번호는 별도 비밀 저장소에 보관합니다.

## 릴리즈 빌드 확인

```bash
flutter build apk --release --dart-define=APP_FLAVOR=prod --dart-define=MOCK_DATA=false
```

Play Store 배포용 App Bundle은 다음 명령으로 생성합니다.

```bash
flutter build appbundle --release --dart-define=APP_FLAVOR=prod --dart-define=MOCK_DATA=false
```
