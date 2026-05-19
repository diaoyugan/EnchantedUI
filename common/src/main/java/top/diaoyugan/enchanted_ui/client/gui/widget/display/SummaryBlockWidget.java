package top.diaoyugan.enchanted_ui.client.gui.widget.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UISummaryItem;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;

import java.util.List;
import java.util.function.Supplier;

public class SummaryBlockWidget extends AbstractWidget implements OverlayRenderableWidget {
    private static final int TITLE_HEIGHT = 18;
    private static final int ROW_HEIGHT = 18;

    private final Supplier<List<UISummaryItem>> itemsSupplier;
    private final Component emptyText;
    private DisplayText.Overlay textOverlay = DisplayText.noOverlay();

    public SummaryBlockWidget(int x, int y, int width, Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows) {
        this(x, y, width, title, itemsSupplier, rows, Component.translatable("eui.display.empty"));
    }

    public SummaryBlockWidget(int x, int y, int width, Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows, Component emptyText) {
        super(x, y, width, TITLE_HEIGHT + ROW_HEIGHT * Math.max(1, rows) + 2, title);
        this.itemsSupplier = itemsSupplier;
        this.emptyText = emptyText;
        this.active = false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = getX();
        int y = getY();
        int width = getWidth();
        List<UISummaryItem> items = itemsSupplier.get();
        int rows = Math.min((getHeight() - TITLE_HEIGHT - 2) / ROW_HEIGHT, items.size());
        DisplayText.Fit title = DisplayText.fit(minecraft, getMessage(), width - 12);

        guiGraphics.fill(x, y, x + width, y + getHeight(), 0xBB202020);
        guiGraphics.outline(x, y, width, getHeight(), 0xFF555555);
        guiGraphics.text(minecraft.font, title.rendered(), x + 6, y + 6, 0xFFFFFFFF, false);

        if (items.isEmpty()) {
            DisplayText.Fit empty = DisplayText.fit(minecraft, emptyText, width - 18);
            guiGraphics.text(minecraft.font, empty.rendered(), x + 8, y + TITLE_HEIGHT + 5, 0xFFAAAAAA, false);
            textOverlay = DisplayText.overlay(title.truncated() ? title : empty, x, y, width, getHeight());
            return;
        }

        DisplayText.Fit hovered = title;
        for (int i = 0; i < rows; i++) {
            UISummaryItem item = items.get(i);
            int rowTop = y + TITLE_HEIGHT + i * ROW_HEIGHT;
            DisplayText.Fit value = DisplayText.fit(minecraft, item.value(), Math.max(30, width / 2));
            int valueWidth = minecraft.font.width(value.rendered());
            DisplayText.Fit label = DisplayText.fit(minecraft, item.label(), width - valueWidth - 18);
            guiGraphics.text(minecraft.font, label.rendered(), x + 8, rowTop + 5, 0xFFBDBDBD, false);
            guiGraphics.text(minecraft.font, value.rendered(), x + width - valueWidth - 8, rowTop + 5, 0xFFFFFFFF, false);
            if (mouseY >= rowTop && mouseY < rowTop + ROW_HEIGHT) {
                hovered = label.truncated() ? label : value;
            }
        }
        textOverlay = DisplayText.overlay(hovered, x, y, width, getHeight());
    }

    @Override
    public void extractOverlayRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        DisplayText.renderOverlay(guiGraphics, Minecraft.getInstance(), textOverlay, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
