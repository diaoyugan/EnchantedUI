package top.diaoyugan.enchanted_ui.client.gui.builder;

import org.jetbrains.annotations.ApiStatus;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.ColorPreviewWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.NumericSliderOptionWidget;

/** Internal widget tuple adapted to the public UIColorGroup. */
@ApiStatus.Internal
public record FormColorGroup(
        NumericSliderOptionWidget r,
        NumericSliderOptionWidget g,
        NumericSliderOptionWidget b,
        NumericSliderOptionWidget a,
        ColorPreviewWidget preview
) {}
