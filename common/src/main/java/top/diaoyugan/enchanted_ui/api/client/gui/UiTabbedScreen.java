package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.client.gui.builder.UI;

public class UiTabbedScreen extends UI.TabbedScreen {

    private static UI.Page adapt(UiPage page) {
        if (page instanceof UiFormPage formPage) {
            return formPage.delegate();
        }
        return new UI.Page() {
            @Override
            public java.util.List<net.minecraft.client.gui.components.AbstractWidget> build(UI.BuildContext ctx) {
                return page.build(new UiBuildContext(ctx));
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

    private static UI.BottomBar adapt(UiBottomBar bottomBar) {
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
}
