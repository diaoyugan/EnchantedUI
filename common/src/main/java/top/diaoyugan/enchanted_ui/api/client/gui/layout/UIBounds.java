package top.diaoyugan.enchanted_ui.api.client.gui.layout;

public record UIBounds(int x, int y, int width, int height) {
    public UIBounds inset(UIInsets insets) {
        return new UIBounds(x + insets.left(), y + insets.top(),
                Math.max(0, width - insets.left() - insets.right()),
                Math.max(0, height - insets.top() - insets.bottom()));
    }
}
