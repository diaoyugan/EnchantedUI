package top.diaoyugan.enchanted_ui.api.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.client.gui.builder.UI;

import java.util.List;
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
        delegate.section(title, nested -> builder.accept(new UIForm(nested)));
        return this;
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
        return UIWidget.wrap(delegate.title(text));
    }

    public UIWidget progressBar(Component label, DoubleSupplier progressSupplier) {
        return UIWidget.wrap(delegate.progressBar(label, progressSupplier));
    }

    public UIWidget progressBar(Component label, int width, DoubleSupplier progressSupplier, int fillColor) {
        return UIWidget.wrap(delegate.progressBar(label, width, progressSupplier, fillColor));
    }

    public UIWidget progressBar(Component label, int width, DoubleSupplier progressSupplier, Supplier<Component> valueSupplier, int fillColor) {
        return UIWidget.wrap(delegate.progressBar(label, width, progressSupplier, valueSupplier, fillColor));
    }

    public UIWidget keyValueRow(Component label, Supplier<Component> valueSupplier) {
        return UIWidget.wrap(delegate.keyValueRow(label, valueSupplier));
    }

    public UIWidget statusBadge(Component label, Supplier<Component> statusSupplier) {
        return UIWidget.wrap(delegate.statusBadge(label, statusSupplier));
    }

    public UIWidget statusBadge(Component label, Supplier<Component> statusSupplier, IntSupplier colorSupplier) {
        return UIWidget.wrap(delegate.statusBadge(label, statusSupplier, colorSupplier));
    }

    public UIWidget emptyState(Component title, Component description) {
        return UIWidget.wrap(delegate.emptyState(title, description));
    }

    public UIWidget emptyState(Component title, Component description, int height) {
        return UIWidget.wrap(delegate.emptyState(title, description, height));
    }

    public UIWidget infoBlock(Component title, Component message) {
        return UIWidget.wrap(delegate.infoBlock(title, message));
    }

    public UIWidget infoBlock(Component title, Component message, int accentColor) {
        return UIWidget.wrap(delegate.infoBlock(title, message, accentColor));
    }

    public UIWidget loadingState(Component title, Component message) {
        return UIWidget.wrap(delegate.loadingState(title, message));
    }

    public UIWidget errorState(Component title, Component message) {
        return UIWidget.wrap(delegate.errorState(title, message));
    }

    public UIWidget errorState(Component title, Component message, Component actionLabel, Runnable action) {
        return UIWidget.wrap(delegate.errorState(title, message, actionLabel, action));
    }

    public UIWidget readonlyList(Component label, Supplier<List<Component>> entriesSupplier) {
        return UIWidget.wrap(delegate.readonlyList(label, entriesSupplier));
    }

    public UIWidget readonlyList(Component label, Supplier<List<Component>> entriesSupplier, int visibleRows) {
        return UIWidget.wrap(delegate.readonlyList(label, entriesSupplier, visibleRows));
    }

    public UIWidget readonlyList(
            Component label,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows,
            Component emptyText,
            IntFunction<Component> overflowText
    ) {
        return UIWidget.wrap(delegate.readonlyList(label, entriesSupplier, visibleRows, emptyText, overflowText));
    }

    public UIWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier) {
        return UIWidget.wrap(delegate.summaryBlock(title, itemsSupplier));
    }

    public UIWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows) {
        return UIWidget.wrap(delegate.summaryBlock(title, itemsSupplier, rows));
    }

    public UIWidget summaryBlock(Component title, Supplier<List<UISummaryItem>> itemsSupplier, int rows, Component emptyText) {
        return UIWidget.wrap(delegate.summaryBlock(title, itemsSupplier, rows, emptyText));
    }

    public UIWidget toggle(Component label, BooleanSupplier getter, Consumer<Boolean> setter) {
        return UIWidget.wrap(delegate.toggle(label, getter, setter));
    }

    public UIWidget button(Component label, Runnable action) {
        return UIWidget.wrap(delegate.button(label, action));
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
        return delegate.toggleRow(leftLabel, leftGetter, leftSetter, rightLabel, rightGetter, rightSetter)
                .stream()
                .map(UIWidget::wrap)
                .toList();
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
        List<UIWidget> row = toggleRow(
                leftLabel, leftGetter, leftSetter,
                rightLabel, rightGetter, rightSetter
        );
        row.getFirst().tooltip(leftTooltip);
        row.getLast().tooltip(rightTooltip);
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
        return (UISlider) UIWidget.wrap(delegate.intSlider(label, min, max, getter, setter, percentage));
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
        return (UISlider) UIWidget.wrap(delegate.intSlider(label, width, min, max, getter, setter, percentage));
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
        return (UISlider) UIWidget.wrap(delegate.longSlider(label, min, max, step, getter, setter, percentage));
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
        return (UISlider) UIWidget.wrap(delegate.longSlider(label, width, min, max, step, getter, setter, percentage));
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
        return (UISlider) UIWidget.wrap(delegate.floatSlider(label, min, max, step, getter, setter, percentage));
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
        return (UISlider) UIWidget.wrap(delegate.floatSlider(label, width, min, max, step, getter, setter, percentage));
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
        return (UISlider) UIWidget.wrap(delegate.doubleSlider(label, min, max, step, getter, setter, percentage));
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
        return (UISlider) UIWidget.wrap(delegate.doubleSlider(label, width, min, max, step, getter, setter, percentage));
    }

    /**
     * Adds a plain text field. Use a validator overload when invalid input should block saving.
     */
    public UITextField textField(
            Component label,
            Supplier<String> getter,
            Consumer<String> setter
    ) {
        return (UITextField) UIWidget.wrap(delegate.textField(label, getter, setter));
    }

    public UITextField textField(
            Component label,
            Supplier<String> getter,
            Consumer<String> setter,
            UITextValidator validator
    ) {
        return (UITextField) UIWidget.wrap(delegate.textField(label, getter, setter, validator));
    }

    public UITextField textField(
            Component label,
            int width,
            Supplier<String> getter,
            Consumer<String> setter,
            UITextValidator validator
    ) {
        return (UITextField) UIWidget.wrap(delegate.textField(label, width, getter, setter, validator));
    }

    /**
     * Adds an integer text field with the default integer range.
     */
    public UITextField intField(
            Component label,
            IntSupplier getter,
            IntConsumer setter
    ) {
        return (UITextField) UIWidget.wrap(delegate.intField(label, getter, setter));
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
        return (UITextField) UIWidget.wrap(delegate.intField(label, min, max, getter, setter));
    }

    public UITextField intField(
            Component label,
            int width,
            IntSupplier getter,
            IntConsumer setter
    ) {
        return (UITextField) UIWidget.wrap(delegate.intField(label, width, getter, setter));
    }

    public UITextField intField(
            Component label,
            int width,
            int min,
            int max,
            IntSupplier getter,
            IntConsumer setter
    ) {
        return (UITextField) UIWidget.wrap(delegate.intField(label, width, min, max, getter, setter));
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
        return (UITextField) UIWidget.wrap(delegate.intField(label, width, min, max, getter, setter, validationMessages));
    }

    /**
     * Adds a double text field with the default double range.
     */
    public UITextField doubleField(
            Component label,
            DoubleSupplier getter,
            DoubleConsumer setter
    ) {
        return (UITextField) UIWidget.wrap(delegate.doubleField(label, getter, setter));
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
        return (UITextField) UIWidget.wrap(delegate.doubleField(label, min, max, getter, setter));
    }

    public UITextField doubleField(
            Component label,
            int width,
            DoubleSupplier getter,
            DoubleConsumer setter
    ) {
        return (UITextField) UIWidget.wrap(delegate.doubleField(label, width, getter, setter));
    }

    public UITextField doubleField(
            Component label,
            int width,
            double min,
            double max,
            DoubleSupplier getter,
            DoubleConsumer setter
    ) {
        return (UITextField) UIWidget.wrap(delegate.doubleField(label, width, min, max, getter, setter));
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
        return (UITextField) UIWidget.wrap(delegate.doubleField(label, width, min, max, getter, setter, validationMessages));
    }

    public UITextArea textArea(
            Component label,
            int height,
            Supplier<String> getter,
            Consumer<String> setter
    ) {
        return (UITextArea) UIWidget.wrap(delegate.textArea(label, height, getter, setter));
    }

    /**
     * Adds a key binding button.
     * <p>
     * Use {@code syncVanilla} when the control should also update the supplied vanilla key mapping.
     */
    public UIWidget keyBinding(
            Component label,
            Supplier<InputConstants.Key> getter,
            Consumer<InputConstants.Key> setter,
            Supplier<Component> displaySupplier,
            KeyMapping vanillaKeyMapping,
            boolean syncVanilla
    ) {
        return UIWidget.wrap(
                delegate.keyBinding(label, getter, setter, displaySupplier, vanillaKeyMapping, syncVanilla)
        );
    }

    /**
     * Adds a key binding button with caller-owned generated text keys.
     */
    public UIWidget keyBinding(
            Component label,
            Supplier<InputConstants.Key> getter,
            Consumer<InputConstants.Key> setter,
            Supplier<Component> displaySupplier,
            KeyMapping vanillaKeyMapping,
            boolean syncVanilla,
            UILocalization.KeyBindingMessages messages
    ) {
        return UIWidget.wrap(delegate.keyBinding(
                label,
                getter,
                setter,
                displaySupplier,
                vanillaKeyMapping,
                syncVanilla,
                messages
        ));
    }

    /**
     * Adds a button for recording a key combination.
     */
    public UIWidget combinationKeyBinding(
            Component label,
            Supplier<Set<Integer>> getter,
            Consumer<Set<Integer>> setter
    ) {
        return UIWidget.wrap(delegate.combinationKeyBinding(label, getter, setter));
    }

    /**
     * Adds a key combination button with caller-owned generated text keys.
     */
    public UIWidget combinationKeyBinding(
            Component label,
            Supplier<Set<Integer>> getter,
            Consumer<Set<Integer>> setter,
            UILocalization.KeyBindingMessages messages
    ) {
        return UIWidget.wrap(
                delegate.combinationKeyBinding(label, getter, setter, messages)
        );
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
        UI.ColorGroup colorGroup = delegate.rgbaSlidersWithPreview(
                title,
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
        return UIWidget.wrap(delegate.dropdownList(label, entriesSupplier));
    }

    public UIWidget dropdownList(
            Component label,
            int width,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows
    ) {
        return UIWidget.wrap(delegate.dropdownList(label, width, entriesSupplier, visibleRows));
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
        return UIWidget.wrap(delegate.dropdownList(label, width, entriesSupplier, visibleRows, emptyText));
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
        return UIWidget.wrap(delegate.editableDropdownList(label, getter, setter, inputHint));
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
        return UIWidget.wrap(
                delegate.editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows)
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
        return UIWidget.wrap(
                delegate.editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows, validator, allowDuplicates)
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
                delegate.editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows, validator, allowDuplicates, duplicateEntryError, emptyText)
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
        return UIWidget.wrap(delegate.select(label, getter, setter, entriesSupplier, display));
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
        return UIWidget.wrap(delegate.select(label, width, getter, setter, entriesSupplier, display, visibleRows, noneText, emptyText));
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
        return UIWidget.wrap(delegate.searchableSelect(label, getter, setter, entriesSupplier, display, searchHint));
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
        return UIWidget.wrap(delegate.searchableSelect(label, getter, setter, entriesSupplier, display, searchHint, noneText, emptyText));
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
        return UIWidget.wrap(delegate.multiSelect(label, getter, setter, entriesSupplier, display));
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
        return UIWidget.wrap(delegate.enumSelect(label, enumClass, getter, setter, display));
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
        return delegate.radioGroup(title, getter, setter, entriesSupplier, display)
                .stream()
                .map(UIWidget::wrap)
                .toList();
    }
}
