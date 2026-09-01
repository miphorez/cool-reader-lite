# Upstream Synchronization

The local repository uses these remotes:

- `origin`: `https://github.com/miphorez/cool-reader-lite.git`
- `upstream`: `https://github.com/buggins/coolreader.git`

The upstream push URL is disabled locally to prevent accidental writes.

## Policy

Upstream changes are reviewed before integration. Do not merge upstream
`master` automatically into a release branch.

1. Fetch both remotes.
2. Review the upstream commit range and release notes.
3. Create a dedicated synchronization branch from local `master`.
4. Merge or cherry-pick the selected upstream changes.
5. Resolve conflicts without discarding fork-specific security or scope changes.
6. Run the verification appropriate to the affected Android, JNI, and engine areas.
7. Submit the synchronization through a pull request.
8. Record the last integrated upstream commit in the pull request and release notes.

Reusable bug fixes developed in this fork may be submitted upstream separately.
