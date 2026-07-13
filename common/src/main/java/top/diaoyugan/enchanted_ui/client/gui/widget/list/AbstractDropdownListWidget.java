package top.diaoyugan.enchanted_ui.client.gui.widget.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;

import java.util.List;

abstract class AbstractDropdownListWidget extends AbstractWidget implements OverlayRenderableWidget {
    protected static final int HEADER_HEIGHT = 20;
    protected static final int ROW_HEIGHT = 20;
    protected static final int PANEL_PADDING = 4;
    protected static final int SCROLLBAR_WIDTH = 6;
    protected static final int DEFAULT_VISIBLE_ROWS = 5;

    private final Component label;
    private final Component emptyText;
    private final int visibleRows;

    private boolean expanded;
    private int scrollIndex;

    protected AbstractDropdownListWidget(int x, int y, int width, Component label, int visibleRows) {
        this(x, y, width, label, visibleRows, UILocalization.frameworkText("dropdown.empty", "No entries"));
    }

    protected AbstractDropdownListWidget(int x, int y, int width, Component label, int visibleRows, Component emptyText) {
        super(x, y, width, HEADER_HEIGHT, label);
        this.label = label;
        this.emptyText = emptyText;
        this.visibleRows = Math.max(1, visibleRows);
    }

    protected abstract List<Component> entries();

    protected Component headerText() {
        return label.copy().append(Component.literal(" (" + entries().size() + ")"));
    }

    protected abstract int footerHeight();

    protected abstract void extractFooter(GuiGraphicsExtractor guiGraphics, int left, int top, int mouseX, int mouseY, float partialTick);

    protected abstract boolean mouseClickedInFooter(MouseButtonEvent event, boolean doubleClick, int left, int top);

    protected abstract boolean mouseReleasedInFooter(MouseButtonEvent event, int left, int top);

    protected abstract boolean mouseDraggedInFooter(MouseButtonEvent event, double dragX, double dragY, int left, int top);

    protected abstract boolean keyPressedInFooter(KeyEvent event);

    protected abstract boolean charTypedInFooter(net.minecraft.client.input.CharacterEvent event);

    protected abstract boolean preeditUpdatedInFooter(net.minecraft.client.input.PreeditEvent event);

    protected abstract int entryActionWidth();

    protected abstract void onEntryAction(int index);

    protected void onEntryClicked(int index) {
    }

    protected boolean entrySelectable() {
        return false;
    }

    protected abstract void setInnerFocus(boolean focused);

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        int backgroundColor = !active ? 0xFF252525 : isHoveredOrFocused() ? 0xFF555555 : 0xFF333333;
        int textColor = active ? 0xFFFFFFFF : 0xFF777777;
        guiGraphics.fill(x, y, x + width, y + height, backgroundColor);
        drawScrollingHeader(guiGraphics, x + 6, x + width - 16, y + 6, textColor);
        guiGraphics.text(
                Minecraft.getInstance().font,
                Component.literal(expanded ? "^" : "v"),
                x + width - 10,
                y + 6,
                textColor,
                false
        );
    }

    private void drawScrollingHeader(
            GuiGraphicsExtractor guiGraphics,
            int left,
            int right,
            int y,
            int color
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Component text = headerText();
        int availableWidth = right - left;
        int textWidth = minecraft.font.width(text);
        int offset = 0;
        if (textWidth > availableWidth) {
            int overflow = textWidth - availableWidth;
            double phase = (System.currentTimeMillis() % 6000L) / 6000.0D;
            double pingPong = 0.5D - 0.5D * Math.cos(phase * Math.PI * 2.0D);
            offset = (int) Math.round(overflow * pingPong);
        }

        guiGraphics.enableScissor(left, getY(), right, getY() + getHeight());
        guiGraphics.text(minecraft.font, text, left - offset, y, color, false);
        guiGraphics.disableScissor();
    }

    @Override
    public void extractOverlayRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!active || !expanded || !visible) {
            return;
        }

        int left = getX();
        int top = getY() + getHeight();
        int right = left + getWidth();
        int bottom = top + overlayHeight();

        guiGraphics.fill(left, top, right, bottom, 0xEE222222);
        guiGraphics.outline(left, top, getWidth(), overlayHeight(), 0xFF666666);

        int listTop = listTop();
        int listHeight = listHeight();
        int listRight = right - PANEL_PADDING - (showsListScrollbar() ? SCROLLBAR_WIDTH + 2 : 0);
        int availableWidth = listRight - left - PANEL_PADDING;
        List<Component> entryComponents = entries();

        guiGraphics.enableScissor(left + PANEL_PADDING, listTop, listRight, listTop + listHeight);
        if (entryComponents.isEmpty()) {
            guiGraphics.text(
                    Minecraft.getInstance().font,
                    emptyText,
                    left + PANEL_PADDING,
                    listTop + 6,
                    0xFFAAAAAA,
                    false
            );
        } else {
            int visibleCount = visibleEntryCount();
            for (int row = 0; row < visibleCount; row++) {
                int index = scrollIndex + row;
                if (index >= entryComponents.size()) {
                    break;
                }
                int rowTop = listTop + row * ROW_HEIGHT;
                boolean hovered = isEntryHovered(mouseX, mouseY, rowTop);
                guiGraphics.fill(left + PANEL_PADDING, rowTop, listRight, rowTop + ROW_HEIGHT, hovered ? 0xAA444444 : 0xAA2A2A2A);
                guiGraphics.text(
                        Minecraft.getInstance().font,
                        entryComponents.get(index),
                        left + PANEL_PADDING + 4,
                        rowTop + 6,
                        0xFFFFFFFF,
                        false
                );
                renderEntryAction(guiGraphics, index, rowTop, listRight);
            }
        }
        guiGraphics.disableScissor();

        if (showsListScrollbar()) {
            renderListScrollbar(guiGraphics, right - PANEL_PADDING - SCROLLBAR_WIDTH, listTop, listHeight, entryComponents.size());
        }

        if (footerHeight() > 0) {
            extractFooter(guiGraphics, left, footerTop(), mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!active) {
            expanded = false;
            setInnerFocus(false);
            return false;
        }
        double mouseX = event.x();
        double mouseY = event.y();
        if (isHeaderHovered(mouseX, mouseY)) {
            expanded = !expanded;
            if (!expanded) {
                setInnerFocus(false);
            }
            playClickSound();
            return true;
        }

        if (!active || !expanded) {
            return false;
        }

        if (!overlayContains(mouseX, mouseY)) {
            expanded = false;
            setInnerFocus(false);
            return false;
        }

        int clickedIndex = entryIndexAt(mouseX, mouseY);
        if (clickedIndex >= 0 && clickedIndex < entries().size()) {
            if (entryActionWidth() > 0 && mouseX >= actionLeft()) {
                onEntryAction(clickedIndex);
                playClickSound();
            } else if (entrySelectable()) {
                onEntryClicked(clickedIndex);
                playClickSound();
            }
            return true;
        }

        return mouseClickedInFooter(event, doubleClick, getX(), footerTop());
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (!active || !expanded) {
            return false;
        }
        return mouseReleasedInFooter(event, getX(), footerTop());
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!expanded) {
            return false;
        }
        return mouseDraggedInFooter(event, dragX, dragY, getX(), footerTop());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!active || !expanded || !overlayContains(mouseX, mouseY)) {
            return false;
        }
        int maxScrollIndex = Math.max(0, entries().size() - visibleEntryCount());
        if (maxScrollIndex == 0) {
            return false;
        }
        if (verticalAmount > 0) {
            scrollIndex = Math.max(0, scrollIndex - 1);
        } else if (verticalAmount < 0) {
            scrollIndex = Math.min(maxScrollIndex, scrollIndex + 1);
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!active || !expanded) {
            return false;
        }
        return keyPressedInFooter(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        if (!active || !expanded) {
            return false;
        }
        return charTypedInFooter(event);
    }

    @Override
    public boolean preeditUpdated(net.minecraft.client.input.PreeditEvent event) {
        if (!active || !expanded) {
            return false;
        }
        return preeditUpdatedInFooter(event);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isHeaderHovered(mouseX, mouseY) || (expanded && overlayContains(mouseX, mouseY));
    }

    @Override
    public boolean isOverlayExpanded() {
        return active && expanded;
    }

    @Override
    public boolean overlayContains(double mouseX, double mouseY) {
        return containsOverlayArea(mouseX, mouseY);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused && !expanded) {
            setInnerFocus(false);
        }
    }

    protected int listTop() {
        return getY() + getHeight() + PANEL_PADDING;
    }

    protected int listHeight() {
        return Math.max(ROW_HEIGHT, visibleEntryCount() * ROW_HEIGHT);
    }

    protected int footerTop() {
        return listTop() + listHeight() + PANEL_PADDING;
    }

    protected int overlayHeight() {
        return PANEL_PADDING + listHeight() + (footerHeight() > 0 ? PANEL_PADDING + footerHeight() : 0) + PANEL_PADDING;
    }

    @Override
    public int overlayBottom() {
        return getY() + getHeight() + overlayHeight();
    }

    protected int visibleEntryCount() {
        return Math.min(visibleRows, Math.max(1, entries().size()));
    }

    protected Component label() {
        return label;
    }

    protected boolean expanded() {
        return expanded;
    }

    protected void collapse() {
        expanded = false;
        setInnerFocus(false);
    }

    protected void playClickSound() {
        playDownSound(Minecraft.getInstance().getSoundManager());
    }

    protected int actionLeft() {
        return getX() + getWidth() - PANEL_PADDING - (showsListScrollbar() ? SCROLLBAR_WIDTH + 2 : 0) - entryActionWidth();
    }

    private boolean showsListScrollbar() {
        return entries().size() > visibleEntryCount();
    }

    private boolean isHeaderHovered(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX < getRight() && mouseY >= getY() && mouseY < getBottom();
    }

    private boolean containsOverlayArea(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX < getRight() && mouseY >= getBottom() && mouseY < overlayBottom();
    }

    private int entryIndexAt(double mouseX, double mouseY) {
        int listTop = listTop();
        int listBottom = listTop + listHeight();
        if (mouseY < listTop || mouseY >= listBottom) {
            return -1;
        }
        int row = (int) ((mouseY - listTop) / ROW_HEIGHT);
        int index = scrollIndex + row;
        return index < entries().size() ? index : -1;
    }

    private boolean isEntryHovered(int mouseX, int mouseY, int rowTop) {
        return mouseX >= getX() + PANEL_PADDING
                && mouseX < getRight() - PANEL_PADDING
                && mouseY >= rowTop
                && mouseY < rowTop + ROW_HEIGHT;
    }

    private void renderEntryAction(GuiGraphicsExtractor guiGraphics, int index, int rowTop, int listRight) {
        if (entryActionWidth() <= 0) {
            return;
        }
        int actionLeft = listRight - entryActionWidth();
        guiGraphics.fill(actionLeft, rowTop, listRight, rowTop + ROW_HEIGHT, 0xAA7A2E2E);
        guiGraphics.centeredText(
                Minecraft.getInstance().font,
                Component.literal("-"),
                actionLeft + (entryActionWidth() / 2),
                rowTop + 6,
                0xFFFFFFFF
        );
    }

    private void renderListScrollbar(GuiGraphicsExtractor guiGraphics, int x, int y, int height, int totalEntries) {
        int visibleEntries = visibleEntryCount();
        int thumbHeight = Math.max(16, (int) Math.round((visibleEntries / (double) totalEntries) * height));
        int trackHeight = Math.max(0, height - thumbHeight);
        int maxScrollIndex = Math.max(1, totalEntries - visibleEntries);
        int thumbY = y + (int) Math.round((scrollIndex / (double) maxScrollIndex) * trackHeight);

        guiGraphics.fill(x, y, x + SCROLLBAR_WIDTH, y + height, 0x66333333);
        guiGraphics.fill(x, thumbY, x + SCROLLBAR_WIDTH, thumbY + thumbHeight, 0xFF888888);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
