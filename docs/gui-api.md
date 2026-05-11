# EnchantedUI GUI API (WIP)

This project is a Minecraft GUI framework. The public, “easy to use” API entrypoint is `top.diaoyugan.enchanted_ui.client.gui.builder.UI`.

## Tabbed screen

Create a tabbed screen with pages and an optional bottom bar:

```java
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.builder.UI;

import java.util.List;

public final class ExampleScreen extends UI.TabbedScreen {
    public ExampleScreen(Screen parent) {
        super(parent, Component.literal("Example"));

        tab(10, 30, 20, Component.literal("Main"), ctx -> List.<AbstractWidget>of(
                // build widgets using ctx.centerX(), ctx.vertical(...), etc.
        ));

        bottomBar(UI.BottomBar.closeOnly(Component.literal("Close")));
    }
}
```

## Concepts

- `UI.TabbedScreen`: generic tab/page container; handles widget add/remove and tab buttons.
- `UI.Page`: a “page” that can build widgets and optionally handle `tick`, `keyPressed`, and `onSave`.
- `UI.BuildContext`: provides `screenWidth`, `screenHeight`, `centerX`, and a `vertical(...)` helper to create `VerticalLayout`.
- `UI.BottomBar`: small, composable bottom-button helpers (`closeOnly`, `saveAndClose`, `saveAndCloseWithExtra`).
- `UI.FormPage` / `UI.Form`: a higher-level form builder (auto vertical layout, widget factories, and basic keybinding handling).

## Example in codebase

- `common/src/main/java/top/diaoyugan/enchanted_ui/client/gui/screen/DemoScreen.java`
