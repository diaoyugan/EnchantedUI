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
import top.diaoyugan.enchanted_ui.client.gui.widget.option.NumericSliderOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.TextWidget;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
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
        public boolean onSave() {
            if (lastForm == null) {
                return true;
            }
            if (!lastForm.save()) {
                return false;
            }
            spec.onSave(lastForm);
            return true;
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

        @Override
        public boolean hasUnsavedChanges() {
            return lastForm != null && lastForm.hasUnsavedChanges();
        }

        @Override
        public void reload() {
            if (lastForm != null) {
                lastForm.reload();
            }
        }

        @Override
        public void markClean() {
            if (lastForm != null) {
                lastForm.markClean();
            }
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
        private final List<BooleanSupplier> dirtyTrackers;
        private final List<Runnable> resetters;
        private final List<Runnable> cleanMarkers;

        public Form(BuildContext ctx, int contentWidth, int startY, int gap) {
            this(
                    ctx,
                    contentWidth,
                    ctx.vertical(contentWidth, startY, gap),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        private Form(
                BuildContext ctx,
                int contentWidth,
                VerticalLayout layout,
                List<AbstractWidget> widgets,
                List<Runnable> savers,
                List<BooleanSupplier> validators,
                List<BooleanSupplier> dirtyTrackers,
                List<Runnable> resetters,
                List<Runnable> cleanMarkers
        ) {
            this.ctx = Objects.requireNonNull(ctx, "ctx");
            this.contentWidth = contentWidth;
            this.layout = layout;
            this.widgets = widgets;
            this.savers = savers;
            this.validators = validators;
            this.dirtyTrackers = dirtyTrackers;
            this.resetters = resetters;
            this.cleanMarkers = cleanMarkers;
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

        public boolean save() {
            if (!runSavers()) {
                return false;
            }
            markClean();
            return true;
        }

        public boolean hasUnsavedChanges() {
            for (BooleanSupplier tracker : dirtyTrackers) {
                if (tracker.getAsBoolean()) {
                    return true;
                }
            }
            return false;
        }

        public void reload() {
            for (Runnable resetter : resetters) {
                resetter.run();
            }
        }

        public void markClean() {
            for (Runnable marker : cleanMarkers) {
                marker.run();
            }
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
                    validators,
                    dirtyTrackers,
                    resetters,
                    cleanMarkers
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
            trackModelValue(getter::getAsBoolean, setter, Function.identity(), null);
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

            trackModelValue(leftGetter::getAsBoolean, leftSetter, Function.identity(), null);
            trackModelValue(rightGetter::getAsBoolean, rightSetter, Function.identity(), null);
            widgets.add(left);
            widgets.add(right);
            layout.next(20);

            return List.of(left, right);
        }

        public NumericSliderOptionWidget intSlider(
                Component label,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter,
                boolean percentage
        ) {
            return intSlider(label, contentWidth, min, max, getter, setter, percentage);
        }

        public NumericSliderOptionWidget intSlider(
                Component label,
                int width,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter,
                boolean percentage
        ) {
            NumericSliderOptionWidget w = numericSlider(
                    layout.x(), layout.y(),
                    width,
                    label,
                    min,
                    max,
                    1.0D,
                    getter::getAsInt,
                    value -> setter.accept((int) Math.round(value)),
                    percentage
            );
            return w;
        }

        public NumericSliderOptionWidget longSlider(
                Component label,
                long min,
                long max,
                long step,
                LongSupplier getter,
                LongConsumer setter,
                boolean percentage
        ) {
            return longSlider(label, contentWidth, min, max, step, getter, setter, percentage);
        }

        public NumericSliderOptionWidget longSlider(
                Component label,
                int width,
                long min,
                long max,
                long step,
                LongSupplier getter,
                LongConsumer setter,
                boolean percentage
        ) {
            return numericSlider(
                    layout.x(),
                    layout.y(),
                    width,
                    label,
                    min,
                    max,
                    Math.max(1L, step),
                    getter::getAsLong,
                    value -> setter.accept(Math.round(value)),
                    percentage
            );
        }

        public NumericSliderOptionWidget floatSlider(
                Component label,
                float min,
                float max,
                float step,
                Supplier<Float> getter,
                Consumer<Float> setter,
                boolean percentage
        ) {
            return floatSlider(label, contentWidth, min, max, step, getter, setter, percentage);
        }

        public NumericSliderOptionWidget floatSlider(
                Component label,
                int width,
                float min,
                float max,
                float step,
                Supplier<Float> getter,
                Consumer<Float> setter,
                boolean percentage
        ) {
            return numericSlider(
                    layout.x(),
                    layout.y(),
                    width,
                    label,
                    min,
                    max,
                    step,
                    () -> getter.get(),
                    value -> setter.accept((float) value),
                    percentage
            );
        }

        public NumericSliderOptionWidget doubleSlider(
                Component label,
                double min,
                double max,
                double step,
                DoubleSupplier getter,
                DoubleConsumer setter,
                boolean percentage
        ) {
            return doubleSlider(label, contentWidth, min, max, step, getter, setter, percentage);
        }

        public NumericSliderOptionWidget doubleSlider(
                Component label,
                int width,
                double min,
                double max,
                double step,
                DoubleSupplier getter,
                DoubleConsumer setter,
                boolean percentage
        ) {
            return numericSlider(
                    layout.x(),
                    layout.y(),
                    width,
                    label,
                    min,
                    max,
                    step,
                    getter,
                    setter,
                    percentage
            );
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
            trackWidgetValue(box::getValue, box::setValue, Function.identity());
            widgets.add(box);
            validators.add(box::validateNow);
            savers.add(() -> setter.accept(box.getValue()));
            layout.next(20);
            return box;
        }

        public ValidatedTextFieldWidget intField(
                Component label,
                IntSupplier getter,
                IntConsumer setter
        ) {
            return intField(label, contentWidth, Integer.MIN_VALUE, Integer.MAX_VALUE, getter, setter);
        }

        public ValidatedTextFieldWidget intField(
                Component label,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter
        ) {
            return intField(label, contentWidth, min, max, getter, setter);
        }

        public ValidatedTextFieldWidget intField(
                Component label,
                int width,
                IntSupplier getter,
                IntConsumer setter
        ) {
            return intField(label, width, Integer.MIN_VALUE, Integer.MAX_VALUE, getter, setter);
        }

        public ValidatedTextFieldWidget intField(
                Component label,
                int width,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter
        ) {
            return typedTextField(
                    label,
                    width,
                    () -> Integer.toString(getter.getAsInt()),
                    value -> setter.accept(Integer.parseInt(value)),
                    value -> validateInt(label, value, min, max)
            );
        }

        public ValidatedTextFieldWidget doubleField(
                Component label,
                DoubleSupplier getter,
                DoubleConsumer setter
        ) {
            return doubleField(label, contentWidth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, getter, setter);
        }

        public ValidatedTextFieldWidget doubleField(
                Component label,
                double min,
                double max,
                DoubleSupplier getter,
                DoubleConsumer setter
        ) {
            return doubleField(label, contentWidth, min, max, getter, setter);
        }

        public ValidatedTextFieldWidget doubleField(
                Component label,
                int width,
                DoubleSupplier getter,
                DoubleConsumer setter
        ) {
            return doubleField(label, width, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, getter, setter);
        }

        public ValidatedTextFieldWidget doubleField(
                Component label,
                int width,
                double min,
                double max,
                DoubleSupplier getter,
                DoubleConsumer setter
        ) {
            return typedTextField(
                    label,
                    width,
                    () -> formatDouble(getter.getAsDouble()),
                    value -> setter.accept(Double.parseDouble(value)),
                    value -> validateDouble(label, value, min, max)
            );
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
            trackWidgetValue(box::getValue, box::setValue, Function.identity());

            widgets.add(box);
            savers.add(() -> setter.accept(box.getValue()));

            layout.next(height);
            return box;
        }

        public KeyBindingButtonWidget keyBinding(
                Component label,
                Supplier<InputConstants.Key> getter,
                Consumer<InputConstants.Key> setter,
                Supplier<Component> displaySupplier,
                KeyMapping vanillaKeyMapping,
                boolean syncVanilla
        ) {
            return keyBinding(
                    label,
                    getter,
                    setter,
                    displaySupplier,
                    vanillaKeyMapping,
                    syncVanilla,
                    "eui.config.keybind.listening"
            );
        }

        public KeyBindingButtonWidget keyBinding(
                Component label,
                Supplier<InputConstants.Key> getter,
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
                    getter,
                    setter,
                    displaySupplier,
                    vanillaKeyMapping,
                    syncVanilla,
                    listeningTranslationKey
            );
            trackModelValue(getter, w::applyExternalKey, Function.identity(), w::refreshMessage);
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
            trackModelValue(getter, setter, value -> value == null ? Set.of() : new LinkedHashSet<>(value), null);
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

            NumericSliderOptionWidget r = intSlider(Component.literal("R"), sliderWidth, 0, 255, () -> rGetter.get(), rSetter, false);
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

            NumericSliderOptionWidget g = intSlider(Component.literal("G"), sliderWidth, 0, 255, () -> gGetter.get(), gSetter, false);
            NumericSliderOptionWidget b = intSlider(Component.literal("B"), sliderWidth, 0, 255, () -> bGetter.get(), bSetter, false);
            NumericSliderOptionWidget a = intSlider(Component.literal("A"), sliderWidth, 0, 255, () -> aGetter.get(), aSetter, alphaAsPercentage);

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
            trackModelValue(getter, setter, ArrayList::new, null);
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
            trackModelValue(getter, setter, Function.identity(), null);
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
            trackModelValue(getter, setter, Function.identity(), null);
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
            trackModelValue(getter, setter, value -> value == null ? Set.of() : new LinkedHashSet<>(value), null);
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
            trackModelValue(getter, setter, Function.identity(), () -> {
                List<T> refreshedEntries = entriesSupplier.get();
                for (int i = 0; i < buttons.size() && i < refreshedEntries.size(); i++) {
                    buttons.get(i).setMessage(radioLabel(getter.get(), refreshedEntries.get(i), display));
                }
            });
            return buttons;
        }

        private <T> Component radioLabel(T current, T entry, Function<T, Component> display) {
            return Component.literal(Objects.equals(current, entry) ? "(*) " : "( ) ").append(display.apply(entry));
        }

        private NumericSliderOptionWidget numericSlider(
                int x,
                int y,
                int width,
                Component label,
                double min,
                double max,
                double step,
                DoubleSupplier getter,
                DoubleConsumer setter,
                boolean percentage
        ) {
            NumericSliderOptionWidget widget = new NumericSliderOptionWidget(
                    x,
                    y,
                    width,
                    20,
                    label,
                    min,
                    max,
                    step,
                    getter,
                    setter,
                    percentage
            );
            trackModelValue(getter::getAsDouble, setter::accept, Function.identity(), widget::refreshFromGetter);
            widgets.add(widget);
            layout.next(20);
            return widget;
        }

        private ValidatedTextFieldWidget typedTextField(
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
            trackWidgetValue(box::getValue, box::setValue, Function.identity());
            widgets.add(box);
            validators.add(box::validateNow);
            savers.add(() -> setter.accept(box.getValue().trim()));
            layout.next(20);
            return box;
        }

        private static Component validateInt(Component label, String value, int min, int max) {
            String trimmed = value.trim();
            if (trimmed.isEmpty()) {
                return Component.literal(label.getString() + " requires a whole number.");
            }
            try {
                int parsed = Integer.parseInt(trimmed);
                if (parsed < min || parsed > max) {
                    return Component.literal(label.getString() + " must be between " + min + " and " + max + ".");
                }
                return null;
            } catch (NumberFormatException ignored) {
                return Component.literal(label.getString() + " requires a whole number.");
            }
        }

        private static Component validateDouble(Component label, String value, double min, double max) {
            String trimmed = value.trim();
            if (trimmed.isEmpty()) {
                return Component.literal(label.getString() + " requires a numeric value.");
            }
            try {
                double parsed = Double.parseDouble(trimmed);
                if (parsed < min || parsed > max) {
                    return Component.literal(label.getString() + " must be between " + formatDouble(min) + " and " + formatDouble(max) + ".");
                }
                return null;
            } catch (NumberFormatException ignored) {
                return Component.literal(label.getString() + " requires a numeric value.");
            }
        }

        private static String formatDouble(double value) {
            if (Double.isInfinite(value)) {
                return value > 0 ? "+inf" : "-inf";
            }
            long rounded = Math.round(value);
            if (Math.abs(value - rounded) < 1.0E-9D) {
                return Long.toString(rounded);
            }
            String formatted = String.format(Locale.ROOT, "%.4f", value);
            int trimIndex = formatted.length();
            while (trimIndex > 0 && formatted.charAt(trimIndex - 1) == '0') {
                trimIndex--;
            }
            if (trimIndex > 0 && formatted.charAt(trimIndex - 1) == '.') {
                trimIndex--;
            }
            return formatted.substring(0, trimIndex);
        }

        private <T> void trackModelValue(
                Supplier<T> currentValue,
                Consumer<T> resetAction,
                Function<T, T> copy,
                @Nullable Runnable afterReset
        ) {
            Snapshot<T> snapshot = new Snapshot<>(copy.apply(currentValue.get()));
            dirtyTrackers.add(() -> !Objects.equals(snapshot.value, copy.apply(currentValue.get())));
            resetters.add(() -> {
                resetAction.accept(copy.apply(snapshot.value));
                if (afterReset != null) {
                    afterReset.run();
                }
            });
            cleanMarkers.add(() -> snapshot.value = copy.apply(currentValue.get()));
        }

        private <T> void trackWidgetValue(
                Supplier<T> widgetValue,
                Consumer<T> resetAction,
                Function<T, T> copy
        ) {
            Snapshot<T> snapshot = new Snapshot<>(copy.apply(widgetValue.get()));
            dirtyTrackers.add(() -> !Objects.equals(snapshot.value, copy.apply(widgetValue.get())));
            resetters.add(() -> resetAction.accept(copy.apply(snapshot.value)));
            cleanMarkers.add(() -> snapshot.value = copy.apply(widgetValue.get()));
        }
    }

    public record ColorGroup(
            NumericSliderOptionWidget r,
            NumericSliderOptionWidget g,
            NumericSliderOptionWidget b,
            NumericSliderOptionWidget a,
            ColorPreviewWidget preview
    ) {
    }

    private static final class Snapshot<T> {
        private T value;

        private Snapshot(T value) {
            this.value = value;
        }
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
                public boolean onSave() {
                    return page.onSave();
                }

                @Override
                public void tick() {
                    page.tick();
                }

                @Override
                public boolean keyPressed(KeyEvent event) {
                    return page.keyPressed(event);
                }

                @Override
                public boolean hasUnsavedChanges() {
                    return page.hasUnsavedChanges();
                }

                @Override
                public void reload() {
                    page.reload();
                }

                @Override
                public void markClean() {
                    page.markClean();
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
                    Button.builder(label, b -> screen.requestClose())
                            .bounds(centerX - 75, bottomY, 150, 20)
                            .build()
            );
        }

        static BottomBar saveAndClose(
                Component closeLabel,
                Component saveAndExitLabel,
                BooleanSupplier saveAction
        ) {
            Objects.requireNonNull(saveAction, "saveAction");
            return (screen, centerX, bottomY) -> {
                screen.add(Button.builder(closeLabel, b -> screen.requestClose())
                        .bounds(centerX - 154, bottomY, 150, 20)
                        .build());

                screen.add(Button.builder(saveAndExitLabel, b -> {
                            if (saveAction.getAsBoolean()) {
                                screen.markAllClean();
                                screen.requestClose();
                            }
                        })
                        .bounds(centerX + 4, bottomY, 150, 20)
                        .build());
            };
        }

        static BottomBar saveAndCloseWithExtra(
                Component closeLabel,
                Component saveAndExitLabel,
                BooleanSupplier saveAction,
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
