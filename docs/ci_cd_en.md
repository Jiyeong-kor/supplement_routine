# CI/CD

## Current Setup

GitHub Actions currently verifies the Flutter reference app, KMP Android/shared, and iOS KMP shell through separate workflows.

Flutter CI runs on pushes to `main`, pull requests targeting `main`, and manual dispatch.

1. Check out the repository
2. Install Flutter stable
3. Install dependencies
4. Run static analysis
5. Run tests
6. Build an Android debug APK

Workflow file:

- `.github/workflows/flutter_ci.yml`

KMP CI runs on pushes to `main`, pull requests targeting `main`, and manual dispatch.

1. Check out the repository
2. Install Temurin Java 17
3. Install Gradle 8.14
4. Run shared checks
5. Build the KMP Android debug APK
6. Verify KMP Android release assemble
7. Verify KMP Android release AAB bundle

Workflow file:

- `.github/workflows/kmp_ci.yml`

iOS KMP CI runs on pull requests targeting `main` and manual dispatch.

1. Check out the repository on a macOS runner
2. Install Temurin Java 17
3. Install Gradle 8.14
4. Build the shared framework for the iOS simulator
5. Verify the iOS release XCFramework artifact path
6. Verify the SwiftUI iOS shell simulator build without signing

Workflow file:

- `.github/workflows/ios_kmp_ci.yml`

The manual release artifact workflow runs from `workflow_dispatch` and creates Android signed APK/AAB artifacts plus iOS signed archive/IPA artifacts. It is intentionally configured to fail when the required signing secrets are missing.

Workflow file:

- `.github/workflows/kmp_release.yml`

## Why CI Comes First

The app is still evolving, so the first priority is to keep every change buildable and tested.

Automated release deployment should be added separately after these prerequisites are ready:

- Play Console app registration
- upload keystore
- Play Console service account JSON
- release versioning policy
- GitHub Secrets configuration
- iOS signing team, bundle id, and provisioning profile
- decision on whether iOS deployment targets TestFlight or App Store first

## Release Workflow

KMP release artifacts are created from a separate manual workflow.

1. Select `all`, `android`, or `ios` from manual dispatch
2. Restore keystore and passwords from GitHub Secrets
3. For Android, run `gradle -p kmp :shared:check :androidApp:assembleRelease :androidApp:bundleRelease`
4. For iOS, build the shared release framework/XCFramework, restore signing/provisioning, and create an archive
5. Export the iOS archive to an `.ipa` with App Store Connect export options
6. Upload Android APK/AAB and iOS `.xcarchive.tar.gz`/`.ipa` artifacts
7. Optionally add fastlane, Google Play, or TestFlight upload steps

## Example GitHub Secrets

Use repository secrets for release automation values such as:

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

Do not commit secrets directly into workflow files or source control.
