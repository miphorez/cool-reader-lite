# Release Process

No public binary release should be created until all required checks are
reproducible from a clean checkout.

## Required release inputs

- A reviewed commit on the protected default branch
- A documented JDK, Android SDK, NDK, CMake, and Gradle toolchain
- Passing automated checks and the applicable device smoke tests
- A reviewed third-party license and attribution inventory
- A version-specific source snapshot matching the distributed binary
- Release notes describing user-visible changes and known limitations

## Signing

Release signing keys and passwords must never be committed to Git, copied into
issues, or stored in build logs. Signing material must be backed up separately
from the working copy.

## Publication

1. Update the application version and changelog.
2. Build and verify release artifacts from the tagged commit.
3. Record checksums for binaries and source archives.
4. Create a signed or annotated Git tag.
5. Publish a GitHub Release with the corresponding source and license notices.
6. Preserve build evidence and smoke-test results for the released version.

The first Cool Reader Lite release will define the final tag and version naming
scheme.
