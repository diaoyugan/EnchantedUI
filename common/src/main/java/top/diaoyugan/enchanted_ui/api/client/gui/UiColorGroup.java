package top.diaoyugan.enchanted_ui.api.client.gui;

public record UIColorGroup(
        UISlider r,
        UISlider g,
        UISlider b,
        UISlider a,
        UIColorPreview preview
) {
}
