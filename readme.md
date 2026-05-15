# Enchanted UI

English | [简体中文](readme_zhcn.md)

EnchantedUI is a Minecraft client-side GUI framework for building modular screens, tabbed tools, settings panels, and reusable widgets.

> Early development version
>  
> The API is still moving. Expect refactors, missing abstractions, and breaking changes while the framework surface is being shaped.

## Current scope

EnchantedUI is no longer just a small config-screen helper. The current codebase provides:

- tabbed screens with page lifecycle hooks
- form-style builders for common widgets
- automatic page scrolling when content exceeds the viewport
- overlay-aware dropdown and list widgets
- validated text input
- single-select, enum-select, searchable select, multi-select, and editable list widgets
- keybinding widgets
- toast and modal dialog helpers
- reusable widget wrappers in the public API package

## Public API

Preferred public entrypoints live under:

`top.diaoyugan.enchanted_ui.api.client.gui`

Main entry helpers:

- `EnchantedUI`
- `UiTabbedScreen`
- `UiFormPage`
- `UiForm`
- `UiPage`
- `UiBottomBar`

The older internal builder entrypoint `top.diaoyugan.enchanted_ui.client.gui.builder.UI` still exists, but it should be treated as implementation-facing, not the preferred external surface.

## Example

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
            form.button(Component.literal("Ping"), () -> showToast(Component.literal("Clicked")));
        }));

        bottomBar(UiBottomBar.closeOnly(Component.literal("Close")));
    }
}
```

## Widget surface today

`UiForm` currently exposes:

- text/title widgets
- buttons, button rows, icon buttons, texture buttons
- toggles and toggle rows
- integer sliders
- validated text fields
- multi-line text areas
- keybinding widgets
- color preview groups
- readonly dropdown lists
- editable dropdown lists
- select, enum-select, searchable select, multi-select
- radio groups
- section nesting and custom widget mounting

At screen level, `UiTabbedScreen` currently exposes:

- tabs and bottom bars
- page lifecycle hooks through `UiPage` / `UiFormSpec`
- `showToast(...)`
- `showDialog(...)`
- `showConfirm(...)`

## Demo coverage

`DemoScreen` is the current integration surface check for the framework. It exercises:

- form widgets
- validated input
- buttons and actions
- selection widgets
- editable and readonly lists
- scrolling
- toast and dialog helpers
- page lifecycle callbacks

## Internal testing artifacts

For local integration testing, the project currently publishes:

- `enchanted_ui-common-<minecraft_version>`
- `enchanted_ui-fabric-<minecraft_version>`

Publish them with:

```powershell
./gradlew publishForInternalTesting
```

Output:

```text
build/test-maven
```

The NeoForge side is intentionally not part of the active testing path yet.

## Recommended usage modes

Standalone runtime dependency:

- compile against `enchanted_ui-common`
- place `enchanted_ui-fabric` in the game `mods` folder

Embedded usage:

- embed `enchanted_ui-common` into another mod when you want to ship the framework implementation with it

Fabric nested-mod packaging:

- use `modImplementation` plus `include` on `enchanted_ui-fabric`

See [docs/internal-testing-usage.md](docs/internal-testing-usage.md) for the concrete local testing flow.

## Documentation

- [docs/gui-api.md](docs/gui-api.md)
- [docs/internal-testing-usage.md](docs/internal-testing-usage.md)

## Notes

- Maven coordinates are still temporary
- documentation may lag behind the latest refactors
- backward compatibility is not guaranteed during this phase
