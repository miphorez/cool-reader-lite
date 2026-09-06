Cool Reader Lite app icon sources

The canonical colored logo is `cr3_logo.svg`. It uses balanced sepia
`#9C704A` on a transparent background.

The adaptive launcher foreground is `cr3_logo_adaptive_foreground.svg`.
Its additional padding keeps the complete mark inside Android launcher masks.
The adaptive background uses the reader page color `#ECE3CB`.

Run `tools/icon_patch/generate_app_logo.ps1` from the repository to regenerate
all launcher, adaptive foreground, toolbar, notification, and high-contrast PNG
resources. The legacy `convert_all.pl` script uses the same canonical SVG files.

Historical raster source files remain in `source/` for reference only.

Context color behavior:

- Launcher, About dialog, help content, and large notification icons keep sepia.
- Toolbar and popup menu icons receive the shared `textColorToolBarLabel` tint.
- Small notification icons use the white monochrome high-contrast resource so
  Android can apply the system notification tint.
