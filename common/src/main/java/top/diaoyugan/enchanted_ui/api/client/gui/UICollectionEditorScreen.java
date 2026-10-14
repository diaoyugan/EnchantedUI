package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIWorkspaceLayout;
import top.diaoyugan.enchanted_ui.api.client.gui.state.UISubscription;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** Named collection editor. Callers supply domain types, stable keys, labels and persistence. */
public class UICollectionEditorScreen<C, T> extends UITabbedScreen {
    public record Labels(Component done, Component create, Component defaultName,
                         Component nameHint, Component save, Component delete) {
        public Labels {
            Objects.requireNonNull(done);
            Objects.requireNonNull(create);
            Objects.requireNonNull(defaultName);
            Objects.requireNonNull(nameHint);
            Objects.requireNonNull(save);
            Objects.requireNonNull(delete);
        }

        public static Labels defaults() {
            return new Labels(
                    UILocalization.frameworkText("collection.done", "Done"),
                    UILocalization.frameworkText("collection.new", "New"),
                    UILocalization.frameworkText("collection.default_name", "New collection"),
                    UILocalization.frameworkText("collection.name_hint", "Collection name"),
                    UILocalization.frameworkText("collection.save", "Save"),
                    UILocalization.frameworkText("collection.delete", "Delete")
            );
        }
    }

    private final UICollectionController<C, T> controller;
    private final Function<C, String> collectionKey;
    private final Function<C, Component> collectionText;
    private final Function<T, String> itemKey;
    private final Function<T, Component> itemText;
    private final UITheme theme;
    private final Labels labels;

    protected UICollectionEditorScreen(@Nullable Screen parent, Component title, Builder<C, T> builder) {
        super(parent, title);
        controller = builder.controller;
        collectionKey = Objects.requireNonNull(builder.collectionKey, "collectionKey");
        collectionText = Objects.requireNonNull(builder.collectionText, "collectionText");
        itemKey = Objects.requireNonNull(builder.itemKey, "itemKey");
        itemText = Objects.requireNonNull(builder.itemText, "itemText");
        theme = builder.theme;
        labels = builder.labels;
        headerTitle(title);
        tabsVisible(false);
        contentViewport(8, 34, 8, 38);
        tab(0, 0, 20, Component.empty(), new EditorPage());
        bottomBar(UIBottomBar.closeOnly(labels.done()));
    }

    public static <C, T> Builder<C, T> builder(UICollectionController<C, T> controller) {
        return new Builder<>(controller);
    }

    private final class EditorPage implements UIPage {
        private final List<UISubscription> subscriptions = new ArrayList<>();

        @Override
        public List<AbstractWidget> build(UIBuildContext context) {
            onClose();
            List<AbstractWidget> widgets = new ArrayList<>();
            UIWorkspaceLayout.Regions regions = UIWorkspaceLayout.builder()
                    .inspector(false).gap(theme.spacing().small()).minimumContentWidth(100)
                    .navigationWidth(width -> Math.min(130, Math.max(76, width / 3)))
                    .build().layout(new UIBounds(context.viewportLeft(), 38, context.availableWidth(),
                            Math.max(0, context.screenHeight() - 82)));
            UIBounds navigation = regions.navigation();
            UIBounds content = regions.content();
            if (navigation.width() > 0 && navigation.height() >= 24) {
                widgets.add(UIVirtualList.<C, String>builder(collectionKey)
                        .bounds(navigation.x(), navigation.y(), navigation.width(), navigation.height() - 24)
                        .items(() -> controller.collections().get()).text(collectionText)
                        .selected(() -> controller.selectedCollection().get()).onSelect(controller::select)
                        .marqueeText(true).theme(theme).build());
                widgets.add(Button.builder(labels.create(), ignored -> controller.create(labels.defaultName().getString()))
                        .bounds(navigation.x(), navigation.y() + navigation.height() - 20, navigation.width(), 20).build());
            }
            if (content.width() < 24 || content.height() < 24) return widgets;
            int buttonWidth = Math.min(40, Math.max(0, (content.width() - 12) / 4));
            int nameWidth = Math.max(0, content.width() - 2 * buttonWidth - 8);
            EditBox name = new EditBox(Minecraft.getInstance().font, content.x(), content.y(), nameWidth, 20, labels.nameHint());
            widgets.add(name);
            Button save = Button.builder(labels.save(), ignored -> {
                C selected = controller.selectedCollection().get();
                if (selected != null && !name.getValue().isBlank()) controller.rename(selected, name.getValue());
            }).bounds(content.x() + nameWidth + 4, content.y(), buttonWidth, 20).build();
            widgets.add(save);
            Button delete = Button.builder(labels.delete(), ignored -> {
                C selected = controller.selectedCollection().get();
                if (selected != null) controller.delete(selected);
            }).bounds(content.x() + content.width() - buttonWidth, content.y(), buttonWidth, 20).build();
            widgets.add(delete);
            UIVirtualList<T, String> members = UIVirtualList.<T, String>builder(itemKey)
                    .bounds(content.x(), content.y() + 24, content.width(), content.height() - 24)
                    .items(() -> controller.availableItems().get()).rowHeight(22)
                    .selectedItems(() -> controller.selectedItems().get()).text(itemText)
                    .marqueeText(true).onMultiSelect(items -> {
                        C selected = controller.selectedCollection().get();
                        if (selected != null) controller.setItems(selected, items);
                    }).theme(theme).build();
            widgets.add(members);
            subscriptions.add(controller.selectedCollection().subscribeNow(selected -> {
                name.setValue(selected == null ? "" : collectionText.apply(selected).getString());
                name.active = selected != null;
                save.active = selected != null;
                delete.active = selected != null;
                members.active = selected != null;
            }));
            return widgets;
        }

        @Override
        public void onClose() {
            subscriptions.forEach(UISubscription::close);
            subscriptions.clear();
        }
    }

    public static final class Builder<C, T> {
        private final UICollectionController<C, T> controller;
        private Function<C, String> collectionKey;
        private Function<C, Component> collectionText;
        private Function<T, String> itemKey;
        private Function<T, Component> itemText;
        private UITheme theme = UIThemes.current();
        private Labels labels = Labels.defaults();

        private Builder(UICollectionController<C, T> controller) { this.controller = Objects.requireNonNull(controller); }
        public Builder<C, T> collectionKey(Function<C, String> value) { collectionKey = Objects.requireNonNull(value); return this; }
        public Builder<C, T> collectionText(Function<C, Component> value) { collectionText = Objects.requireNonNull(value); return this; }
        public Builder<C, T> itemKey(Function<T, String> value) { itemKey = Objects.requireNonNull(value); return this; }
        public Builder<C, T> itemText(Function<T, Component> value) { itemText = Objects.requireNonNull(value); return this; }
        public Builder<C, T> theme(UITheme value) { theme = Objects.requireNonNull(value); return this; }
        public Builder<C, T> labels(Labels value) { labels = Objects.requireNonNull(value); return this; }
        public UICollectionEditorScreen<C, T> build(@Nullable Screen parent, Component title) {
            return new UICollectionEditorScreen<>(parent, title, this);
        }
    }
}
