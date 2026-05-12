package top.diaoyugan.enchanted_ui.api.client.gui;

import top.diaoyugan.enchanted_ui.client.gui.widget.option.ColorPreviewWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.IntSliderOptionWidget;

public record UiColorGroup(
        IntSliderOptionWidget r,
        IntSliderOptionWidget g,
        IntSliderOptionWidget b,
        IntSliderOptionWidget a,
        ColorPreviewWidget preview
) {
}
