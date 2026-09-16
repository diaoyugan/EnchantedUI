package top.diaoyugan.enchanted_ui.client.gui.screen.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import top.diaoyugan.enchanted_ui.client.gui.widget.WidgetConditions;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.scroll.ScrollBarWidget;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Owns one built page's viewport, scrolling, clipping, and overlay routing.
 * <p>
 * Widget coordinates in {@code basePositions} never include scroll offset.
 * Every refresh derives visible positions from those coordinates, preventing
 * resize, reload, and overlay expansion from accumulating coordinate drift.
 */
final class PageViewport {
    private static final int SCROLLBAR_WIDTH = 8;

    private final List<AbstractWidget> widgets;
    private final List<OverlayRenderableWidget> overlays;
    private final Map<AbstractWidget, WidgetPosition> basePositions;
    private final ScrollBarWidget scrollBar;
    private final int viewportLeft;
    private final int viewportRight;
    private final int viewportTop;
    private final int viewportBottom;
    private final int baseMaxScroll;

    private int scrollOffset;

    PageViewport(
            List<AbstractWidget> widgets,
            int viewportLeft,
            int viewportTop,
            int viewportRight,
            int viewportBottom
    ) {
        this.widgets = widgets;
        this.viewportLeft = viewportLeft;
        this.viewportRight = viewportRight;
        this.overlays = widgets.stream()
                .filter(OverlayRenderableWidget.class::isInstance)
                .map(OverlayRenderableWidget.class::cast)
                .toList();
        this.basePositions = new IdentityHashMap<>();
        for (AbstractWidget widget : widgets) {
            basePositions.put(widget, new WidgetPosition(widget.getX(), widget.getY()));
        }

        this.viewportTop = viewportTop;
        this.viewportBottom = viewportBottom;

        int contentBottom = widgets.stream()
                .mapToInt(widget -> widget.getY() + widget.getHeight())
                .max()
                .orElse(this.viewportBottom);
        int viewportHeight = viewportBottom - viewportTop;
        this.baseMaxScroll = Math.max(0, contentBottom - viewportBottom);

        int contentRight = widgets.stream().mapToInt(AbstractWidget::getRight).max().orElse(viewportRight - 12);
        int scrollBarX = Math.max(
                viewportLeft,
                Math.min(viewportRight - SCROLLBAR_WIDTH, contentRight + 4)
        );
        this.scrollBar = new ScrollBarWidget(
                scrollBarX,
                viewportTop,
                SCROLLBAR_WIDTH,
                viewportHeight,
                viewportHeight,
                contentBottom - viewportTop,
                () -> scrollOffset,
                this::maxScroll,
                this::setScrollOffset
        );

        refreshWidgetStates();
    }

    void attach(Consumer<AbstractWidget> addWidget, Consumer<AbstractWidget> addRenderableWidget) {
        widgets.forEach(addWidget);
        addRenderableWidget.accept(scrollBar);
    }

    void detach(Consumer<AbstractWidget> removeWidget) {
        widgets.forEach(removeWidget);
        removeWidget.accept(scrollBar);
    }

    boolean scrollBy(double verticalAmount) {
        if (maxScroll() <= 0 || verticalAmount == 0) {
            return false;
        }
        int nextOffset = verticalAmount > 0 ? scrollOffset - 20 : scrollOffset + 20;
        return setScrollOffset(nextOffset);
    }

    void refreshWidgetStates() {
        positionWidgets();
        int maxScroll = maxScroll();
        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
            positionWidgets();
            maxScroll = maxScroll();
        }
        scrollBar.visible = maxScroll > 0;
        scrollBar.active = maxScroll > 0;
    }

    void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(viewportLeft, viewportTop, viewportRight, viewportBottom);
        for (AbstractWidget widget : widgets) {
            if (widget.visible) {
                widget.extractRenderState(graphics, mouseX, mouseY, partialTick);
            }
        }
        graphics.disableScissor();
    }

    void extractOverlayRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(viewportLeft, viewportTop, viewportRight, viewportBottom);
        for (OverlayRenderableWidget overlay : overlays) {
            if (overlay instanceof AbstractWidget widget && widget.visible) {
                overlay.extractOverlayRenderState(graphics, mouseX, mouseY, partialTick);
            }
        }
        graphics.disableScissor();
    }

    boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!insideViewport(event.x(), event.y())) {
            return false;
        }
        for (int i = overlays.size() - 1; i >= 0; i--) {
            OverlayRenderableWidget overlay = overlays.get(i);
            if (overlay instanceof AbstractWidget widget && widget.visible && overlay.isOverlayExpanded()
                    && widget.mouseClicked(event, doubleClick)) {
                return true;
            }
        }
        return false;
    }

    boolean mouseReleased(MouseButtonEvent event) {
        if (!insideViewport(event.x(), event.y())) {
            return false;
        }
        for (int i = overlays.size() - 1; i >= 0; i--) {
            OverlayRenderableWidget overlay = overlays.get(i);
            if (overlay instanceof AbstractWidget widget && widget.visible && overlay.isOverlayExpanded()
                    && widget.mouseReleased(event)) {
                return true;
            }
        }
        return false;
    }

    boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!insideViewport(event.x(), event.y())) {
            return false;
        }
        for (int i = overlays.size() - 1; i >= 0; i--) {
            OverlayRenderableWidget overlay = overlays.get(i);
            if (overlay instanceof AbstractWidget widget && widget.visible && overlay.isOverlayExpanded()
                    && widget.mouseDragged(event, dragX, dragY)) {
                return true;
            }
        }
        return false;
    }

    boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!insideViewport(mouseX, mouseY)) {
            return false;
        }
        for (int i = overlays.size() - 1; i >= 0; i--) {
            OverlayRenderableWidget overlay = overlays.get(i);
            if (overlay instanceof AbstractWidget widget && widget.visible && overlay.isOverlayExpanded()
                    && widget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }
        return false;
    }

    boolean keyPressed(KeyEvent event) {
        for (int i = overlays.size() - 1; i >= 0; i--) {
            OverlayRenderableWidget overlay = overlays.get(i);
            if (overlay instanceof AbstractWidget widget && widget.visible && overlay.isOverlayExpanded()
                    && widget.keyPressed(event)) {
                return true;
            }
        }
        return false;
    }

    boolean charTyped(CharacterEvent event) {
        for (int i = overlays.size() - 1; i >= 0; i--) {
            OverlayRenderableWidget overlay = overlays.get(i);
            if (overlay instanceof AbstractWidget widget && widget.visible && overlay.isOverlayExpanded()
                    && widget.charTyped(event)) {
                return true;
            }
        }
        return false;
    }

    boolean preeditUpdated(PreeditEvent event) {
        for (int i = overlays.size() - 1; i >= 0; i--) {
            OverlayRenderableWidget overlay = overlays.get(i);
            if (overlay instanceof AbstractWidget widget && widget.visible && overlay.isOverlayExpanded()
                    && widget.preeditUpdated(event)) {
                return true;
            }
        }
        return false;
    }

    private boolean setScrollOffset(int nextOffset) {
        int clamped = Math.max(0, Math.min(maxScroll(), nextOffset));
        if (clamped == scrollOffset) {
            return false;
        }
        scrollOffset = clamped;
        refreshWidgetStates();
        return true;
    }

    private void positionWidgets() {
        int maxScroll = maxScroll();
        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
        }
        for (AbstractWidget widget : widgets) {
            WidgetPosition base = basePositions.get(widget);
            widget.setX(base.x());
            widget.setY(base.y() - scrollOffset);
            boolean conditionVisible = WidgetConditions.evaluateVisible(widget);
            boolean intersects = intersectsViewport(widget) || expandedOverlayIntersectsViewport(widget);
            widget.visible = intersects && conditionVisible;
            widget.active = intersects && conditionVisible && WidgetConditions.evaluateActive(widget);
            WidgetConditions.refreshTooltip(widget);
            if (!widget.visible) {
                widget.setFocused(false);
            }
        }
    }

    private int maxScroll() {
        int maxScroll = baseMaxScroll;
        for (OverlayRenderableWidget overlay : overlays) {
            if (!(overlay instanceof AbstractWidget widget) || !overlay.isOverlayExpanded()
                    || !WidgetConditions.evaluateVisible(widget)) {
                continue;
            }
            maxScroll = Math.max(maxScroll, scrollOffset + overlay.overlayBottom() - viewportBottom);
        }
        return maxScroll;
    }

    private boolean insideViewport(double mouseX, double mouseY) {
        return mouseX >= viewportLeft && mouseX < viewportRight
                && mouseY >= viewportTop && mouseY < viewportBottom;
    }

    private boolean intersectsViewport(AbstractWidget widget) {
        return widget.getRight() > viewportLeft && widget.getX() < viewportRight
                && widget.getBottom() > viewportTop && widget.getY() < viewportBottom;
    }

    private boolean expandedOverlayIntersectsViewport(AbstractWidget widget) {
        if (!(widget instanceof OverlayRenderableWidget overlay) || !overlay.isOverlayExpanded()) {
            return false;
        }
        return widget.getBottom() < viewportBottom && overlay.overlayBottom() > viewportTop;
    }

    private record WidgetPosition(int x, int y) {
    }
}
