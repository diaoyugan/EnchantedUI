package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.client.gui.screen.base.BaseTabbedScreen;

public class UiTabbedScreen extends BaseTabbedScreen {

    private static BaseTabbedScreen.Page adapt(UiPage page) {
        return new BaseTabbedScreen.Page() {
            @Override
            public java.util.List<net.minecraft.client.gui.components.AbstractWidget> build(BaseTabbedScreen.BuildContext ctx) {
                return page.build(new UiBuildContext(ctx));
            }

            @Override
            public void onOpen() {
                page.onOpen();
            }

            @Override
            public void onClose() {
                page.onClose();
            }

            @Override
            public void onShow() {
                page.onShow();
            }

            @Override
            public void onHide() {
                page.onHide();
            }

            @Override
            public void onPageChanged(int previousPage, int currentPage) {
                page.onPageChanged(previousPage, currentPage);
            }

            @Override
            public void onSave() {
                page.onSave();
            }

            @Override
            public void tick() {
                page.tick();
            }

            @Override
            public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
                return page.keyPressed(event);
            }
        };
    }

    private static BaseTabbedScreen.BottomBar adapt(UiBottomBar bottomBar) {
        return (screen, centerX, bottomY) -> bottomBar.add((UiTabbedScreen) screen, centerX, bottomY);
    }

    public UiTabbedScreen(@Nullable Screen parent, Component title) {
        super(parent, title);
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> T add(T widget) {
        return super.add(widget);
    }

    public UiTabbedScreen tab(int x, int y, int height, Component label, UiPage page) {
        super.tab(x, y, height, label, adapt(page));
        return this;
    }

    public UiTabbedScreen tab(int x, int y, int height, Component label, Style style, UiPage page) {
        super.tab(x, y, height, label, style, adapt(page));
        return this;
    }

    public UiTabbedScreen bottomBar(UiBottomBar bottomBar) {
        super.bottomBar(adapt(bottomBar));
        return this;
    }

    public void showToast(Component message) {
        super.showToast(message);
    }

    public void showToast(Component message, int durationTicks) {
        super.showToast(message, durationTicks);
    }

    public void showDialog(Component title, java.util.List<Component> lines, UiDialogAction... actions) {
        super.showDialog(
                title,
                lines,
                java.util.Arrays.stream(actions)
                        .map(action -> new BaseTabbedScreen.DialogAction(action.label(), action.action(), action.closeAfterRun()))
                        .toArray(BaseTabbedScreen.DialogAction[]::new)
        );
    }

    public void showConfirm(Component title, Component message, Runnable confirmAction) {
        super.showConfirm(title, message, confirmAction);
    }

    public void closeDialog() {
        super.closeDialog();
    }
}
