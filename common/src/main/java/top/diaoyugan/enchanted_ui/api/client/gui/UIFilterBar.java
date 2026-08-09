package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Search field with optional supplier-backed actions arranged along its right edge. */
public final class UIFilterBar implements UIWidgetGroup {
    private final Component searchNarration;
    private final Component searchHint;
    private final Supplier<String> query;
    private final Consumer<String> onQueryChanged;
    private final List<Action> actions;
    private final int height;
    private final int gap;

    private UIFilterBar(Builder builder) {
        searchNarration = builder.searchNarration;
        searchHint = builder.searchHint;
        query = builder.query;
        onQueryChanged = builder.onQueryChanged;
        actions = List.copyOf(builder.actions);
        height = builder.height;
        gap = builder.gap;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public List<AbstractWidget> build(UIBounds bounds) {
        List<AbstractWidget> widgets = new ArrayList<>();
        int actionWidth = actions.stream().mapToInt(Action::width).sum();
        int gaps = Math.max(0, actions.size() - 1) * gap;
        int searchGap = searchNarration == null || actions.isEmpty() ? 0 : gap;
        int searchWidth = Math.max(0, bounds.width() - actionWidth - gaps - searchGap);
        int widgetHeight = Math.min(height, bounds.height());

        if (searchNarration != null && searchWidth > 0) {
            EditBox search = new EditBox(
                    Minecraft.getInstance().font,
                    bounds.x(),
                    bounds.y(),
                    searchWidth,
                    widgetHeight,
                    searchNarration
            );
            search.setHint(searchHint);
            search.setValue(query.get());
            search.setResponder(onQueryChanged);
            widgets.add(search);
        }

        int cursor = bounds.x() + bounds.width() - actionWidth - gaps;
        for (Action action : actions) {
            UIDynamicButton button = new UIDynamicButton(
                    cursor,
                    bounds.y(),
                    action.width(),
                    widgetHeight,
                    action.label(),
                    action.narration(),
                    action.onPress()
            );
            button.setTooltip(Tooltip.create(action.narration().get()));
            widgets.add(button);
            cursor += action.width() + gap;
        }
        return widgets;
    }

    private record Action(
            int width,
            Supplier<Component> label,
            Supplier<Component> narration,
            Runnable onPress
    ) {}

    public static final class Builder {
        private Component searchNarration;
        private Component searchHint = Component.empty();
        private Supplier<String> query = () -> "";
        private Consumer<String> onQueryChanged = ignored -> {};
        private final List<Action> actions = new ArrayList<>();
        private int height = 20;
        private int gap = 4;

        private Builder() {}

        public Builder search(
                Component narration,
                Component hint,
                Supplier<String> query,
                Consumer<String> onQueryChanged
        ) {
            searchNarration = Objects.requireNonNull(narration);
            searchHint = Objects.requireNonNull(hint);
            this.query = Objects.requireNonNull(query);
            this.onQueryChanged = Objects.requireNonNull(onQueryChanged);
            return this;
        }

        public Builder action(
                int width,
                Supplier<Component> label,
                Supplier<Component> narration,
                Runnable onPress
        ) {
            actions.add(new Action(
                    Math.max(1, width),
                    Objects.requireNonNull(label),
                    Objects.requireNonNull(narration),
                    Objects.requireNonNull(onPress)
            ));
            return this;
        }

        public Builder height(int value) {
            height = Math.max(1, value);
            return this;
        }

        public Builder gap(int value) {
            gap = Math.max(0, value);
            return this;
        }

        public UIFilterBar build() {
            if (searchNarration == null && actions.isEmpty()) {
                throw new IllegalStateException("A filter bar needs a search field or at least one action");
            }
            return new UIFilterBar(this);
        }
    }
}
