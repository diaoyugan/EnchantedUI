package top.diaoyugan.enchanted_ui.api.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.client.gui.builder.UI;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Auto-layout helper used by {@link UIFormPage}.
 * <p>
 * Add controls in the order they should appear. Most methods take a
 * {@link Component} label, a getter that reads your current value, and a setter
 * that writes the value when the form is saved or the control changes.
 * <p>
 * Prefer {@link Component#translatable(String, Object...)} with your own mod
 * namespace for user-facing text. EnchantedUI default keys are only fallbacks.
 */
public final class UIForm {

    private static final int DEFAULT_PROGRESS_COLOR = 0xFF3977C2;
    private static final int DEFAULT_STATUS_COLOR = 0xFF3C6E47;
    private static final int DEFAULT_INFO_ACCENT_COLOR = 0xFF3977C2;
    private static final int DEFAULT_EMPTY_STATE_HEIGHT = 44;
    private static final int DEFAULT_VISIBLE_ROWS = 5;
    private static final int DEFAULT_SUMMARY_ROWS = 4;

    private final UI.Form delegate;

    UIForm(UI.Form delegate) {
        this.delegate = delegate;
    }

    UI.Form delegate() {
        return delegate;
    }

    /**
     * Returns the current build context, including screen size and layout helpers.
     */
    public UIBuildContext ctx() {
        return new UIBuildContext(delegate.ctx());
    }

    /**
     * Width used by standard controls created by this form.
     */
    public int contentWidth() {
        return delegate.contentWidth();
    }

    /**
     * Gives direct access to the vertical layout cursor for advanced positioning.
     */
    public UIVerticalLayout layout() {
        return new UIVerticalLayout(delegate.layout());
    }

    /**
     * Returns widgets created by this form. This is mainly useful for advanced integrations.
     */
    public List<AbstractWidget> widgets() {
        return delegate.widgets();
    }

    /**
     * Runs all field validators and returns {@code true} when the form can be saved.
     */
    public boolean validate() {
        return delegate.validate();
    }

    /**
     * Validates the form and runs registered save actions.
     */
    public boolean save() {
        return delegate.save();
    }

    /**
     * Returns whether current widget values differ from their last clean state.
     */
    public boolean hasUnsavedChanges() {
        return delegate.hasUnsavedChanges();
    }

    /**
     * Resets tracked widgets from their getters.
     */
    public void reload() {
        delegate.reload();
    }

    /**
     * Marks the current form values as clean without changing them.
     */
    public void markClean() {
        delegate.markClean();
    }

    /**
     * Inserts vertical space before the next control.
     */
    public UIForm space(int height) {
        delegate.space(height);
        return this;
    }

    /**
     * Builds a titled nested section aligned to the parent form. Use the
     * overload with an explicit indent when a visual hierarchy is desired.
     */
    public UIForm section(Component title, Consumer<UIForm> builder) {
        return section(title, 0, builder);
    }

    /**
     * Builds a titled nested section using a custom indent.
     */
    public UIForm section(Component title, int indent, Consumer<UIForm> builder) {
        delegate.section(title, indent, nested -> builder.accept(new UIForm(nested)));
        return this;
    }

    /**
     * Adds a vanilla widget to the form and advances the layout by {@code height}.
     */
    public UIWidget widget(AbstractWidget widget, int height) {
        return UIWidget.wrap(delegate.widget(widget, height));
    }

    /**
     * Adds a simple text title row.
     */
    public UIWidget title(Component text) {
        return UIWidget.wrap(delegate.display().title(text));
    }

    public UIWidget progressBar(Component label, DoubleSupplier progressSupplier) {
        return progressBar(label, contentWidth(), progressSupplier, defaultProgressValue(progressSupplier), DEFAULT_PROGRESS_COLOR);
    }

    public UIWidget progressBar(Component label, int width, DoubleSupplier progressSupplier, int fillColor) {
        return progressBar(label, width, progressSupplier, defaultProgressValue(progressSupplier), fillColor);
    }

    public UIWidget progressBar(Component label, int width, DoubleSupplier progressSupplier, Supplier<Component> valueSupplier, int fillColor) {
        return UIWidget.wrap(delegate.display().progressBar(label, width, progressSupplier, valueSupplier, fillColor));
    }

    public UIWidget keyValueRow(Component label, Supplier<Component> valueSupplier) {
        return UIWidget.wrap(delegate.display().keyValueRow(label, valueSupplier));
    }

    public UIWidget statusBadge(Component label, Supplier<Component> statusSupplier) {
        return statusBadge(label, statusSupplier, () -> DEFAULT_STATUS_COLOR);
    }

    public UIWidget statusBadge(Component label, Supplier<Component> statusSupplier, IntSupplier colorSupplier) {
        return UIWidget.wrap(delegate.display().statusBadge(label, statusSupplier, colorSupplier));
    }

    public UIWidget emptyState(Component title, Component description) {
        return emptyState(title, description, DEFAULT_EMPTY_STATE_HEIGHT);
    }

    public UIWidget emptyState(Component title, Component description, int height) {
        return UIWidget.wrap(delegate.display().emptyState(title, description, height));
    }

    public UIWidget infoBlock(Component title, Component message) {
        return infoBlock(title, message, DEFAULT_INFO_ACCENT_COLOR);
    }

    public UIWidget infoBlock(Component title, Component message, int accentColor) {
        return UIWidget.wrap(delegate.display().infoBlock(title, message, accentColor));
    }

    public UIWidget loadingState(Component title, Component message) {
        return UIWidget.wrap(delegate.display().loadingState(title, message));
    }

    public UIWidget errorState(Component title, Component message) {
        return errorState(title, message, null, null);
    }

    public UIWidget errorState(Component title, Component message, Component actionLabel, Runnable action) {
        return UIWidget.wrap(delegate.display().errorState(title, message, actionLabel, action));
    }

    public UIWidget readonlyList(Component label, Supplier<List<Component>> entriesSupplier) {
        return readonlyList(label, entriesSupplier, DEFAULT_VISIBLE_ROWS);
    }

    public UIWidget readonlyList(Component label, Supplier<List<Component>> entriesSupplier, int visibleRows) {
        return readonlyList(
                label,
                entriesSupplier,
                visibleRows,
                UILocalization.frameworkText("display.empty", "No data"),
                hiddenCount -> UILocalization.frameworkText("display.more", "+%d more", hiddenCount)
        );
    }

    public UIWidget readonlyList(
            Component label,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows,
            Component emptyText,
            IntFunction<Component> overflowText
    ) {
        return UIWidget.wrap(delegate.display().readonlyList(label, entriesSupplier, visibleRows, emptyText, overflowText));
    }

    public UIWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier) {
        return summaryBlock(title, itemsSupplier, DEFAULT_SUMMARY_ROWS);
    }

    public UIWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows) {
        return summaryBlock(title, itemsSupplier, rows, UILocalization.frameworkText("display.empty", "No data"));
    }

    public UIWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows, Component emptyText) {
        return UIWidget.wrap(delegate.display().summaryBlock(title, itemsSupplier, rows, emptyText));
    }

    public UIWidget toggle(Component label, BooleanSupplier getter, Consumer<Boolean> setter) {
        return UIWidget.wrap(delegate.toggle(label, getter, setter));
    }

    public UIWidget button(Component label, Runnable action) {
        return button(label, contentWidth(), action);
    }

    public UIWidget button(Component label, int width, Runnable action) {
        return UIWidget.wrap(delegate.button(label, width, action));
    }

    public List<UIWidget> buttonRow(
            Component leftLabel,
            Runnable leftAction,
            Component rightLabel,
            Runnable rightAction
    ) {
        return delegate.buttonRow(leftLabel, leftAction, rightLabel, rightAction)
                .stream()
                .map(UIWidget::wrap)
                .toList();
    }

    public List<UIWidget> toggleRow(
            Component leftLabel,
            BooleanSupplier leftGetter,
            Consumer<Boolean> leftSetter,
            Component rightLabel,
            BooleanSupplier rightGetter,
            Consumer<Boolean> rightSetter
    ) {
        return toggleRow(
                leftLabel, leftGetter, leftSetter, null,
                rightLabel, rightGetter, rightSetter, null
        );
    }

    public List<UIWidget> toggleRow(
            Component leftLabel,
            BooleanSupplier leftGetter,
            Consumer<Boolean> leftSetter,
            Component leftTooltip,
            Component rightLabel,
            BooleanSupplier rightGetter,
            Consumer<Boolean> rightSetter,
            Component rightTooltip
    ) {
        List<UIWidget> row = delegate.toggleRow(
                        leftLabel, leftGetter, leftSetter,
                        rightLabel, rightGetter, rightSetter
                ).stream()
                .map(UIWidget::wrap)
                .toList();
        if (leftTooltip != null) {
            row.getFirst().tooltip(leftTooltip);
        }
        if (rightTooltip != null) {
            row.getLast().tooltip(rightTooltip);
        }
        return row;
    }

    /**
     * Adds an integer slider.
     *
     * @param percentage when true, the displayed value is formatted as a percentage of the range
     */
    public UISlider intSlider(
            Component label,
            int min,
            int max,
            IntSupplier getter,
            IntConsumer setter,
            boolean percentage
    ) {
        return intSlider(label, contentWidth(), min, max, getter, setter, percentage);
    }

    /**
     * Adds an integer slider with a custom width.
     */
    public UISlider intSlider(
            Component label,
            int width,
            int min,
            int max,
            IntSupplier getter,
            IntConsumer setter,
            boolean percentage
    ) {
        return (UISlider) UIWidget.wrap(delegate.inputs().intSlider(label, width, min, max, getter, setter, percentage));
    }

    /**
     * Adds a long slider. The value is snapped to {@code step}.
     */
    public UISlider longSlider(
            Component label,
            long min,
            long max,
            long step,
            LongSupplier getter,
            LongConsumer setter,
            boolean percentage
    ) {
        return longSlider(label, contentWidth(), min, max, step, getter, setter, percentage);
    }

    public UISlider longSlider(
            Component label,
            int width,
            long min,
            long max,
            long step,
            LongSupplier getter,
            LongConsumer setter,
            boolean percentage
    ) {
        return (UISlider) UIWidget.wrap(delegate.inputs().longSlider(label, width, min, max, step, getter, setter, percentage));
    }

    /**
     * Adds a float slider. The value is snapped to {@code step}.
     */
    public UISlider floatSlider(
            Component label,
            float min,
            float max,
            float step,
            Supplier<Float> getter,
            Consumer<Float> setter,
            boolean percentage
    ) {
        return floatSlider(label, contentWidth(), min, max, step, getter, setter, percentage);
    }

    public UISlider floatSlider(
            Component label,
            int width,
            float min,
            float max,
            float step,
            Supplier<Float> getter,
            Consumer<Float> setter,
            boolean percentage
    ) {
        return (UISlider) UIWidget.wrap(delegate.inputs().floatSlider(label, width, min, max, step, getter, setter, percentage));
    }

    /**
     * Adds a double slider. The value is snapped to {@code step}.
     */
    public UISlider doubleSlider(
            Component label,
            double min,
            double max,
            double step,
            DoubleSupplier getter,
            DoubleConsumer setter,
            boolean percentage
    ) {
        return doubleSlider(label, contentWidth(), min, max, step, getter, setter, percentage);
    }

    public UISlider doubleSlider(
            Component label,
            int width,
            double min,
            double max,
            double step,
            DoubleSupplier getter,
            DoubleConsumer setter,
            boolean percentage
    ) {
        return (UISlider) UIWidget.wrap(delegate.inputs().doubleSlider(label, width, min, max, step, getter, setter, percentage));
    }

    /**
     * Adds a plain text field. Use a validator overload when invalid input should block saving.
     */
    public UITextField textField(
            Component label,
            Supplier<String> getter,
            Consumer<String> setter
    ) {
        return textField(label, contentWidth(), getter, setter, UITextValidator.alwaysValid());
    }

    public UITextField textField(
            Component label,
            Supplier<String> getter,
            Consumer<String> setter,
            UITextValidator validator
    ) {
        return textField(label, contentWidth(), getter, setter, validator);
    }

    public UITextField textField(
            Component label,
            int width,
            Supplier<String> getter,
            Consumer<String> setter,
            UITextValidator validator
    ) {
        return (UITextField) UIWidget.wrap(delegate.inputs().textField(label, width, getter, setter, validator));
    }

    /**
     * Adds an integer text field with the default integer range.
     */
    public UITextField intField(
            Component label,
            IntSupplier getter,
            IntConsumer setter
    ) {
        return intField(label, contentWidth(), Integer.MIN_VALUE, Integer.MAX_VALUE, getter, setter, UILocalization.FieldValidationMessages.intDefaults());
    }

    /**
     * Adds an integer text field and rejects values outside {@code min} and {@code max}.
     */
    public UITextField intField(
            Component label,
            int min,
            int max,
            IntSupplier getter,
            IntConsumer setter
    ) {
        return intField(label, contentWidth(), min, max, getter, setter, UILocalization.FieldValidationMessages.intDefaults());
    }

    public UITextField intField(
            Component label,
            int width,
            IntSupplier getter,
            IntConsumer setter
    ) {
        return intField(label, width, Integer.MIN_VALUE, Integer.MAX_VALUE, getter, setter, UILocalization.FieldValidationMessages.intDefaults());
    }

    public UITextField intField(
            Component label,
            int width,
            int min,
            int max,
            IntSupplier getter,
            IntConsumer setter
    ) {
        return intField(label, width, min, max, getter, setter, UILocalization.FieldValidationMessages.intDefaults());
    }

    /**
     * Adds an integer text field with caller-owned validation translation keys.
     */
    public UITextField intField(
            Component label,
            int width,
            int min,
            int max,
            IntSupplier getter,
            IntConsumer setter,
            UILocalization.FieldValidationMessages validationMessages
    ) {
        return (UITextField) UIWidget.wrap(delegate.inputs().intField(label, width, min, max, getter, setter, validationMessages));
    }

    /**
     * Adds a double text field with the default double range.
     */
    public UITextField doubleField(
            Component label,
            DoubleSupplier getter,
            DoubleConsumer setter
    ) {
        return doubleField(label, contentWidth(), -Double.MAX_VALUE, Double.MAX_VALUE, getter, setter, UILocalization.FieldValidationMessages.doubleDefaults());
    }

    /**
     * Adds a double text field and rejects values outside {@code min} and {@code max}.
     */
    public UITextField doubleField(
            Component label,
            double min,
            double max,
            DoubleSupplier getter,
            DoubleConsumer setter
    ) {
        return doubleField(label, contentWidth(), min, max, getter, setter, UILocalization.FieldValidationMessages.doubleDefaults());
    }

    public UITextField doubleField(
            Component label,
            int width,
            DoubleSupplier getter,
            DoubleConsumer setter
    ) {
        return doubleField(label, width, -Double.MAX_VALUE, Double.MAX_VALUE, getter, setter, UILocalization.FieldValidationMessages.doubleDefaults());
    }

    public UITextField doubleField(
            Component label,
            int width,
            double min,
            double max,
            DoubleSupplier getter,
            DoubleConsumer setter
    ) {
        return doubleField(label, width, min, max, getter, setter, UILocalization.FieldValidationMessages.doubleDefaults());
    }

    /**
     * Adds a double text field with caller-owned validation translation keys.
     */
    public UITextField doubleField(
            Component label,
            int width,
            double min,
            double max,
            DoubleSupplier getter,
            DoubleConsumer setter,
            UILocalization.FieldValidationMessages validationMessages
    ) {
        return (UITextField) UIWidget.wrap(delegate.inputs().doubleField(label, width, min, max, getter, setter, validationMessages));
    }

    public UITextArea textArea(
            Component label,
            int height,
            Supplier<String> getter,
            Consumer<String> setter
    ) {
        return (UITextArea) UIWidget.wrap(delegate.inputs().textArea(label, height, getter, setter));
    }

    /** Adds a control that records one keyboard key. */
    public UIWidget keyBinding(
            Component label,
            Supplier<InputConstants.Key> getter,
            Consumer<InputConstants.Key> setter
    ) {
        return keyBinding(label, getter, setter, UILocalization.KeyBindingMessages.defaults());
    }

    /**
     * Adds a key binding button with caller-owned generated text keys.
     */
    public UIWidget keyBinding(
            Component label,
            Supplier<InputConstants.Key> getter,
            Consumer<InputConstants.Key> setter,
            UILocalization.KeyBindingMessages messages
    ) {
        return UIWidget.wrap(delegate.inputs().keyBinding(label, getter, setter, messages));
    }

    /** Adds a control that records an ordered keyboard/mouse combination. */
    public UIWidget keyCombination(
            Component label,
            Supplier<? extends java.util.Collection<String>> getter,
            Consumer<List<String>> setter
    ) {
        return keyCombination(label, getter, setter, UILocalization.KeyBindingMessages.defaults());
    }

    /**
     * Adds a key combination button with caller-owned generated text keys.
     */
    public UIWidget keyCombination(
            Component label,
            Supplier<? extends java.util.Collection<String>> getter,
            Consumer<List<String>> setter,
            UILocalization.KeyBindingMessages messages
    ) {
        return UIWidget.wrap(delegate.inputs().keyCombination(label, getter, setter, messages));
    }

    /**
     * Adds R, G, B, and A sliders next to a live color preview.
     * <p>
     * The channel labels use EnchantedUI fallback translation keys. Use the
     * {@link #rgbaSlidersWithPreview(Component, UILocalization.ColorLabels, Supplier, IntConsumer, Supplier, IntConsumer, Supplier, IntConsumer, Supplier, IntConsumer, boolean)}
     * overload when the text should belong to your mod's namespace.
     *
     * @param alphaAsPercentage when true, the alpha slider displays 0-255 as 0-100%
     */
    public UIColorGroup rgbaSlidersWithPreview(
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
        return rgbaSlidersWithPreview(
                title,
                UILocalization.ColorLabels.defaults(),
                rGetter,
                rSetter,
                gGetter,
                gSetter,
                bGetter,
                bSetter,
                aGetter,
                aSetter,
                alphaAsPercentage
        );
    }

    /**
     * Adds R, G, B, and A sliders using caller-provided channel labels.
     * <p>
     * Use this overload when the channel text should belong to your mod's
     * namespace instead of EnchantedUI's default fallback keys.
     */
    public UIColorGroup rgbaSlidersWithPreview(
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
        UI.ColorGroup colorGroup = delegate.rgbaSlidersWithPreview(
                title,
                labels,
                rGetter,
                rSetter,
                gGetter,
                gSetter,
                bGetter,
                bSetter,
                aGetter,
                aSetter,
                alphaAsPercentage
        );
        return new UIColorGroup(
                (UISlider) UIWidget.wrap(colorGroup.r()),
                (UISlider) UIWidget.wrap(colorGroup.g()),
                (UISlider) UIWidget.wrap(colorGroup.b()),
                (UISlider) UIWidget.wrap(colorGroup.a()),
                UIWidget.wrap(colorGroup.preview())
        );
    }

    public UIWidget iconButton(
            int buttonSize,
            Identifier iconTexture,
            int texW,
            int texH,
            Identifier hoverTexture,
            int hoverTexW,
            int hoverTexH,
            int iconSize,
            Runnable action
    ) {
        return UIWidget.wrap(delegate.iconButton(
                buttonSize, iconTexture, texW, texH, hoverTexture, hoverTexW, hoverTexH, iconSize, action
        ));
    }

    public UIWidget textureButton(
            int width,
            int height,
            Identifier texture,
            int texW,
            int texH,
            Identifier hoverTexture,
            int hoverTexW,
            int hoverTexH,
            Runnable action
    ) {
        return UIWidget.wrap(delegate.textureButton(
                width, height, texture, texW, texH, hoverTexture, hoverTexW, hoverTexH, action
        ));
    }

    /**
     * Adds a read-only dropdown list.
     */
    public UIWidget dropdownList(Component label, Supplier<List<Component>> entriesSupplier) {
        return dropdownList(label, contentWidth(), entriesSupplier, DEFAULT_VISIBLE_ROWS, dropdownEmptyText());
    }

    /**
     * Adds a read-only dropdown list using the form's automatic width.
     */
    public UIWidget dropdownList(
            Component label,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows
    ) {
        return dropdownList(label, contentWidth(), entriesSupplier, visibleRows, dropdownEmptyText());
    }

    /**
     * Adds a read-only dropdown list with caller-provided empty text and automatic width.
     */
    public UIWidget dropdownList(
            Component label,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows,
            Component emptyText
    ) {
        return dropdownList(label, contentWidth(), entriesSupplier, visibleRows, emptyText);
    }

    public UIWidget dropdownList(
            Component label,
            int width,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows
    ) {
        return dropdownList(label, width, entriesSupplier, visibleRows, dropdownEmptyText());
    }

    /**
     * Adds a read-only dropdown list with caller-provided empty text.
     */
    public UIWidget dropdownList(
            Component label,
            int width,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows,
            Component emptyText
    ) {
        return UIWidget.wrap(delegate.inputs().dropdownList(label, width, entriesSupplier, visibleRows, emptyText));
    }

    /**
     * Adds an editable list with an input field and add button.
     */
    public UIWidget editableDropdownList(
            Component label,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint
    ) {
        return editableDropdownList(
                label, contentWidth(), getter, setter, inputHint, dropdownAddLabel(), DEFAULT_VISIBLE_ROWS,
                UITextValidator.alwaysValid(), true, duplicateEntryError(), dropdownEmptyText()
        );
    }

    /**
     * Adds a configurable editable dropdown using the form's automatic width.
     */
    public UIWidget editableDropdownList(
            Component label,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint,
            Component addLabel,
            int visibleRows
    ) {
        return editableDropdownList(
                label, contentWidth(), getter, setter, inputHint, addLabel, visibleRows,
                UITextValidator.alwaysValid(), true, duplicateEntryError(), dropdownEmptyText()
        );
    }

    /**
     * Adds a validated editable dropdown using the form's automatic width.
     */
    public UIWidget editableDropdownList(
            Component label,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint,
            Component addLabel,
            int visibleRows,
            UITextValidator validator,
            boolean allowDuplicates
    ) {
        return editableDropdownList(
                label, contentWidth(), getter, setter, inputHint, addLabel, visibleRows,
                validator, allowDuplicates, duplicateEntryError(), dropdownEmptyText()
        );
    }

    /**
     * Adds a fully localized editable dropdown using the form's automatic width.
     */
    public UIWidget editableDropdownList(
            Component label,
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
        return editableDropdownList(
                label, contentWidth(), getter, setter, inputHint, addLabel, visibleRows,
                validator, allowDuplicates, duplicateEntryError, emptyText
        );
    }

    public UIWidget editableDropdownList(
            Component label,
            int width,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint,
            Component addLabel,
            int visibleRows
    ) {
        return editableDropdownList(
                label, width, getter, setter, inputHint, addLabel, visibleRows,
                UITextValidator.alwaysValid(), true, duplicateEntryError(), dropdownEmptyText()
        );
    }

    public UIWidget editableDropdownList(
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
        return editableDropdownList(
                label, width, getter, setter, inputHint, addLabel, visibleRows,
                validator, allowDuplicates, duplicateEntryError(), dropdownEmptyText()
        );
    }

    /**
     * Adds an editable list with caller-provided generated text.
     */
    public UIWidget editableDropdownList(
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
        return UIWidget.wrap(
                delegate.inputs().editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows, validator, allowDuplicates, duplicateEntryError, emptyText)
        );
    }

    /**
     * Adds a single-select dropdown.
     *
     * @param display converts each option into the text shown to the user
     */
    public <T> UIWidget select(
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display
    ) {
        return select(
                label, contentWidth(), getter, setter, entriesSupplier, display, DEFAULT_VISIBLE_ROWS,
                selectNoneText(), dropdownEmptyText()
        );
    }

    /**
     * Adds a single-select dropdown with a custom row count and automatic width.
     */
    public <T> UIWidget select(
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display,
            int visibleRows
    ) {
        return select(
                label, contentWidth(), getter, setter, entriesSupplier, display, visibleRows,
                selectNoneText(), dropdownEmptyText()
        );
    }

    /**
     * Adds a fully localized single-select dropdown using the form's automatic width.
     */
    public <T> UIWidget select(
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display,
            int visibleRows,
            Component noneText,
            Component emptyText
    ) {
        return select(label, contentWidth(), getter, setter, entriesSupplier, display, visibleRows, noneText, emptyText);
    }

    /**
     * Adds a single-select dropdown with caller-provided "none" and empty text.
     */
    public <T> UIWidget select(
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
        return UIWidget.wrap(delegate.inputs().select(label, width, getter, setter, entriesSupplier, display, visibleRows, noneText, emptyText));
    }

    /**
     * Adds a searchable single-select dropdown.
     */
    public <T> UIWidget searchableSelect(
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display,
            Component searchHint
    ) {
        return searchableSelect(
                label, contentWidth(), getter, setter, entriesSupplier, display, searchHint,
                DEFAULT_VISIBLE_ROWS, selectNoneText(), dropdownEmptyText()
        );
    }

    /**
     * Adds a searchable single-select dropdown with caller-provided "none" and empty text.
     */
    public <T> UIWidget searchableSelect(
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display,
            Component searchHint,
            Component noneText,
            Component emptyText
    ) {
        return searchableSelect(
                label, contentWidth(), getter, setter, entriesSupplier, display, searchHint,
                DEFAULT_VISIBLE_ROWS, noneText, emptyText
        );
    }

    /** Adds a fully configurable searchable single-select dropdown. */
    public <T> UIWidget searchableSelect(
            Component label,
            int width,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display,
            Component searchHint,
            int visibleRows,
            Component noneText,
            Component emptyText
    ) {
        return UIWidget.wrap(delegate.inputs().searchableSelect(
                label, width, getter, setter, entriesSupplier, display, searchHint, visibleRows, noneText, emptyText
        ));
    }

    /**
     * Adds a multi-select dropdown backed by a {@link Set}.
     */
    public <T> UIWidget multiSelect(
            Component label,
            Supplier<Set<T>> getter,
            Consumer<Set<T>> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display
    ) {
        return multiSelect(label, contentWidth(), getter, setter, entriesSupplier, display, DEFAULT_VISIBLE_ROWS);
    }

    /** Adds a multi-select dropdown with explicit width and visible row count. */
    public <T> UIWidget multiSelect(
            Component label,
            int width,
            Supplier<Set<T>> getter,
            Consumer<Set<T>> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display,
            int visibleRows
    ) {
        return UIWidget.wrap(delegate.inputs().multiSelect(
                label, width, getter, setter, entriesSupplier, display, visibleRows
        ));
    }

    /**
     * Adds a select dropdown containing every value from an enum.
     */
    public <E extends Enum<E>> UIWidget enumSelect(
            Component label,
            Class<E> enumClass,
            Supplier<E> getter,
            Consumer<E> setter,
            Function<E, Component> display
    ) {
        return select(
                label,
                contentWidth(),
                getter,
                setter,
                () -> List.of(enumClass.getEnumConstants()),
                display,
                DEFAULT_VISIBLE_ROWS,
                selectNoneText(),
                dropdownEmptyText()
        );
    }

    /**
     * Adds a small radio-button style group.
     */
    public <T> List<UIWidget> radioGroup(
            Component title,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display
    ) {
        return radioGroup(title, contentWidth(), getter, setter, entriesSupplier, display);
    }

    /** Adds a radio-button style group with an explicit button width. */
    public <T> List<UIWidget> radioGroup(
            Component title,
            int width,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display
    ) {
        return delegate.inputs().radioGroup(title, width, getter, setter, entriesSupplier, display)
                .stream()
                .map(UIWidget::wrap)
                .toList();
    }

    private static Supplier<Component> defaultProgressValue(DoubleSupplier progressSupplier) {
        return () -> Component.literal(String.format(
                Locale.ROOT,
                "%.0f%%",
                Math.max(0.0D, Math.min(1.0D, progressSupplier.getAsDouble())) * 100.0D
        ));
    }

    private static Component dropdownAddLabel() {
        return UILocalization.frameworkText("dropdown.add", "Add");
    }

    private static Component dropdownEmptyText() {
        return UILocalization.frameworkText("dropdown.empty", "No entries");
    }

    private static Component selectNoneText() {
        return UILocalization.frameworkText("select.none", "None");
    }

    private static Component duplicateEntryError() {
        return UILocalization.frameworkText("validation.duplicate_entry", "This entry already exists.");
    }
}
