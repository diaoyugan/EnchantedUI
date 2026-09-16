# EnchantedUI Architecture

## Layers

1. `api.client.gui` is the consumer surface: presets, `UIForm`, `UIPage`, and widget wrappers.
2. `client.gui.screen.base` owns the Minecraft `Screen` runtime. `BaseTabbedScreen` is the event/lifecycle bridge; package-private collaborators own concrete runtime state.
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
- `PageViewport` always derives scrolled positions from immutable base coordinates.
- Page widgets and expanded overlays share one clipped content viewport.
- Responsive tab strips show complete buttons only and reveal hidden tabs through navigation, wheel input, or page selection.
- `section(title, builder)` preserves parent alignment; indentation is explicit.

`BaseTabbedScreen` is the Minecraft event and lifecycle bridge, not the implementation
home for every screen feature. Its package-private collaborators have explicit ownership:

- `TabStripController`: tab measurement, placement, overflow, navigation, and selection.
- `PageStackController`: page definitions, active-page lifecycle, and event routing.
- `PageViewport`: widget coordinates, clipping, scrolling, and expanded overlays.
- `CloseCoordinator`: unsaved-change prompting and close confirmation.
- `ScreenModal`, `ToastController`, and `BottomBarBackdropWidget`: transient presentation state.

These collaborators remain package-private so the public screen API stays stable. Do not
move their state back into `BaseTabbedScreen` merely to avoid a small bridge method.

`UIForm` is the only consumer-facing form model and directly owns shared layout,
state, interaction routing, and lifecycle data. It delegates widget creation to
`FormInputFactory` and `FormDisplayFactory`. New controls should be added to a
factory and exposed once through `UIForm`; there is no parallel internal form model.

Every overloaded control family has one public canonical overload containing
the complete parameter set. Shorter overloads only supply named defaults and
delegate to that canonical overload. Factories implement only the canonical
shape; they do not duplicate convenience overloads.

Input widgets may consume keyboard and mouse events while visibly recording a
value. Runtime polling, binding activation, registration, persistence, and
global `KeyMapping` mutation are outside the common GUI library.
