# CI/CD

## Current Setup

GitHub Actions currently runs on pushes to `main`, pull requests targeting `main`, and manual dispatch.

1. Check out the repository
2. Install Flutter stable
3. Install dependencies
4. Run static analysis
5. Run tests
6. Build an Android debug APK

Workflow file:

- `.github/workflows/flutter_ci.yml`

## Why CI Comes First

The app is still evolving, so the first priority is to keep every change buildable and tested.

Automated release deployment should be added separately after these prerequisites are ready:

- Play Console app registration
- upload keystore
- Play Console service account JSON
- release versioning policy
- GitHub Secrets configuration

## Next Step

After release preparation is complete, add a separate release workflow.

1. Trigger from a `release` tag or manual dispatch
2. Restore keystore and passwords from GitHub Secrets
3. Run `flutter build appbundle --release`
4. Upload the signed AAB as an artifact
5. Optionally add fastlane or Google Play deployment

## Example GitHub Secrets

Use repository secrets for release automation values such as:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `PLAY_STORE_SERVICE_ACCOUNT_JSON`

Do not commit secrets directly into workflow files or source control.
