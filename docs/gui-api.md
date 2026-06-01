# EnchantedUI GUI API

This document describes the current public API surface for EnchantedUI.

## Public entrypoint

Preferred package:

`top.diaoyugan.enchanted_ui.api.client.gui`

Main types:

- `EnchantedUI`
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

## Text and localization

All user-facing labels are `Component` values. For reusable mods, pass
`Component.translatable("your_mod.some_key")` instead of `Component.literal(...)`
so the text belongs to your own namespace.

EnchantedUI also ships fallback language keys under
`assets/enchanted_ui/lang/*.json`. Those keys are global to the `enchanted_ui`
namespace, so another mod or resource pack overriding them affects every screen
that uses the fallback. When text belongs to your mod, use the overloads that
accept your own labels or translation keys.

Built-in generated text currently includes:

- `eui.config.rgba.red`
- `eui.config.rgba.green`
- `eui.config.rgba.blue`
- `eui.config.rgba.alpha`
- `eui.config.color_preview`
- `eui.config.keybind.current`
- `eui.config.keybind.none`
- `eui.config.keybind.listening`
- `eui.dropdown.empty`
- `eui.dropdown.add`
- `eui.select.none`
- `eui.validation.duplicate_entry`
- `eui.validation.int.required`
- `eui.validation.int.range`
- `eui.validation.double.required`
- `eui.validation.double.range`
- `eui.display.empty`
- `eui.display.more`
- `eui.dialog.confirm`
- `eui.dialog.unsaved_changes.title`
- `eui.dialog.unsaved_changes.message`
- `eui.dialog.unsaved_changes.discard`
- `eui.dialog.unsaved_changes.cancel`

Example for caller-owned generated text:

```java
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

- `common/src/main/java/top/diaoyugan/enchanted_ui/client/gui/screen/DemoScreen.java`

