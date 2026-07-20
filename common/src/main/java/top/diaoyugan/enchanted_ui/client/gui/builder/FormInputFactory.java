package top.diaoyugan.enchanted_ui.client.gui.builder;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;
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

@ApiStatus.Internal
public final class FormInputFactory {
    private final int contentWidth;
    private final VerticalLayout layout;
    private final List<AbstractWidget> widgets;
    private final FormStateController state;
    private final FormInteractionRegistry interactions;

    FormInputFactory(int contentWidth, VerticalLayout layout, List<AbstractWidget> widgets, FormStateController state, FormInteractionRegistry interactions) {
        this.contentWidth = contentWidth;
        this.layout = layout;
        this.widgets = widgets;
        this.state = state;
        this.interactions = interactions;
    }


    public NumericSliderOptionWidget intSlider(Component label, int width, int min, int max, IntSupplier getter, IntConsumer setter, boolean percentage) {
        return numericSlider(
                layout.x(), layout.y(), width, label, min, max, 1.0D,
                getter::getAsInt, value -> setter.accept((int) Math.round(value)), percentage
        );
    }


    public NumericSliderOptionWidget longSlider(Component label, int width, long min, long max, long step, LongSupplier getter, LongConsumer setter, boolean percentage) {
        return numericSlider(
                layout.x(), layout.y(), width, label, min, max, Math.max(1L, step),
                getter::getAsLong, value -> setter.accept(Math.round(value)), percentage
        );
    }


    public NumericSliderOptionWidget floatSlider(Component label, int width, float min, float max, float step, Supplier<Float> getter, Consumer<Float> setter, boolean percentage) {
        return numericSlider(
                layout.x(), layout.y(), width, label, min, max, step,
                getter::get, value -> setter.accept((float) value), percentage
        );
    }


    public NumericSliderOptionWidget doubleSlider(Component label, int width, double min, double max, double step, DoubleSupplier getter, DoubleConsumer setter, boolean percentage) {
        return numericSlider(layout.x(), layout.y(), width, label, min, max, step, getter, setter, percentage);
    }


    public ValidatedTextFieldWidget textField(Component label, int width, Supplier<String> getter, Consumer<String> setter, UITextValidator validator) {
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


    public ValidatedTextFieldWidget intField(Component label, int width, int min, int max, IntSupplier getter, IntConsumer setter, UILocalization.FieldValidationMessages validationMessages) {
        return typedTextField(
                label, width,
                () -> Integer.toString(getter.getAsInt()),
                value -> setter.accept(Integer.parseInt(value)),
                value -> validateInt(label, value, min, max, validationMessages)
        );
    }


    public ValidatedTextFieldWidget doubleField(Component label, int width, double min, double max, DoubleSupplier getter, DoubleConsumer setter, UILocalization.FieldValidationMessages validationMessages) {
        return typedTextField(
                label, width,
                () -> formatDouble(getter.getAsDouble()),
                value -> setter.accept(Double.parseDouble(value)),
                value -> validateDouble(label, value, min, max, validationMessages)
        );
    }

    public MultiLineEditBox textArea(Component label, int height, Supplier<String> getter, Consumer<String> setter) {
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


    public KeyBindingButtonWidget keyBinding(Component label, Supplier<InputConstants.Key> getter, Consumer<InputConstants.Key> setter, UILocalization.KeyBindingMessages messages) {
        KeyBindingButtonWidget widget = new KeyBindingButtonWidget(
                layout.x(), layout.y(), contentWidth, 20, label,
                getter, setter, messages
        );
        trackModelValue(getter, widget::applyExternalKey, Function.identity(), widget::refreshMessage);
        interactions.onKeyPressed(widget::keyPressed);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }


    public CombinationKeyBindingButtonWidget keyCombination(Component label, Supplier<? extends java.util.Collection<String>> getter, Consumer<List<String>> setter, UILocalization.KeyBindingMessages messages) {
        CombinationKeyBindingButtonWidget widget = new CombinationKeyBindingButtonWidget(
                layout.x(), layout.y(), contentWidth, 20, label, getter, setter, messages
        );
        trackModelValue(() -> List.copyOf(getter.get()), widget::applyExternalBinding, List::copyOf, widget::refreshMessage);
        interactions.onKeyPressed(widget::keyPressed);
        interactions.onKeyReleased(widget::keyReleased);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    UI.ColorGroup rgbaSlidersWithPreview(Component title, UILocalization.ColorLabels labels, Supplier<Integer> rGetter, IntConsumer rSetter, Supplier<Integer> gGetter, IntConsumer gSetter, Supplier<Integer> bGetter, IntConsumer bSetter, Supplier<Integer> aGetter, IntConsumer aSetter, boolean alphaAsPercentage) {
        title(title);
        int sliderWidth = 90;
        int sliderHeight = 20;
        int previewHeight = (sliderHeight * 4) + 24;

        NumericSliderOptionWidget r = intSlider(labels.red(), sliderWidth, 0, 255, rGetter::get, rSetter, false);
        int previewX = layout.x() + sliderWidth;
        ColorPreviewWidget preview = new ColorPreviewWidget(previewX + 20, r.getY(), sliderWidth, previewHeight, labels.preview(), rGetter::get, gGetter::get, bGetter::get, aGetter::get);
        widgets.add(preview);

        NumericSliderOptionWidget g = intSlider(labels.green(), sliderWidth, 0, 255, gGetter::get, gSetter, false);
        NumericSliderOptionWidget b = intSlider(labels.blue(), sliderWidth, 0, 255, bGetter::get, bSetter, false);
        NumericSliderOptionWidget a = intSlider(labels.alpha(), sliderWidth, 0, 255, aGetter::get, aSetter, alphaAsPercentage);
        return new UI.ColorGroup(r, g, b, a, preview);
    }


    public DropdownListWidget dropdownList(Component label, int width, Supplier<List<Component>> entriesSupplier, int visibleRows, Component emptyText) {
        DropdownListWidget widget = new DropdownListWidget(layout.x(), layout.y(), width, label, entriesSupplier, visibleRows, emptyText);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }


    public EditableDropdownListWidget editableDropdownList(Component label, int width, Supplier<List<String>> getter, Consumer<List<String>> setter, Component inputHint, Component addLabel, int visibleRows, UITextValidator validator, boolean allowDuplicates, Component duplicateEntryError, Component emptyText) {
        EditableDropdownListWidget widget = new EditableDropdownListWidget(
                layout.x(), layout.y(), width, label, getter, setter, inputHint, addLabel, visibleRows, validator, allowDuplicates, duplicateEntryError, emptyText
        );
        trackModelValue(getter, setter, ArrayList::new, null);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }


    public <T> SelectDropdownWidget<T> select(Component label, int width, Supplier<T> getter, Consumer<T> setter, Supplier<List<T>> entriesSupplier, Function<T, Component> display, int visibleRows, Component noneText, Component emptyText) {
        SelectDropdownWidget<T> widget = new SelectDropdownWidget<>(layout.x(), layout.y(), width, label, getter, setter, entriesSupplier, display, visibleRows, noneText, emptyText);
        trackModelValue(getter, setter, Function.identity(), null);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    public <T> SearchableSelectDropdownWidget<T> searchableSelect(Component label, int width, Supplier<T> getter, Consumer<T> setter, Supplier<List<T>> entriesSupplier, Function<T, Component> display, Component searchHint, int visibleRows, Component noneText, Component emptyText) {
        SearchableSelectDropdownWidget<T> widget = new SearchableSelectDropdownWidget<>(
                layout.x(), layout.y(), width, label, getter, setter, entriesSupplier, display, searchHint, visibleRows, noneText, emptyText
        );
        trackModelValue(getter, setter, Function.identity(), null);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    public <T> MultiSelectDropdownWidget<T> multiSelect(Component label, int width, Supplier<Set<T>> getter, Consumer<Set<T>> setter, Supplier<List<T>> entriesSupplier, Function<T, Component> display, int visibleRows) {
        MultiSelectDropdownWidget<T> widget = new MultiSelectDropdownWidget<>(
                layout.x(), layout.y(), width, label, getter, setter, entriesSupplier, display, visibleRows
        );
        trackModelValue(getter, setter, value -> value == null ? Set.of() : new LinkedHashSet<>(value), null);
        widgets.add(widget);
        layout.next(20);
        return widget;
    }

    public <T> List<Button> radioGroup(Component title, int width, Supplier<T> getter, Consumer<T> setter, Supplier<List<T>> entriesSupplier, Function<T, Component> display) {
        title(title);
        List<T> entries = entriesSupplier.get();
        List<Button> buttons = new ArrayList<>();
        for (T entry : entries) {
            Button button = Button.builder(radioLabel(getter.get(), entry, display), b -> {
                setter.accept(entry);
                for (Button candidate : buttons) {
                    candidate.setMessage(radioLabel(getter.get(), entries.get(buttons.indexOf(candidate)), display));
                }
            }).bounds(layout.x(), layout.y(), width, 20).build();
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

    private static Component validateInt(Component label, String value, int min, int max, UILocalization.FieldValidationMessages validationMessages) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return validationMessages.required(label);
        }
        try {
            int parsed = Integer.parseInt(trimmed);
            if (parsed < min || parsed > max) {
                return validationMessages.intRange(label, min, max);
            }
            return null;
        } catch (NumberFormatException ignored) {
            return validationMessages.required(label);
        }
    }

    private static Component validateDouble(Component label, String value, double min, double max, UILocalization.FieldValidationMessages validationMessages) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return validationMessages.required(label);
        }
        try {
            double parsed = Double.parseDouble(trimmed);
            if (parsed < min || parsed > max) {
                return validationMessages.doubleRange(label, formatDouble(min), formatDouble(max));
            }
            return null;
        } catch (NumberFormatException ignored) {
            return validationMessages.required(label);
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
