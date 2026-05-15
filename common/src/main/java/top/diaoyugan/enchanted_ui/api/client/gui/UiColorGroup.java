package top.diaoyugan.enchanted_ui.api.client.gui;

public record UiColorGroup(
        UiSlider r,
        UiSlider g,
        UiSlider b,
        UiSlider a,
        UiColorPreview preview
) {
}
