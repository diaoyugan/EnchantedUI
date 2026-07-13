package top.diaoyugan.enchanted_ui.api.client.gui;

import top.diaoyugan.enchanted_ui.client.gui.builder.UI;
import top.diaoyugan.enchanted_ui.client.gui.screen.base.BaseTabbedScreen;

public final class UIBuildContext {
    private final int screenWidth;
    private final int screenHeight;
    private final int centerX;

    UIBuildContext(UI.BuildContext delegate) {
        this(
                delegate.screenWidth(),
                delegate.screenHeight(),
                delegate.centerX(),
                delegate.viewportLeft(),
                delegate.viewportRight()
        );
    }

    UIBuildContext(BaseTabbedScreen.BuildContext delegate) {
        this(
                delegate.screenWidth(),
                delegate.screenHeight(),
                delegate.centerX(),
                delegate.viewportLeft(),
                delegate.viewportRight()
        );
    }

    private UIBuildContext(int screenWidth, int screenHeight, int centerX, int viewportLeft, int viewportRight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.centerX = centerX;
        this.viewportLeft = viewportLeft;
        this.viewportRight = viewportRight;
    }

    private final int viewportLeft;
    private final int viewportRight;

    UI.BuildContext delegate() {
        return new UI.BuildContext(screenWidth, screenHeight, centerX, viewportLeft, viewportRight);
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

    public int viewportLeft() {
        return viewportLeft;
    }

    public int viewportRight() {
        return viewportRight;
    }

    public int availableWidth() {
        return Math.max(0, viewportRight - viewportLeft);
    }

    public UIVerticalLayout vertical(int contentWidth, int startY, int gap) {
        return new UIVerticalLayout(delegate().vertical(contentWidth, startY, gap));
    }

    public UIHorizontalLayout horizontal(int startX, int startY, int gap) {
        return new UIHorizontalLayout(delegate().horizontal(startX, startY, gap));
    }
}
