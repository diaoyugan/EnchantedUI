package top.diaoyugan.enchanted_ui.client.gui.builder;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.api.client.gui.UITextValidator;
import top.diaoyugan.enchanted_ui.client.gui.layout.VerticalLayout;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.CombinationKeyBindingButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.KeyBindingButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.ValidatedTextFieldWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.DropdownListWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.EditableDropdownListWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.MultiSelectDropdownWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.SearchableSelectDropdownWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.SelectDropdownWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.ColorPreviewWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.NumericSliderOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.TextWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

final class FormInputFactory {
    private final int contentWidth;
    private final VerticalLayout layout;
    private final List<AbstractWidget> widgets;
    private final FormStateController state;

    FormInputFactory(int contentWidth, VerticalLayout layout, List<AbstractWidget> widgets, FormStateController state) {
        this.contentWidth = contentWidth;
        this.layout = layout;
        this.widgets = widgets;
        this.state = state;
    }

    NumericSliderOptionWidget intSlider(Component label, int min, int max, IntSupplier getter, IntConsumer setter, boolean percentage) {
        return intSlider(label, contentWidth, min, max, getter, setter, percentage);
    }

    NumericSliderOptionWidget intSlider(Component label, int width, int min, int max, IntSupplier getter, IntConsumer setter, boolean percentage) {
        return numericSlider(
                layout.x(), layout.y(), width, label, min, max, 1.0D,
                getter::getAsInt, value -> setter.accept((int) Math.round(value)), percentage
        );
    }

    NumericSliderOptionWidget longSlider(Component label, long min, long max, long step, LongSupplier getter, LongConsumer setter, boolean percentage) {
        return longSlider(label, contentWidth, min, max, step, getter, setter, percentage);
    }

    NumericSliderOptionWidget longSlider(Component label, int width, long min, long max, long step, LongSupplier getter, LongConsumer setter, boolean percentage) {
        return numericSlider(
                layout.x(), layout.y(), width, label, min, max, Math.max(1L, step),
                getter::getAsLong, value -> setter.accept(Math.round(value)), percentage
        );
    }

    NumericSliderOptionWidget floatSlider(Component label, float min, float max, float step, Supplier<Float> getter, Consumer<Float> setter, boolean percentage) {
        return floatSlider(label, contentWidth, min, max, step, getter, setter, percentage);
    }

    NumericSliderOptionWidget floatSlider(Component label, int width, float min, float max, float step, Supplier<Float> getter, Consumer<Float> setter, boolean percentage) {
        return numericSlider(
                layout.x(), layout.y(), width, label, min, max, step,
                getter::get, value -> setter.accept((float) value), percentage
        );
    }

    NumericSliderOptionWidget doubleSlider(Component label, double min, double max, double step, DoubleSupplier getter, DoubleConsumer setter, boolean percentage) {
        return doubleSlider(label, contentWidth, min, max, step, getter, setter, percentage);
    }

    NumericSliderOptionWidget doubleSlider(Component label, int width, double min, double max, double step, DoubleSupplier getter, DoubleConsumer setter, boolean percentage) {
        return numericSlider(layout.x(), layout.y(), width, label, min, max, step, getter, setter, percentage);
    }

    ValidatedTextFieldWidget textField(Component label, Supplier<String> getter, Consumer<String> setter) {
        return textField(label, contentWidth, getter, setter, UITextValidator.alwaysValid());
    }

    ValidatedTextFieldWidget textField(Component label, Supplier<String> getter, Consumer<String> setter, UITextValidator validator) {
        return textField(label, contentWidth, getter, setter, validator);
    }

    ValidatedTextFieldWidget textField(Component label, int width, Supplier<String> getter, Consumer<String> setter, UITextValidator validator) {
        title(label);
        ValidatedTextFieldWidget box = new ValidatedTextFieldWidget(layout.x(), layout.y(), width, 20, label, validator);
        box.setValue(getter.get());
        box.validateNow();
        trackWidgetValue(box::getValue, box::setValue, Function.identity());
        widgets.add(box);
        state.addValidator(box::validateNow);
        state.addSaver(() -> setter.accept(box.getValue()));
        layout.next(20);
        return box;
    }

    ValidatedTextFieldWidget intField(Component label, IntSupplier getter, IntConsumer setter) {
        return intField(label, contentWidth, Integer.MIN_VALUE, Integer.MAX_VALUE, getter, setter);
    }

    ValidatedTextFieldWidget intField(Component label, int min, int max, IntSupplier getter, IntConsumer setter) {
        return intField(label, contentWidth, min, max, getter, setter);
    }

    ValidatedTextFieldWidget intField(Component label, int width, IntSupplier getter, IntConsumer setter) {
        return intField(label, width, Integer.MIN_VALUE, Integer.MAX_VALUE, getter, setter);
    }

    ValidatedTextFieldWidget intField(Component label, int width, int min, int max, IntSupplier getter, IntConsumer setter) {
        return typedTextField(
                label, width,
                () -> Integer.toString(getter.getAsInt()),
                value -> setter.accept(Integer.parseInt(value)),
                value -> validateInt(label, value, min, max)
        );
    }

    ValidatedTextFieldWidget doubleField(Component label, DoubleSupplier getter, DoubleConsumer setter) {
        return doubleField(label, contentWidth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, getter, setter);
    }

    ValidatedTextFieldWidget doubleField(Component label, double min, double max, DoubleSupplier getter, DoubleConsumer setter) {
        return doubleField(label, contentWidth, min, max, getter, setter);
    }

    ValidatedTextFieldWidget doubleField(Component label, int width, DoubleSupplier getter, DoubleConsumer setter) {
        return doubleField(label, width, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, getter, setter);
    }

    ValidatedTextFieldWidget doubleField(Component label, int width, double min, double max, DoubleSupplier getter, DoubleConsumer setter) {
        return typedTextField(
                label, width,
                () -> formatDouble(getter.getAsDouble()),
                value -> setter.accept(Double.parseDouble(value)),
                value -> validateDouble(label, value, min, max)
        );
    }

    MultiLineEditBox textArea(Component label, int height, Supplier<String> getter, Consumer<String> setter) {
        MultiLineEditBox box = MultiLineEditBox.builder()
                .setX(layout.x())
                .setY(layout.y())
                .setShowBackground(true)
                .setShowDecorations(true)
                .build(Minecraft.getInstance().font, contentWidth, height, label);
        box.setValue(getter.get());
        trackWidgetValue(box::getValue, box::setValue, Function.identity());
        widgets.add(box);
        state.addSaver(() -> setter.accept(box.getValue()));
        layout.next(height);
        return box;
    }

    KeyBindingButtonWidget keyBinding(Component label, Supplier<InputConstants.Key> getter, Consumer<InputConstants.Key> setter, Supplier<Component> displaySupplier, KeyMapping vanillaKeyMapping, boolean syncVanilla) {
        return keyBinding(label, getter, setter, displaySupplier, vanillaKeyMapping, syncVanilla, "eui.config.keybind.listening");
    }

    KeyBindingButtonWidget keyBinding(Component label, Supplier<InputConstants.Key> getter, Consumer<InputConstants.Key> setter, Supplier<Component> displaySupplier, KeyMapping vanillaKeyMapping, boolean syncVanilla, String listeningTranslationKey) {
        KeyBindingButtonWidget widget = new KeyBindingButtonWidget(
                layout.x(), layout.y(), contentWidth, 20, label,
                getter, setter, displaySupplier, vanillaKeyMapping, syncVanilla, listeningTranslationKey
        );
        trackModelValue(getter, widget::applyExternalKey, Function.identity(), widget::refreshMessage);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    CombinationKeyBindingButtonWidget combinationKeyBinding(Component label, Supplier<Set<Integer>> getter, Consumer<Set<Integer>> setter) {
        return combinationKeyBinding(label, getter, setter, "eui.config.keybind.listening");
    }

    CombinationKeyBindingButtonWidget combinationKeyBinding(Component label, Supplier<Set<Integer>> getter, Consumer<Set<Integer>> setter, String listeningTranslationKey) {
        CombinationKeyBindingButtonWidget widget = new CombinationKeyBindingButtonWidget(
                layout.x(), layout.y(), contentWidth, 20, label, getter, setter, listeningTranslationKey
        );
        trackModelValue(getter, setter, value -> value == null ? Set.of() : new LinkedHashSet<>(value), null);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    UI.ColorGroup rgbaSlidersWithPreview(Component title, Supplier<Integer> rGetter, IntConsumer rSetter, Supplier<Integer> gGetter, IntConsumer gSetter, Supplier<Integer> bGetter, IntConsumer bSetter, Supplier<Integer> aGetter, IntConsumer aSetter, boolean alphaAsPercentage) {
        title(title);
        int sliderWidth = 90;
        int sliderHeight = 20;
        int previewHeight = (sliderHeight * 4) + 24;

        NumericSliderOptionWidget r = intSlider(Component.translatable("eui.config.rgba.red"), sliderWidth, 0, 255, rGetter::get, rSetter, false);
        int previewX = layout.x() + sliderWidth;
        ColorPreviewWidget preview = new ColorPreviewWidget(previewX + 20, r.getY(), sliderWidth, previewHeight, rGetter::get, gGetter::get, bGetter::get, aGetter::get);
        widgets.add(preview);

        NumericSliderOptionWidget g = intSlider(Component.translatable("eui.config.rgba.green"), sliderWidth, 0, 255, gGetter::get, gSetter, false);
        NumericSliderOptionWidget b = intSlider(Component.translatable("eui.config.rgba.blue"), sliderWidth, 0, 255, bGetter::get, bSetter, false);
        NumericSliderOptionWidget a = intSlider(Component.translatable("eui.config.rgba.alpha"), sliderWidth, 0, 255, aGetter::get, aSetter, alphaAsPercentage);
        return new UI.ColorGroup(r, g, b, a, preview);
    }

    DropdownListWidget dropdownList(Component label, Supplier<List<Component>> entriesSupplier) {
        return dropdownList(label, contentWidth, entriesSupplier, 5);
    }

    DropdownListWidget dropdownList(Component label, int width, Supplier<List<Component>> entriesSupplier, int visibleRows) {
        DropdownListWidget widget = new DropdownListWidget(layout.x(), layout.y(), width, label, entriesSupplier, visibleRows);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    EditableDropdownListWidget editableDropdownList(Component label, Supplier<List<String>> getter, Consumer<List<String>> setter, Component inputHint) {
        return editableDropdownList(label, contentWidth, getter, setter, inputHint, Component.translatable("eui.dropdown.add"), 5, UITextValidator.alwaysValid(), true);
    }

    EditableDropdownListWidget editableDropdownList(Component label, int width, Supplier<List<String>> getter, Consumer<List<String>> setter, Component inputHint, Component addLabel, int visibleRows) {
        return editableDropdownList(label, width, getter, setter, inputHint, addLabel, visibleRows, UITextValidator.alwaysValid(), true);
    }

    EditableDropdownListWidget editableDropdownList(Component label, int width, Supplier<List<String>> getter, Consumer<List<String>> setter, Component inputHint, Component addLabel, int visibleRows, UITextValidator validator, boolean allowDuplicates) {
        EditableDropdownListWidget widget = new EditableDropdownListWidget(
                layout.x(), layout.y(), width, label, getter, setter, inputHint, addLabel, visibleRows, validator, allowDuplicates
        );
        trackModelValue(getter, setter, ArrayList::new, null);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    <T> SelectDropdownWidget<T> select(Component label, Supplier<T> getter, Consumer<T> setter, Supplier<List<T>> entriesSupplier, Function<T, Component> display) {
        return select(label, contentWidth, getter, setter, entriesSupplier, display, 5);
    }

    <T> SelectDropdownWidget<T> select(Component label, int width, Supplier<T> getter, Consumer<T> setter, Supplier<List<T>> entriesSupplier, Function<T, Component> display, int visibleRows) {
        SelectDropdownWidget<T> widget = new SelectDropdownWidget<>(layout.x(), layout.y(), width, label, getter, setter, entriesSupplier, display, visibleRows);
        trackModelValue(getter, setter, Function.identity(), null);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    <E extends Enum<E>> SelectDropdownWidget<E> enumSelect(Component label, Class<E> enumClass, Supplier<E> getter, Consumer<E> setter, Function<E, Component> display) {
        return select(label, getter, setter, () -> Arrays.asList(enumClass.getEnumConstants()), display);
    }

    <T> SearchableSelectDropdownWidget<T> searchableSelect(Component label, Supplier<T> getter, Consumer<T> setter, Supplier<List<T>> entriesSupplier, Function<T, Component> display, Component searchHint) {
        SearchableSelectDropdownWidget<T> widget = new SearchableSelectDropdownWidget<>(
                layout.x(), layout.y(), contentWidth, label, getter, setter, entriesSupplier, display, searchHint, 5
        );
        trackModelValue(getter, setter, Function.identity(), null);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    <T> MultiSelectDropdownWidget<T> multiSelect(Component label, Supplier<Set<T>> getter, Consumer<Set<T>> setter, Supplier<List<T>> entriesSupplier, Function<T, Component> display) {
        MultiSelectDropdownWidget<T> widget = new MultiSelectDropdownWidget<>(
                layout.x(), layout.y(), contentWidth, label, getter, setter, entriesSupplier, display, 5
        );
        trackModelValue(getter, setter, value -> value == null ? Set.of() : new LinkedHashSet<>(value), null);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    <T> List<Button> radioGroup(Component title, Supplier<T> getter, Consumer<T> setter, Supplier<List<T>> entriesSupplier, Function<T, Component> display) {
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

    private TextWidget title(Component text) {
        TextWidget widget = new TextWidget(layout.x(), layout.y(), text);
        widgets.add(widget);
        layout.next(10);
        return widget;
    }

    private <T> void trackModelValue(Supplier<T> currentValue, Consumer<T> resetAction, Function<T, T> copy, @Nullable Runnable afterReset) {
        state.trackModelValue(currentValue, resetAction, copy, afterReset);
    }

    private <T> void trackWidgetValue(Supplier<T> widgetValue, Consumer<T> resetAction, Function<T, T> copy) {
        state.trackWidgetValue(widgetValue, resetAction, copy);
    }

    private NumericSliderOptionWidget numericSlider(int x, int y, int width, Component label, double min, double max, double step, DoubleSupplier getter, DoubleConsumer setter, boolean percentage) {
        NumericSliderOptionWidget widget = new NumericSliderOptionWidget(x, y, width, 20, label, min, max, step, getter, setter, percentage);
        trackModelValue(getter::getAsDouble, setter::accept, Function.identity(), widget::refreshFromGetter);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    private ValidatedTextFieldWidget typedTextField(Component label, int width, Supplier<String> getter, Consumer<String> setter, UITextValidator validator) {
        title(label);
        ValidatedTextFieldWidget box = new ValidatedTextFieldWidget(layout.x(), layout.y(), width, 20, label, validator);
        box.setValue(getter.get());
        box.validateNow();
        trackWidgetValue(box::getValue, box::setValue, Function.identity());
        widgets.add(box);
        state.addValidator(box::validateNow);
        state.addSaver(() -> setter.accept(box.getValue().trim()));
        layout.next(20);
        return box;
    }

    private static <T> Component radioLabel(T current, T entry, Function<T, Component> display) {
        return Component.literal(Objects.equals(current, entry) ? "(*) " : "( ) ").append(display.apply(entry));
    }

    private static Component validateInt(Component label, String value, int min, int max) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return Component.translatable("eui.validation.int.required", label);
        }
        try {
            int parsed = Integer.parseInt(trimmed);
            if (parsed < min || parsed > max) {
                return Component.translatable("eui.validation.int.range", label, min, max);
            }
            return null;
        } catch (NumberFormatException ignored) {
            return Component.translatable("eui.validation.int.required", label);
        }
    }

    private static Component validateDouble(Component label, String value, double min, double max) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return Component.translatable("eui.validation.double.required", label);
        }
        try {
            double parsed = Double.parseDouble(trimmed);
            if (parsed < min || parsed > max) {
                return Component.translatable("eui.validation.double.range", label, formatDouble(min), formatDouble(max));
            }
            return null;
        } catch (NumberFormatException ignored) {
            return Component.translatable("eui.validation.double.required", label);
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
}
