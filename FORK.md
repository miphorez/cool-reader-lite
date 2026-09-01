# Cool Reader Lite Fork Notice

Cool Reader Lite is an independent fork of the CoolReader project maintained at
<https://github.com/buggins/coolreader>.

## Baseline

- Upstream repository: `buggins/coolreader`
- Upstream branch: `master`
- Baseline commit: `dcc2d4c88b7ee66b8ee1223833c1a72cad3cdbc5`
- Upstream application version: `3.2.59-1`
- Baseline tag in this fork: `crl-baseline-3.2.59-1`

## Direction

The fork is intended to provide a smaller and more maintainable Android reader.
The first product direction is offline-first reading with a deliberately reduced
feature surface. Legacy network services, obsolete integrations, and optional
features may be removed after compatibility and migration review.

The rendering engine is retained initially. Android platform modernization and
architectural separation will be performed incrementally with regression tests.

## Relationship to upstream

This project is not endorsed by or affiliated with the upstream maintainers.
Issues specific to Cool Reader Lite should be reported in this repository.
Reusable fixes may be proposed upstream when appropriate.

## Licensing and authorship

The project is distributed under GPL-2.0-or-later. Existing copyright notices,
authorship, and license files must be preserved. Contributors retain copyright
in their original contributions and agree to distribute those contributions
under the project license.

Third-party components and assets may have their own compatible licenses. Their
notices must be retained and reviewed before each public binary release.
