package top.diaoyugan.enchanted_ui.client.gui.screen.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UIScreenStyle;

/** Draws the configured bottom-bar background and separator. */
final class BottomBarBackdropWidget extends AbstractWidget {
    private final UIScreenStyle style;

    BottomBarBackdropWidget(int x, int y, int width, int height, UIScreenStyle style) {
        super(x, y, width, height, Component.empty());
        this.style = style;
        this.active = false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!style.bottomBarBlur() && (style.bottomBarBackgroundColor() >>> 24) == 0) {
            return;
        }
        if (style.bottomBarBlur() && !style.backgroundBlur()) {
            graphics.blurBeforeThisStratum();
        }
        graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), style.bottomBarBackgroundColor());
        if ((style.bottomBarSeparatorColor() >>> 24) != 0) {
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + 1, style.bottomBarSeparatorColor());
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
