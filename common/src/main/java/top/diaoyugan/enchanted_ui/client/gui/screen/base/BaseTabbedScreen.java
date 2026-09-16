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
import org.jetbrains.annotations.ApiStatus;
import top.diaoyugan.enchanted_ui.api.client.gui.UIScreenStyle;
import top.diaoyugan.enchanted_ui.api.client.gui.UITabLayout;
import top.diaoyugan.enchanted_ui.api.client.gui.UIUnsavedChangesPrompt;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Runtime screen engine behind the public {@code UITabbedScreen} facade.
 * <p>
 * This class owns the Minecraft {@link Screen} lifecycle and coordinates its
 * package-private runtime collaborators. It bridges Minecraft events and page
 * lifecycle hooks without owning tab layout, viewport scrolling, modal, toast,
 * or guarded-close implementation state. Form construction stays in the builder
 * package and visual presets stay in the public API package.
 */
@ApiStatus.Internal
public class BaseTabbedScreen extends Screen {
    @Nullable
    private final Screen parent;
    private final List<TabSpec> tabs = new ArrayList<>();
    private final PageStackController pageStack = new PageStackController();
    private final ToastController toasts = new ToastController();
    private final TabStripController tabStrip = new TabStripController();
    private final CloseCoordinator closeCoordinator = new CloseCoordinator();

    // Declarative screen configuration. Presets populate these before the first init.
    @Nullable
    private Component headerTitle;
    private int contentLeft;
    private int contentTop = 10;
    private int contentRightMargin;
    private int contentBottomMargin = 36;

    // Runtime state rebuilt or reattached when Minecraft resizes the screen.
    private BottomBar bottomBar = BottomBar.none();
    private UIScreenStyle style = UIScreenStyle.DEFAULT;
    @Nullable
    private Component sidebarTitle;
    @Nullable
    private ScreenModal modal;

    public BaseTabbedScreen(@Nullable Screen parent, Component title) {
        super(title);
        this.parent = parent;
    }

    public <T extends GuiEventListener & Renderable & NarratableEntry> T add(T widget) {
        return addRenderableWidget(widget);
    }

    public BaseTabbedScreen tab(int x, int y, int height, Component label, Page page) {
        Objects.requireNonNull(page, "page");
        tabs.add(new TabSpec(x, y, height, label));
        pageStack.add(page);
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
        tabStrip.layout(Objects.requireNonNull(layout, "layout"));
        return this;
    }

    public BaseTabbedScreen tabsVisible(boolean visible) {
        tabStrip.visible(visible);
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
        closeCoordinator.prompt(Objects.requireNonNull(prompt, "prompt"));
        return this;
    }

    public UIUnsavedChangesPrompt unsavedChangesPrompt() {
        return closeCoordinator.prompt();
    }

    public int currentPage() {
        return pageStack.currentIndex();
    }

    public void showPage(int index) {
        if (pageStack.show(index, this::addWidget, this::addRenderableWidget, this::removeWidget)) {
            tabStrip.select(index);
        }
    }

    @Nullable
    public Screen parent() {
        return parent;
    }

    @Override
    protected void init() {
        clearWidgets();

        buildTabButtons();

        int viewportRight = Math.max(1, width - contentRightMargin);
        int viewportLeft = Math.min(effectiveContentLeft(), Math.max(0, viewportRight - 40));
        int viewportTop = effectiveContentTop();
        int viewportBottom = Math.max(viewportTop + 20, height - contentBottomMargin);
        int centerX = viewportLeft + (viewportRight - viewportLeft) / 2;

        pageStack.rebuild(
                new BuildContext(width, height, centerX, viewportLeft, viewportRight),
                viewportLeft,
                viewportTop,
                viewportRight,
                viewportBottom
        );
        showPage(pageStack.currentIndex());

        addRenderableWidget(new BottomBarBackdropWidget(0, height - 36, width, 36, style));
        bottomBar.add(this, width / 2, height - 28);
    }

    private void buildTabButtons() {
        if (minecraft == null) return;
        List<TabStripController.TabDefinition> definitions = new ArrayList<>(tabs.size());
        for (int i = 0; i < tabs.size(); i++) {
            TabSpec tab = tabs.get(i);
            int index = i;
            definitions.add(new TabStripController.TabDefinition(
                    tab.x(), tab.y(), tab.height(), tab.label(), () -> showPage(index)
            ));
        }
        tabStrip.build(definitions, sidebarTitle, minecraft.font, width, height, this::addRenderableWidget);
    }

    private int effectiveContentLeft() {
        return tabStrip.effectiveContentLeft(contentLeft);
    }

    private int effectiveContentTop() {
        return tabStrip.effectiveContentTop(contentTop);
    }

    public boolean saveAll() {
        return pageStack.saveAll();
    }

    public boolean hasUnsavedChanges() {
        return pageStack.hasUnsavedChanges();
    }

    public void reloadAll() {
        pageStack.reloadAll();
    }

    public void markAllClean() {
        pageStack.markAllClean();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (modal != null && modal.keyPressed(event)) {
            return true;
        }
        if (pageStack.keyPressed(event)) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (pageStack.keyReleased(event)) {
            return true;
        }
        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        if (modal != null && modal.charTyped(event)) {
            return true;
        }
        if (pageStack.charTyped(event)) {
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public boolean preeditUpdated(net.minecraft.client.input.PreeditEvent event) {
        if (modal != null && modal.preeditUpdated(event)) {
            return true;
        }
        if (pageStack.preeditUpdated(event)) {
            return true;
        }
        return super.preeditUpdated(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (modal != null && modal.mouseClicked(event, doubleClick, width, height)) {
            return true;
        }
        if (pageStack.mouseClicked(event, doubleClick)) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (modal != null && modal.mouseReleased(event)) {
            return true;
        }
        if (pageStack.mouseReleased(event)) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (modal != null && modal.mouseDragged(event, dragX, dragY)) {
            return true;
        }
        if (pageStack.mouseDragged(event, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (modal != null) {
            return true;
        }
        if (tabStrip.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        if (pageStack.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        return pageStack.scrollBy(verticalAmount);
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
        pageStack.extractRenderState(guiGraphics, backgroundMouseX, backgroundMouseY, partialTick);
        if (minecraft != null) {
            toasts.extractRenderState(guiGraphics, minecraft.font, width);
        }
        if (modal != null && minecraft != null) {
            modal.extractRenderState(guiGraphics, minecraft.font, width, height, mouseX, mouseY, partialTick);
        }
    }

    private void renderSidebarTitle(GuiGraphicsExtractor guiGraphics) {
        Button firstTab = tabStrip.firstButton();
        if (minecraft == null || sidebarTitle == null || tabs.isEmpty() || firstTab == null) {
            return;
        }
        int centerX = firstTab.getX() + firstTab.getWidth() / 2;
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
        pageStack.refreshWidgetStates();
        pageStack.tick();
        toasts.tick();
    }

    @Override
    public void onClose() {
        requestClose();
    }

    public void requestClose() {
        closeCoordinator.request(this);
    }

    boolean hasOpenModal() {
        return modal != null;
    }

    void forceClose() {
        pageStack.close();
        if (minecraft != null) {
            minecraft.setScreenAndShow(parent);
        }
        closeCoordinator.reset();
    }

    public void showToast(Component message) {
        showToast(message, 60);
    }

    public void showToast(Component message, int durationTicks) {
        toasts.show(message, durationTicks);
    }

    public void showDialog(Component title, List<Component> lines, DialogAction... actions) {
        List<DialogAction> resolvedActions = actions.length == 0
                ? List.of(new DialogAction(CommonComponents.GUI_OK, this::closeDialog, true))
                : List.of(actions);
        modal = new ScreenModal(title, lines, resolvedActions, this::closeDialog);
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

        default boolean keyReleased(KeyEvent event) {
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

    private record TabSpec(int x, int y, int height, Component label) {
        private TabSpec {
            Objects.requireNonNull(label, "label");
        }
    }
}
