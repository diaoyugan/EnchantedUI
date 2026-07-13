package top.diaoyugan.enchanted_ui.standalone.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UIForm;
import top.diaoyugan.enchanted_ui.api.client.gui.UIInfoScreen;

/** Integration demo for long, interactive single-page content. */
public final class InfoDemoScreen extends UIInfoScreen {

    private int interactions;

    public InfoDemoScreen(Screen parent) {
        super(parent, Component.literal("Interactive Information Preset"), Component.literal("Close"));
    }

    @Override
    protected void buildContent(UIForm form) {
        form.infoBlock(
                Component.literal("About this preset"),
                Component.literal("Long content is clipped to the content viewport and scrolls independently of the title and bottom bar.")
        );
        form.buttonRow(
                Component.literal("Open Sidebar Preset"),
                () -> Minecraft.getInstance().setScreenAndShow(new DemoScreen(parent())),
                Component.literal("Open Top Tabs Preset"),
                () -> Minecraft.getInstance().setScreenAndShow(new TopTabbedDemoScreen(parent()))
        );
        form.statusBadge(
                Component.literal("Interaction status"),
                () -> Component.literal(interactions == 0 ? "Waiting" : "Used " + interactions + " times"),
                () -> interactions == 0 ? 0xFF777777 : 0xFF3C6E47
        );
        form.progressBar(Component.literal("Demo progress"), () -> Math.min(1.0D, interactions / 10.0D));
        form.button(Component.literal("Run interaction"), () -> {
            interactions++;
            showToast(Component.literal("Information action " + interactions));
        });

        for (int i = 1; i <= 24; i++) {
            form.keyValueRow(
                    Component.literal("Information row " + i),
                    () -> Component.literal("Visible through automatic scrolling")
            );
        }
    }
}
