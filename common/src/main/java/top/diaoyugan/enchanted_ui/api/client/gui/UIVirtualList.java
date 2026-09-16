package top.diaoyugan.enchanted_ui.api.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;
import top.diaoyugan.enchanted_ui.api.client.gui.animation.UIAnimation;
import java.time.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Selectable, keyboard accessible list that only visits visible rows while rendering.
 * Selection is stored by key so it remains stable when items are refreshed or filtered.
 */
public final class UIVirtualList<T, K> extends AbstractWidget {
    public record RowState(boolean selected, boolean playing, boolean hovered, boolean focused, boolean actionHovered) {}

    @FunctionalInterface
    public interface RowRenderer<T> {
        void render(GuiGraphicsExtractor graphics, UIBounds bounds, T item, RowState state);
    }

    private final Supplier<? extends Collection<T>> itemsSupplier;
    private final Function<T, K> keyFunction;
    private final int rowHeight;
    private final boolean multiSelect;
    private final Consumer<T> onSelect;
    private final Consumer<List<T>> onMultiSelect;
    private final Consumer<T> onActivate;
    private final RowRenderer<T> rowRenderer;
    private final Predicate<T> playing;
    private final BiPredicate<T, String> filter;
    private final BiConsumer<T, Integer> rowAction;
    private final int actionWidth;
    private final UITheme theme;
    private final boolean marqueeText;
    private final UIAnimation marqueeAnimation=UIAnimation.pingPong().duration(Duration.ofSeconds(5));
    private final Supplier<T> selectedSupplier;
    private final Supplier<? extends Collection<T>> selectedItemsSupplier;
    private final Set<K> selectedKeys = new HashSet<>();
    private String query = "";
    private int scrollOffset;
    private int focusedIndex = -1;

    private UIVirtualList(Builder<T, K> b) {
        super(b.x, b.y, b.width, b.height, b.label);
        itemsSupplier = b.itemsSupplier;
        keyFunction = b.keyFunction;
        rowHeight = b.rowHeight;
        multiSelect = b.multiSelect;
        onSelect = b.onSelect;
        onMultiSelect = b.onMultiSelect;
        onActivate = b.onActivate;
        rowRenderer = b.rowRenderer != null ? b.rowRenderer : defaultRenderer(b.text);
        playing = b.playing;
        filter = b.filter;
        rowAction = b.rowAction;
        actionWidth = b.actionWidth;
        theme = b.theme;
        marqueeText = b.marqueeText;
        selectedSupplier = b.selectedSupplier;
        selectedItemsSupplier = b.selectedItemsSupplier;
    }

    public static <T> Builder<T, T> builder() { return new Builder<>(); }
    public static <T, K> Builder<T, K> builder(Function<T, K> key) { return new Builder<T, K>().key(key); }

    public UIVirtualList<T, K> query(String query) {
        this.query = query == null ? "" : query;
        scrollOffset = 0;
        focusedIndex = -1;
        return this;
    }

    public String query() { return query; }
    public Set<K> selectedKeys() { return Set.copyOf(selectedKeys); }
    public void clearSelection() { selectedKeys.clear(); notifySelection(); }

    public void selectKey(K key) {
        if (!multiSelect) selectedKeys.clear();
        selectedKeys.add(key);
        List<T> items = visibleItems();
        focusedIndex = indexOfKey(items, key);
        scrollToIndex(focusedIndex);
        notifySelection();
    }

    public void scrollToKey(K key) { scrollToIndex(indexOfKey(visibleItems(), key)); }
    public void scrollToItem(T item) { if (item != null) scrollToKey(keyFunction.apply(item)); }
    public int firstVisibleIndex() { return scrollOffset / rowHeight; }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        syncExternalSelection();
        List<T> items = visibleItems();
        clampScroll(items.size());
        UITheme.Colors colors = theme.colors();
        g.fill(getX(), getY(), getX() + width, getY() + height, colors.surface());
        g.outline(getX(), getY(), width, height, colors.border());
        g.enableScissor(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1);
        int first = firstVisibleIndex();
        int pixelOffset = scrollOffset % rowHeight;
        int visibleCount = height / rowHeight + 2;
        for (int index = first; index < Math.min(items.size(), first + visibleCount); index++) {
            int y = getY() + (index - first) * rowHeight - pixelOffset;
            T item = items.get(index);
            boolean hovered = mouseX >= getX() && mouseX < getX() + width && mouseY >= y && mouseY < y + rowHeight;
            boolean actionHovered = hovered && rowAction != null && mouseX >= getX() + width - actionWidth;
            rowRenderer.render(g, new UIBounds(getX() + 1, y, width - 2, rowHeight), item,
                    new RowState(selectedKeys.contains(keyFunction.apply(item)), playing.test(item), hovered,
                            isFocused() && index == focusedIndex, actionHovered));
        }
        g.disableScissor();
        renderScrollbar(g, items.size());
    }

    private RowRenderer<T> defaultRenderer(Function<T, Component> text) {
        return (g, bounds, item, state) -> {
            UITheme.Colors colors = theme.colors();
            int color = state.playing() ? colors.playing() : state.selected() ? colors.accent()
                    : state.hovered() || state.focused() ? colors.surfaceHovered() : colors.surface();
            g.fill(bounds.x(), bounds.y(), bounds.x() + bounds.width(), bounds.y() + bounds.height(), color);
            Component label = text.apply(item);
            int textX=bounds.x()+6;
            int available=Math.max(1,bounds.width()-12-(rowAction==null?0:actionWidth));
            int textWidth=Minecraft.getInstance().font.width(label);
            if(marqueeText&&textWidth>available)textX-=Math.round((textWidth-available)*marqueeAnimation.value());
            g.enableScissor(bounds.x()+1,bounds.y(),bounds.x()+6+available,bounds.y()+bounds.height());
            g.text(Minecraft.getInstance().font, label, textX,
                    bounds.y() + Math.max(1, (bounds.height() - 9) / 2), colors.text(), false);
            g.disableScissor();
            if (state.actionHovered()) {
                g.text(Minecraft.getInstance().font, Component.literal("..."), bounds.x() + bounds.width() - actionWidth + 5,
                        bounds.y() + Math.max(1, (bounds.height() - 9) / 2), colors.text(), false);
            }
        };
    }

    private void renderScrollbar(GuiGraphicsExtractor g, int itemCount) {
        int content = itemCount * rowHeight;
        if (content <= height) return;
        int trackX = getX() + width - 3;
        int thumbHeight = Math.max(12, height * height / content);
        int thumbY = getY() + (int) ((height - thumbHeight) * (scrollOffset / (double) maxScroll(itemCount)));
        g.fill(trackX, getY() + 1, trackX + 2, getY() + height - 1, theme.colors().border());
        g.fill(trackX, thumbY, trackX + 2, thumbY + thumbHeight, theme.colors().textMuted());
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        List<T> items = visibleItems();
        int index = (int) ((event.y() - getY() + scrollOffset) / rowHeight);
        if (index < 0 || index >= items.size()) return;
        T item = items.get(index);
        focusedIndex = index;
        setFocused(true);
        if (rowAction != null && event.x() >= getX() + width - actionWidth) {
            rowAction.accept(item, event.button());
            return;
        }
        select(item);
        if (doubleClick) onActivate.accept(item);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        scrollOffset -= (int) Math.round(verticalAmount * rowHeight * 2.5);
        clampScroll(visibleItems().size());
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!isFocused()) return false;
        List<T> items = visibleItems();
        if (items.isEmpty()) return false;
        if (event.key() == InputConstants.KEY_UP) moveFocus(items, -1);
        else if (event.key() == InputConstants.KEY_DOWN) moveFocus(items, 1);
        else if (event.key() == InputConstants.KEY_HOME) focus(items, 0);
        else if (event.key() == InputConstants.KEY_END) focus(items, items.size() - 1);
        else if (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_NUMPADENTER) {
            if (focusedIndex >= 0 && focusedIndex < items.size()) onActivate.accept(items.get(focusedIndex));
        } else if (event.key() == InputConstants.KEY_SPACE) {
            if (focusedIndex >= 0 && focusedIndex < items.size()) select(items.get(focusedIndex));
        } else return false;
        return true;
    }

    private void moveFocus(List<T> items, int delta) { focus(items, Math.max(0, Math.min(items.size() - 1, focusedIndex < 0 ? 0 : focusedIndex + delta))); }
    private void focus(List<T> items, int index) { focusedIndex = index; select(items.get(index)); scrollToIndex(index); }
    private void select(T item) {
        K key = keyFunction.apply(item);
        if (multiSelect) {
            if (!selectedKeys.remove(key)) selectedKeys.add(key);
        } else { selectedKeys.clear(); selectedKeys.add(key); }
        onSelect.accept(item);
        notifySelection();
    }

    private void notifySelection() {
        if (!multiSelect) return;
        Collection<T> allItems = itemsSupplier.get();
        if (allItems == null) allItems = List.of();
        List<T> selected = allItems.stream().filter(item -> selectedKeys.contains(keyFunction.apply(item))).toList();
        onMultiSelect.accept(selected);
    }

    private void syncExternalSelection() {
        if (selectedItemsSupplier != null) {
            Collection<T> values = selectedItemsSupplier.get();
            selectedKeys.clear();
            if (values != null) for (T item : values) if (item != null) selectedKeys.add(keyFunction.apply(item));
        } else if (selectedSupplier != null) {
            T item = selectedSupplier.get();
            selectedKeys.clear();
            if (item != null) selectedKeys.add(keyFunction.apply(item));
        }
    }

    private List<T> visibleItems() {
        Collection<T> source = itemsSupplier.get();
        if (source == null || source.isEmpty()) return List.of();
        List<T> result = new ArrayList<>(source.size());
        for (T item : source) if (query.isBlank() || filter.test(item, query)) result.add(item);
        return result;
    }

    private int indexOfKey(List<T> items, K key) {
        for (int i = 0; i < items.size(); i++) if (Objects.equals(keyFunction.apply(items.get(i)), key)) return i;
        return -1;
    }
    private int maxScroll(int count) { return Math.max(0, count * rowHeight - height); }
    private void clampScroll(int count) { scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll(count))); }
    private void scrollToIndex(int index) {
        if (index < 0) return;
        int top = index * rowHeight, bottom = top + rowHeight;
        if (top < scrollOffset) scrollOffset = top;
        else if (bottom > scrollOffset + height) scrollOffset = bottom - height;
        clampScroll(visibleItems().size());
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput output) {}

    public static final class Builder<T, K> {
        private int x, y, width = 160, height = 120, rowHeight = 24, actionWidth = 24;
        private Component label = Component.empty();
        private Supplier<? extends Collection<T>> itemsSupplier = List::of;
        @SuppressWarnings("unchecked") private Function<T, K> keyFunction = item -> (K) item;
        private Function<T, Component> text = item -> Component.literal(String.valueOf(item));
        private boolean multiSelect;
        private Consumer<T> onSelect = item -> {};
        private Consumer<List<T>> onMultiSelect = items -> {};
        private Consumer<T> onActivate = item -> {};
        private RowRenderer<T> rowRenderer;
        private Predicate<T> playing = item -> false;
        private BiPredicate<T, String> filter = (item, query) -> String.valueOf(item).toLowerCase().contains(query.toLowerCase());
        private BiConsumer<T, Integer> rowAction;
        private UITheme theme = UIThemes.current();
        private boolean marqueeText;
        private Supplier<T> selectedSupplier;
        private Supplier<? extends Collection<T>> selectedItemsSupplier;

        public Builder<T, K> bounds(int x, int y, int width, int height) { this.x=x; this.y=y; this.width=width; this.height=height; return this; }
        public Builder<T, K> label(Component label) { this.label=label; return this; }
        public Builder<T, K> items(Supplier<? extends Collection<T>> items) { itemsSupplier=Objects.requireNonNull(items); return this; }
        public Builder<T, K> key(Function<T, K> key) { keyFunction=Objects.requireNonNull(key); return this; }
        public Builder<T, K> text(Function<T, Component> text) { this.text=Objects.requireNonNull(text); return this; }
        public Builder<T, K> rowHeight(int value) { rowHeight=Math.max(12, value); return this; }
        public Builder<T, K> multiSelect(boolean value) { multiSelect=value; return this; }
        public Builder<T, K> selected(Supplier<T> value) { selectedSupplier=Objects.requireNonNull(value); selectedItemsSupplier=null; return this; }
        public Builder<T, K> selectedItems(Supplier<? extends Collection<T>> value) { selectedItemsSupplier=Objects.requireNonNull(value); selectedSupplier=null; multiSelect=true; return this; }
        public Builder<T, K> onSelect(Consumer<T> value) { onSelect=Objects.requireNonNull(value); return this; }
        public Builder<T, K> onMultiSelect(Consumer<List<T>> value) { onMultiSelect=Objects.requireNonNull(value); return this; }
        public Builder<T, K> onActivate(Consumer<T> value) { onActivate=Objects.requireNonNull(value); return this; }
        public Builder<T, K> row(RowRenderer<T> value) { rowRenderer=Objects.requireNonNull(value); return this; }
        public Builder<T, K> marqueeText(boolean value) { marqueeText=value; return this; }
        public Builder<T, K> playing(Predicate<T> value) { playing=Objects.requireNonNull(value); return this; }
        public Builder<T, K> filter(BiPredicate<T, String> value) { filter=Objects.requireNonNull(value); return this; }
        public Builder<T, K> rowAction(int width, BiConsumer<T, Integer> value) { actionWidth=Math.max(12,width); rowAction=Objects.requireNonNull(value); return this; }
        public Builder<T, K> theme(UITheme value) { theme=Objects.requireNonNull(value); return this; }
        public UIVirtualList<T, K> build() { return new UIVirtualList<>(this); }
    }
}
