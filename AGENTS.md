# Cool Reader Lite Agent Rules

## Language and scope

- Keep repository text, code, identifiers, commit messages, pull request text,
  tags, release notes, and asset names in English.
- Make the smallest change that fully satisfies the request.
- Do not commit local IDE metadata, credentials, signing material, private user
  data, or temporary release files.
- Use `master` as the protected default branch. Repository changes must reach it
  through a pull request.
- Follow `docs/ANDROID_DESIGN_RULES.md` for Android UI changes. New or updated
  screens must use the shared flat visual language, theme-derived colors,
  disabled transient touch highlights, and the 16dp horizontal content gutter.

## Build authorization

- Do not run Gradle unless the user explicitly asks for a build or compilation
  verification.
- An end-to-end request to create a pull request, merge it into `master`, and
  update the download file is explicit authorization for the Gradle builds and
  artifact checks required by the release workflow below.

## End-to-end preview release request

Treat a request whose meaning is "create the pull request, merge it into
master, and update the download file" as one complete preview-release task.
Do not stop after merging the code or after editing a link. Complete every
applicable step below in the same task.

1. Read the live values in `android/app/build.gradle`, inspect existing GitHub
   releases, and read the current Obsidian download note. Do not rely on a
   remembered version.
2. Select the next unused preview `versionName`. If the current value has
   already been published, increment the preview ordinal on the current version
   line. A stable release requires an explicit request and release signing.
3. Update `versionName` and the base `versionCode` in the same pull request as
   the user-visible change. Never defer the version bump to a correction pull
   request.
4. Preserve the ABI version-code invariant described below and verify that the
   new codes cannot overlap any previously published build.
5. Build and verify the candidate before merge. Create a feature branch, commit
   only intended files, push it, and open the pull request with
   `--repo miphorez/cool-reader-lite`.
6. Wait for every required pull request check. Merge only after all required
   checks and the candidate build pass. The end-to-end request explicitly
   authorizes this merge.
7. Synchronize the local `master` with `origin/master`, verify the clean checkout
   and exact merge commit, and rebuild the distributable APK from this merged
   commit. Never publish an APK produced from the pre-merge feature branch.
8. Verify the final APK package, `versionName`, `versionCode`, SDK levels,
   signature, included ABIs, size, SHA-256, and source commit. When a suitable
   ADB device is connected, install, launch, and run the applicable smoke test.
   Report device verification separately from build verification.
9. Create a new GitHub pre-release targeting the exact merged `master` commit.
   The tag must be `v<versionName>`. Never replace an existing tag or asset
   unless the user explicitly requests replacement.
10. Upload the verified universal debug APK with a versioned filename. State
    clearly that it is debug-signed and is not a production release.
11. Update the Obsidian download note at
    `C:\D_Drive\Dynalist\Dynalist\Работа\Мои проекты\Cool Reader\GitHub\01_Скачать_и_установить.md`
    with the version, publication date, release page, direct APK URL, byte size,
    SHA-256, package name, and exact source commit.
12. Verify the published asset state, size, and digest against the local APK,
    then remove only temporary upload copies. Report the pull request, merge
    commit, release, direct download URL, checksum, and updated note.

If a required check, build, package inspection, signature inspection, upload,
or digest comparison fails, do not claim completion. Do not merge or publish
past a failed gate; report the exact blocker.

## Android version codes and ABI splits

`android/app/build.gradle` defines a base `versionCode`. ABI-specific outputs use
this formula:

`outputVersionCode = baseVersionCode + abiCode`

The current ABI code map is:

- `armeabi`: `1`
- `armeabi-v7a`: `2`
- `mips`: `3`
- `x86`: `4`
- `x86_64`: `5`
- `arm64-v8a`: `6`
- `mips64`: `7`

Before every release, calculate the increment from the live map rather than
copying this list blindly. The next base code must satisfy:

`newBaseVersionCode > previousBaseVersionCode + max(abiCodes)`

With the current maximum ABI code of `7`, the base `versionCode` must increase
by at least `8`. For example, after base code `32594`, the next base code must be
at least `32602`; its highest current ABI-specific code is then `32609`.

The universal APK uses the base code. ABI-specific APKs use the offset codes.
Changing `versionName` without applying this rule is incomplete release work.

## Release documentation

- Follow `docs/RELEASE_PROCESS.md` for the detailed release gates.
- The Obsidian release workflow is documented in
  `GitHub/02_Версионирование_и_публикация.md` inside the Cool Reader project
  vault.
