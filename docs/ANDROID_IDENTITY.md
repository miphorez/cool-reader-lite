# Android Identity

## Installed application identity

- Product name: `Cool Reader Lite`
- Application ID: `io.github.miphorez.coolreaderlite`
- Upstream baseline version: `3.2.59-1`

The new application ID makes Cool Reader Lite independent from the upstream
Android application. It can be installed alongside an upstream build and cannot
replace or update an upstream installation signed by another key.

Application-private data is isolated by Android. Existing upstream settings,
history, and books are not migrated automatically.

## Source namespace

The Java namespace remains `org.coolreader` temporarily. Native JNI entry points,
ProGuard rules, manifests, and a large part of the source tree depend on these
class names. Renaming the source namespace before adding JNI regression coverage
would create unnecessary risk without changing the installed application
identity.

Runtime intent actions and notification channel identifiers use the generated
`BuildConfig.APPLICATION_ID` where possible. The application process uses the
default Android process name derived from the application ID.

## External integrations

Google Drive implementation code is currently disabled and no active OAuth
client configuration is present. Re-enabling it requires credentials registered
for the new application ID and the release signing certificate.

The legacy donation UI, Android Market links, billing service integration, and
billing permission have been removed. Cool Reader Lite does not configure or
offer in-app billing products.

The inherited icon is temporary and remains subject to the asset-license and
branding audit. A distinct Cool Reader Lite icon is required before the first
public binary release.

## Deferred migration

A future source-namespace migration must include JNI symbol updates, generated
headers, ProGuard rules, Java package paths, manifests, tests, and upgrade notes.
It should not be combined with unrelated feature work.
