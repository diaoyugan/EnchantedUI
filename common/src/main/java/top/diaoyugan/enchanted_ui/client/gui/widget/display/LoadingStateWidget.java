package top.diaoyugan.enchanted_ui.client.gui.widget.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class LoadingStateWidget extends AbstractWidget {
    private final Component message;

    public LoadingStateWidget(int x, int y, int width, int height, Component title, Component message) {
        super(x, y, width, Math.max(
                height,
                DisplayText.wrappedBlockHeight(
                        Minecraft.getInstance(), title, message.copy().append("..."), width - 16, 7, 4, 7
                )
        ), title);
        this.message = message;
        this.active = false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        String dots = ".".repeat((int) ((System.currentTimeMillis() / 350L) % 4L));

        guiGraphics.fill(x, y, x + width, y + height, 0xBB202020);
        guiGraphics.outline(x, y, width, height, 0xFF555555);
        int bodyY = DisplayText.renderWrapped(
                guiGraphics, minecraft, getMessage(), x + 8, y + 7, width - 16, 0xFFFFFFFF, true
        ) + 4;
        DisplayText.renderWrapped(
                guiGraphics, minecraft, message.copy().append(dots), x + 8, bodyY, width - 16, 0xFFAAAAAA, true
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
