package top.diaoyugan.enchanted_ui.client.gui.screen.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.TabButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.WidgetConditions;
import top.diaoyugan.enchanted_ui.client.gui.widget.overlay.OverlayRenderableWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.scroll.ScrollBarWidget;
import top.diaoyugan.enchanted_ui.api.client.gui.UIScreenStyle;
import top.diaoyugan.enchanted_ui.api.client.gui.UITabLayout;
import top.diaoyugan.enchanted_ui.api.client.gui.UIUnsavedChangesPrompt;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Runtime screen engine behind the public {@code UITabbedScreen} facade.
 * <p>
 * This class deliberately owns the Minecraft {@link Screen} lifecycle and the
 * collaborators that depend on it directly: responsive tab placement, active
 * page attachment, modal/toast rendering, and close/save coordination. Form
 * construction remains in the builder package; visual presets remain in the
 * public API package.
 */
@ApiStatus.Internal
public class BaseTabbedScreen extends Screen {
    private static final int TAB_MIN_WIDTH = 60;
    private static final int TAB_PADDING = 10;

    @Nullable
    private final Screen parent;
    private final List<TabSpec> tabs = new ArrayList<>();
    private final List<PageView> pages = new ArrayList<>();
    private final List<Button> tabButtons = new ArrayList<>();
    private final List<ToastEntry> toasts = new ArrayList<>();

    // Declarative screen configuration. Presets populate these before the first init.
    @Nullable
    private UITabLayout tabLayout;
    @Nullable
    private Button previousTabsButton;
    @Nullable
    private Button nextTabsButton;
    @Nullable
    private Component headerTitle;
    private boolean tabsVisible = true;
    private int tabWindowStart;
    private int visibleTabCount;
    private int computedTabWidth = TAB_MIN_WIDTH;
    private boolean tabStripOverflow;
    private int tabStripLeft;
    private int tabStripTop;
    private int tabStripRight;
    private int tabStripBottom;
    private int contentLeft;
    private int contentTop = 10;
    private int contentRightMargin;
    private int contentBottomMargin = 36;

    // Runtime state rebuilt or reattached when Minecraft resizes the screen.
    private int currentPage = 0;
    private BottomBar bottomBar = BottomBar.none();
    private boolean opened;
    private boolean pageAttached;
    private boolean closeConfirmed;
    private UIScreenStyle style = UIScreenStyle.DEFAULT;
    private UIUnsavedChangesPrompt unsavedChangesPrompt = UIUnsavedChangesPrompt.defaults();
    @Nullable
    private Component sidebarTitle;
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

    public BaseTabbedScreen sidebarTitle(Component title) {
        this.sidebarTitle = Objects.requireNonNull(title, "title");
        return this;
    }

    public BaseTabbedScreen headerTitle(Component title) {
        this.headerTitle = Objects.requireNonNull(title, "title");
        return this;
    }

    public BaseTabbedScreen tabLayout(UITabLayout layout) {
        this.tabLayout = Objects.requireNonNull(layout, "layout");
        return this;
    }

    public BaseTabbedScreen tabsVisible(boolean visible) {
        this.tabsVisible = visible;
        return this;
    }

    public BaseTabbedScreen contentViewport(int left, int top, int rightMargin, int bottomMargin) {
        if (left < 0 || top < 0 || rightMargin < 0 || bottomMargin < 0) {
            throw new IllegalArgumentException("Content viewport values cannot be negative");
        }
        this.contentLeft = left;
        this.contentTop = top;
        this.contentRightMargin = rightMargin;
        this.contentBottomMargin = bottomMargin;
        return this;
    }

    public BaseTabbedScreen bottomBar(BottomBar bottomBar) {
        this.bottomBar = Objects.requireNonNull(bottomBar, "bottomBar");
        return this;
    }

    public BaseTabbedScreen style(UIScreenStyle style) {
        this.style = Objects.requireNonNull(style, "style");
        return this;
    }

    public UIScreenStyle style() {
        return style;
    }

    public BaseTabbedScreen unsavedChangesPrompt(UIUnsavedChangesPrompt prompt) {
        this.unsavedChangesPrompt = Objects.requireNonNull(prompt, "prompt");
        return this;
    }

    public UIUnsavedChangesPrompt unsavedChangesPrompt() {
        return unsavedChangesPrompt;
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
        pages.clear();
        tabButtons.clear();
        clearWidgets();
        pageAttached = false;

        buildTabButtons();

        int viewportRight = Math.max(1, width - contentRightMargin);
        int viewportLeft = Math.min(effectiveContentLeft(), Math.max(0, viewportRight - 40));
        int viewportTop = effectiveContentTop();
        int viewportBottom = Math.max(viewportTop + 20, height - contentBottomMargin);
        int centerX = viewportLeft + (viewportRight - viewportLeft) / 2;

        for (TabSpec tab : tabs) {
            pages.add(new PageView(
                    tab.page.build(new BuildContext(width, height, centerX, viewportLeft, viewportRight)),
                    viewportLeft,
                    viewportTop,
                    viewportRight,
                    viewportBottom
            ));
        }

        if (!opened) {
            for (TabSpec tab : tabs) {
                tab.page.onOpen();
            }
            opened = true;
        }

        showPage(currentPage);

        addRenderableWidget(new BottomBarBackdropWidget(0, height - 36, width, 36, style));
        bottomBar.add(this, width / 2, height - 28);
    }

    private void buildTabButtons() {
        if (minecraft == null || !tabsVisible) return;

        int computedWidth = TAB_MIN_WIDTH;
        for (TabSpec tab : tabs) {
            computedWidth = Math.max(computedWidth, minecraft.font.width(tab.label) + TAB_PADDING);
        }
        if (sidebarTitle != null) {
            computedWidth = Math.max(computedWidth, minecraft.font.width(sidebarTitle) + TAB_PADDING);
        }

        computedTabWidth = Math.min(computedWidth, Math.max(TAB_MIN_WIDTH, width - 20));

        for (int i = 0; i < tabs.size(); i++) {
            TabSpec tab = tabs.get(i);
            int index = i;
            Button btn = new TabButtonWidget(
                    tab.x, tab.y, computedTabWidth, tab.height,
                    tab.label,
                    b -> showPage(index)
            );
            tabButtons.add(btn);
            addRenderableWidget(btn);
        }

        if (tabLayout != null && !tabButtons.isEmpty()) {
            previousTabsButton = Button.builder(
                            tabLayout.orientation() == UITabLayout.Orientation.VERTICAL
                                    ? Component.literal("▲") : Component.literal("◀"),
                            button -> shiftTabWindow(-1)
                    )
                    .bounds(0, 0, 20, 16)
                    .build();
            nextTabsButton = Button.builder(
                            tabLayout.orientation() == UITabLayout.Orientation.VERTICAL
                                    ? Component.literal("▼") : Component.literal("▶"),
                            button -> shiftTabWindow(1)
                    )
                    .bounds(0, 0, 20, 16)
                    .build();
            addRenderableWidget(previousTabsButton);
            addRenderableWidget(nextTabsButton);
            layoutAutomaticTabs();
        }

        updateTabButtons();
    }

    private int effectiveContentLeft() {
        if (tabLayout != null
                && tabLayout.reserveContentSpace()
                && tabLayout.orientation() == UITabLayout.Orientation.VERTICAL) {
            return Math.max(contentLeft, tabLayout.startX() + computedTabWidth + 8);
        }
        return contentLeft;
    }

    private int effectiveContentTop() {
        if (tabLayout != null
                && tabLayout.reserveContentSpace()
                && tabLayout.orientation() == UITabLayout.Orientation.HORIZONTAL) {
            int tabHeight = tabs.stream().mapToInt(TabSpec::height).max().orElse(20);
            return Math.max(contentTop, tabLayout.startY() + tabHeight + 8);
        }
        return contentTop;
    }

    private void layoutAutomaticTabs() {
        if (tabLayout == null || previousTabsButton == null || nextTabsButton == null || tabButtons.isEmpty()) {
            return;
        }
        if (tabLayout.orientation() == UITabLayout.Orientation.VERTICAL) {
            layoutVerticalTabs();
        } else {
            layoutHorizontalTabs();
        }
    }

    private void layoutVerticalTabs() {
        if (tabLayout == null || previousTabsButton == null || nextTabsButton == null) return;

        int tabHeight = tabs.stream().mapToInt(TabSpec::height).max().orElse(20);
        int availableBottom = Math.max(tabLayout.startY() + tabHeight, height - tabLayout.endMargin());
        int availableHeight = availableBottom - tabLayout.startY();
        int fullHeight = tabButtons.size() * tabHeight + Math.max(0, tabButtons.size() - 1) * tabLayout.gap();
        tabStripOverflow = fullHeight > availableHeight;

        int contentStart = tabLayout.startY();
        int contentBottom = availableBottom;
        if (tabStripOverflow) {
            contentStart += 20;
            contentBottom -= 20;
        }
        visibleTabCount = Math.max(1, (contentBottom - contentStart + tabLayout.gap()) / (tabHeight + tabLayout.gap()));
        tabWindowStart = Math.max(0, Math.min(tabWindowStart, Math.max(0, tabButtons.size() - visibleTabCount)));

        for (int i = 0; i < tabButtons.size(); i++) {
            Button button = tabButtons.get(i);
            boolean visible = !tabStripOverflow || (i >= tabWindowStart && i < tabWindowStart + visibleTabCount);
            button.visible = visible;
            if (visible) {
                int visibleIndex = tabStripOverflow ? i - tabWindowStart : i;
                button.setX(tabLayout.startX());
                button.setY(contentStart + visibleIndex * (tabHeight + tabLayout.gap()));
                button.setWidth(computedTabWidth);
            }
        }

        previousTabsButton.visible = tabStripOverflow;
        nextTabsButton.visible = tabStripOverflow;
        previousTabsButton.active = tabWindowStart > 0;
        nextTabsButton.active = tabWindowStart + visibleTabCount < tabButtons.size();
        previousTabsButton.setX(tabLayout.startX());
        previousTabsButton.setY(tabLayout.startY());
        previousTabsButton.setWidth(computedTabWidth);
        nextTabsButton.setX(tabLayout.startX());
        nextTabsButton.setY(availableBottom - 16);
        nextTabsButton.setWidth(computedTabWidth);

        tabStripLeft = tabLayout.startX();
        tabStripTop = tabLayout.startY();
        tabStripRight = tabLayout.startX() + computedTabWidth;
        tabStripBottom = availableBottom;
    }

    private void layoutHorizontalTabs() {
        if (tabLayout == null || previousTabsButton == null || nextTabsButton == null) return;

        int tabHeight = tabs.stream().mapToInt(TabSpec::height).max().orElse(20);
        int availableRight = Math.max(tabLayout.startX() + TAB_MIN_WIDTH, width - tabLayout.endMargin());
        int availableWidth = availableRight - tabLayout.startX();
        int fullWidth = tabButtons.size() * computedTabWidth
                + Math.max(0, tabButtons.size() - 1) * tabLayout.gap();
        tabStripOverflow = fullWidth > availableWidth;

        int contentStart = tabLayout.startX();
        int contentRight = availableRight;
        int horizontalTabWidth = computedTabWidth;
        if (tabStripOverflow) {
            contentStart += 24;
            contentRight -= 24;
            horizontalTabWidth = Math.min(computedTabWidth, Math.max(20, contentRight - contentStart));
        }
        visibleTabCount = Math.max(1, (contentRight - contentStart + tabLayout.gap())
                / (horizontalTabWidth + tabLayout.gap()));
        tabWindowStart = Math.max(0, Math.min(tabWindowStart, Math.max(0, tabButtons.size() - visibleTabCount)));

        for (int i = 0; i < tabButtons.size(); i++) {
            Button button = tabButtons.get(i);
            boolean visible = !tabStripOverflow || (i >= tabWindowStart && i < tabWindowStart + visibleTabCount);
            button.visible = visible;
            if (visible) {
                int visibleIndex = tabStripOverflow ? i - tabWindowStart : i;
                button.setX(contentStart + visibleIndex * (horizontalTabWidth + tabLayout.gap()));
                button.setY(tabLayout.startY());
                button.setWidth(horizontalTabWidth);
            }
        }

        previousTabsButton.visible = tabStripOverflow;
        nextTabsButton.visible = tabStripOverflow;
        previousTabsButton.active = tabWindowStart > 0;
        nextTabsButton.active = tabWindowStart + visibleTabCount < tabButtons.size();
        previousTabsButton.setX(tabLayout.startX());
        previousTabsButton.setY(tabLayout.startY() + Math.max(0, (tabHeight - 16) / 2));
        nextTabsButton.setX(availableRight - 20);
        nextTabsButton.setY(tabLayout.startY() + Math.max(0, (tabHeight - 16) / 2));

        tabStripLeft = tabLayout.startX();
        tabStripTop = tabLayout.startY();
        tabStripRight = availableRight;
        tabStripBottom = tabLayout.startY() + tabHeight;
    }

    private void shiftTabWindow(int direction) {
        if (!tabStripOverflow || direction == 0) return;
        int next = Math.max(0, Math.min(
                Math.max(0, tabButtons.size() - visibleTabCount),
                tabWindowStart + direction
        ));
        if (next != tabWindowStart) {
            tabWindowStart = next;
            layoutAutomaticTabs();
        }
    }

    private void revealCurrentTab() {
        if (!tabStripOverflow || currentPage < 0 || currentPage >= tabButtons.size()) return;
        if (currentPage < tabWindowStart) {
            tabWindowStart = currentPage;
            layoutAutomaticTabs();
        } else if (currentPage >= tabWindowStart + visibleTabCount) {
            tabWindowStart = currentPage - visibleTabCount + 1;
            layoutAutomaticTabs();
        }
    }

    private void updateTabButtons() {
        revealCurrentTab();
        for (int i = 0; i < tabButtons.size(); i++) {
            WidgetConditions.setActiveState(tabButtons.get(i), i != currentPage);
        }
    }

    public boolean saveAll() {
        boolean saved = true;
        for (TabSpec tab : tabs) {
            saved &= tab.page.onSave();
        }
        return saved;
    }

    public boolean hasUnsavedChanges() {
        for (TabSpec tab : tabs) {
            if (tab.page.hasUnsavedChanges()) {
                return true;
            }
        }
        return false;
    }

    public void reloadAll() {
        for (TabSpec tab : tabs) {
            tab.page.reload();
        }
    }

    public void markAllClean() {
        for (TabSpec tab : tabs) {
            tab.page.markClean();
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
        if (tabStripOverflow
                && mouseX >= tabStripLeft && mouseX < tabStripRight
                && mouseY >= tabStripTop && mouseY < tabStripBottom) {
            double amount = horizontalAmount != 0 ? horizontalAmount : verticalAmount;
            if (amount != 0) {
                shiftTabWindow(amount > 0 ? -1 : 1);
                return true;
            }
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
    protected void extractBlurredBackground(GuiGraphicsExtractor graphics) {
        if (style.backgroundBlur()) {
            super.extractBlurredBackground(graphics);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int backgroundMouseX = modal == null ? mouseX : Integer.MIN_VALUE;
        int backgroundMouseY = modal == null ? mouseY : Integer.MIN_VALUE;
        super.extractRenderState(guiGraphics, backgroundMouseX, backgroundMouseY, partialTick);
        renderHeaderTitle(guiGraphics);
        renderSidebarTitle(guiGraphics);
        if (currentPage >= 0 && currentPage < pages.size()) {
            pages.get(currentPage).extractWidgetRenderState(guiGraphics, backgroundMouseX, backgroundMouseY, partialTick);
            pages.get(currentPage).extractOverlayRenderState(guiGraphics, backgroundMouseX, backgroundMouseY, partialTick);
        }
        renderToasts(guiGraphics);
        if (modal != null) {
            modal.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderSidebarTitle(GuiGraphicsExtractor guiGraphics) {
        if (minecraft == null || sidebarTitle == null || tabs.isEmpty() || tabButtons.isEmpty()) {
            return;
        }
        int centerX = tabButtons.getFirst().getX() + tabButtons.getFirst().getWidth() / 2;
        int top = tabs.stream().mapToInt(TabSpec::y).min().orElse(20);
        guiGraphics.centeredText(minecraft.font, sidebarTitle, centerX, Math.max(4, top - 16), 0xFFFFFFFF);
    }

    private void renderHeaderTitle(GuiGraphicsExtractor guiGraphics) {
        if (minecraft == null || headerTitle == null) return;
        guiGraphics.centeredText(minecraft.font, headerTitle, width / 2, 8, 0xFFFFFFFF);
    }

    @Override
    public void tick() {
        super.tick();
        if (currentPage >= 0 && currentPage < pages.size()) {
            pages.get(currentPage).refreshWidgetStates();
        }
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
        requestClose();
    }

    public void requestClose() {
        if (closeConfirmed) {
            forceClose();
            return;
        }
        if (modal != null) {
            return;
        }
        if (hasUnsavedChanges()) {
            showDialog(
                    unsavedChangesPrompt.title(),
                    unsavedChangesPrompt.lines(),
                    new DialogAction(unsavedChangesPrompt.discardLabel(), () -> {
                        closeConfirmed = true;
                        forceClose();
                    }, true),
                    new DialogAction(unsavedChangesPrompt.cancelLabel(), () -> {
                    }, true)
            );
            return;
        }
        forceClose();
    }

    private void forceClose() {
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
        closeConfirmed = false;
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
        showConfirm(
                title,
                message,
                UILocalization.frameworkText("dialog.confirm", "Confirm"),
                CommonComponents.GUI_CANCEL,
                confirmAction
        );
    }

    public void showConfirm(
            Component title,
            Component message,
            Component confirmLabel,
            Component cancelLabel,
            Runnable confirmAction
    ) {
        showDialog(
                title,
                List.of(message),
                new DialogAction(confirmLabel, confirmAction, true),
                new DialogAction(cancelLabel, () -> {}, true)
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

    public record BuildContext(
            int screenWidth,
            int screenHeight,
            int centerX,
            int viewportLeft,
            int viewportRight
    ) {
        public BuildContext(int screenWidth, int screenHeight, int centerX) {
            this(screenWidth, screenHeight, centerX, 0, screenWidth);
        }

        public int availableWidth() {
            return Math.max(0, viewportRight - viewportLeft);
        }
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

        default boolean onSave() { return true; }

        default boolean hasUnsavedChanges() {
            return false;
        }

        default void reload() {
        }

        default void markClean() {
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

    /**
     * Owns one built page's viewport state.
     * <p>
     * Widget coordinates in {@code basePositions} never include scroll offset.
     * Every refresh derives visible positions from those coordinates, which
     * prevents resize, reload, and overlay expansion from accumulating drift.
     */
    private static final class PageView {
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

        private PageView(
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

        private void addTo(BaseTabbedScreen screen) {
            for (AbstractWidget widget : widgets) {
                screen.addWidget(widget);
            }
            screen.addRenderableWidget(scrollBar);
        }

        private void removeFrom(BaseTabbedScreen screen) {
            for (AbstractWidget widget : widgets) {
                screen.removeWidget(widget);
            }
            screen.removeWidget(scrollBar);
        }

        private boolean scrollBy(double verticalAmount) {
            if (maxScroll() <= 0 || verticalAmount == 0) {
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
            int clamped = Math.max(0, Math.min(maxScroll(), nextOffset));
            if (clamped == scrollOffset) {
                return false;
            }
            scrollOffset = clamped;
            refreshWidgetStates();
            return true;
        }

        private void refreshWidgetStates() {
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

        private void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.enableScissor(viewportLeft, viewportTop, viewportRight, viewportBottom);
            for (AbstractWidget widget : widgets) {
                if (widget.visible) {
                    widget.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
                }
            }
            guiGraphics.disableScissor();
        }

        private void extractOverlayRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.enableScissor(viewportLeft, viewportTop, viewportRight, viewportBottom);
            for (OverlayRenderableWidget overlay : overlays) {
                if (overlay instanceof AbstractWidget widget && widget.visible) {
                    overlay.extractOverlayRenderState(guiGraphics, mouseX, mouseY, partialTick);
                }
            }
            guiGraphics.disableScissor();
        }

        private boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (!insideViewport(event.x(), event.y())) {
                return false;
            }
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
            if (!insideViewport(event.x(), event.y())) {
                return false;
            }
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
            if (!insideViewport(event.x(), event.y())) {
                return false;
            }
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
            if (!insideViewport(mouseX, mouseY)) {
                return false;
            }
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

        private int maxScroll() {
            int maxScroll = baseMaxScroll;
            for (OverlayRenderableWidget overlay : overlays) {
                if (!(overlay instanceof AbstractWidget widget) || !overlay.isOverlayExpanded() || !WidgetConditions.evaluateVisible(widget)) {
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

    private static final class BottomBarBackdropWidget extends AbstractWidget {
        private final UIScreenStyle style;

        private BottomBarBackdropWidget(int x, int y, int width, int height, UIScreenStyle style) {
            super(x, y, width, height, Component.empty());
            this.style = style;
            this.active = false;
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (!style.bottomBarBlur() && (style.bottomBarBackgroundColor() >>> 24) == 0) {
                return;
            }

            if (style.bottomBarBlur() && !style.backgroundBlur()) {
                guiGraphics.blurBeforeThisStratum();
            }
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), style.bottomBarBackgroundColor());
            if ((style.bottomBarSeparatorColor() >>> 24) != 0) {
                guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + 1, style.bottomBarSeparatorColor());
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }
}
