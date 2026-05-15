# EnchantedUI GUI API

This document describes the current public API surface for EnchantedUI.

## Public entrypoint

Preferred package:

`top.diaoyugan.enchanted_ui.api.client.gui`

Main types:

- `EnchantedUI`
- `UiTabbedScreen`
- `UiPage`
- `UiFormPage`
- `UiFormSpec`
- `UiForm`
- `UiBottomBar`

## Basic screen example

```java
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.EnchantedUI;
import top.diaoyugan.enchanted_ui.api.client.gui.UiBottomBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UiTabbedScreen;

public final class ExampleScreen extends UiTabbedScreen {
    public ExampleScreen(Screen parent) {
        super(parent, Component.literal("Example"));

        tab(10, 30, 20, Component.literal("Main"), EnchantedUI.formPage(220, form -> {
            form.title(Component.literal("General"));
            form.toggle(Component.literal("Enabled"), () -> true, value -> {});
            form.button(Component.literal("Run"), () -> showToast(Component.literal("Clicked")));
        }));

        bottomBar(UiBottomBar.closeOnly(Component.literal("Close")));
    }
}
```

## Core concepts

### `UiTabbedScreen`

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
- `showToast(...)`
- `showDialog(...)`
- `showConfirm(...)`

### `UiPage`

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

### `UiFormPage` and `UiFormSpec`

Higher-level page wrapper for form-style screens.

Use `EnchantedUI.formPage(...)` when you want auto layout and the form widget surface.

`UiFormSpec` exposes the same lifecycle model as `UiPage`, but with `UiForm` access.

### `UiBuildContext`

Screen build context:

- `screenWidth()`
- `screenHeight()`
- `centerX()`
- `vertical(...)`
- `horizontal(...)`

## `UiForm` widget surface

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

Boolean / numeric:

- `toggle(...)`
- `toggleRow(...)`
- `intSlider(...)`
- `rgbaSlidersWithPreview(...)`

Text input:

- `textField(...)`
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
- `UiTextValidator`

## Widget wrappers

The public API returns wrapper types rather than exposing internal widget classes directly.

Current wrappers include:

- `UiWidget`
- `UiButton`
- `UiText`
- `UiToggle`
- `UiSlider`
- `UiTextField`
- `UiDropdownList`
- `UiEditableDropdownList`
- `UiKeyBinding`
- `UiCombinationKeyBinding`
- `UiColorPreview`

Common `UiWidget` capabilities:

- tooltip helpers
- visible / active / focused state
- position and size updates
- message updates
- access to the underlying vanilla widget through `vanilla()`

## Dialog and feedback helpers

`UiTabbedScreen` currently supports:

- `showToast(Component)`
- `showToast(Component, int durationTicks)`
- `showDialog(Component title, List<Component> lines, UiDialogAction... actions)`
- `showConfirm(Component title, Component message, Runnable confirmAction)`

## Internal example

Reference implementation:

- `common/src/main/java/top/diaoyugan/enchanted_ui/client/gui/screen/DemoScreen.java`
