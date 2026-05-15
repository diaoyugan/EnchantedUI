package top.diaoyugan.enchanted_ui.client.gui.widget.overlay;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface OverlayRenderableWidget {
    void extractOverlayRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick);

    default boolean isOverlayExpanded() {
        return false;
    }

    default boolean overlayContains(double mouseX, double mouseY) {
        return false;
    }
}
