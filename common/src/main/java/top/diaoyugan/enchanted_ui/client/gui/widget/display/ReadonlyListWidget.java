package top.diaoyugan.enchanted_ui.client.gui.widget.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;

import java.util.List;
import java.util.function.Supplier;

public class ReadonlyListWidget extends AbstractWidget implements OverlayRenderableWidget {
    private static final int HEADER_HEIGHT = 18;
    private static final int ROW_HEIGHT = 18;

    private final Supplier<List<Component>> entriesSupplier;
    private final int visibleRows;
    private final Component emptyText;
    private final java.util.function.IntFunction<Component> overflowText;
    private DisplayText.Overlay textOverlay = DisplayText.noOverlay();

    public ReadonlyListWidget(int x, int y, int width, Component label, Supplier<List<Component>> entriesSupplier, int visibleRows) {
        this(
                x,
                y,
                width,
                label,
                entriesSupplier,
                visibleRows,
                Component.translatable("eui.display.empty"),
                hiddenCount -> Component.translatable("eui.display.more", hiddenCount)
        );
    }

    public ReadonlyListWidget(
            int x,
            int y,
            int width,
            Component label,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows,
            Component emptyText,
            java.util.function.IntFunction<Component> overflowText
    ) {
        super(x, y, width, HEADER_HEIGHT + ROW_HEIGHT * Math.max(1, visibleRows) + 2, label);
        this.entriesSupplier = entriesSupplier;
        this.visibleRows = Math.max(1, visibleRows);
        this.emptyText = emptyText;
        this.overflowText = overflowText;
        this.active = false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int x = getX();
        int y = getY();
        int width = getWidth();
        List<Component> entries = entriesSupplier.get();
        int rows = Math.min(visibleRows, Math.max(1, entries.size()));
        DisplayText.Fit title = DisplayText.fit(minecraft, getMessage(), width - 12);

        guiGraphics.fill(x, y, x + width, y + getHeight(), 0xBB202020);
        guiGraphics.outline(x, y, width, getHeight(), 0xFF555555);
        guiGraphics.text(minecraft.font, title.rendered(), x + 6, y + 6, 0xFFFFFFFF, false);

        if (entries.isEmpty()) {
            DisplayText.Fit empty = DisplayText.fit(minecraft, emptyText, width - 18);
            guiGraphics.text(minecraft.font, empty.rendered(), x + 8, y + HEADER_HEIGHT + 6, 0xFFAAAAAA, false);
            textOverlay = DisplayText.overlay(title.truncated() ? title : empty, x, y, width, getHeight());
            return;
        }

        DisplayText.Fit hovered = title;
        for (int i = 0; i < rows; i++) {
            int rowTop = y + HEADER_HEIGHT + i * ROW_HEIGHT;
            DisplayText.Fit entry = DisplayText.fit(minecraft, entries.get(i), width - 18);
            guiGraphics.fill(x + 4, rowTop, x + width - 4, rowTop + ROW_HEIGHT, i % 2 == 0 ? 0x552A2A2A : 0x55333333);
            guiGraphics.text(minecraft.font, entry.rendered(), x + 8, rowTop + 5, 0xFFE8E8E8, false);
            if (mouseY >= rowTop && mouseY < rowTop + ROW_HEIGHT) {
                hovered = entry;
            }
        }

        if (entries.size() > visibleRows) {
            Component more = overflowText.apply(entries.size() - visibleRows);
            guiGraphics.text(minecraft.font, more, x + width - minecraft.font.width(more) - 8, y + getHeight() - 13, 0xFFAAAAAA, false);
        }
        textOverlay = DisplayText.overlay(hovered, x, y, width, getHeight());
    }

    @Override
    public void extractOverlayRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        renderHiddenEntriesOverlay(guiGraphics, minecraft, mouseX, mouseY);
        DisplayText.renderOverlay(guiGraphics, minecraft, textOverlay, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    private void renderHiddenEntriesOverlay(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, int mouseX, int mouseY) {
        List<Component> entries = entriesSupplier.get();
        if (entries.size() <= visibleRows || !isOverflowHovered(minecraft, mouseX, mouseY)) {
            return;
        }

        int hiddenCount = entries.size() - visibleRows;
        int panelWidth = Math.max(getWidth(), hiddenEntriesWidth(minecraft, entries) + 16);
        int panelHeight = hiddenCount * ROW_HEIGHT + 8;
        int panelX = getX();
        int panelY = getY() > panelHeight + 4 ? getY() - panelHeight - 2 : getY() + getHeight() + 2;

        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xEE111111);
        guiGraphics.outline(panelX, panelY, panelWidth, panelHeight, 0xFF777777);

        for (int i = visibleRows; i < entries.size(); i++) {
            int rowTop = panelY + 4 + (i - visibleRows) * ROW_HEIGHT;
            DisplayText.Fit entry = DisplayText.fit(minecraft, entries.get(i), panelWidth - 16);
            guiGraphics.text(minecraft.font, entry.rendered(), panelX + 8, rowTop + 5, 0xFFFFFFFF, false);
        }
    }

    private boolean isOverflowHovered(Minecraft minecraft, int mouseX, int mouseY) {
        List<Component> entries = entriesSupplier.get();
        if (entries.size() <= visibleRows) {
            return false;
        }
        Component more = overflowText.apply(entries.size() - visibleRows);
        int moreWidth = minecraft.font.width(more);
        int left = getX() + getWidth() - moreWidth - 8;
        int top = getY() + getHeight() - 16;
        return mouseX >= left && mouseX < left + moreWidth && mouseY >= top && mouseY < top + 13;
    }

    private int hiddenEntriesWidth(Minecraft minecraft, List<Component> entries) {
        int width = 0;
        for (int i = visibleRows; i < entries.size(); i++) {
            width = Math.max(width, minecraft.font.width(entries.get(i)));
        }
        return width;
    }
}
