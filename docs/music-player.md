# Music player UI preset

The common module now includes a responsive music-library preset and reusable primitives for other dense screens.

## Open the preset

Implement `UIMusicPlayerModel`, then return the screen from your client entry point:

```java
Screen screen = EnchantedUI.musicPlayer(parent, Component.literal("Music Library"), playerModel);
```

The model exposes observable tracks, playlists, selection, current playback, progress, and volume. Transport methods remain owned by the consuming mod, so EUI does not assume a particular audio engine.

The standalone integration demo can be opened from the main demo screen or directly with:

```text
/enchantedui demo music
```

The preset is tuned for Minecraft's scaled GUI dimensions: it enables three columns from roughly 480 logical pixels, keeps a compact playlist sidebar plus a now-playing strip at common 320–480 widths, and only reduces the sidebar further at unusually narrow sizes. Its transport controls remain in the fixed bottom dock rather than scrolling with tracks.

## Reusable primitives

```java
UIState<Track> selected = UIState.of(null);

UIVirtualList<Track, String> list = UIVirtualList.<Track, String>builder(Track::id)
        .bounds(x, y, width, height)
        .items(player::tracks)
        .rowHeight(24)
        .selected(selected::get)
        .onSelect(selected::set)
        .onActivate(player::play)
        .playing(player::isPlaying)
        .filter((track, query) -> track.searchText().contains(query))
        .build();
```

`UIVirtualList` renders only visible rows and supports stable key selection, single/multiple selection, double-click and Enter activation, Home/End/arrow navigation, filtering, scrolling to a key, distinct playing/selected/hover/focus states, and a row action zone.

Other additions include:

- `UISplitLayout`, `UIGridLayout`, `UIStackLayout`, `UIScreenLayout`, `UIScrollPane`, `UIInsets`, and `UIBounds`
- `UIImageWidget` with contain/cover/stretch fitting, circle/rounded masking, rotation, scaling, opacity, pivot, border, background, shadow, and dynamic textures
- frame-time `UIAnimation` clocks with once/loop/ping-pong modes, pause continuity, easing, and reduced-motion control
- `UIState`, `UIBinding`, `UIComputed`, and detachable `UISubscription`
- public `UIIconButton` dynamic icon, toggle-selected, circular, tooltip, action, and narration support
- `UITheme` tokens for colors, spacing, typography, radii, shadows, and animation timing
- arbitrary fixed bottom docks via `UIBottomBar.dock(...)`

## Sorting, playback modes, and registration paths

The integrating mod controls the available sort options and playback modes through `UIMusicPlayerModel`. EUI cycles the supplied values and never implements audio queue behavior itself.

```java
UIState<UIPlaybackMode> mode = UIState.of(UIPlaybackMode.ALL_LOOP);
UIState<UIMusicSortOption> sort = UIState.of(UIMusicSortOption.DEFAULT);

@Override public UIBinding<UIPlaybackMode> playbackMode() { return mode; }
@Override public void playbackMode(UIPlaybackMode value) { mode.set(value); }
@Override public UIBinding<UIMusicSortOption> sort() { return sort; }
@Override public void sort(UIMusicSortOption value) { sort.set(value); }
```

Registration navigation is also model-owned. A mod may expose provider, directory, album, or custom nodes in any hierarchy:

```java
new UIMusicRegistrationPath(
        "external_album",
        "external_files",
        Component.literal("My Album"),
        UIMusicRegistrationPath.Kind.ALBUM,
        track -> track.registrationPathId().equals("external_album")
);
```

This supports structures such as `Vanilla file scan / Album` and `External file registration / Album` without coupling EUI to a scanner or filesystem.

## User-created playlists

Implement `UIMusicPlaylistController` and return it from `UIMusicPlayerModel.playlistController()`. The player then exposes an **Edit playlists** entry. The supplied GUI supports creating, renaming, deleting, and editing membership with a multi-select virtual list. Persistence remains entirely controlled by the mod.

The editor can also be opened directly:

```java
Screen editor = EnchantedUI.userPlaylistEditor(parent, title, playlistController);
```
