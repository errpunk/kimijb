# Release Guide

This project publishes to two destinations:

1. JetBrains Marketplace (primary distribution)
2. GitHub Releases (release notes + signed ZIP artifact)

## Prerequisites

- Repository `production` environment with required reviewers enabled
- GitHub Secrets configured:
  - `CERTIFICATE_CHAIN`
  - `PRIVATE_KEY`
  - `PRIVATE_KEY_PASSWORD`
  - `PUBLISH_TOKEN`

## Versioning Rules

- Update `version` in `build.gradle`
- Create a tag in the format `vX.Y.Z`
- The release workflow validates that tag version equals `build.gradle` version

## CI and CD Flow

### CI (`.github/workflows/ci.yml`)

Triggered by push and PR (and manually via `workflow_dispatch`):
- `./gradlew test`
- `./gradlew verifyPluginProjectConfiguration`
- `./gradlew verifyPlugin`
- `./gradlew buildPlugin`

If commit/PR title contains `wip:`, CI is skipped by guard job.

### Release (`.github/workflows/release.yml`)

Triggered by pushing tags like `v0.0.1`:
1. Validate tag format and version match
2. `verifyPlugin`
3. `signPlugin`
4. `publishPlugin` to JetBrains Marketplace
5. Create GitHub Release and upload `*-signed.zip`

The job uses `environment: production`, so release requires manual approval first.

## Recommended Release Steps

1. Ensure main branch is green on CI
2. Bump version in `build.gradle`
3. Commit and push
4. Create and push tag:

```bash
git tag v0.0.2
git push origin v0.0.2
```

5. Approve production deployment in GitHub Actions
6. Confirm both destinations:
- Marketplace plugin update visible
- GitHub Release created with signed ZIP

## Troubleshooting

- `Version mismatch` in release job:
  - Ensure tag (without leading `v`) matches `build.gradle` version exactly
- `signPlugin` fails:
  - Verify certificate/private key/password secrets are complete PEM content
- `publishPlugin` fails:
  - Verify `PUBLISH_TOKEN` is valid and has Marketplace publish permission
