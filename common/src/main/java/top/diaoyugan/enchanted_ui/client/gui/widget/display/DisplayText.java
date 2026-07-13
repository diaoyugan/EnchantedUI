package top.diaoyugan.enchanted_ui.client.gui.widget.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

final class DisplayText {
    private static final String ELLIPSIS = "...";

    private DisplayText() {
    }

    static List<FormattedCharSequence> wrap(Minecraft minecraft, Component text, int maxWidth) {
        return minecraft.font.split(text, Math.max(1, maxWidth));
    }

    static int wrappedBlockHeight(
            Minecraft minecraft,
            Component title,
            Component body,
            int maxWidth,
            int topPadding,
            int lineGap,
            int bottomPadding
    ) {
        int titleLines = Math.max(1, wrap(minecraft, title, maxWidth).size());
        int bodyLines = Math.max(1, wrap(minecraft, body, maxWidth).size());
        return topPadding
                + titleLines * minecraft.font.lineHeight
                + lineGap
                + bodyLines * minecraft.font.lineHeight
                + bottomPadding;
    }

    static int renderWrapped(
            GuiGraphicsExtractor guiGraphics,
            Minecraft minecraft,
            Component text,
            int x,
            int y,
            int maxWidth,
            int color,
            boolean centered
    ) {
        List<FormattedCharSequence> lines = wrap(minecraft, text, maxWidth);
        if (lines.isEmpty()) {
            return y + minecraft.font.lineHeight;
        }
        int lineY = y;
        for (FormattedCharSequence line : lines) {
            if (centered) {
                guiGraphics.centeredText(minecraft.font, line, x + maxWidth / 2, lineY, color);
            } else {
                guiGraphics.text(minecraft.font, line, x, lineY, color, false);
            }
            lineY += minecraft.font.lineHeight;
        }
        return lineY;
    }

    static Fit fit(Minecraft minecraft, Component text, int maxWidth) {
        if (maxWidth <= 0) {
            return new Fit(Component.empty(), text, minecraft.font.width(text) > 0);
        }
        if (minecraft.font.width(text) <= maxWidth) {
            return new Fit(text, text, false);
        }
        int ellipsisWidth = minecraft.font.width(ELLIPSIS);
        if (maxWidth <= ellipsisWidth) {
            return new Fit(Component.empty(), text, true);
        }
        Component rendered = Component.literal(minecraft.font.plainSubstrByWidth(text.getString(), maxWidth - ellipsisWidth) + ELLIPSIS);
        return new Fit(rendered, text, true);
    }

    static Overlay overlay(Fit fit, int widgetX, int widgetY, int widgetWidth, int widgetHeight) {
        return new Overlay(fit, widgetX, widgetY, widgetWidth, widgetHeight);
    }

    static Overlay noOverlay() {
        return new Overlay(new Fit(Component.empty(), Component.empty(), false), 0, 0, 0, 0);
    }

    static void renderOverlay(
            GuiGraphicsExtractor guiGraphics,
            Minecraft minecraft,
            Overlay overlay,
            int mouseX,
            int mouseY
    ) {
        Fit fit = overlay.fit();
        int widgetX = overlay.widgetX();
        int widgetY = overlay.widgetY();
        int widgetWidth = overlay.widgetWidth();
        int widgetHeight = overlay.widgetHeight();
        if (!fit.truncated() || mouseX < widgetX || mouseX >= widgetX + widgetWidth || mouseY < widgetY || mouseY >= widgetY + widgetHeight) {
            return;
        }
        int maxTextWidth = Math.max(80, Math.min(260, widgetWidth + 60));
        Fit tooltip = fit(minecraft, fit.full(), maxTextWidth);
        int width = minecraft.font.width(tooltip.rendered()) + 8;
        int height = 17;
        int x = widgetX + Math.max(0, (widgetWidth - width) / 2);
        int y = widgetY > height + 4 ? widgetY - height - 2 : widgetY + widgetHeight + 2;
        guiGraphics.fill(x, y, x + width, y + height, 0xEE111111);
        guiGraphics.outline(x, y, width, height, 0xFF777777);
        guiGraphics.text(minecraft.font, tooltip.rendered(), x + 4, y + 5, 0xFFFFFFFF, false);
    }

    record Fit(Component rendered, Component full, boolean truncated) {
    }

    record Overlay(Fit fit, int widgetX, int widgetY, int widgetWidth, int widgetHeight) {
    }
}
