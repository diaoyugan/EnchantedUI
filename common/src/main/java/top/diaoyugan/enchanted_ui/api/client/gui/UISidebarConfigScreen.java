package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Preset for the common configuration-screen layout: a vertically stacked
 * sidebar, an automatically positioned form area, and a bottom action bar.
 * <p>
 * Implementations only declare their pages in {@link #buildConfig(Builder)};
 * this class owns the page coordinates and spacing. The declaration is delayed
 * until Minecraft initializes the screen, so subclass fields are fully
 * initialized before page callbacks are captured.
 */
public abstract class UISidebarConfigScreen extends UIConfigScreenPreset {

    public static final int DEFAULT_CONTENT_WIDTH = 220;

    protected UISidebarConfigScreen(
            @Nullable Screen parent,
            Component title,
            Component sidebarTitle
    ) {
        super(parent, title, DEFAULT_CONTENT_WIDTH, 10, 20);
        sidebarTitle(Objects.requireNonNull(sidebarTitle, "sidebarTitle"));
        tabLayout(UITabLayout.sidebar());
        contentViewport(0, 10, 8, 36);
        bottomBar(UIBottomBar.closeOnly(CommonComponents.GUI_DONE));
    }

}
