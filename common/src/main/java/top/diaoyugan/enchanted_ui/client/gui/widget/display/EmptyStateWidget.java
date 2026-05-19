package top.diaoyugan.enchanted_ui.client.gui.widget.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;

public class EmptyStateWidget extends AbstractWidget implements OverlayRenderableWidget {
    private final Component description;
    private DisplayText.Overlay textOverlay = DisplayText.noOverlay();

    public EmptyStateWidget(int x, int y, int width, int height, Component title, Component description) {
        super(x, y, width, height, title);
        this.description = description;
        this.active = false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        DisplayText.Fit title = DisplayText.fit(minecraft, getMessage(), width - 12);
        DisplayText.Fit body = DisplayText.fit(minecraft, description, width - 12);

        guiGraphics.fill(x, y, x + width, y + height, 0xBB202020);
        guiGraphics.outline(x, y, width, height, 0xFF555555);
        guiGraphics.centeredText(minecraft.font, title.rendered(), x + width / 2, y + 10, 0xFFFFFFFF);
        guiGraphics.centeredText(minecraft.font, body.rendered(), x + width / 2, y + 26, 0xFFAAAAAA);
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
