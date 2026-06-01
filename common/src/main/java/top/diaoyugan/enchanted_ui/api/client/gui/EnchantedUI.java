package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
    public static UITabbedScreen tabbed(Screen parent, Component title) {
        return new UITabbedScreen(parent, title);
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
