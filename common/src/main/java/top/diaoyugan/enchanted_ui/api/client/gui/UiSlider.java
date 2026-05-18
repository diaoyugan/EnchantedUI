package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.NumericSliderOptionWidget;

import java.util.function.DoubleFunction;

public final class UISlider extends UIWidget {
    private final NumericSliderOptionWidget delegate;

    UISlider(NumericSliderOptionWidget delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    public UISlider setCustomValueKey(String key) {
        delegate.setCustomValueKey(key);
        return this;
    }

    public UISlider setValueFormatter(DoubleFunction<Component> formatter) {
        delegate.setValueFormatter(formatter);
        return this;
    }

    public UISlider percentage(boolean percentage) {
        delegate.percentage(percentage);
        return this;
    }

    public double value() {
        return delegate.numericValue();
    }

    @Override
    public UISlider tooltip(net.minecraft.network.chat.Component tooltip) {
        super.tooltip(tooltip);
        return this;
    }

    @Override
    public UISlider tooltip(net.minecraft.client.gui.components.Tooltip tooltip) {
        super.tooltip(tooltip);
        return this;
    }
}
