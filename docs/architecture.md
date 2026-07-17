# EnchantedUI Architecture

## Layers

1. `api.client.gui` is the consumer surface: presets, `UIForm`, `UIPage`, and widget wrappers.
2. `client.gui.screen.base` owns the Minecraft `Screen` lifecycle, tabs, viewports, scrolling, dialogs, and closing.
3. `client.gui.builder` is the form engine. It shares one vertical cursor and dirty-state controller while input and display factories create widgets.
4. `client.gui.widget` contains concrete widgets grouped by behavior.

`standalone` contains integration demos and commands; it is not part of the embeddable common library.

## Presets

`UIConfigScreenPreset` owns the declaration lifecycle and shared builder for tabbed config presets.
Visual presets only configure their tab layout and content viewport:

- `UISidebarConfigScreen`: vertical sidebar.
- `UITopTabbedConfigScreen`: horizontal tabs below a title.
- `UIInfoScreen`: one tabless information/interaction page.

New tabbed presets should extend `UIConfigScreenPreset` instead of copying its builder.

## Runtime invariants

- Forms advance their layout cursor by each widget's final `getHeight()`.
- `PageView` always derives scrolled positions from immutable base coordinates.
- Page widgets and expanded overlays share one clipped content viewport.
- Responsive tab strips show complete buttons only and reveal hidden tabs through navigation, wheel input, or page selection.
- `section(title, builder)` preserves parent alignment; indentation is explicit.

`BaseTabbedScreen` keeps its lifecycle-local PageView, modal, and toast collaborators together. Extract them only when they can expose a stable interface without Screen bridge methods. Internal `UI` is the implementation engine behind `UIForm`, not a second recommended consumer API.
