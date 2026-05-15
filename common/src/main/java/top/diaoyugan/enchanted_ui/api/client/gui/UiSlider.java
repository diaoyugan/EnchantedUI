package top.diaoyugan.enchanted_ui.api.client.gui;

import top.diaoyugan.enchanted_ui.client.gui.widget.option.IntSliderOptionWidget;

public final class UiSlider extends UiWidget {
    private final IntSliderOptionWidget delegate;

    UiSlider(IntSliderOptionWidget delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    public UiSlider setCustomValueKey(String key) {
        delegate.setCustomValueKey(key);
        return this;
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
