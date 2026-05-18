package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class EnchantedUI {

    private EnchantedUI() {
    }

    public static UITabbedScreen tabbed(Screen parent, Component title) {
        return new UITabbedScreen(parent, title);
    }

    public static UIFormPage formPage(int contentWidth, UIFormSpec spec) {
        return new UIFormPage(contentWidth, spec);
    }

    public static UIFormPage formPage(int contentWidth, int startY, int gap, UIFormSpec spec) {
        return new UIFormPage(contentWidth, startY, gap, spec);
    }

    public static Minecraft mc() {
        return Minecraft.getInstance();
    }
}
