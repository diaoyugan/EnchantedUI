package top.diaoyugan.enchanted_ui.client.gui.widget.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;

public class ErrorStateWidget extends AbstractWidget implements OverlayRenderableWidget {
    private final Component message;
    @Nullable
    private final Component actionLabel;
    @Nullable
    private final Runnable action;
    private DisplayText.Overlay textOverlay = DisplayText.noOverlay();
    private DisplayText.Overlay actionOverlay = DisplayText.noOverlay();

    public ErrorStateWidget(
            int x,
            int y,
            int width,
            int height,
            Component title,
            Component message,
            @Nullable Component actionLabel,
            @Nullable Runnable action
    ) {
        super(x, y, width, height, title);
        this.message = message;
        this.actionLabel = actionLabel;
        this.action = action;
        this.active = action != null;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        DisplayText.Fit title = DisplayText.fit(minecraft, getMessage(), width - 14);
        DisplayText.Fit body = DisplayText.fit(minecraft, message, action == null ? width - 14 : width - 78);

        guiGraphics.fill(x, y, x + width, y + height, 0xBB2A2020);
        guiGraphics.fill(x, y, x + 3, y + height, 0xFFB94A48);
        guiGraphics.outline(x, y, width, height, 0xFF6F4A4A);
        guiGraphics.text(minecraft.font, title.rendered(), x + 8, y + 7, 0xFFFFFFFF, false);
        guiGraphics.text(minecraft.font, body.rendered(), x + 8, y + 23, 0xFFFFB0A8, false);

        if (actionLabel != null && action != null) {
            int left = actionLeft();
            int top = actionTop();
            int color = containsAction(mouseX, mouseY) ? 0xFF73403E : 0xFF5A3432;
            DisplayText.Fit actionText = DisplayText.fit(minecraft, actionLabel, actionWidth() - 8);
            guiGraphics.fill(left, top, left + actionWidth(), top + 18, color);
            guiGraphics.outline(left, top, actionWidth(), 18, 0xFF9A6A66);
            guiGraphics.centeredText(minecraft.font, actionText.rendered(), left + actionWidth() / 2, top + 5, 0xFFFFFFFF);
            actionOverlay = DisplayText.overlay(actionText, left, top, actionWidth(), 18);
        } else {
            actionOverlay = DisplayText.noOverlay();
        }
        textOverlay = DisplayText.overlay(title.truncated() ? title : body, x, y, width, height);
    }

    @Override
    public void extractOverlayRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        DisplayText.renderOverlay(guiGraphics, minecraft, actionOverlay, mouseX, mouseY);
        DisplayText.renderOverlay(guiGraphics, minecraft, textOverlay, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (action != null && containsAction(event.x(), event.y())) {
            action.run();
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    private int actionWidth() {
        return 58;
    }

    private int actionLeft() {
        return getX() + getWidth() - actionWidth() - 6;
    }

    private int actionTop() {
        return getY() + getHeight() - 24;
    }

    private boolean containsAction(double mouseX, double mouseY) {
        return mouseX >= actionLeft() && mouseX < actionLeft() + actionWidth()
                && mouseY >= actionTop() && mouseY < actionTop() + 18;
    }
}
