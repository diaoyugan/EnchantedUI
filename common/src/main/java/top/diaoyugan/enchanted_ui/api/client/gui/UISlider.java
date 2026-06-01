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
