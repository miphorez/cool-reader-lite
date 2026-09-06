# Figma Interface Icons

Local library extracted from `C:\Users\mipho\Downloads\all_icons.svg`.

## Contents

- `index.html` - local icon gallery for visual review.
- `manifest.json` - icon metadata and label-to-file mapping.
- `icons\` - extracted SVG icons, one file per icon.

## Label Format

Labels use this format:

```text
icon:<3-digit-index>:<figma-name>
```

Example:

```text
icon:001:accounts
```

Clicking an icon in `index.html` copies only the label.

## Conversion Request

Use this request format when asking Codex to convert an icon into an Android module:

```text
Take icon:
label: icon:001:accounts
module: feature/cards

Convert it to an Android vector drawable for this module.
```

Optional fields:

```text
drawable: ic_accounts
color: @color/white
tintable: true
replace: false
```

## Codex Lookup Rule

When a request contains `label`, Codex should:

1. Open `manifest.json`.
2. Find the manifest entry with the matching `label`.
3. Read the SVG file from the entry's `file` field.
4. Convert or adapt that SVG into the requested Android `module`.
5. Preserve the original SVG colors unless the request provides a specific color or tint rule.

Default Android target path:

```text
<module>\src\main\res\drawable\
```
