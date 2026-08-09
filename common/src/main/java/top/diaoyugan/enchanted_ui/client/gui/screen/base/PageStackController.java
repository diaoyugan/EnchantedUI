package top.diaoyugan.enchanted_ui.client.gui.screen.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Owns page definitions, active-page lifecycle, and viewport event routing. */
final class PageStackController {
    private final List<BaseTabbedScreen.Page> definitions = new ArrayList<>();
    private final List<PageViewport> viewports = new ArrayList<>();

    private int currentIndex;
    private boolean opened;
    private boolean attached;

    void add(BaseTabbedScreen.Page page) {
        definitions.add(page);
    }

    void rebuild(
            BaseTabbedScreen.BuildContext context,
            int viewportLeft,
            int viewportTop,
            int viewportRight,
            int viewportBottom
    ) {
        viewports.clear();
        attached = false;
        for (BaseTabbedScreen.Page page : definitions) {
            viewports.add(new PageViewport(
                    page.build(context),
                    viewportLeft,
                    viewportTop,
                    viewportRight,
                    viewportBottom
            ));
        }
        if (!opened) {
            definitions.forEach(BaseTabbedScreen.Page::onOpen);
            opened = true;
        }
    }

    boolean show(
            int index,
            Consumer<AbstractWidget> addWidget,
            Consumer<AbstractWidget> addRenderableWidget,
            Consumer<AbstractWidget> removeWidget
    ) {
        if (index < 0 || index >= viewports.size()) {
            return false;
        }

        int previousIndex = currentIndex;
        if (attached && currentIndex < viewports.size()) {
            definitions.get(currentIndex).onHide();
            viewports.get(currentIndex).detach(removeWidget);
        }

        viewports.get(index).attach(addWidget, addRenderableWidget);
        currentIndex = index;
        attached = true;
        definitions.get(index).onShow();
        if (previousIndex != index) {
            definitions.forEach(page -> page.onPageChanged(previousIndex, index));
        }
        return true;
    }

    int currentIndex() {
        return currentIndex;
    }

    boolean saveAll() {
        boolean saved = true;
        for (BaseTabbedScreen.Page page : definitions) {
            saved &= page.onSave();
        }
        return saved;
    }

    boolean hasUnsavedChanges() {
        return definitions.stream().anyMatch(BaseTabbedScreen.Page::hasUnsavedChanges);
    }

    void reloadAll() {
        definitions.forEach(BaseTabbedScreen.Page::reload);
    }

    void markAllClean() {
        definitions.forEach(BaseTabbedScreen.Page::markClean);
    }

    boolean keyPressed(KeyEvent event) {
        if (activeViewport() != null && activeViewport().keyPressed(event)) {
            return true;
        }
        return activePage() != null && activePage().keyPressed(event);
    }

    boolean keyReleased(KeyEvent event) {
        return activePage() != null && activePage().keyReleased(event);
    }

    boolean charTyped(CharacterEvent event) {
        return activeViewport() != null && activeViewport().charTyped(event);
    }

    boolean preeditUpdated(PreeditEvent event) {
        return activeViewport() != null && activeViewport().preeditUpdated(event);
    }

    boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return activeViewport() != null && activeViewport().mouseClicked(event, doubleClick);
    }

    boolean mouseReleased(MouseButtonEvent event) {
        return activeViewport() != null && activeViewport().mouseReleased(event);
    }

    boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return activeViewport() != null && activeViewport().mouseDragged(event, dragX, dragY);
    }

    boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return activeViewport() != null
                && activeViewport().mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    boolean scrollBy(double verticalAmount) {
        return activeViewport() != null && activeViewport().scrollBy(verticalAmount);
    }

    void refreshWidgetStates() {
        if (activeViewport() != null) {
            activeViewport().refreshWidgetStates();
        }
    }

    void tick() {
        if (activePage() != null) {
            activePage().tick();
        }
    }

    void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (activeViewport() == null) {
            return;
        }
        activeViewport().extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
        activeViewport().extractOverlayRenderState(graphics, mouseX, mouseY, partialTick);
    }

    void close() {
        if (attached && currentIndex >= 0 && currentIndex < definitions.size()) {
            definitions.get(currentIndex).onHide();
        }
        if (opened) {
            definitions.forEach(BaseTabbedScreen.Page::onClose);
        }
    }

    private BaseTabbedScreen.Page activePage() {
        return currentIndex >= 0 && currentIndex < definitions.size() ? definitions.get(currentIndex) : null;
    }

    private PageViewport activeViewport() {
        return currentIndex >= 0 && currentIndex < viewports.size() ? viewports.get(currentIndex) : null;
    }
}
