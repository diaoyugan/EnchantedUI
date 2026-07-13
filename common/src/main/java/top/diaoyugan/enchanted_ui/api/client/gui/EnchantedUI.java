package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Small entrypoint for building EnchantedUI screens and form pages.
 * <p>
 * Most mods start with {@link #tabbed(Screen, Component)} for a full screen or
 * {@link #formPage(int, UIFormSpec)} when adding a form tab to a screen.
 */
public final class EnchantedUI {

    private EnchantedUI() {
    }

    /**
     * Creates a tabbed screen container.
     *
     * @param parent the screen to return to when this screen closes, or {@code null}
     * @param title the screen title shown by Minecraft
     */
    public static UITabbedScreen tabbed(@Nullable Screen parent, Component title) {
        return new UITabbedScreen(parent, title);
    }

    /**
     * Creates a sidebar config screen without requiring a named subclass.
     * Prefer extending {@link UISidebarConfigScreen} when the screen owns state
     * or helper methods.
     */
    public static UISidebarConfigScreen configScreen(
            @Nullable Screen parent,
            Component title,
            Component sidebarTitle,
            java.util.function.Consumer<UIConfigScreenPreset.Builder> config
    ) {
        java.util.Objects.requireNonNull(config, "config");
        return new UISidebarConfigScreen(parent, title, sidebarTitle) {
            @Override
            protected void buildConfig(Builder builder) {
                config.accept(builder);
            }
        };
    }

    /** Creates the horizontal-tab config preset without a named subclass. */
    public static UITopTabbedConfigScreen topTabbedConfigScreen(
            @Nullable Screen parent,
            Component title,
            java.util.function.Consumer<UIConfigScreenPreset.Builder> config
    ) {
        java.util.Objects.requireNonNull(config, "config");
        return new UITopTabbedConfigScreen(parent, title) {
            @Override
            protected void buildConfig(Builder builder) {
                config.accept(builder);
            }
        };
    }

    /** Creates an interactive single-page information preset without a named subclass. */
    public static UIInfoScreen infoScreen(
            @Nullable Screen parent,
            Component title,
            Component closeLabel,
            java.util.function.Consumer<UIForm> content
    ) {
        java.util.Objects.requireNonNull(content, "content");
        return new UIInfoScreen(parent, title, closeLabel) {
            @Override
            protected void buildContent(UIForm form) {
                content.accept(form);
            }
        };
    }

    /**
     * Creates a form page with default vertical spacing.
     *
     * @param contentWidth width used by standard form controls
     * @param spec callback that builds the form and handles its lifecycle
     */
    public static UIFormPage formPage(int contentWidth, UIFormSpec spec) {
        return new UIFormPage(contentWidth, spec);
    }

    /**
     * Creates a form page with custom vertical layout settings.
     *
     * @param contentWidth width used by standard form controls
     * @param startY first row Y position inside the page
     * @param gap vertical gap inserted between rows
     * @param spec callback that builds the form and handles its lifecycle
     */
    public static UIFormPage formPage(int contentWidth, int startY, int gap, UIFormSpec spec) {
        return new UIFormPage(contentWidth, startY, gap, spec);
    }

}
