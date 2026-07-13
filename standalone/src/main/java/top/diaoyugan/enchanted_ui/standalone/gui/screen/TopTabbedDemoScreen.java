package top.diaoyugan.enchanted_ui.standalone.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UIBottomBar;
import top.diaoyugan.enchanted_ui.api.client.gui.UITopTabbedConfigScreen;

/** Integration demo that intentionally overflows the horizontal tab strip. */
public final class TopTabbedDemoScreen extends UITopTabbedConfigScreen {

    private int interactions;

    public TopTabbedDemoScreen(Screen parent) {
        super(parent, Component.literal("Top Tab Config Preset"));
    }

    @Override
    protected void buildConfig(Builder config) {
        for (int i = 1; i <= 12; i++) {
            int pageNumber = i;
            config.formPage(Component.literal("Page " + pageNumber), form -> {
                form.infoBlock(
                        Component.literal("Responsive top tabs"),
                        Component.literal("This is page " + pageNumber + ". Resize the window or use the tab arrows and mouse wheel.")
                );
                if (pageNumber == 1) {
                    form.buttonRow(
                            Component.literal("Open Sidebar Preset"),
                            () -> Minecraft.getInstance().setScreenAndShow(new DemoScreen(parent())),
                            Component.literal("Open Information Preset"),
                            () -> Minecraft.getInstance().setScreenAndShow(new InfoDemoScreen(parent()))
                    );
                }
                form.button(Component.literal("Interact"), () -> {
                    interactions++;
                    showToast(Component.literal("Interactions: " + interactions));
                });
            });
        }
        config.bottomBar(UIBottomBar.closeOnly(Component.literal("Close")));
    }
}
