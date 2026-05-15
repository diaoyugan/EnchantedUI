package top.diaoyugan.enchanted_ui.client.gui.widget.scroll;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class ScrollBarWidget extends AbstractWidget {
    private static final int MIN_THUMB_HEIGHT = 24;

    private final IntSupplier scrollSupplier;
    private final IntSupplier maxScrollSupplier;
    private final IntConsumer scrollConsumer;
    private final int viewportHeight;
    private final int contentHeight;

    public ScrollBarWidget(
            int x,
            int y,
            int width,
            int height,
            int viewportHeight,
            int contentHeight,
            IntSupplier scrollSupplier,
            IntSupplier maxScrollSupplier,
            IntConsumer scrollConsumer
    ) {
        super(x, y, width, height, Component.empty());
        this.viewportHeight = viewportHeight;
        this.contentHeight = contentHeight;
        this.scrollSupplier = scrollSupplier;
        this.maxScrollSupplier = maxScrollSupplier;
        this.scrollConsumer = scrollConsumer;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        int thumbHeight = getThumbHeight();
        int thumbY = getThumbY(thumbHeight);

        guiGraphics.fill(x, y, x + width, y + height, 0x66333333);
        guiGraphics.fill(x, thumbY, x + width, thumbY + thumbHeight, isHoveredOrFocused() ? 0xFFAAAAAA : 0xFF888888);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        updateScrollFromMouse(event.y());
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        updateScrollFromMouse(event.y());
    }

    private void updateScrollFromMouse(double mouseY) {
        int maxScroll = maxScrollSupplier.getAsInt();
        if (maxScroll <= 0) {
            scrollConsumer.accept(0);
            return;
        }

        int thumbHeight = getThumbHeight();
        int trackHeight = Math.max(1, getHeight() - thumbHeight);
        double relative = (mouseY - getY() - (thumbHeight / 2.0)) / trackHeight;
        int scroll = (int) Math.round(clamp(relative, 0.0, 1.0) * maxScroll);
        scrollConsumer.accept(scroll);
    }

    private int getThumbHeight() {
        if (contentHeight <= 0) {
            return getHeight();
        }
        return Math.max(MIN_THUMB_HEIGHT, Math.min(getHeight(), (int) Math.round((viewportHeight / (double) contentHeight) * getHeight())));
    }

    private int getThumbY(int thumbHeight) {
        int maxScroll = maxScrollSupplier.getAsInt();
        if (maxScroll <= 0) {
            return getY();
        }
        int trackHeight = Math.max(0, getHeight() - thumbHeight);
        double ratio = scrollSupplier.getAsInt() / (double) maxScroll;
        return getY() + (int) Math.round(trackHeight * ratio);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
