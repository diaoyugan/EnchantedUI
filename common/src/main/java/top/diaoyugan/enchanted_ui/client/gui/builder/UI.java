package top.diaoyugan.enchanted_ui.client.gui.builder;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import top.diaoyugan.enchanted_ui.api.client.gui.UITextValidator;
import top.diaoyugan.enchanted_ui.api.client.gui.UISummaryItem;
import top.diaoyugan.enchanted_ui.client.gui.layout.HorizontalLayout;
import top.diaoyugan.enchanted_ui.client.gui.layout.VerticalLayout;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.IconButton;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.TextureButton;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.EmptyStateWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.ErrorStateWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.InfoBlockWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.KeyValueRowWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.LoadingStateWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.ProgressBarWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.ReadonlyListWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.StatusBadgeWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.display.SummaryBlockWidget;
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
import java.util.function.IntFunction;
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

    public record BuildContext(int screenWidth, int screenHeight, int centerX) {
        public VerticalLayout vertical(int contentWidth, int startY, int gap) {
            int leftX = centerX - contentWidth / 2;
            return new VerticalLayout(leftX, startY, gap);
        }

        public HorizontalLayout horizontal(int startX, int startY, int gap) {
            return new HorizontalLayout(startX, startY, gap);
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

    public static final class FormPage {

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

        public List<AbstractWidget> build(BuildContext ctx) {
            Form form = new Form(ctx, contentWidth, startY, gap);
            spec.build(form);
            lastForm = form;
            return form.widgets();
        }

        public boolean onSave() {
            if (lastForm == null) {
                return true;
            }
            if (!lastForm.runSavers()) {
                return false;
            }
            spec.onSave(lastForm);
            lastForm.markClean();
            return true;
        }

        public void onOpen() {
            if (lastForm != null) {
                spec.onOpen(lastForm);
            }
        }

        public void onClose() {
            if (lastForm != null) {
                spec.onClose(lastForm);
            }
        }

        public void onShow() {
            if (lastForm != null) {
                spec.onShow(lastForm);
            }
        }

        public void onHide() {
            if (lastForm != null) {
                spec.onHide(lastForm);
            }
        }

        public void onPageChanged(int previousPage, int currentPage) {
            if (lastForm != null) {
                spec.onPageChanged(lastForm, previousPage, currentPage);
            }
        }

        public void tick() {
            if (lastForm == null) return;
            lastForm.tick();
            spec.tick(lastForm);
        }

        public boolean keyPressed(KeyEvent event) {
            if (lastForm == null) return false;
            if (lastForm.keyPressed(event)) return true;
            return spec.keyPressed(lastForm, event);
        }

        public boolean hasUnsavedChanges() {
            return lastForm != null && lastForm.hasUnsavedChanges();
        }

        public void reload() {
            if (lastForm != null) {
                lastForm.reload();
            }
        }

        public void markClean() {
            if (lastForm != null) {
                lastForm.markClean();
            }
        }
    }

    public static final class Form {
        private final BuildContext ctx;
        private final int contentWidth;
        private final VerticalLayout layout;
        private final List<AbstractWidget> widgets;
        private final FormStateController state;
        private final FormInputFactory inputs;
        private final FormDisplayFactory display;

        public Form(BuildContext ctx, int contentWidth, int startY, int gap) {
            this(
                    ctx,
                    contentWidth,
                    ctx.vertical(contentWidth, startY, gap),
                    new ArrayList<>(),
                    new FormStateController()
            );
        }

        private Form(
                BuildContext ctx,
                int contentWidth,
                VerticalLayout layout,
                List<AbstractWidget> widgets,
                FormStateController state
        ) {
            this.ctx = Objects.requireNonNull(ctx, "ctx");
            this.contentWidth = contentWidth;
            this.layout = layout;
            this.widgets = widgets;
            this.state = state;
            this.inputs = new FormInputFactory(contentWidth, layout, widgets, state);
            this.display = new FormDisplayFactory(contentWidth, layout, widgets);
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
            return state.validate();
        }

        public boolean runSavers() {
            return state.runSavers();
        }

        public boolean save() {
            return state.save();
        }

        public boolean hasUnsavedChanges() {
            return state.hasUnsavedChanges();
        }

        public void reload() {
            state.reload();
        }

        public void markClean() {
            state.markClean();
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
                    state
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

        public ProgressBarWidget progressBar(Component label, DoubleSupplier progressSupplier) {
            return display.progressBar(label, progressSupplier);
        }

        public ProgressBarWidget progressBar(Component label, int width, DoubleSupplier progressSupplier, int fillColor) {
            return display.progressBar(label, width, progressSupplier, fillColor);
        }

        public ProgressBarWidget progressBar(Component label, int width, DoubleSupplier progressSupplier, Supplier<Component> valueSupplier, int fillColor) {
            return display.progressBar(label, width, progressSupplier, valueSupplier, fillColor);
        }

        public KeyValueRowWidget keyValueRow(Component label, Supplier<Component> valueSupplier) {
            return display.keyValueRow(label, valueSupplier);
        }

        public StatusBadgeWidget statusBadge(Component label, Supplier<Component> statusSupplier) {
            return display.statusBadge(label, statusSupplier);
        }

        public StatusBadgeWidget statusBadge(Component label, Supplier<Component> statusSupplier, IntSupplier colorSupplier) {
            return display.statusBadge(label, statusSupplier, colorSupplier);
        }

        public EmptyStateWidget emptyState(Component title, Component description) {
            return display.emptyState(title, description);
        }

        public EmptyStateWidget emptyState(Component title, Component description, int height) {
            return display.emptyState(title, description, height);
        }

        public InfoBlockWidget infoBlock(Component title, Component message) {
            return display.infoBlock(title, message);
        }

        public InfoBlockWidget infoBlock(Component title, Component message, int accentColor) {
            return display.infoBlock(title, message, accentColor);
        }

        public LoadingStateWidget loadingState(Component title, Component message) {
            return display.loadingState(title, message);
        }

        public ErrorStateWidget errorState(Component title, Component message) {
            return display.errorState(title, message);
        }

        public ErrorStateWidget errorState(Component title, Component message, Component actionLabel, Runnable action) {
            return display.errorState(title, message, actionLabel, action);
        }

        public ReadonlyListWidget readonlyList(Component label, Supplier<List<Component>> entriesSupplier) {
            return display.readonlyList(label, entriesSupplier);
        }

        public ReadonlyListWidget readonlyList(Component label, Supplier<List<Component>> entriesSupplier, int visibleRows) {
            return display.readonlyList(label, entriesSupplier, visibleRows);
        }

        public ReadonlyListWidget readonlyList(
                Component label,
                Supplier<List<Component>> entriesSupplier,
                int visibleRows,
                Component emptyText,
                IntFunction<Component> overflowText
        ) {
            return display.readonlyList(label, entriesSupplier, visibleRows, emptyText, overflowText);
        }

        public SummaryBlockWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier) {
            return display.summaryBlock(title, itemsSupplier);
        }

        public SummaryBlockWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows) {
            return display.summaryBlock(title, itemsSupplier, rows);
        }

        public SummaryBlockWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows, Component emptyText) {
            return display.summaryBlock(title, itemsSupplier, rows, emptyText);
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
            BooleanOptionWidget right = new BooleanOptionWidget(
                    layout.x() + halfWidth + 4, layout.y(),
                    halfWidth, 20,
                    rightLabel,
                    rightGetter,
                    rightSetter
            );
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
            return inputs.intSlider(label, min, max, getter, setter, percentage);
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
            return inputs.intSlider(label, width, min, max, getter, setter, percentage);
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
            return inputs.longSlider(label, min, max, step, getter, setter, percentage);
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
            return inputs.longSlider(label, width, min, max, step, getter, setter, percentage);
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
            return inputs.floatSlider(label, min, max, step, getter, setter, percentage);
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
            return inputs.floatSlider(label, width, min, max, step, getter, setter, percentage);
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
            return inputs.doubleSlider(label, min, max, step, getter, setter, percentage);
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
            return inputs.doubleSlider(label, width, min, max, step, getter, setter, percentage);
        }

        public ValidatedTextFieldWidget textField(
                Component label,
                Supplier<String> getter,
                Consumer<String> setter
        ) {
            return inputs.textField(label, getter, setter);
        }

        public ValidatedTextFieldWidget textField(
                Component label,
                Supplier<String> getter,
                Consumer<String> setter,
                UITextValidator validator
        ) {
            return inputs.textField(label, getter, setter, validator);
        }

        public ValidatedTextFieldWidget textField(
                Component label,
                int width,
                Supplier<String> getter,
                Consumer<String> setter,
                UITextValidator validator
        ) {
            return inputs.textField(label, width, getter, setter, validator);
        }

        public ValidatedTextFieldWidget intField(
                Component label,
                IntSupplier getter,
                IntConsumer setter
        ) {
            return inputs.intField(label, getter, setter);
        }

        public ValidatedTextFieldWidget intField(
                Component label,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter
        ) {
            return inputs.intField(label, min, max, getter, setter);
        }

        public ValidatedTextFieldWidget intField(
                Component label,
                int width,
                IntSupplier getter,
                IntConsumer setter
        ) {
            return inputs.intField(label, width, getter, setter);
        }

        public ValidatedTextFieldWidget intField(
                Component label,
                int width,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter
        ) {
            return inputs.intField(label, width, min, max, getter, setter);
        }

        public ValidatedTextFieldWidget intField(
                Component label,
                int width,
                int min,
                int max,
                IntSupplier getter,
                IntConsumer setter,
                UILocalization.FieldValidationMessages validationMessages
        ) {
            return inputs.intField(label, width, min, max, getter, setter, validationMessages);
        }

        public ValidatedTextFieldWidget doubleField(
                Component label,
                DoubleSupplier getter,
                DoubleConsumer setter
        ) {
            return inputs.doubleField(label, getter, setter);
        }

        public ValidatedTextFieldWidget doubleField(
                Component label,
                double min,
                double max,
                DoubleSupplier getter,
                DoubleConsumer setter
        ) {
            return inputs.doubleField(label, min, max, getter, setter);
        }

        public ValidatedTextFieldWidget doubleField(
                Component label,
                int width,
                DoubleSupplier getter,
                DoubleConsumer setter
        ) {
            return inputs.doubleField(label, width, getter, setter);
        }

        public ValidatedTextFieldWidget doubleField(
                Component label,
                int width,
                double min,
                double max,
                DoubleSupplier getter,
                DoubleConsumer setter
        ) {
            return inputs.doubleField(label, width, min, max, getter, setter);
        }

        public ValidatedTextFieldWidget doubleField(
                Component label,
                int width,
                double min,
                double max,
                DoubleSupplier getter,
                DoubleConsumer setter,
                UILocalization.FieldValidationMessages validationMessages
        ) {
            return inputs.doubleField(label, width, min, max, getter, setter, validationMessages);
        }

        public MultiLineEditBox textArea(
                Component label,
                int height,
                Supplier<String> getter,
                Consumer<String> setter
        ) {
            return inputs.textArea(label, height, getter, setter);
        }

        public KeyBindingButtonWidget keyBinding(
                Component label,
                Supplier<InputConstants.Key> getter,
                Consumer<InputConstants.Key> setter,
                Supplier<Component> displaySupplier,
                KeyMapping vanillaKeyMapping,
                boolean syncVanilla
        ) {
            return inputs.keyBinding(label, getter, setter, displaySupplier, vanillaKeyMapping, syncVanilla);
        }

        public KeyBindingButtonWidget keyBinding(
                Component label,
                Supplier<InputConstants.Key> getter,
                Consumer<InputConstants.Key> setter,
                Supplier<Component> displaySupplier,
                KeyMapping vanillaKeyMapping,
                boolean syncVanilla,
                UILocalization.KeyBindingMessages messages
        ) {
            return inputs.keyBinding(label, getter, setter, displaySupplier, vanillaKeyMapping, syncVanilla, messages);
        }

        public CombinationKeyBindingButtonWidget combinationKeyBinding(
                Component label,
                Supplier<Set<Integer>> getter,
                Consumer<Set<Integer>> setter
        ) {
            return inputs.combinationKeyBinding(label, getter, setter);
        }

        public CombinationKeyBindingButtonWidget combinationKeyBinding(
                Component label,
                Supplier<Set<Integer>> getter,
                Consumer<Set<Integer>> setter,
                UILocalization.KeyBindingMessages messages
        ) {
            return inputs.combinationKeyBinding(label, getter, setter, messages);
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
            return inputs.rgbaSlidersWithPreview(title, rGetter, rSetter, gGetter, gSetter, bGetter, bSetter, aGetter, aSetter, alphaAsPercentage);
        }

        public ColorGroup rgbaSlidersWithPreview(
                Component title,
                UILocalization.ColorLabels labels,
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
            return inputs.rgbaSlidersWithPreview(title, labels, rGetter, rSetter, gGetter, gSetter, bGetter, bSetter, aGetter, aSetter, alphaAsPercentage);
        }

        public DropdownListWidget dropdownList(Component label, Supplier<List<Component>> entriesSupplier) {
            return inputs.dropdownList(label, entriesSupplier);
        }

        public DropdownListWidget dropdownList(Component label, int width, Supplier<List<Component>> entriesSupplier, int visibleRows) {
            return inputs.dropdownList(label, width, entriesSupplier, visibleRows);
        }

        public DropdownListWidget dropdownList(Component label, int width, Supplier<List<Component>> entriesSupplier, int visibleRows, Component emptyText) {
            return inputs.dropdownList(label, width, entriesSupplier, visibleRows, emptyText);
        }

        public EditableDropdownListWidget editableDropdownList(
                Component label,
                Supplier<List<String>> getter,
                Consumer<List<String>> setter,
                Component inputHint
        ) {
            return inputs.editableDropdownList(label, getter, setter, inputHint);
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
            return inputs.editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows);
        }

        public EditableDropdownListWidget editableDropdownList(
                Component label,
                int width,
                Supplier<List<String>> getter,
                Consumer<List<String>> setter,
                Component inputHint,
                Component addLabel,
                int visibleRows,
                UITextValidator validator,
                boolean allowDuplicates
        ) {
            return inputs.editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows, validator, allowDuplicates);
        }

        public EditableDropdownListWidget editableDropdownList(
                Component label,
                int width,
                Supplier<List<String>> getter,
                Consumer<List<String>> setter,
                Component inputHint,
                Component addLabel,
                int visibleRows,
                UITextValidator validator,
                boolean allowDuplicates,
                Component duplicateEntryError,
                Component emptyText
        ) {
            return inputs.editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows, validator, allowDuplicates, duplicateEntryError, emptyText);
        }

        public <T> SelectDropdownWidget<T> select(
                Component label,
                Supplier<T> getter,
                Consumer<T> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display
        ) {
            return inputs.select(label, getter, setter, entriesSupplier, display);
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
            return inputs.select(label, width, getter, setter, entriesSupplier, display, visibleRows);
        }

        public <T> SelectDropdownWidget<T> select(
                Component label,
                int width,
                Supplier<T> getter,
                Consumer<T> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display,
                int visibleRows,
                Component noneText,
                Component emptyText
        ) {
            return inputs.select(label, width, getter, setter, entriesSupplier, display, visibleRows, noneText, emptyText);
        }

        public <E extends Enum<E>> SelectDropdownWidget<E> enumSelect(
                Component label,
                Class<E> enumClass,
                Supplier<E> getter,
                Consumer<E> setter,
                Function<E, Component> display
        ) {
            return inputs.enumSelect(label, enumClass, getter, setter, display);
        }

        public <T> SearchableSelectDropdownWidget<T> searchableSelect(
                Component label,
                Supplier<T> getter,
                Consumer<T> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display,
                Component searchHint
        ) {
            return inputs.searchableSelect(label, getter, setter, entriesSupplier, display, searchHint);
        }

        public <T> SearchableSelectDropdownWidget<T> searchableSelect(
                Component label,
                Supplier<T> getter,
                Consumer<T> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display,
                Component searchHint,
                Component noneText,
                Component emptyText
        ) {
            return inputs.searchableSelect(label, getter, setter, entriesSupplier, display, searchHint, noneText, emptyText);
        }

        public <T> MultiSelectDropdownWidget<T> multiSelect(
                Component label,
                Supplier<Set<T>> getter,
                Consumer<Set<T>> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display
        ) {
            return inputs.multiSelect(label, getter, setter, entriesSupplier, display);
        }

        public <T> List<Button> radioGroup(
                Component title,
                Supplier<T> getter,
                Consumer<T> setter,
                Supplier<List<T>> entriesSupplier,
                Function<T, Component> display
        ) {
            return inputs.radioGroup(title, getter, setter, entriesSupplier, display);
        }

        private <T> void trackModelValue(
                Supplier<T> currentValue,
                Consumer<T> resetAction,
                Function<T, T> copy,
                @Nullable Runnable afterReset
        ) {
            state.trackModelValue(currentValue, resetAction, copy, afterReset);
        }

        private <T> void trackWidgetValue(
                Supplier<T> widgetValue,
                Consumer<T> resetAction,
                Function<T, T> copy
        ) {
            state.trackWidgetValue(widgetValue, resetAction, copy);
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

}
