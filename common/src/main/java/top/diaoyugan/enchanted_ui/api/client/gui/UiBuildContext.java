package top.diaoyugan.enchanted_ui.api.client.gui;

import top.diaoyugan.enchanted_ui.client.gui.builder.UI;
import top.diaoyugan.enchanted_ui.client.gui.layout.VerticalLayout;

public record UiBuildContext(UI.BuildContext delegate) {

    public int screenWidth() {
        return delegate.screenWidth();
    }

    public int screenHeight() {
        return delegate.screenHeight();
    }

    public int centerX() {
        return delegate.centerX();
    }

    public VerticalLayout vertical(int contentWidth, int startY, int gap) {
        return delegate.vertical(contentWidth, startY, gap);
    }
}
