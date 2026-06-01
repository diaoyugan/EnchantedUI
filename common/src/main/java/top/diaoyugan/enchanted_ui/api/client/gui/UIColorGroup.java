package top.diaoyugan.enchanted_ui.api.client.gui;

/**
 * Sliders and preview returned by {@link UIForm#rgbaSlidersWithPreview}.
 * <p>
 * Each channel can be customized like any other {@link UISlider}; for example,
 * add a tooltip to {@link #a()} or read the current value from {@link #r()}.
 *
 * @param r red channel slider, normally 0-255
 * @param g green channel slider, normally 0-255
 * @param b blue channel slider, normally 0-255
 * @param a alpha channel slider, normally 0-255
 * @param preview live preview widget using the four channel values
 */
public record UIColorGroup(
        UISlider r,
        UISlider g,
        UISlider b,
        UISlider a,
        UIWidget preview
) {
}
