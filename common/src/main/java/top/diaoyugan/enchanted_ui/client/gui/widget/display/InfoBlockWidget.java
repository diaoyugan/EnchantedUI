package top.diaoyugan.enchanted_ui.client.gui.widget.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;

public class InfoBlockWidget extends AbstractWidget implements OverlayRenderableWidget {
    private final Component message;
    private final int accentColor;
    private DisplayText.Overlay textOverlay = DisplayText.noOverlay();

    public InfoBlockWidget(int x, int y, int width, int height, Component title, Component message, int accentColor) {
        super(x, y, width, height, title);
        this.message = message;
        this.accentColor = accentColor;
        this.active = false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        DisplayText.Fit title = DisplayText.fit(minecraft, getMessage(), width - 14);
        DisplayText.Fit body = DisplayText.fit(minecraft, message, width - 14);

        guiGraphics.fill(x, y, x + width, y + height, 0xCC252525);
        guiGraphics.fill(x, y, x + 3, y + height, accentColor);
        guiGraphics.outline(x, y, width, height, 0xFF555555);
        guiGraphics.text(minecraft.font, title.rendered(), x + 8, y + 7, 0xFFFFFFFF, false);
        guiGraphics.text(minecraft.font, body.rendered(), x + 8, y + 23, 0xFFBDBDBD, false);
        textOverlay = DisplayText.overlay(title.truncated() ? title : body, x, y, width, height);
    }

    @Override
    public void extractOverlayRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        DisplayText.renderOverlay(guiGraphics, Minecraft.getInstance(), textOverlay, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
