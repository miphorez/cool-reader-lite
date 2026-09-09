# Release Process

No public binary release should be created until all required checks are
reproducible from a clean checkout. Preview APKs are debug-signed GitHub
pre-releases for testing; they are not production releases.

## Required release inputs

- A reviewed commit on the protected default branch
- A documented JDK, Android SDK, NDK, CMake, and Gradle toolchain
- Passing automated checks and the applicable device smoke tests
- A reviewed third-party license and attribution inventory
- A version-specific source snapshot matching the distributed binary
- Release notes describing user-visible changes and known limitations

## Versioning

`android/app/build.gradle` is the source of truth for Android `versionName` and
the base `versionCode`.

Each published preview uses a new `versionName`, tag, and asset. If the current
preview is already published, increment the preview ordinal on the same version
line. Stable releases require an explicit decision and the permanent release
signing setup.

ABI-specific APKs add the configured ABI code to the base code:

`outputVersionCode = baseVersionCode + abiCode`

The next base code must be greater than the highest code derived from the
previous base:

`newBaseVersionCode > previousBaseVersionCode + max(abiCodes)`

The current maximum ABI code is `7`, so the base `versionCode` must increase by
at least `8`. Read the live ABI map before every release in case it changes.
The version bump belongs in the same pull request as the release change.

## Signing

Release signing keys and passwords must never be committed to Git, copied into
issues, or stored in build logs. Signing material must be backed up separately
from the working copy.

## Publication

1. Inspect the current Gradle version, ABI map, GitHub releases, and download
   note.
2. Update `versionName`, the ABI-safe base `versionCode`, and release notes in
   the change pull request.
3. Build and verify the candidate, then open the pull request against protected
   `master`.
4. Wait for every required check and merge only after the checks and build pass.
5. Synchronize a clean local `master` to the exact merge commit.
6. Rebuild the distributable from merged `master`; do not reuse the feature
   branch artifact.
7. Verify package, version, SDK levels, signature, ABIs, size, SHA-256, and the
   embedded source commit. Run the applicable device smoke test when available.
8. Create a new tag named `v<versionName>` at that exact commit and publish a
   GitHub pre-release with the versioned universal debug APK.
9. Verify the remote asset size and digest against the local artifact.
10. Update the Cool Reader Obsidian download note with the release page, direct
    APK URL, byte size, SHA-256, package name, and source commit.
11. Preserve build and smoke-test evidence and remove temporary upload copies.

Use `--repo miphorez/cool-reader-lite` for GitHub CLI pull request and release
operations. Never overwrite an existing tag or release asset as a shortcut for
a missed version bump.

An end-to-end request to create a pull request, merge it into `master`, and
update the download file authorizes the Gradle builds and release operations
required by this process. It is complete only after the published asset and the
Obsidian download note have both been verified.
