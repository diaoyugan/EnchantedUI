package top.diaoyugan.enchanted_ui.client.gui.widget.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SearchableSelectDropdownWidget<T> extends AbstractDropdownListWidget {
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final Supplier<List<T>> entriesSupplier;
    private final Function<T, Component> display;
    private final EditBox search;

    public SearchableSelectDropdownWidget(
            int x,
            int y,
            int width,
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display,
            Component searchHint,
            int visibleRows
    ) {
        super(x, y, width, label, visibleRows);
        this.getter = getter;
        this.setter = setter;
        this.entriesSupplier = entriesSupplier;
        this.display = display;
        this.search = new EditBox(Minecraft.getInstance().font, x, y, width - (PANEL_PADDING * 2), ROW_HEIGHT, searchHint);
        this.search.setHint(searchHint);
        this.search.setCanLoseFocus(true);
    }

    @Override
    protected List<Component> entries() {
        return filteredEntries().stream().map(display).toList();
    }

    @Override
    protected Component headerText() {
        T current = getter.get();
        Component currentText = current == null ? Component.translatable("eui.select.none") : display.apply(current);
        return label().copy().append(Component.literal(": ")).append(currentText);
    }

    @Override
    protected void onEntryClicked(int index) {
        List<T> filtered = filteredEntries();
        if (index >= 0 && index < filtered.size()) {
            setter.accept(filtered.get(index));
            collapse();
        }
    }

    @Override
    protected int footerHeight() {
        return ROW_HEIGHT;
    }

    @Override
    protected void extractFooter(GuiGraphicsExtractor guiGraphics, int left, int top, int mouseX, int mouseY, float partialTick) {
        search.setRectangle(getWidth() - (PANEL_PADDING * 2), ROW_HEIGHT, left + PANEL_PADDING, top);
        search.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected boolean mouseClickedInFooter(MouseButtonEvent event, boolean doubleClick, int left, int top) {
        if (search.mouseClicked(event, doubleClick)) {
            search.setFocused(true);
            return true;
        }
        return false;
    }

    @Override
    protected boolean mouseReleasedInFooter(MouseButtonEvent event, int left, int top) {
        return search.mouseReleased(event);
    }

    @Override
    protected boolean mouseDraggedInFooter(MouseButtonEvent event, double dragX, double dragY, int left, int top) {
        return search.mouseDragged(event, dragX, dragY);
    }

    @Override
    protected boolean keyPressedInFooter(KeyEvent event) {
        return search.isFocused() && search.keyPressed(event);
    }

    @Override
    protected boolean charTypedInFooter(net.minecraft.client.input.CharacterEvent event) {
        return search.isFocused() && search.charTyped(event);
    }

    @Override
    protected boolean preeditUpdatedInFooter(net.minecraft.client.input.PreeditEvent event) {
        return search.isFocused() && search.preeditUpdated(event);
    }

    @Override
    protected int entryActionWidth() {
        return 0;
    }

    @Override
    protected void onEntryAction(int index) {
    }

    @Override
    protected void setInnerFocus(boolean focused) {
        search.setFocused(focused);
    }

    private List<T> filteredEntries() {
        String query = search.getValue().trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            return entriesSupplier.get();
        }
        return entriesSupplier.get().stream()
                .filter(entry -> display.apply(entry).getString().toLowerCase(Locale.ROOT).contains(query))
                .toList();
    }
}
