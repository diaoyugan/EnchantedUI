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
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class UiForm {

    private final UI.Form delegate;

    UiForm(UI.Form delegate) {
        this.delegate = delegate;
    }

    UI.Form delegate() {
        return delegate;
    }

    public UiBuildContext ctx() {
        return new UiBuildContext(delegate.ctx());
    }

    public int contentWidth() {
        return delegate.contentWidth();
    }

    public UiVerticalLayout layout() {
        return new UiVerticalLayout(delegate.layout());
    }

    public List<AbstractWidget> widgets() {
        return delegate.widgets();
    }

    public boolean validate() {
        return delegate.validate();
    }

    public UiForm space(int height) {
        delegate.space(height);
        return this;
    }

    public UiForm section(Component title, Consumer<UiForm> builder) {
        delegate.section(title, nested -> builder.accept(new UiForm(nested)));
        return this;
    }

    public UiForm section(Component title, int indent, Consumer<UiForm> builder) {
        delegate.section(title, indent, nested -> builder.accept(new UiForm(nested)));
        return this;
    }

    public UiWidget widget(AbstractWidget widget, int height) {
        return UiWidget.wrap(delegate.widget(widget, height));
    }

    public UiText title(Component text) {
        return (UiText) UiWidget.wrap(delegate.title(text));
    }

    public UiToggle toggle(Component label, BooleanSupplier getter, Consumer<Boolean> setter) {
        return (UiToggle) UiWidget.wrap(delegate.toggle(label, getter, setter));
    }

    public UiButton button(Component label, Runnable action) {
        return (UiButton) UiWidget.wrap(delegate.button(label, action));
    }

    public UiButton button(Component label, int width, Runnable action) {
        return (UiButton) UiWidget.wrap(delegate.button(label, width, action));
    }

    public List<UiButton> buttonRow(
            Component leftLabel,
            Runnable leftAction,
            Component rightLabel,
            Runnable rightAction
    ) {
        return delegate.buttonRow(leftLabel, leftAction, rightLabel, rightAction)
                .stream()
                .map(button -> (UiButton) UiWidget.wrap(button))
                .toList();
    }

    public List<UiToggle> toggleRow(
            Component leftLabel,
            BooleanSupplier leftGetter,
            Consumer<Boolean> leftSetter,
            Component rightLabel,
            BooleanSupplier rightGetter,
            Consumer<Boolean> rightSetter
    ) {
        return delegate.toggleRow(leftLabel, leftGetter, leftSetter, rightLabel, rightGetter, rightSetter)
                .stream()
                .map(widget -> (UiToggle) UiWidget.wrap(widget))
                .toList();
    }

    public UiSlider intSlider(
            Component label,
            int min,
            int max,
            IntSupplier getter,
            IntConsumer setter,
            boolean percentage
    ) {
        return (UiSlider) UiWidget.wrap(delegate.intSlider(label, min, max, getter, setter, percentage));
    }

    public UiSlider intSlider(
            Component label,
            int width,
            int min,
            int max,
            IntSupplier getter,
            IntConsumer setter,
            boolean percentage
    ) {
        return (UiSlider) UiWidget.wrap(delegate.intSlider(label, width, min, max, getter, setter, percentage));
    }

    public UiTextField textField(
            Component label,
            Supplier<String> getter,
            Consumer<String> setter
    ) {
        return (UiTextField) UiWidget.wrap(delegate.textField(label, getter, setter));
    }

    public UiTextField textField(
            Component label,
            Supplier<String> getter,
            Consumer<String> setter,
            UiTextValidator validator
    ) {
        return (UiTextField) UiWidget.wrap(delegate.textField(label, getter, setter, validator));
    }

    public UiTextField textField(
            Component label,
            int width,
            Supplier<String> getter,
            Consumer<String> setter,
            UiTextValidator validator
    ) {
        return (UiTextField) UiWidget.wrap(delegate.textField(label, width, getter, setter, validator));
    }

    public MultiLineEditBox textArea(
            Component label,
            int height,
            Supplier<String> getter,
            Consumer<String> setter
    ) {
        return delegate.textArea(label, height, getter, setter);
    }

    public UiKeyBinding keyBinding(
            Component label,
            Consumer<InputConstants.Key> setter,
            Supplier<Component> displaySupplier,
            KeyMapping vanillaKeyMapping,
            boolean syncVanilla
    ) {
        return (UiKeyBinding) UiWidget.wrap(
                delegate.keyBinding(label, setter, displaySupplier, vanillaKeyMapping, syncVanilla)
        );
    }

    public UiKeyBinding keyBinding(
            Component label,
            Consumer<InputConstants.Key> setter,
            Supplier<Component> displaySupplier,
            KeyMapping vanillaKeyMapping,
            boolean syncVanilla,
            String listeningTranslationKey
    ) {
        return (UiKeyBinding) UiWidget.wrap(delegate.keyBinding(
                label,
                setter,
                displaySupplier,
                vanillaKeyMapping,
                syncVanilla,
                listeningTranslationKey
        ));
    }

    public UiCombinationKeyBinding combinationKeyBinding(
            Component label,
            Supplier<Set<Integer>> getter,
            Consumer<Set<Integer>> setter
    ) {
        return (UiCombinationKeyBinding) UiWidget.wrap(delegate.combinationKeyBinding(label, getter, setter));
    }

    public UiCombinationKeyBinding combinationKeyBinding(
            Component label,
            Supplier<Set<Integer>> getter,
            Consumer<Set<Integer>> setter,
            String listeningTranslationKey
    ) {
        return (UiCombinationKeyBinding) UiWidget.wrap(
                delegate.combinationKeyBinding(label, getter, setter, listeningTranslationKey)
        );
    }

    public UiColorGroup rgbaSlidersWithPreview(
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
        return new UiColorGroup(
                (UiSlider) UiWidget.wrap(colorGroup.r()),
                (UiSlider) UiWidget.wrap(colorGroup.g()),
                (UiSlider) UiWidget.wrap(colorGroup.b()),
                (UiSlider) UiWidget.wrap(colorGroup.a()),
                (UiColorPreview) UiWidget.wrap(colorGroup.preview())
        );
    }

    public UiButton iconButton(
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
        return (UiButton) UiWidget.wrap(delegate.iconButton(
                buttonSize, iconTexture, texW, texH, hoverTexture, hoverTexW, hoverTexH, iconSize, action
        ));
    }

    public UiButton textureButton(
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
        return (UiButton) UiWidget.wrap(delegate.textureButton(
                width, height, texture, texW, texH, hoverTexture, hoverTexW, hoverTexH, action
        ));
    }

    public UiDropdownList dropdownList(Component label, Supplier<List<Component>> entriesSupplier) {
        return (UiDropdownList) UiWidget.wrap(delegate.dropdownList(label, entriesSupplier));
    }

    public UiDropdownList dropdownList(
            Component label,
            int width,
            Supplier<List<Component>> entriesSupplier,
            int visibleRows
    ) {
        return (UiDropdownList) UiWidget.wrap(delegate.dropdownList(label, width, entriesSupplier, visibleRows));
    }

    public UiEditableDropdownList editableDropdownList(
            Component label,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint
    ) {
        return (UiEditableDropdownList) UiWidget.wrap(delegate.editableDropdownList(label, getter, setter, inputHint));
    }

    public UiEditableDropdownList editableDropdownList(
            Component label,
            int width,
            Supplier<List<String>> getter,
            Consumer<List<String>> setter,
            Component inputHint,
            Component addLabel,
            int visibleRows
    ) {
        return (UiEditableDropdownList) UiWidget.wrap(
                delegate.editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows)
        );
    }

    public UiEditableDropdownList editableDropdownList(
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
        return (UiEditableDropdownList) UiWidget.wrap(
                delegate.editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows, validator, allowDuplicates)
        );
    }

    public <T> UiDropdownList select(
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display
    ) {
        return (UiDropdownList) UiWidget.wrap(delegate.select(label, getter, setter, entriesSupplier, display));
    }

    public <T> UiDropdownList searchableSelect(
            Component label,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display,
            Component searchHint
    ) {
        return (UiDropdownList) UiWidget.wrap(delegate.searchableSelect(label, getter, setter, entriesSupplier, display, searchHint));
    }

    public <T> UiDropdownList multiSelect(
            Component label,
            Supplier<Set<T>> getter,
            Consumer<Set<T>> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display
    ) {
        return (UiDropdownList) UiWidget.wrap(delegate.multiSelect(label, getter, setter, entriesSupplier, display));
    }

    public <E extends Enum<E>> UiDropdownList enumSelect(
            Component label,
            Class<E> enumClass,
            Supplier<E> getter,
            Consumer<E> setter,
            Function<E, Component> display
    ) {
        return (UiDropdownList) UiWidget.wrap(delegate.enumSelect(label, enumClass, getter, setter, display));
    }

    public <T> List<UiButton> radioGroup(
            Component title,
            Supplier<T> getter,
            Consumer<T> setter,
            Supplier<List<T>> entriesSupplier,
            Function<T, Component> display
    ) {
        return delegate.radioGroup(title, getter, setter, entriesSupplier, display)
                .stream()
                .map(button -> (UiButton) UiWidget.wrap(button))
                .toList();
    }
}
