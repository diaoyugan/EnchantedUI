package top.diaoyugan.enchanted_ui.api.client.gui;

import top.diaoyugan.enchanted_ui.client.gui.builder.UI;
import top.diaoyugan.enchanted_ui.client.gui.screen.base.BaseTabbedScreen;

public final class UiBuildContext {
    private final int screenWidth;
    private final int screenHeight;
    private final int centerX;

    UiBuildContext(UI.BuildContext delegate) {
        this(delegate.screenWidth(), delegate.screenHeight(), delegate.centerX());
    }

    UiBuildContext(BaseTabbedScreen.BuildContext delegate) {
        this(delegate.screenWidth(), delegate.screenHeight(), delegate.centerX());
    }

    private UiBuildContext(int screenWidth, int screenHeight, int centerX) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.centerX = centerX;
    }

    UI.BuildContext delegate() {
        return new UI.BuildContext(screenWidth, screenHeight, centerX);
    }

    public int screenWidth() {
        return screenWidth;
    }

    public int screenHeight() {
        return screenHeight;
    }

    public int centerX() {
        return centerX;
    }

    public UiVerticalLayout vertical(int contentWidth, int startY, int gap) {
        return new UiVerticalLayout(new UI.BuildContext(screenWidth, screenHeight, centerX).vertical(contentWidth, startY, gap));
    }

    public UiHorizontalLayout horizontal(int startX, int startY, int gap) {
        return new UiHorizontalLayout(new UI.BuildContext(screenWidth, screenHeight, centerX).horizontal(startX, startY, gap));
    }
}
