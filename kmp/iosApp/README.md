# iOS Shell 준비

이 폴더는 KMP shared module을 iOS 앱에 연결하기 위한 자리입니다.

현재 scaffold PR에서는 Xcode project를 추가하지 않습니다. 이유는 다음과 같습니다.

- 첫 PR의 목표는 Android와 shared KMP build가 동작하는 최소 골격입니다.
- iOS build는 macOS runner, signing, Xcode project 설정이 필요하므로 별도 PR에서 다룹니다.
- shared module은 이미 `iosX64`, `iosArm64`, `iosSimulatorArm64` target을 선언해 iOS 연결을 준비합니다.

후속 `ios/shared-integration` 작업에서 추가할 항목:

- Xcode project 또는 Swift Package 연결 방식 결정
- shared framework export task 확인
- SwiftUI app shell 추가
- iOS CI 또는 수동 검증 절차 추가
