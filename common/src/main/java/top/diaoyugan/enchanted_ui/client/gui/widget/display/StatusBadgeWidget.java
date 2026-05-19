package top.diaoyugan.enchanted_ui.client.gui.widget.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class StatusBadgeWidget extends AbstractWidget implements OverlayRenderableWidget {
    private final Supplier<Component> statusSupplier;
    private final IntSupplier colorSupplier;
    private DisplayText.Overlay textOverlay = DisplayText.noOverlay();

    public StatusBadgeWidget(
            int x,
            int y,
            int width,
            int height,
            Component label,
            Supplier<Component> statusSupplier,
            IntSupplier colorSupplier
    ) {
        super(x, y, width, height, label);
        this.statusSupplier = statusSupplier;
        this.colorSupplier = colorSupplier;
        this.active = false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        DisplayText.Fit status = DisplayText.fit(minecraft, statusSupplier.get(), Math.max(20, width / 2));
        int badgeWidth = Math.min(width - 12, minecraft.font.width(status.rendered()) + 14);
        int badgeX = x + width - badgeWidth - 4;
        DisplayText.Fit label = DisplayText.fit(minecraft, getMessage(), badgeX - x - 12);

        guiGraphics.fill(x, y, x + width, y + height, 0xCC252525);
        guiGraphics.text(minecraft.font, label.rendered(), x + 6, y + 6, 0xFFE0E0E0, false);
        guiGraphics.fill(badgeX, y + 3, badgeX + badgeWidth, y + height - 3, colorSupplier.getAsInt());
        guiGraphics.outline(badgeX, y + 3, badgeWidth, height - 6, 0xAAFFFFFF);
        guiGraphics.centeredText(minecraft.font, status.rendered(), badgeX + badgeWidth / 2, y + 6, 0xFFFFFFFF);
        textOverlay = DisplayText.overlay(label.truncated() ? label : status, x, y, width, height);
    }

    @Override
    public void extractOverlayRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        DisplayText.renderOverlay(guiGraphics, Minecraft.getInstance(), textOverlay, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
