package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class EnchantedUI {

    private EnchantedUI() {
    }

    public static UiTabbedScreen tabbed(Screen parent, Component title) {
        return new UiTabbedScreen(parent, title);
    }

    public static UiFormPage formPage(int contentWidth, UiFormSpec spec) {
        return new UiFormPage(contentWidth, spec);
    }

    public static UiFormPage formPage(int contentWidth, int startY, int gap, UiFormSpec spec) {
        return new UiFormPage(contentWidth, startY, gap, spec);
    }

    public static Minecraft mc() {
        return Minecraft.getInstance();
    }
}
