package top.diaoyugan.enchanted_ui.client.gui.builder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.api.client.gui.UiTextValidator;
import top.diaoyugan.enchanted_ui.client.gui.layout.HorizontalLayout;
import top.diaoyugan.enchanted_ui.client.gui.layout.VerticalLayout;
import top.diaoyugan.enchanted_ui.client.gui.screen.base.BaseTabbedScreen;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.IconButton;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.TextureButton;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.CombinationKeyBindingButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.KeyBindingButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.ValidatedTextFieldWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.DropdownListWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.EditableDropdownListWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.MultiSelectDropdownWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.SearchableSelectDropdownWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.SelectDropdownWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.BooleanOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.ColorPreviewWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.IntSliderOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.TextWidget;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * Public entrypoint for building EnchantedUI-based screens.
 */
public final class UI {

    private UI() {
    }

    public static TabbedScreen tabbed(net.minecraft.client.gui.screens.Screen parent, Component title) {
        return new TabbedScreen(parent, title);
    }

    public record BuildContext(int screenWidth, int screenHeight, int centerX) {
        public VerticalLayout vertical(int contentWidth, int startY, int gap) {
            int leftX = centerX - contentWidth / 2;
            return new VerticalLayout(leftX, startY, gap);
        }

        public HorizontalLayout horizontal(int startX, int startY, int gap) {
            return new HorizontalLayout(startX, startY, gap);
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

        default void onSave() {
        }

        default void tick() {
        }

        default boolean keyPressed(KeyEvent event) {
            return false;
        }
    }

    public interface FormSpec {
        void build(Form form);

        default void onOpen(Form form) {
        }

        default void onClose(Form form) {
        }

        default void onShow(Form form) {
        }

        default void onHide(Form form) {
        }

        default void onPageChanged(Form form, int previousPage, int currentPage) {
        }

        default void onSave(Form form) {
        }

        default void tick(Form form) {
        }

        default boolean keyPressed(Form form, KeyEvent event) {
            return false;
        }
    }

    public static final class FormPage implements Page {

        private final int contentWidth;
        private final int startY;
        private final int gap;
        private final FormSpec spec;

        private Form lastForm;

        public FormPage(int contentWidth, int startY, int gap, FormSpec spec) {
            this.contentWidth = contentWidth;
            this.startY = startY;
            this.gap = gap;
            this.spec = Objects.requireNonNull(spec, "spec");
        }

        public Form lastForm() {
            return lastForm;
        }

        @Override
        public List<AbstractWidget> build(BuildContext ctx) {
            Form form = new Form(ctx, contentWidth, startY, gap);
            spec.build(form);
            lastForm = form;
            return form.widgets();
        }

        @Override
        public void onSave() {
            if (lastForm == null) return;
            if (!lastForm.runSavers()) {
                return;
            }
            spec.onSave(lastForm);
        }

        @Override
        public void onOpen() {
            if (lastForm != null) {
                spec.onOpen(lastForm);
            }
        }

        @Override
        public void onClose() {
            if (lastForm != null) {
                spec.onClose(lastForm);
            }
        }

        @Override
        public void onShow() {
            if (lastForm != null) {
                spec.onShow(lastForm);
            }
        }

        @Override
        public void onHide() {
            if (lastForm != null) {
                spec.onHide(lastForm);
            }
        }

        @Override
        public void onPageChanged(int previousPage, int currentPage) {
            if (lastForm != null) {
                spec.onPageChanged(lastForm, previousPage, currentPage);
            }
        }

        @Override
        public void tick() {
            if (lastForm == null) return;
            lastForm.tick();
            spec.tick(lastForm);
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (lastForm == null) return false;
            if (lastForm.keyPressed(event)) return true;
            return spec.keyPressed(lastForm, event);
        }
    }

    public static FormPage formPage(int contentWidth, FormSpec spec) {
        return new FormPage(contentWidth, 10, 4, spec);
    }

    public static FormPage formPage(int contentWidth, int startY, int gap, FormSpec spec) {
        return new FormPage(contentWidth, startY, gap, spec);
    }

    public static final class Form {
        private final BuildContext ctx;
        private final int contentWidth;
        private final VerticalLayout layout;
        private final List<AbstractWidget> widgets;
        private final List<Runnable> savers;
        private final List<BooleanSupplier> validators;

        public Form(BuildContext ctx, int contentWidth, int startY, int gap) {
            this(ctx, contentWidth, ctx.vertical(contentWidth, startY, gap), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        private Form(
                BuildContext ctx,
                int contentWidth,
                VerticalLayout layout,
                List<AbstractWidget> widgets,
                List<Runnable> savers,
                List<BooleanSupplier> validators
        ) {
            this.ctx = Objects.requireNonNull(ctx, "ctx");
            this.contentWidth = contentWidth;
            this.layout = layout;
            this.widgets = widgets;
            this.savers = savers;
            this.validators = validators;
        }

        public BuildContext ctx() {
            return ctx;
        }

        public int contentWidth() {
            return contentWidth;
        }

        public VerticalLayout layout() {
            return layout;
        }

        public List<AbstractWidget> widgets() {
            return widgets;
        }

        public boolean validate() {
            boolean valid = true;
            for (BooleanSupplier validator : validators) {
                valid &= validator.getAsBoolean();
            }
            return valid;
        }

        public boolean runSavers() {
            if (!validate()) {
                return false;
            }
            for (Runnable saver : savers) saver.run();
            return true;
        }

        public void tick() {
            for (AbstractWidget w : widgets) {
                if (w instanceof CombinationKeyBindingButtonWidget combo) {
                    combo.tick();
                }
            }
        }

        public boolean keyPressed(KeyEvent event) {
            boolean handled = false;
            for (AbstractWidget w : widgets) {
                if (w instanceof KeyBindingButtonWidget single) {
                    handled |= single.keyPressed(event);
                } else if (w instanceof CombinationKeyBindingButtonWidget combo) {
                    handled |= combo.keyPressed(event);
                }
            }
            return handled;
        }

        public Form space(int height) {
            layout.next(height);
            return this;
        }

        public <T extends AbstractWidget> T widget(T widget, int height) {
            widgets.add(widget);
            layout.next(height);
            return widget;
        }

        public Form section(Component title, Consumer<Form> builder) {
            return section(title, 12, builder);
        }

        public Form section(Component title, int indent, Consumer<Form> builder) {
            title(title);
            Form nested = new Form(
                    ctx,
                    Math.max(40, contentWidth - indent),
                    new VerticalLayout(layout.x() + indent, layout.y(), layout.gap()),
                    widgets,
                    savers,
                    validators
            );
            builder.accept(nested);
            layout.setY(nested.layout.y());
            return this;
        }

        public TextWidget title(Component text) {
            TextWidget w = new TextWidget(layout.x(), layout.y(), text);
            widgets.add(w);
            layout.next(10);
            return w;
        }

        public BooleanOptionWidget toggle(Component label, BooleanSupplier getter, Consumer<Boolean> setter) {
            BooleanOptionWidget w = new BooleanOptionWidget(
                    layout.x(), layout.y(),
                    contentWidth, 20,
                    label,
                    getter,
                    setter
            );
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public Button button(Component label, Runnable action) {
            return button(label, contentWidth, action);
        }

        public Button button(Component label, int width, Runnable action) {
            Button button = Button.builder(label, b -> action.run())
                    .bounds(layout.x(), layout.y(), width, 20)
                    .build();
            widgets.add(button);
            layout.next(20);
            return button;
        }

        public List<Button> buttonRow(
                Component leftLabel,
                Runnable leftAction,
                Component rightLabel,
                Runnable rightAction
        ) {
            int halfWidth = (contentWidth - 4) / 2;
            Button left = Button.builder(leftLabel, b -> leftAction.run())
                    .bounds(layout.x(), layout.y(), halfWidth, 20)
                    .build();
            Button right = Button.builder(rightLabel, b -> rightAction.run())
                    .bounds(layout.x() + halfWidth + 4, layout.y(), halfWidth, 20)
                    .build();
            widgets.add(left);
            widgets.add(right);
            layout.next(20);
            return List.of(left, right);
        }

        public IconButton iconButton(
                int buttonSize,
                Identifier iconTexture,
                int texW,
                int texH,
                @Nullable Identifier hoverTexture,
                int hoverTexW,
                int hoverTexH,
                int iconSize,
                Runnable action
        ) {
            IconButton.Builder builder = new IconButton.Builder(layout.x(), layout.y(), buttonSize, buttonSize)
                    .icon(iconTexture, texW, texH)
                    .iconSize(iconSize)
                    .onPress(b -> action.run());
            if (hoverTexture != null) {
                builder.hoverIcon(hoverTexture, hoverTexW, hoverTexH);
            }
            IconButton button = builder.build();
            widgets.add(button);
            layout.next(buttonSize);
            return button;
        }

        public TextureButton textureButton(
                int width,
                int height,
                Identifier texture,
                int texW,
                int texH,
                @Nullable Identifier hoverTexture,
                int hoverTexW,
                int hoverTexH,
                Runnable action
        ) {
            TextureButton.Builder builder = new TextureButton.Builder(layout.x(), layout.y(), width, height)
                    .texture(texture, texW, texH)
                    .onPress(b -> action.run());
            if (hoverTexture != null) {
                builder.hoverTexture(hoverTexture, hoverTexW, hoverTexH);
            }
            TextureButton button = builder.build();
            widgets.add(button);
            layout.next(height);
            return button;
        }

        public List<BooleanOptionWidget> toggleRow(
                Component leftLabel,
                BooleanSupplier leftGetter,
                Consumer<Boolean> leftSetter,
                Component rightLabel,
                BooleanSupplier rightGetter,
                Consumer<Boolean> rightSetter
        ) {
            int halfWidth = (contentWidth - 4) / 2;

            BooleanOptionWidget left = new BooleanOptionWidget(
                    layout.x(), layout.y(),
                    halfWidth, 20,
                    leftLabel,
                    leftGetter,
                    leftSetter
            );
            left.setHalfWidth();

            BooleanOptionWidget right = new BooleanOptionWidget(
                    layout.x() + halfWidth + 4, layout.y(),
                    halfWidth, 20,
                    rightLabel,
                    rightGetter,
                    rightSetter
            );
            right.setHalfWidth();

            widgets.add(left);
            widgets.add(right);
            layout.next(20);

            return List.of(left, right);
        }

        public IntSliderOptionWidget intSlider(
                Component label,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter,
                boolean percentage
        ) {
            IntSliderOptionWidget w = new IntSliderOptionWidget(
                    layout.x(), layout.y(),
                    contentWidth, 20,
                    label,
                    min, max,
                    getter,
                    setter,
                    percentage
            );
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public IntSliderOptionWidget intSlider(
                Component label,
                int width,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter,
                boolean percentage
        ) {
            IntSliderOptionWidget w = new IntSliderOptionWidget(
                    layout.x(), layout.y(),
                    width, 20,
                    label,
                    min, max,
                    getter,
                    setter,
                    percentage
            );
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public ValidatedTextFieldWidget textField(
                Component label,
                Supplier<String> getter,
                Consumer<String> setter
        ) {
            return textField(label, contentWidth, getter, setter, UiTextValidator.alwaysValid());
        }

        public ValidatedTextFieldWidget textField(
                Component label,
                Supplier<String> getter,
                Consumer<String> setter,
                UiTextValidator validator
        ) {
            return textField(label, contentWidth, getter, setter, validator);
        }

        public ValidatedTextFieldWidget textField(
                Component label,
                int width,
                Supplier<String> getter,
                Consumer<String> setter,
                UiTextValidator validator
        ) {
            title(label);
            ValidatedTextFieldWidget box = new ValidatedTextFieldWidget(
                    layout.x(),
                    layout.y(),
                    width,
                    20,
                    label,
                    validator
            );
            box.setValue(getter.get());
            box.validateNow();
            widgets.add(box);
            validators.add(box::validateNow);
            savers.add(() -> setter.accept(box.getValue()));
            layout.next(20);
            return box;
        }

        public MultiLineEditBox textArea(
                Component label,
                int height,
                Supplier<String> getter,
                Consumer<String> setter
        ) {
            MultiLineEditBox box = MultiLineEditBox.builder()
                    .setX(layout.x())
                    .setY(layout.y())
                    .setShowBackground(true)
                    .setShowDecorations(true)
                    .build(
                            Minecraft.getInstance().font,
                            contentWidth,
                            height,
                            label
                    );

            box.setValue(getter.get());

            widgets.add(box);
            savers.add(() -> setter.accept(box.getValue()));

            layout.next(height);
            return box;
        }

        public KeyBindingButtonWidget keyBinding(
                Component label,
                Consumer<InputConstants.Key> setter,
                Supplier<Component> displaySupplier,
                KeyMapping vanillaKeyMapping,
                boolean syncVanilla
        ) {
            return keyBinding(
                    label,
                    setter,
                    displaySupplier,
                    vanillaKeyMapping,
                    syncVanilla,
                    "eui.config.keybind.listening"
            );
        }

        public KeyBindingButtonWidget keyBinding(
                Component label,
                Consumer<InputConstants.Key> setter,
                Supplier<Component> displaySupplier,
                KeyMapping vanillaKeyMapping,
                boolean syncVanilla,
                String listeningTranslationKey
        ) {
            KeyBindingButtonWidget w = new KeyBindingButtonWidget(
                    layout.x(), layout.y(),
                    contentWidth, 20,
                    label,
                    setter,
                    displaySupplier,
                    vanillaKeyMapping,
                    syncVanilla,
                    listeningTranslationKey
            );
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public CombinationKeyBindingButtonWidget combinationKeyBinding(
                Component label,
                Supplier<Set<Integer>> getter,
                Consumer<Set<Integer>> setter
        ) {
            return combinationKeyBinding(
                    label,
                    getter,
                    setter,
                    "eui.config.keybind.listening"
            );
        }

        public CombinationKeyBindingButtonWidget combinationKeyBinding(
                Component label,
                Supplier<Set<Integer>> getter,
                Consumer<Set<Integer>> setter,
                String listeningTranslationKey
        ) {
            CombinationKeyBindingButtonWidget w = new CombinationKeyBindingButtonWidget(
                    layout.x(), layout.y(),
                    contentWidth, 20,
                    label,
                    getter,
                    setter,
                    listeningTranslationKey
            );
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public ColorGroup rgbaSlidersWithPreview(
                Component title,
                Supplier<Integer> rGetter,
                IntConsumer rSetter,
                Supplier<Integer> gGetter,
                IntConsumer gSetter,
                Supplier<Integer> bGetter,
                IntConsumer bSetter,
                Supplier<Integer> aGetter,
                IntConsumer aSetter,
                boolean alphaAsPercentage
        ) {
            title(title);

            int sliderWidth = 90;
            int sliderHeight = 20;
            int previewHeight = (sliderHeight * 4) + 24;

            IntSliderOptionWidget r = intSlider(Component.literal("R"), sliderWidth, 0, 255, () -> rGetter.get(), rSetter, false);
            int previewX = layout.x() + sliderWidth;

            ColorPreviewWidget preview = new ColorPreviewWidget(
                    previewX + 20,
                    r.getY(),
                    sliderWidth,
                    previewHeight,
                    rGetter::get,
                    gGetter::get,
                    bGetter::get,
                    aGetter::get
            );
            widgets.add(preview);

            IntSliderOptionWidget g = intSlider(Component.literal("G"), sliderWidth, 0, 255, () -> gGetter.get(), gSetter, false);
            IntSliderOptionWidget b = intSlider(Component.literal("B"), sliderWidth, 0, 255, () -> bGetter.get(), bSetter, false);
            IntSliderOptionWidget a = intSlider(Component.literal("A"), sliderWidth, 0, 255, () -> aGetter.get(), aSetter, alphaAsPercentage);

            return new ColorGroup(r, g, b, a, preview);
        }

        public DropdownListWidget dropdownList(Component label, Supplier<List<Component>> entriesSupplier) {
            return dropdownList(label, contentWidth, entriesSupplier, 5);
        }

        public DropdownListWidget dropdownList(Component label, int width, Supplier<List<Component>> entriesSupplier, int visibleRows) {
            DropdownListWidget widget = new DropdownListWidget(layout.x(), layout.y(), width, label, entriesSupplier, visibleRows);
            widgets.add(widget);
            layout.next(20);
            return widget;
        }

        public EditableDropdownListWidget editableDropdownList(
                Component label,
                Supplier<List<String>> getter,
                Consumer<List<String>> setter,
                Component inputHint
        ) {
            return editableDropdownList(
                    label,
                    contentWidth,
                    getter,
                    setter,
                    inputHint,
                    Component.translatable("eui.dropdown.add"),
                    5,
                    UiTextValidator.alwaysValid(),
                    true
            );
        }

        public EditableDropdownListWidget editableDropdownList(
                Component label,
                int width,
                Supplier<List<String>> getter,
                Consumer<List<String>> setter,
                Component inputHint,
                Component addLabel,
                int visibleRows
        ) {
            return editableDropdownList(
                    label,
                    width,
                    getter,
                    setter,
                    inputHint,
                    addLabel,
                    visibleRows,
                    UiTextValidator.alwaysValid(),
                    true
            );
        }

        public EditableDropdownListWidget editableDropdownList(
                Component label,
                int width,
                Supplier<List<String>> getter,
                Consumer<List<String>> setter,
                Component inputHint,
                Component addLabel,
                int visibleRows,
                UiTextValidator validator,
                boolean allowDuplicates
        ) {
            EditableDropdownListWidget widget = new EditableDropdownListWidget(
                    layout.x(),
                    layout.y(),
                    width,
                    label,
                    getter,
                    setter,
                    inputHint,
                    addLabel,
                    visibleRows,
                    validator,
                    allowDuplicates
            );
            widgets.add(widget);
            layout.next(20);
            return widget;
        }

        public <T> SelectDropdownWidget<T> select(
                Component label,
                Supplier<T> getter,
                Consumer<T> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display
        ) {
            return select(label, contentWidth, getter, setter, entriesSupplier, display, 5);
        }

        public <T> SelectDropdownWidget<T> select(
                Component label,
                int width,
                Supplier<T> getter,
                Consumer<T> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display,
                int visibleRows
        ) {
            SelectDropdownWidget<T> widget = new SelectDropdownWidget<>(
                    layout.x(), layout.y(), width, label, getter, setter, entriesSupplier, display, visibleRows
            );
            widgets.add(widget);
            layout.next(20);
            return widget;
        }

        public <E extends Enum<E>> SelectDropdownWidget<E> enumSelect(
                Component label,
                Class<E> enumClass,
                Supplier<E> getter,
                Consumer<E> setter,
                Function<E, Component> display
        ) {
            return select(label, getter, setter, () -> Arrays.asList(enumClass.getEnumConstants()), display);
        }

        public <T> SearchableSelectDropdownWidget<T> searchableSelect(
                Component label,
                Supplier<T> getter,
                Consumer<T> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display,
                Component searchHint
        ) {
            SearchableSelectDropdownWidget<T> widget = new SearchableSelectDropdownWidget<>(
                    layout.x(), layout.y(), contentWidth, label, getter, setter, entriesSupplier, display, searchHint, 5
            );
            widgets.add(widget);
            layout.next(20);
            return widget;
        }

        public <T> MultiSelectDropdownWidget<T> multiSelect(
                Component label,
                Supplier<Set<T>> getter,
                Consumer<Set<T>> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display
        ) {
            MultiSelectDropdownWidget<T> widget = new MultiSelectDropdownWidget<>(
                    layout.x(), layout.y(), contentWidth, label, getter, setter, entriesSupplier, display, 5
            );
            widgets.add(widget);
            layout.next(20);
            return widget;
        }

        public <T> List<Button> radioGroup(
                Component title,
                Supplier<T> getter,
                Consumer<T> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display
        ) {
            title(title);
            List<T> entries = entriesSupplier.get();
            List<Button> buttons = new ArrayList<>();
            for (T entry : entries) {
                Button button = Button.builder(radioLabel(getter.get(), entry, display), b -> {
                    setter.accept(entry);
                    for (Button candidate : buttons) {
                        candidate.setMessage(radioLabel(getter.get(), entries.get(buttons.indexOf(candidate)), display));
                    }
                }).bounds(layout.x(), layout.y(), contentWidth, 20).build();
                widgets.add(button);
                buttons.add(button);
                layout.next(20);
            }
            return buttons;
        }

        private <T> Component radioLabel(T current, T entry, Function<T, Component> display) {
            return Component.literal(Objects.equals(current, entry) ? "(*) " : "( ) ").append(display.apply(entry));
        }
    }

    public record ColorGroup(
            IntSliderOptionWidget r,
            IntSliderOptionWidget g,
            IntSliderOptionWidget b,
            IntSliderOptionWidget a,
            ColorPreviewWidget preview
    ) {
    }

    public static class TabbedScreen extends BaseTabbedScreen {
        public TabbedScreen(@Nullable net.minecraft.client.gui.screens.Screen parent, Component title) {
            super(parent, title);
        }

        public TabbedScreen tab(int x, int y, int height, Component label, Page page) {
            super.tab(x, y, height, label, adapt(page));
            return this;
        }

        public TabbedScreen tab(int x, int y, int height, Component label, Style style, Page page) {
            super.tab(x, y, height, label, style, adapt(page));
            return this;
        }

        public TabbedScreen bottomBar(BottomBar bottomBar) {
            super.bottomBar(adapt(bottomBar));
            return this;
        }

        private static BaseTabbedScreen.Page adapt(Page page) {
            return new BaseTabbedScreen.Page() {
                @Override
                public List<AbstractWidget> build(BaseTabbedScreen.BuildContext ctx) {
                    return page.build(new BuildContext(ctx.screenWidth(), ctx.screenHeight(), ctx.centerX()));
                }

                @Override
                public void onOpen() {
                    page.onOpen();
                }

                @Override
                public void onClose() {
                    page.onClose();
                }

                @Override
                public void onShow() {
                    page.onShow();
                }

                @Override
                public void onHide() {
                    page.onHide();
                }

                @Override
                public void onPageChanged(int previousPage, int currentPage) {
                    page.onPageChanged(previousPage, currentPage);
                }

                @Override
                public void onSave() {
                    page.onSave();
                }

                @Override
                public void tick() {
                    page.tick();
                }

                @Override
                public boolean keyPressed(KeyEvent event) {
                    return page.keyPressed(event);
                }
            };
        }

        private static BaseTabbedScreen.BottomBar adapt(BottomBar bottomBar) {
            return (screen, centerX, bottomY) -> bottomBar.add((TabbedScreen) screen, centerX, bottomY);
        }
    }

    public interface BottomBar {
        void add(TabbedScreen screen, int centerX, int bottomY);

        static BottomBar none() {
            return (screen, centerX, bottomY) -> {
            };
        }

        static BottomBar closeOnly(Component label) {
            return (screen, centerX, bottomY) -> screen.add(
                    Button.builder(label, b -> screen.onClose())
                            .bounds(centerX - 75, bottomY, 150, 20)
                            .build()
            );
        }

        static BottomBar saveAndClose(
                Component closeLabel,
                Component saveAndExitLabel,
                Runnable saveAction
        ) {
            Objects.requireNonNull(saveAction, "saveAction");
            return (screen, centerX, bottomY) -> {
                screen.add(Button.builder(closeLabel, b -> screen.onClose())
                        .bounds(centerX - 154, bottomY, 150, 20)
                        .build());

                screen.add(Button.builder(saveAndExitLabel, b -> {
                            saveAction.run();
                            screen.onClose();
                        })
                        .bounds(centerX + 4, bottomY, 150, 20)
                        .build());
            };
        }

        static BottomBar saveAndCloseWithExtra(
                Component closeLabel,
                Component saveAndExitLabel,
                Runnable saveAction,
                Component extraLabel,
                Tooltip extraTooltip,
                Runnable extraAction
        ) {
            Objects.requireNonNull(saveAction, "saveAction");
            Objects.requireNonNull(extraAction, "extraAction");
            return (screen, centerX, bottomY) -> {
                saveAndClose(closeLabel, saveAndExitLabel, saveAction).add(screen, centerX, bottomY);

                Button extra = Button.builder(extraLabel, b -> extraAction.run())
                        .bounds(centerX - 194, bottomY, 20, 20)
                        .build();
                if (extraTooltip != null) {
                    extra.setTooltip(extraTooltip);
                }
                screen.add(extra);
            };
        }
    }

    public static Minecraft mc() {
        return Minecraft.getInstance();
    }
}
