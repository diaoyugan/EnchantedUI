# EnchantedUI GUI API

This document describes the current public API surface for EnchantedUI.

## Public entrypoint

Preferred package:

`top.diaoyugan.enchanted_ui.api.client.gui`

Main types:

- `EnchantedUI`
- `UIConfigScreenPreset`
- `UISidebarConfigScreen`
- `UITopTabbedConfigScreen`
- `UIInfoScreen`
- `UITabLayout`
- `UITabbedScreen`
- `UIPage`
- `UIFormPage`
- `UIFormSpec`
- `UIForm`
- `UIBottomBar`
- `UIWidget`
- `UISlider`
- `UITextField`
- `UITextArea`
- `UIColorGroup`
- `UILocalization.ColorLabels`
- `UILocalization.FieldValidationMessages`
- `UILocalization.KeyBindingMessages`

## Basic screen example

```java
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.EnchantedUI;
import top.diaoyugan.enchanted_ui.api.client.gui.UIBottomBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UITabbedScreen;

public final class ExampleScreen extends UITabbedScreen {
    public ExampleScreen(Screen parent) {
        super(parent, Component.literal("Example"));

        tab(10, 30, 20, Component.literal("Main"), EnchantedUI.formPage(220, form -> {
            form.title(Component.literal("General"));
            form.toggle(Component.literal("Enabled"), () -> true, value -> {});
            form.button(Component.literal("Run"), () -> showToast(Component.literal("Clicked")));
        }));

        bottomBar(UIBottomBar.closeOnly(Component.literal("Close")));
    }
}
```

## Screen presets

For a conventional mod config screen, extend `UISidebarConfigScreen`. The
preset owns sidebar coordinates, tab spacing, and the default form width, so
the implementation only declares pages and actions:

```java
public final class ExampleConfigScreen extends UISidebarConfigScreen {
    private boolean enabled = true;

    public ExampleConfigScreen(Screen parent) {
        super(
                parent,
                Component.translatable("example.config.title"),
                Component.translatable("example.name")
        );
    }

    @Override
    protected void buildConfig(Builder config) {
        config.formPage(Component.translatable("example.config.general"), form -> {
            form.toggle(
                    Component.translatable("example.config.enabled"),
                    () -> enabled,
                    value -> enabled = value
            );
        });

        config.bottomBar(UIBottomBar.saveAndClose(
                Component.translatable("example.config.close"),
                Component.translatable("example.config.save"),
                this::saveAll
        ));
    }
}
```

Use `config.formPage(...)` for automatically laid-out forms and
`config.page(...)` for a custom `UIPage`. Advanced overloads allow a custom
form width or tab text style without exposing screen coordinates. For small
stateless screens, `EnchantedUI.configScreen(...)` provides the same preset
without a named subclass.

Additional presets:

- `UITopTabbedConfigScreen` renders a centered title with a horizontal tab
  strip below it. Use it when the sidebar would consume too much horizontal
  space.
- `UIInfoScreen` provides one titled, scrollable form surface for about, help,
  status, summary, and similar pages. It supports all display helpers plus
  interactive form elements such as buttons and toggles.

`EnchantedUI.topTabbedConfigScreen(...)` and `EnchantedUI.infoScreen(...)`
provide anonymous variants. The standalone integration demos can be opened
with `/enchantedui demo top` and `/enchantedui demo info`.

### Responsive and overflow behavior

Preset layouts use `UITabLayout`. When tabs exceed the available height or
width, only complete buttons inside the strip viewport are shown; arrow buttons
and the mouse wheel move the tab window. Selecting a page programmatically also
reveals its tab. Page content uses a separate scissored viewport, keeps the
title/tab strip and bottom bar fixed, and scrolls vertically when needed.

Form widths are maximums rather than hard minimums: they shrink to the current
content viewport on small windows. Overlay widgets are clipped to the same
viewport and participate in dynamic vertical scroll-range calculation.

Text-oriented display blocks (`infoBlock`, `emptyState`, `loadingState`, and
`errorState`) wrap their title and body and grow downward automatically. A
custom height is treated as a minimum height. Full prose no longer depends on
the truncated-text hover overlay.

`section(title, builder)` groups controls without changing their horizontal
alignment or width. Use `section(title, indent, builder)` when an intentionally
indented nested layout is desired.

## Text and localization

All user-facing labels are `Component` values. For reusable mods, pass
`Component.translatable("your_mod.some_key")` instead of `Component.literal(...)`
so the text belongs to your own namespace.

Built-in generated text uses `UILocalization.frameworkKey(...)`. Its prefix is
computed from the runtime package of `UILocalization`, so Shadow relocation also
isolates the translation keys. Every built-in component has an English fallback;
the standalone Fabric and NeoForge shells additionally ship localized values for
the original package prefix. Embedded consumers may provide localized values for
their relocated prefix or pass their own labels and translation keys.

Example for caller-owned generated text:

```
form.rgbaSlidersWithPreview(
        Component.translatable("my_mod.color.title"),
        new UILocalization.ColorLabels(
                Component.translatable("my_mod.color.red"),
                Component.translatable("my_mod.color.green"),
                Component.translatable("my_mod.color.blue"),
                Component.translatable("my_mod.color.alpha"),
                Component.translatable("my_mod.color.preview")
        ),
        () -> r, value -> r = value,
        () -> g, value -> g = value,
        () -> b, value -> b = value,
        () -> a, value -> a = value,
        false
);
```

## Core concepts

### `UITabbedScreen`

Generic screen container with:

- tabs
- bottom bar helpers
- automatic page scrolling
- overlay-aware event routing
- toast helpers
- modal dialog helpers

Useful methods:

- `tab(...)`
- `bottomBar(...)`
- `showPage(...)`
- `saveAll()`
- `hasUnsavedChanges()`
- `reloadAll()`
- `requestClose()`
- `showToast(...)`
- `showDialog(...)`
- `showConfirm(...)`
- `unsavedChangesPrompt(...)`

### `UIPage`

Lower-level page contract when you want to build widgets yourself.

Lifecycle hooks:

- `build(...)`
- `onOpen()`
- `onClose()`
- `onShow()`
- `onHide()`
- `onPageChanged(...)`
- `onSave()`
- `tick()`
- `keyPressed(...)`

### `UIFormPage` and `UIFormSpec`

Higher-level page wrapper for form-style screens.

Use `EnchantedUI.formPage(...)` when you want auto layout and the form widget surface.

`UIFormSpec` exposes the same lifecycle model as `UIPage`, but with `UIForm` access.

### `UIBuildContext`

Screen build context:

- `screenWidth()`
- `screenHeight()`
- `centerX()`
- `vertical(...)`
- `horizontal(...)`

## `UIForm` widget surface

Current helpers include:

- `title(...)`
- `space(...)`
- `section(...)`
- `widget(...)`

Buttons:

- `button(...)`
- `buttonRow(...)`
- `iconButton(...)`
- `textureButton(...)`

Display:

- `progressBar(...)`
- `keyValueRow(...)`
- `statusBadge(...)`
- `emptyState(...)`
- `infoBlock(...)`
- `loadingState(...)`
- `errorState(...)`
- `readonlyList(...)`
- `summaryBlock(...)`

Display text is supplied as `Component` values. Helpers that include default status text also expose overloads for custom value, empty, and overflow text where the component needs framework-generated copy.

Boolean / numeric:

- `toggle(...)`
- `toggleRow(...)`
- `intSlider(...)`
- `longSlider(...)`
- `floatSlider(...)`
- `doubleSlider(...)`
- `rgbaSlidersWithPreview(...)`

`toggleRow(...)` includes an overload that accepts one tooltip for each toggle,
so callers do not need to retrieve the first and last row entries manually.

Slider labels are supplied by the caller. `rgbaSlidersWithPreview(...)` has a
fallback overload that uses the built-in RGBA keys listed above, and a safer
overload that accepts `UILocalization.ColorLabels`. Use `UILocalization.ColorLabels` with your own
namespace when building a screen for another mod.

Text input:

- `textField(...)`
- `intField(...)`
- `doubleField(...)`
- `textArea(...)`

`intField(...)` and `doubleField(...)` validate input before saving. Their
fallback error messages use the validation keys listed above. Use the overloads
that accept `UILocalization.FieldValidationMessages` to provide your own translation keys, or
`textField(..., UITextValidator)` for fully custom validation rules.

Keybinding:

- `keyBinding(...)`
- `combinationKeyBinding(...)`

Key binding helpers have fallback generated text for current, none, and
listening states. Use the overloads that accept `UILocalization.KeyBindingMessages` when
those labels should use your own namespace.

Lists and selection:

- `dropdownList(...)`
- `editableDropdownList(...)`
- `select(...)`
- `enumSelect(...)`
- `searchableSelect(...)`
- `multiSelect(...)`
- `radioGroup(...)`

Dropdown and select helpers also include overloads for generated text such as
empty-list text, duplicate-entry errors, and the "none selected" label. Prefer
those overloads when the default copy would appear in your mod's screen.

Validation:

- `validate()`
- `UITextValidator`

Form state:

- `save()`
- `hasUnsavedChanges()`
- `reload()`
- `markClean()`

Widget state:

- `visibleIf(...)`
- `activeIf(...)`
- `disabledTooltip(...)` / `inactiveTooltip(...)`

Tabbed screens can call `sidebarTitle(Component)` to render a title above the
tabs. The tab column expands to fit the title and the title is centered against
the computed column width.

## Widget wrappers

The public API returns wrapper types rather than exposing internal widget classes directly.

Current wrappers include:

- `UIWidget`
- `UISlider`
- `UITextField`
- `UITextArea`
- `UIColorGroup`

Most controls return `UIWidget`. Sliders, single-line text fields, and text
areas return specialized wrappers because they expose extra value helpers.

Common `UIWidget` capabilities:

- tooltip helpers
- visible / active / focused state
- position and size updates
- message updates
- access to the underlying vanilla widget through `vanilla()`

## Dialog and feedback helpers

`UITabbedScreen` currently supports:

- `showToast(Component)`
- `showToast(Component, int durationTicks)`
- `showDialog(Component title, List<Component> lines, UIDialogAction... actions)`
- `showConfirm(Component title, Component message, Runnable confirmAction)`
- `showConfirm(Component title, Component message, Component confirmLabel, Component cancelLabel, Runnable confirmAction)`
- `unsavedChangesPrompt(UIUnsavedChangesPrompt prompt)`

Screens with dirty pages use the built-in localized unsaved changes prompt by default. Override it per screen when the prompt needs business-specific wording:

```java
unsavedChangesPrompt(UIUnsavedChangesPrompt.of(
        Component.literal("Discard profile edits?"),
        Component.literal("The current profile has unsaved changes.")
));
```

Use the full factory when the action labels also need custom copy:

```java
unsavedChangesPrompt(UIUnsavedChangesPrompt.of(
        Component.literal("Leave editor?"),
        List.of(Component.literal("Unsaved rule changes will be lost.")),
        Component.literal("Leave"),
        Component.literal("Keep editing")
));
```

## Internal example

Reference implementation:

- `standalone/src/main/java/top/diaoyugan/enchanted_ui/standalone/gui/screen/DemoScreen.java`

