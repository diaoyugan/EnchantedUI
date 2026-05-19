package top.diaoyugan.enchanted_ui.client.gui.widget.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;

import java.util.Locale;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ProgressBarWidget extends AbstractWidget implements OverlayRenderableWidget {
    private final DoubleSupplier progressSupplier;
    private final Supplier<Component> valueSupplier;
    private final int fillColor;
    private DisplayText.Overlay textOverlay = DisplayText.noOverlay();

    public ProgressBarWidget(int x, int y, int width, int height, Component label, DoubleSupplier progressSupplier, int fillColor) {
        this(x, y, width, height, label, progressSupplier, () -> Component.literal(String.format(Locale.ROOT, "%.0f%%", Math.max(0.0D, Math.min(1.0D, progressSupplier.getAsDouble())) * 100.0D)), fillColor);
    }

    public ProgressBarWidget(int x, int y, int width, int height, Component label, DoubleSupplier progressSupplier, Supplier<Component> valueSupplier, int fillColor) {
        super(x, y, width, height, label);
        this.progressSupplier = progressSupplier;
        this.valueSupplier = valueSupplier;
        this.fillColor = fillColor;
        this.active = false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        double progress = Math.max(0.0D, Math.min(1.0D, progressSupplier.getAsDouble()));
        int fillWidth = (int) Math.round((width - 2) * progress);
        DisplayText.Fit value = DisplayText.fit(minecraft, valueSupplier.get(), Math.max(24, width / 3));
        int valueWidth = minecraft.font.width(value.rendered());
        DisplayText.Fit label = DisplayText.fit(minecraft, getMessage(), width - valueWidth - 18);

        guiGraphics.fill(x, y, x + width, y + height, 0xFF2B2B2B);
        if (fillWidth > 0) {
            guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + height - 1, fillColor);
        }
        guiGraphics.outline(x, y, width, height, 0xFF666666);

        guiGraphics.text(minecraft.font, label.rendered(), x + 6, y + 6, 0xFFFFFFFF, false);
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
