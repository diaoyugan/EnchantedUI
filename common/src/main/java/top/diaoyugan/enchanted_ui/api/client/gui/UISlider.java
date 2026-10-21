package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.NumericSliderOptionWidget;

import java.util.function.DoubleFunction;

/**
 * Public wrapper around a numeric slider created by {@link UIForm}.
 * <p>
 * Use this to customize how the current value is displayed or to add common
 * widget behavior such as tooltips and visibility rules.
 */
public final class UISlider extends UISpecializedWidget<UISlider> {
    private final NumericSliderOptionWidget delegate;

    UISlider(NumericSliderOptionWidget delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    /**
     * Uses a translation key to format the value text.
     * <p>
     * The slider passes the formatted numeric value as the first argument.
     * Example language entry: {@code "my_mod.value.blocks": "%s blocks"}.
     */
    public UISlider setCustomValueKey(String key) {
        delegate.setCustomValueKey(key);
        return this;
    }

    /**
     * Uses a custom formatter for the value text shown after the slider label.
     * The formatter receives the raw numeric value, even in percentage mode.
     */
    public UISlider setValueFormatter(DoubleFunction<Component> formatter) {
        delegate.setValueFormatter(formatter);
        return this;
    }

    /**
     * Switches the default value display between the raw number and range percentage.
     */
    public UISlider percentage(boolean percentage) {
        delegate.percentage(percentage);
        return this;
    }

    /**
     * Sets the maximum number of decimal places in the default percentage display (0-15).
     * Defaults to 0 (round to an integer); trailing zeroes are omitted.
     * This changes only the display, not the stored value or step size.
     */
    public UISlider percentageDecimalPlaces(int decimalPlaces) {
        delegate.percentageDecimalPlaces(decimalPlaces);
        return this;
    }

    /**
     * Enables percentage mode and formats its value in the range 0-100.
     * Replaces any custom value key or raw value formatter; the result must include
     * any desired percent sign. Passing null restores the default percentage format.
     */
    public UISlider setPercentageFormatter(DoubleFunction<Component> formatter) {
        delegate.setPercentageFormatter(formatter);
        return this;
    }

    /**
     * Returns the current numeric value after clamping and step snapping.
     */
    public double value() {
        return delegate.numericValue();
    }

    @Override
    protected UISlider self() {
        return this;
    }
}
