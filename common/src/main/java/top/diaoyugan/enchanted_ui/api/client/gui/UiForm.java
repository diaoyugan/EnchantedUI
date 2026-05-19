package top.diaoyugan.enchanted_ui.api.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.client.gui.builder.UI;

import java.util.Arrays;
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

public final class UIForm {

    private final UI.Form delegate;

    UIForm(UI.Form delegate) {
        this.delegate = delegate;
    }

    UI.Form delegate() {
        return delegate;
    }

    public UIBuildContext ctx() {
        return new UIBuildContext(delegate.ctx());
    }

    public int contentWidth() {
        return delegate.contentWidth();
    }

    public UIVerticalLayout layout() {
        return new UIVerticalLayout(delegate.layout());
    }

    public List<AbstractWidget> widgets() {
        return delegate.widgets();
    }

    public boolean validate() {
        return delegate.validate();
    }

    public boolean save() {
        return delegate.save();
    }

    public boolean hasUnsavedChanges() {
        return delegate.hasUnsavedChanges();
    }

    public void reload() {
        delegate.reload();
    }

    public void markClean() {
        delegate.markClean();
    }

    public UIForm space(int height) {
        delegate.space(height);
        return this;
    }

    public UIForm section(Component title, Consumer<UIForm> builder) {
        delegate.section(title, nested -> builder.accept(new UIForm(nested)));
        return this;
    }

    public UIForm section(Component title, int indent, Consumer<UIForm> builder) {
        delegate.section(title, indent, nested -> builder.accept(new UIForm(nested)));
        return this;
    }

    public UIWidget widget(AbstractWidget widget, int height) {
        return UIWidget.wrap(delegate.widget(widget, height));
    }

    public UIText title(Component text) {
        return (UIText) UIWidget.wrap(delegate.title(text));
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

    public UIToggle toggle(Component label, BooleanSupplier getter, Consumer<Boolean> setter) {
        return (UIToggle) UIWidget.wrap(delegate.toggle(label, getter, setter));
    }

    public UIButton button(Component label, Runnable action) {
        return (UIButton) UIWidget.wrap(delegate.button(label, action));
    }

    public UIButton button(Component label, int width, Runnable action) {
        return (UIButton) UIWidget.wrap(delegate.button(label, width, action));
    }

    public List<UIButton> buttonRow(
            Component leftLabel,
            Runnable leftAction,
            Component rightLabel,
            Runnable rightAction
    ) {
        return delegate.buttonRow(leftLabel, leftAction, rightLabel, rightAction)
                .stream()
                .map(button -> (UIButton) UIWidget.wrap(button))
                .toList();
    }

    public List<UIToggle> toggleRow(
            Component leftLabel,
            BooleanSupplier leftGetter,
            Consumer<Boolean> leftSetter,
            Component rightLabel,
            BooleanSupplier rightGetter,
            Consumer<Boolean> rightSetter
    ) {
        return delegate.toggleRow(leftLabel, leftGetter, leftSetter, rightLabel, rightGetter, rightSetter)
                .stream()
                .map(widget -> (UIToggle) UIWidget.wrap(widget))
                .toList();
    }

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

    public UITextField intField(
            Component label,
            IntSupplier getter,
            IntConsumer setter
    ) {
        return (UITextField) UIWidget.wrap(delegate.intField(label, getter, setter));
    }

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

    public UITextField doubleField(
            Component label,
            DoubleSupplier getter,
            DoubleConsumer setter
    ) {
        return (UITextField) UIWidget.wrap(delegate.doubleField(label, getter, setter));
    }

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

    public MultiLineEditBox textArea(
            Component label,
            int height,
            Supplier<String> getter,
            Consumer<String> setter
    ) {
        return delegate.textArea(label, height, getter, setter);
    }

    public UIKeyBinding keyBinding(
            Component label,
            Supplier<InputConstants.Key> getter,
            Consumer<InputConstants.Key> setter,
            Supplier<Component> displaySupplier,
            KeyMapping vanillaKeyMapping,
            boolean syncVanilla
    ) {
        return (UIKeyBinding) UIWidget.wrap(
                delegate.keyBinding(label, getter, setter, displaySupplier, vanillaKeyMapping, syncVanilla)
        );
    }

    public UIKeyBinding keyBinding(
            Component label,
            Supplier<InputConstants.Key> getter,
            Consumer<InputConstants.Key> setter,
            Supplier<Component> displaySupplier,
            KeyMapping vanillaKeyMapping,
            boolean syncVanilla,
            String listeningTranslationKey
    ) {
        return (UIKeyBinding) UIWidget.wrap(delegate.keyBinding(
                label,
                getter,
                setter,
                displaySupplier,
                vanillaKeyMapping,
                syncVanilla,
                listeningTranslationKey
        ));
    }

    public UICombinationKeyBinding combinationKeyBinding(
            Component label,
            Supplier<Set<Integer>> getter,
            Consumer<Set<Integer>> setter
    ) {
        return (UICombinationKeyBinding) UIWidget.wrap(delegate.combinationKeyBinding(label, getter, setter));
    }

    public UICombinationKeyBinding combinationKeyBinding(
            Component label,
            Supplier<Set<Integer>> getter,
            Consumer<Set<Integer>> setter,
            String listeningTranslationKey
    ) {
        return (UICombinationKeyBinding) UIWidget.wrap(
                delegate.combinationKeyBinding(label, getter, setter, listeningTranslationKey)
        );
    }

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
                (UIColorPreview) UIWidget.wrap(colorGroup.preview())
        );
    }

    public UIButton iconButton(
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
        return (UIButton) UIWidget.wrap(delegate.iconButton(
                buttonSize, iconTexture, texW, texH, hoverTexture, hoverTexW, hoverTexH, iconSize, action
        ));
    }

    public UIButton textureButton(
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
        return (UIButton) UIWidget.wrap(delegate.textureButton(
                width, height, texture, texW, texH, hoverTexture, hoverTexW, hoverTexH, action
        ));
    }

    public UIDropdownList dropdownList(Component label, Supplier<List<Component>> entriesSupplier) {
        return (UIDropdownList) UIWidget.wrap(delegate.dropdownList(label, entriesSupplier));
    }

    public UIDropdownList dropdownList(
            Component label,
            int width,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows
    ) {
        return (UIDropdownList) UIWidget.wrap(delegate.dropdownList(label, width, entriesSupplier, visibleRows));
    }

    public UIEditableDropdownList editableDropdownList(
            Component label,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint
    ) {
        return (UIEditableDropdownList) UIWidget.wrap(delegate.editableDropdownList(label, getter, setter, inputHint));
    }

    public UIEditableDropdownList editableDropdownList(
            Component label,
            int width,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint,
            Component addLabel,
            int visibleRows
    ) {
        return (UIEditableDropdownList) UIWidget.wrap(
                delegate.editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows)
        );
    }

    public UIEditableDropdownList editableDropdownList(
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
        return (UIEditableDropdownList) UIWidget.wrap(
                delegate.editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows, validator, allowDuplicates)
        );
    }

    public <T> UIDropdownList select(
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display
    ) {
        return (UIDropdownList) UIWidget.wrap(delegate.select(label, getter, setter, entriesSupplier, display));
    }

    public <T> UIDropdownList searchableSelect(
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display,
            Component searchHint
    ) {
        return (UIDropdownList) UIWidget.wrap(delegate.searchableSelect(label, getter, setter, entriesSupplier, display, searchHint));
    }

    public <T> UIDropdownList multiSelect(
            Component label,
            Supplier<Set<T>> getter,
            Consumer<Set<T>> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display
    ) {
        return (UIDropdownList) UIWidget.wrap(delegate.multiSelect(label, getter, setter, entriesSupplier, display));
    }

    public <E extends Enum<E>> UIDropdownList enumSelect(
            Component label,
            Class<E> enumClass,
            Supplier<E> getter,
            Consumer<E> setter,
            Function<E, Component> display
    ) {
        return (UIDropdownList) UIWidget.wrap(delegate.enumSelect(label, enumClass, getter, setter, display));
    }

    public <T> List<UIButton> radioGroup(
            Component title,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display
    ) {
        return delegate.radioGroup(title, getter, setter, entriesSupplier, display)
                .stream()
                .map(button -> (UIButton) UIWidget.wrap(button))
                .toList();
    }
}
