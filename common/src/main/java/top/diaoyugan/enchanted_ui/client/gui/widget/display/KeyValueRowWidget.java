package top.diaoyugan.enchanted_ui.client.gui.widget.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;

import java.util.function.Supplier;

public class KeyValueRowWidget extends AbstractWidget implements OverlayRenderableWidget {
    private final Supplier<Component> valueSupplier;
    private DisplayText.Overlay textOverlay = DisplayText.noOverlay();

    public KeyValueRowWidget(int x, int y, int width, int height, Component label, Supplier<Component> valueSupplier) {
        super(x, y, width, height, label);
        this.valueSupplier = valueSupplier;
        this.active = false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        DisplayText.Fit value = DisplayText.fit(minecraft, valueSupplier.get(), Math.max(30, width / 2));
        int valueWidth = minecraft.font.width(value.rendered());
        DisplayText.Fit label = DisplayText.fit(minecraft, getMessage(), width - valueWidth - 18);

        guiGraphics.fill(x, y, x + width, y + height, 0xCC252525);
        guiGraphics.text(minecraft.font, label.rendered(), x + 6, y + 6, 0xFFE0E0E0, false);
        guiGraphics.text(minecraft.font, value.rendered(), x + width - valueWidth - 6, y + 6, 0xFFFFFFFF, false);
        textOverlay = DisplayText.overlay(label.truncated() ? label : value, x, y, width, height);
    }

    @Override
    public void extractOverlayRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        DisplayText.renderOverlay(guiGraphics, Minecraft.getInstance(), textOverlay, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
