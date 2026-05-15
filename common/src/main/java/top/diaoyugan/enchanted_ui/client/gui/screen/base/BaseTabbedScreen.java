package top.diaoyugan.enchanted_ui.client.gui.screen.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.TabButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.scroll.ScrollBarWidget;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BaseTabbedScreen extends Screen {
    private static final int TAB_MIN_WIDTH = 60;
    private static final int TAB_PADDING = 10;

    @Nullable
    private final Screen parent;
    private final List<TabSpec> tabs = new ArrayList<>();
    private final List<PageView> pages = new ArrayList<>();
    private final List<Button> tabButtons = new ArrayList<>();
    private final List<ToastEntry> toasts = new ArrayList<>();

    private int currentPage = 0;
    private BottomBar bottomBar = BottomBar.none();
    private boolean opened;
    private boolean pageAttached;
    @Nullable
    private ModalDialog modal;

    public BaseTabbedScreen(@Nullable Screen parent, Component title) {
        super(title);
        this.parent = parent;
    }

    public <T extends GuiEventListener & Renderable & NarratableEntry> T add(T widget) {
        return addRenderableWidget(widget);
    }

    public BaseTabbedScreen tab(int x, int y, int height, Component label, Page page) {
        tabs.add(new TabSpec(x, y, height, label, page));
        return this;
    }

    public BaseTabbedScreen tab(int x, int y, int height, Component label, Style style, Page page) {
        return tab(x, y, height, label.copy().setStyle(style), page);
    }

    public BaseTabbedScreen bottomBar(BottomBar bottomBar) {
        this.bottomBar = Objects.requireNonNull(bottomBar, "bottomBar");
        return this;
    }

    public int currentPage() {
        return currentPage;
    }

    public void showPage(int index) {
        if (index < 0 || index >= pages.size()) return;

        int previousPage = currentPage;

        if (pageAttached && currentPage < pages.size()) {
            tabs.get(currentPage).page.onHide();
            pages.get(currentPage).removeFrom(this);
        }

        pages.get(index).addTo(this);
        currentPage = index;
        pageAttached = true;
        tabs.get(index).page.onShow();
        if (previousPage != index) {
            for (TabSpec tab : tabs) {
                tab.page.onPageChanged(previousPage, index);
            }
        }
        updateTabButtons();
    }

    @Nullable
    public Screen parent() {
        return parent;
    }

    @Override
    protected void init() {
        int centerX = width / 2 + 10;

        pages.clear();
        tabButtons.clear();
        clearWidgets();
        pageAttached = false;

        for (TabSpec tab : tabs) {
            pages.add(new PageView(tab.page.build(new BuildContext(width, height, centerX)), width, height));
        }

        if (!opened) {
            for (TabSpec tab : tabs) {
                tab.page.onOpen();
            }
            opened = true;
        }

        buildTabButtons();
        showPage(currentPage);

        bottomBar.add(this, width / 2, height - 28);
    }

    private void buildTabButtons() {
        if (minecraft == null) return;

        int computedWidth = TAB_MIN_WIDTH;
        for (TabSpec tab : tabs) {
            computedWidth = Math.max(computedWidth, minecraft.font.width(tab.label) + TAB_PADDING);
        }

        for (int i = 0; i < tabs.size(); i++) {
            TabSpec tab = tabs.get(i);
            int index = i;
            Button btn = new TabButtonWidget(
                    tab.x, tab.y, computedWidth, tab.height,
                    tab.label,
                    b -> showPage(index)
            );
            tabButtons.add(btn);
            addRenderableWidget(btn);
        }

        updateTabButtons();
    }

    private void updateTabButtons() {
        for (int i = 0; i < tabButtons.size(); i++) {
            tabButtons.get(i).active = (i != currentPage);
        }
    }

    public void saveAll() {
        for (TabSpec tab : tabs) {
            tab.page.onSave();
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (modal != null && modal.keyPressed(event)) {
            return true;
        }
        if (currentPage >= 0 && currentPage < pages.size() && pages.get(currentPage).keyPressed(event)) {
            return true;
        }
        if (currentPage >= 0 && currentPage < tabs.size()) {
            if (tabs.get(currentPage).page.keyPressed(event)) return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        if (modal != null && modal.charTyped(event)) {
            return true;
        }
        if (currentPage >= 0 && currentPage < pages.size() && pages.get(currentPage).charTyped(event)) {
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public boolean preeditUpdated(net.minecraft.client.input.PreeditEvent event) {
        if (modal != null && modal.preeditUpdated(event)) {
            return true;
        }
        if (currentPage >= 0 && currentPage < pages.size() && pages.get(currentPage).preeditUpdated(event)) {
            return true;
        }
        return super.preeditUpdated(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (modal != null && modal.mouseClicked(event, doubleClick)) {
            return true;
        }
        if (currentPage >= 0 && currentPage < pages.size() && pages.get(currentPage).mouseClicked(event, doubleClick)) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (modal != null && modal.mouseReleased(event)) {
            return true;
        }
        if (currentPage >= 0 && currentPage < pages.size() && pages.get(currentPage).mouseReleased(event)) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (modal != null && modal.mouseDragged(event, dragX, dragY)) {
            return true;
        }
        if (currentPage >= 0 && currentPage < pages.size() && pages.get(currentPage).mouseDragged(event, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (modal != null) {
            return true;
        }
        if (currentPage >= 0 && currentPage < pages.size() && pages.get(currentPage).mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        if (currentPage >= 0 && currentPage < pages.size()) {
            return pages.get(currentPage).scrollBy(verticalAmount);
        }
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        if (currentPage >= 0 && currentPage < pages.size()) {
            pages.get(currentPage).extractOverlayRenderState(guiGraphics, mouseX, mouseY, partialTick);
        }
        renderToasts(guiGraphics);
        if (modal != null) {
            modal.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (currentPage >= 0 && currentPage < tabs.size()) {
            tabs.get(currentPage).page.tick();
        }
        for (ToastEntry toast : toasts) {
            toast.tick();
        }
        toasts.removeIf(ToastEntry::expired);
    }

    @Override
    public void onClose() {
        if (pageAttached && currentPage >= 0 && currentPage < tabs.size()) {
            tabs.get(currentPage).page.onHide();
        }
        if (opened) {
            for (TabSpec tab : tabs) {
                tab.page.onClose();
            }
        }
        if (minecraft != null) {
            minecraft.setScreenAndShow(parent);
        }
    }

    public void showToast(Component message) {
        showToast(message, 60);
    }

    public void showToast(Component message, int durationTicks) {
        toasts.add(new ToastEntry(message, Math.max(20, durationTicks)));
    }

    public void showDialog(Component title, List<Component> lines, DialogAction... actions) {
        List<DialogAction> resolvedActions = actions.length == 0
                ? List.of(new DialogAction(CommonComponents.GUI_OK, this::closeDialog, true))
                : List.of(actions);
        modal = new ModalDialog(title, lines, resolvedActions);
    }

    public void showConfirm(Component title, Component message, Runnable confirmAction) {
        showDialog(
                title,
                List.of(message),
                new DialogAction(Component.translatable("eui.dialog.confirm"), confirmAction, true),
                new DialogAction(CommonComponents.GUI_CANCEL, () -> {}, true)
        );
    }

    public void closeDialog() {
        modal = null;
    }

    private void renderToasts(GuiGraphicsExtractor guiGraphics) {
        if (minecraft == null || toasts.isEmpty()) {
            return;
        }
        int y = 10;
        for (ToastEntry toast : toasts) {
            int width = minecraft.font.width(toast.message) + 16;
            int x = this.width - width - 10;
            guiGraphics.fill(x, y, x + width, y + 18, 0xCC202020);
            guiGraphics.outline(x, y, width, 18, 0xFF666666);
            guiGraphics.text(minecraft.font, toast.message, x + 8, y + 5, 0xFFFFFFFF, false);
            y += 22;
        }
    }

    public record BuildContext(int screenWidth, int screenHeight, int centerX) {
    }

    public interface Page {
        List<AbstractWidget> build(BuildContext ctx);

        default void onOpen() {
        }

        default void onClose() {
        }

        default void onShow() {
        }

        default void onHide() {
        }

        default void onPageChanged(int previousPage, int currentPage) {
        }

        default void onSave() {
        }

        default void tick() {
        }

        default boolean keyPressed(KeyEvent event) {
            return false;
        }
    }

    @FunctionalInterface
    public interface BottomBar {
        void add(BaseTabbedScreen screen, int centerX, int bottomY);

        static BottomBar none() {
            return (screen, centerX, bottomY) -> {
            };
        }
    }

    public record DialogAction(Component label, Runnable action, boolean closeAfterRun) {
        public DialogAction {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(action, "action");
        }
    }

    private record TabSpec(int x, int y, int height, Component label, Page page) {
        private TabSpec {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(page, "page");
        }
    }

    private static final class PageView {
        private static final int VIEWPORT_TOP = 10;
        private static final int VIEWPORT_BOTTOM_MARGIN = 36;
        private static final int SCROLLBAR_WIDTH = 8;

        private final List<AbstractWidget> widgets;
        private final List<OverlayRenderableWidget> overlays;
        private final Map<AbstractWidget, WidgetPosition> basePositions;
        @Nullable
        private final ScrollBarWidget scrollBar;
        private final int viewportTop;
        private final int viewportBottom;
        private final int maxScroll;

        private int scrollOffset;

        private PageView(List<AbstractWidget> widgets, int screenWidth, int screenHeight) {
            this.widgets = widgets;
            this.overlays = widgets.stream()
                    .filter(OverlayRenderableWidget.class::isInstance)
                    .map(OverlayRenderableWidget.class::cast)
                    .toList();
            this.basePositions = new IdentityHashMap<>();
            for (AbstractWidget widget : widgets) {
                basePositions.put(widget, new WidgetPosition(widget.getX(), widget.getY()));
            }

            this.viewportTop = VIEWPORT_TOP;
            this.viewportBottom = Math.max(VIEWPORT_TOP + 20, screenHeight - VIEWPORT_BOTTOM_MARGIN);

            int contentBottom = widgets.stream()
                    .mapToInt(widget -> widget.getY() + widget.getHeight())
                    .max()
                    .orElse(viewportBottom);
            int viewportHeight = viewportBottom - viewportTop;
            this.maxScroll = Math.max(0, contentBottom - viewportBottom);

            if (maxScroll > 0) {
                int contentRight = widgets.stream().mapToInt(AbstractWidget::getRight).max().orElse(screenWidth - 12);
                int scrollBarX = Math.min(screenWidth - 12, contentRight + 4);
                this.scrollBar = new ScrollBarWidget(
                        scrollBarX,
                        viewportTop,
                        SCROLLBAR_WIDTH,
                        viewportHeight,
                        viewportHeight,
                        contentBottom - viewportTop,
                        () -> scrollOffset,
                        () -> maxScroll,
                        this::setScrollOffset
                );
            } else {
                this.scrollBar = null;
            }

            applyScroll();
        }

        private void addTo(BaseTabbedScreen screen) {
            for (AbstractWidget widget : widgets) {
                screen.addRenderableWidget(widget);
            }
            if (scrollBar != null) {
                screen.addRenderableWidget(scrollBar);
            }
        }

        private void removeFrom(BaseTabbedScreen screen) {
            for (AbstractWidget widget : widgets) {
                screen.removeWidget(widget);
            }
            if (scrollBar != null) {
                screen.removeWidget(scrollBar);
            }
        }

        private boolean scrollBy(double verticalAmount) {
            if (maxScroll <= 0 || verticalAmount == 0) {
                return false;
            }
            int nextOffset = scrollOffset;
            if (verticalAmount > 0) {
                nextOffset -= 20;
            } else {
                nextOffset += 20;
            }
            return setScrollOffset(nextOffset);
        }

        private boolean setScrollOffset(int nextOffset) {
            int clamped = Math.max(0, Math.min(maxScroll, nextOffset));
            if (clamped == scrollOffset) {
                return false;
            }
            scrollOffset = clamped;
            applyScroll();
            return true;
        }

        private void applyScroll() {
            for (AbstractWidget widget : widgets) {
                WidgetPosition base = basePositions.get(widget);
                widget.setX(base.x());
                widget.setY(base.y() - scrollOffset);
                boolean intersects = widget.getBottom() > viewportTop && widget.getY() < viewportBottom;
                widget.visible = intersects;
                if (!intersects) {
                    widget.setFocused(false);
                }
            }
        }

        private void extractOverlayRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            for (OverlayRenderableWidget overlay : overlays) {
                if (overlay instanceof AbstractWidget widget && widget.visible) {
                    overlay.extractOverlayRenderState(guiGraphics, mouseX, mouseY, partialTick);
                }
            }
        }

        private boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            for (int i = overlays.size() - 1; i >= 0; i--) {
                OverlayRenderableWidget overlay = overlays.get(i);
                if (!(overlay instanceof AbstractWidget widget) || !widget.visible || !overlay.isOverlayExpanded()) {
                    continue;
                }
                if (widget.mouseClicked(event, doubleClick)) {
                    return true;
                }
            }
            return false;
        }

        private boolean mouseReleased(MouseButtonEvent event) {
            for (int i = overlays.size() - 1; i >= 0; i--) {
                OverlayRenderableWidget overlay = overlays.get(i);
                if (!(overlay instanceof AbstractWidget widget) || !widget.visible || !overlay.isOverlayExpanded()) {
                    continue;
                }
                if (widget.mouseReleased(event)) {
                    return true;
                }
            }
            return false;
        }

        private boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
            for (int i = overlays.size() - 1; i >= 0; i--) {
                OverlayRenderableWidget overlay = overlays.get(i);
                if (!(overlay instanceof AbstractWidget widget) || !widget.visible || !overlay.isOverlayExpanded()) {
                    continue;
                }
                if (widget.mouseDragged(event, dragX, dragY)) {
                    return true;
                }
            }
            return false;
        }

        private boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            for (int i = overlays.size() - 1; i >= 0; i--) {
                OverlayRenderableWidget overlay = overlays.get(i);
                if (!(overlay instanceof AbstractWidget widget) || !widget.visible || !overlay.isOverlayExpanded()) {
                    continue;
                }
                if (widget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                    return true;
                }
            }
            return false;
        }

        private boolean keyPressed(KeyEvent event) {
            for (int i = overlays.size() - 1; i >= 0; i--) {
                OverlayRenderableWidget overlay = overlays.get(i);
                if (!(overlay instanceof AbstractWidget widget) || !widget.visible || !overlay.isOverlayExpanded()) {
                    continue;
                }
                if (widget.keyPressed(event)) {
                    return true;
                }
            }
            return false;
        }

        private boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
            for (int i = overlays.size() - 1; i >= 0; i--) {
                OverlayRenderableWidget overlay = overlays.get(i);
                if (!(overlay instanceof AbstractWidget widget) || !widget.visible || !overlay.isOverlayExpanded()) {
                    continue;
                }
                if (widget.charTyped(event)) {
                    return true;
                }
            }
            return false;
        }

        private boolean preeditUpdated(net.minecraft.client.input.PreeditEvent event) {
            for (int i = overlays.size() - 1; i >= 0; i--) {
                OverlayRenderableWidget overlay = overlays.get(i);
                if (!(overlay instanceof AbstractWidget widget) || !widget.visible || !overlay.isOverlayExpanded()) {
                    continue;
                }
                if (widget.preeditUpdated(event)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final class ToastEntry {
        private final Component message;
        private int remainingTicks;

        private ToastEntry(Component message, int remainingTicks) {
            this.message = message;
            this.remainingTicks = remainingTicks;
        }

        private void tick() {
            remainingTicks--;
        }

        private boolean expired() {
            return remainingTicks <= 0;
        }
    }

    private final class ModalDialog {
        private final Component title;
        private final List<Component> lines;
        private final List<Button> buttons;

        private int left;
        private int top;
        private int width;
        private int height;

        private ModalDialog(Component title, List<Component> lines, List<DialogAction> actions) {
            this.title = title;
            this.lines = List.copyOf(lines);
            this.buttons = actions.stream().map(action -> Button.builder(action.label(), b -> {
                action.action().run();
                if (action.closeAfterRun()) {
                    closeDialog();
                }
            }).bounds(0, 0, 90, 20).build()).toList();
        }

        private boolean keyPressed(KeyEvent event) {
            if (event.key() == com.mojang.blaze3d.platform.InputConstants.KEY_ESCAPE) {
                closeDialog();
                return true;
            }
            return false;
        }

        private boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
            return true;
        }

        private boolean preeditUpdated(net.minecraft.client.input.PreeditEvent event) {
            return true;
        }

        private boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            layoutButtons();
            for (Button button : buttons) {
                if (button.mouseClicked(event, doubleClick)) {
                    return true;
                }
            }
            return true;
        }

        private boolean mouseReleased(MouseButtonEvent event) {
            for (Button button : buttons) {
                if (button.mouseReleased(event)) {
                    return true;
                }
            }
            return true;
        }

        private boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
            for (Button button : buttons) {
                if (button.mouseDragged(event, dragX, dragY)) {
                    return true;
                }
            }
            return true;
        }

        private void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (minecraft == null) {
                return;
            }
            layoutButtons();

            guiGraphics.fill(0, 0, BaseTabbedScreen.this.width, BaseTabbedScreen.this.height, 0x88000000);
            guiGraphics.fill(left, top, left + width, top + height, 0xEE1E1E1E);
            guiGraphics.outline(left, top, width, height, 0xFF777777);
            guiGraphics.text(minecraft.font, title, left + 10, top + 10, 0xFFFFFFFF, false);

            int textY = top + 30;
            for (Component line : lines) {
                guiGraphics.text(minecraft.font, line, left + 10, textY, 0xFFD0D0D0, false);
                textY += 12;
            }

            for (Button button : buttons) {
                button.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        private void layoutButtons() {
            int lineHeight = lines.size() * 12;
            width = Math.max(180, Math.min(320, BaseTabbedScreen.this.width - 40));
            height = 62 + lineHeight + 24;
            left = (BaseTabbedScreen.this.width - width) / 2;
            top = (BaseTabbedScreen.this.height - height) / 2;

            int totalButtonsWidth = buttons.size() * 90 + Math.max(0, buttons.size() - 1) * 6;
            int buttonX = left + (width - totalButtonsWidth) / 2;
            int buttonY = top + height - 30;
            for (Button button : buttons) {
                button.setPosition(buttonX, buttonY);
                buttonX += 96;
            }
        }
    }

    private record WidgetPosition(int x, int y) {
    }
}
