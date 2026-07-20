# Enchanted UI

English | [简体中文](readme_zhcn.md)

EnchantedUI is a Minecraft client-side GUI framework for building modular screens, tabbed tools, settings panels, and reusable widgets.

> ⚠️ Early development version
>  
> The API is still moving. Expect refactors, missing abstractions, and breaking changes while the framework surface is being shaped.

> ⚠️ The project's documentation is currently AI-generated and will be rewritten once the project is more mature.

## Current scope

EnchantedUI is no longer just a small config-screen helper. The current codebase provides:

- tabbed screens with page lifecycle hooks
- sidebar, top-tabbed, and single-page information screen presets
- responsive tab overflow navigation and clipped content viewports
- form-style builders for common widgets
- automatic page scrolling when content exceeds the viewport
- overlay-aware dropdown and list widgets
- validated text input
- single-select, enum-select, searchable select, multi-select, and editable list widgets
- keybinding widgets
- toast and modal dialog helpers
- a small public wrapper layer for common widget operations
- basic display blocks for progress, status, key-value data, readonly lists, summaries, loading, errors, and empty states
- caller-owned localization for framework-generated UI text

## Public API

Preferred public entrypoints live under:

`top.diaoyugan.enchanted_ui.api.client.gui`

Main entry helpers:

- `EnchantedUI`
- `UIConfigScreenPreset`
- `UISidebarConfigScreen`
- `UITopTabbedConfigScreen`
- `UIInfoScreen`
- `UITabLayout`
- `UITabbedScreen`
- `UIFormPage`
- `UIForm`
- `UIPage`
- `UIBottomBar`
- `UIWidget`
- `UISlider`
- `UITextField`
- `UITextArea`
- `UILocalization`

The older internal builder entrypoint `top.diaoyugan.enchanted_ui.client.gui.builder.UI` still exists, but it should be treated as implementation-facing, not the preferred external surface.

## Example

```java
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UIBottomBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UISidebarConfigScreen;

public final class ExampleScreen extends UISidebarConfigScreen {
    public ExampleScreen(Screen parent) {
        super(
                parent,
                Component.translatable("example.screen.title"),
                Component.translatable("example.name")
        );
    }

    @Override
    protected void buildConfig(Builder config) {
        config.formPage(Component.translatable("example.tab.main"), form -> {
            form.title(Component.translatable("example.section.general"));
            form.toggle(Component.translatable("example.enabled"), () -> true, value -> {});
            form.button(Component.translatable("example.ping"), () -> showToast(Component.translatable("example.clicked")));
        });

        config.bottomBar(UIBottomBar.closeOnly(Component.translatable("example.close")));
    }
}
```

Use `UITopTabbedConfigScreen` for horizontal tabs below a centered title and
`UIInfoScreen` for a single scrollable information page with optional
interactive controls. Extend `UITabbedScreen` directly when a custom layout is
more appropriate than a preset.

## Text and localization

All labels are `Component` values. For reusable mods, prefer
`Component.translatable("your_mod.some_key")` so text belongs to your own
namespace.

Framework-generated text uses keys derived from
`UILocalization.class.getPackageName()` and includes an English fallback.
Relocating the library therefore relocates its default translation keys too.
Use the overloads that accept custom `Component` values or `UILocalization.*`
records when the consumer mod owns the wording.

## Widget surface today

`UIForm` currently exposes:

- text/title widgets
- buttons, button rows, icon buttons, texture buttons
- progress bars, key-value rows, status badges, info blocks, loading states, error states, readonly lists, summary blocks, and empty-state blocks
- toggles and toggle rows
- integer, long, float, and double sliders
- validated text fields
- integer and double number fields
- multi-line text areas with `UITextArea` value access
- keybinding widgets
- color preview groups
- readonly dropdown lists
- editable dropdown lists
- select, enum-select, searchable select, multi-select
- radio groups
- section nesting and custom widget mounting
- wrapped display blocks that grow with their title and body text
- form dirty-state helpers (`hasUnsavedChanges()`, `save()`, `reload()`, `markClean()`)
- widget state conditions (`visibleIf(...)`, `activeIf(...)`)

Most controls return `UIWidget`; sliders, text fields, text areas, and color
groups return specialized wrappers when they expose extra value helpers. Display
text is provided as `Component` values, and default generated copy can be
overridden through the relevant overloads.

At screen level, `UITabbedScreen` currently exposes:

- tabs and bottom bars
- page lifecycle hooks through `UIPage` / `UIFormSpec`
- `showToast(...)`
- `showDialog(...)`
- `showConfirm(...)`
- `unsavedChangesPrompt(...)`

## Demo coverage

The standalone module includes three mutually navigable integration demos:

- `/enchantedui demo` — sidebar config preset
- `/enchantedui demo top` — overflowing horizontal tab preset
- `/enchantedui demo info` — long, interactive information page

Together they exercise:

- form widgets
- validated input
- buttons and actions
- selection widgets
- display widgets
- editable and readonly lists
- scrolling
- toast and dialog helpers
- page lifecycle callbacks
- responsive tab overflow and viewport clipping

## Internal testing artifacts

For local integration testing, the project currently publishes:

- `enchanted_ui-common-<minecraft_version>`
- `enchanted_ui-fabric-<minecraft_version>`
- `enchanted_ui-neoforge-<minecraft_version>`

Publish them with:

```powershell
./gradlew publishForInternalTesting
```

Output:

```text
build/test-maven
```

## Recommended usage modes

Standalone runtime dependency:

- compile against `enchanted_ui-common`
- place the matching `enchanted_ui-fabric` or `enchanted_ui-neoforge` shell in
  the game `mods` folder

Embedded usage:

- shade/merge only `enchanted_ui-common`
- relocate `top.diaoyugan.enchanted_ui` into a private package owned by the
  consumer mod
- do not embed either platform shell, because those jars intentionally contain
  Loader metadata and the public standalone resource namespace

See [docs/internal-testing-usage.md](docs/internal-testing-usage.md) for the concrete local testing flow.

## Documentation

- [docs/gui-api.md](docs/gui-api.md)
- [docs/architecture.md](docs/architecture.md)
- [docs/internal-testing-usage.md](docs/internal-testing-usage.md)

## Notes

- Maven coordinates are still temporary
- the docs describe the current public surface, but the API may still change
- backward compatibility is not guaranteed during this phase

