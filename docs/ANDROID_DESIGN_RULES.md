# Android Design Rules

These rules define the shared visual direction for Cool Reader Lite Android UI.

## Visual language

- Use flat surfaces, flat icons, and flat colors.
- Do not add gradients, decorative elevation, or ornamental shadows.
- Separate regions with a subtle theme-derived tonal fill or a thin divider.
- Derive visual colors from the active interface theme so the treatment follows theme changes.

## Interaction feedback

- Do not show transient pressed, focused, hovered, or ripple highlights for touch actions.
- Keep persistent states such as checked, selected, active, and disabled visually distinct.
- Preserve the existing action callback and confirmation behavior when removing transient feedback.

## Screen spacing

- Use `@dimen/screen_horizontal_padding` as the standard horizontal content gutter.
- The standard gutter is 16dp on both sides in portrait and landscape.
- Full-width structural surfaces such as top-bar backgrounds may extend edge to edge.
- Top-bar action touch targets may occupy their edge slots; align titles and screen content to the shared spacing system.

## Top bars

- Use `ScreenTopBar` for the unified screen-level top bar.
- Keep titles on one line and truncate with a visible `...` suffix when needed.
- Resolve system screen titles from localized resources when the screen is shown.
- Use the shared chevron for Back and the vertical three-dot glyph for More.
- Keep top bars flat and use the active theme's tonal palette for separation.

## Book covers

- Render cover thumbnails as flat images without decorative drop shadows or depth effects.
- Give cover thumbnails 8dp of vertical spacing above and below in lists and on the home screen.
