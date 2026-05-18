package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.NumericSliderOptionWidget;

import java.util.function.DoubleFunction;

public final class UiSlider extends UiWidget {
    private final NumericSliderOptionWidget delegate;

    UiSlider(NumericSliderOptionWidget delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    public UiSlider setCustomValueKey(String key) {
        delegate.setCustomValueKey(key);
        return this;
    }

    public UiSlider setValueFormatter(DoubleFunction<Component> formatter) {
        delegate.setValueFormatter(formatter);
        return this;
    }

    public UiSlider percentage(boolean percentage) {
        delegate.percentage(percentage);
        return this;
    }

    public double value() {
        return delegate.numericValue();
    }

    @Override
    public UiSlider tooltip(net.minecraft.network.chat.Component tooltip) {
        super.tooltip(tooltip);
        return this;
    }

    @Override
    public UiSlider tooltip(net.minecraft.client.gui.components.Tooltip tooltip) {
        super.tooltip(tooltip);
        return this;
    }
}
