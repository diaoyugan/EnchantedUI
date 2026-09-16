package top.diaoyugan.enchanted_ui.api.client.gui.layout;

public record UIInsets(int top, int right, int bottom, int left) {
    public static UIInsets all(int value) { return new UIInsets(value, value, value, value); }
    public static UIInsets symmetric(int vertical, int horizontal) { return new UIInsets(vertical, horizontal, vertical, horizontal); }
}
