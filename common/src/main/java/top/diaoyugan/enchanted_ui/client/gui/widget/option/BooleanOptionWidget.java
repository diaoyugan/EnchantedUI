package top.diaoyugan.enchanted_ui.client.gui.widget.option;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class BooleanOptionWidget extends BaseOptionWidget {

    private static final int HORIZONTAL_PADDING = 6;
    private static final int STATE_GAP = 8;
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;

    public BooleanOptionWidget(
            int x, int y, int width, int height,
            Component label,
            BooleanSupplier getter,
            Consumer<Boolean> setter
    ) {
        super(x, y, width, height, label);
        this.getter = getter;
        this.setter = setter;
    }

    public void tooltip(Component tooltip) {
        setTooltip(Tooltip.create(tooltip));
    }

    @Override
    protected void renderContent(
            GuiGraphicsExtractor g,
            int x, int y,
            int width, int height,
            int mouseX, int mouseY
    ) {
        Minecraft mc = Minecraft.getInstance();
        boolean value = getter.getAsBoolean();

        Component state = value
                ? Component.translatable("options.on")
                : Component.translatable("options.off");
        int stateWidth = mc.font.width(state);
        int stateX = x + width - HORIZONTAL_PADDING - stateWidth;
        int labelRight = Math.max(x + HORIZONTAL_PADDING, stateX - STATE_GAP);
        int textColor = active ? 0xFFFFFFFF : 0xFF777777;
        int stateColor = active ? (value ? 0xFF55FF55 : 0xFFFF5555) : 0xFF777777;

        g.enableScissor(x + HORIZONTAL_PADDING, y, labelRight, y + height);
        drawScrollingText(g, mc, x + HORIZONTAL_PADDING, labelRight, y + 6, textColor);
        g.disableScissor();
        g.text(mc.font, state, stateX, y + 6, stateColor, false);
    }

    private void drawScrollingText(
            GuiGraphicsExtractor graphics,
            Minecraft minecraft,
            int left,
            int right,
            int y,
            int color
    ) {
        int availableWidth = right - left;
        int textWidth = minecraft.font.width(getMessage());
        if (textWidth <= availableWidth) {
            graphics.text(minecraft.font, getMessage(), left, y, color, false);
            return;
        }

        int overflow = textWidth - availableWidth;
        double phase = (System.currentTimeMillis() % 6000L) / 6000.0D;
        double pingPong = 0.5D - 0.5D * Math.cos(phase * Math.PI * 2.0D);
        int offset = (int) Math.round(overflow * pingPong);
        graphics.text(minecraft.font, getMessage(), left - offset, y, color, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (active) {
            setter.accept(!getter.getAsBoolean());
        }
    }
}
