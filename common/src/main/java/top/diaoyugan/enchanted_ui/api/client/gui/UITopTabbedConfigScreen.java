package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Configuration-screen preset with a centered title and a horizontal tab strip.
 * Tabs automatically collapse into a scrollable window when they do not fit.
 */
public abstract class UITopTabbedConfigScreen extends UIConfigScreenPreset {

    public static final int DEFAULT_CONTENT_WIDTH = 320;
    protected UITopTabbedConfigScreen(@Nullable Screen parent, Component title) {
        super(parent, title, DEFAULT_CONTENT_WIDTH, 60, 20);
        headerTitle(Objects.requireNonNull(title, "title"));
        tabLayout(UITabLayout.top());
        contentViewport(8, 56, 8, 36);
        bottomBar(UIBottomBar.closeOnly(CommonComponents.GUI_DONE));
    }

}
