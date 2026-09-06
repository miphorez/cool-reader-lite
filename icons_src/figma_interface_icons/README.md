# Cool Reader Lite interface icon source

This directory contains the imported Figma interface icon library used by the
incremental Cool Reader Lite icon patch.

- `index.html` is the original local review gallery.
- `manifest.json` maps stable `icon:<index>:<name>` labels to SVG files.
- `icons/` contains the unmodified 24 x 24 SVG masters.
- `README.original.md` preserves the source package notes.
- `tools/icon_patch/icon_mapping.json` records audited Cool Reader mappings.

The Android application does not load these SVG files directly. Approved
mappings are rasterized into the existing drawable resource names and density
sizes by `tools/icon_patch/generate_patch.js`. The script requires Node.js and
the `sharp` package. Runtime tinting remains in the existing View-based UI and
does not change `minSdkVersion` or introduce AndroidX.
