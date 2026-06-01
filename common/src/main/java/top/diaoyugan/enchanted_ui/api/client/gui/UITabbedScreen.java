package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.client.gui.screen.base.BaseTabbedScreen;

public class UITabbedScreen extends BaseTabbedScreen {

    private static BaseTabbedScreen.Page adapt(UIPage page) {
        return new BaseTabbedScreen.Page() {
            @Override
            public java.util.List<net.minecraft.client.gui.components.AbstractWidget> build(BaseTabbedScreen.BuildContext ctx) {
                return page.build(new UIBuildContext(ctx));
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
            public boolean onSave() {
                return page.onSave();
            }

            @Override
            public void tick() {
                page.tick();
            }

            @Override
            public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
                return page.keyPressed(event);
            }

            @Override
            public boolean hasUnsavedChanges() {
                return page.hasUnsavedChanges();
            }

            @Override
            public void reload() {
                page.reload();
            }

            @Override
            public void markClean() {
                page.markClean();
            }
        };
    }

    private static BaseTabbedScreen.BottomBar adapt(UIBottomBar bottomBar) {
        return (screen, centerX, bottomY) -> bottomBar.add((UITabbedScreen) screen, centerX, bottomY);
    }

    public UITabbedScreen(@Nullable Screen parent, Component title) {
        super(parent, title);
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> T add(T widget) {
        return super.add(widget);
    }

    public UITabbedScreen tab(int x, int y, int height, Component label, UIPage page) {
        super.tab(x, y, height, label, adapt(page));
        return this;
    }

    public UITabbedScreen tab(int x, int y, int height, Component label, Style style, UIPage page) {
        super.tab(x, y, height, label, style, adapt(page));
        return this;
    }

    public UITabbedScreen bottomBar(UIBottomBar bottomBar) {
        super.bottomBar(adapt(bottomBar));
        return this;
    }

    @Override
    public UITabbedScreen style(UIScreenStyle style) {
        super.style(style);
        return this;
    }

    @Override
    public UITabbedScreen unsavedChangesPrompt(UIUnsavedChangesPrompt prompt) {
        super.unsavedChangesPrompt(prompt);
        return this;
    }

    public void showToast(Component message) {
        super.showToast(message);
    }

    public void showToast(Component message, int durationTicks) {
        super.showToast(message, durationTicks);
    }

    public void showDialog(Component title, java.util.List<Component> lines, UIDialogAction... actions) {
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

    public void showConfirm(
            Component title,
            Component message,
            Component confirmLabel,
            Component cancelLabel,
            Runnable confirmAction
    ) {
        super.showConfirm(title, message, confirmLabel, cancelLabel, confirmAction);
    }

    public void closeDialog() {
        super.closeDialog();
    }

    public void requestClose() {
        super.requestClose();
    }

    @Override
    public boolean saveAll() {
        return super.saveAll();
    }

    @Override
    public boolean hasUnsavedChanges() {
        return super.hasUnsavedChanges();
    }

    public void reloadAll() {
        super.reloadAll();
    }

    public void markAllClean() {
        super.markAllClean();
    }
}
