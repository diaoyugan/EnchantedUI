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

Text input:

- `textField(...)`
- `intField(...)`
- `doubleField(...)`
- `textArea(...)`

Keybinding:

- `keyBinding(...)`
- `combinationKeyBinding(...)`

Lists and selection:

- `dropdownList(...)`
- `editableDropdownList(...)`
- `select(...)`
- `enumSelect(...)`
- `searchableSelect(...)`
- `multiSelect(...)`
- `radioGroup(...)`

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
- `UIButton`
- `UIText`
- `UIToggle`
- `UISlider`
- `UITextField`
- `UIDropdownList`
- `UIEditableDropdownList`
- `UIKeyBinding`
- `UICombinationKeyBinding`
- `UIColorPreview`

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

