package top.diaoyugan.enchanted_ui.api.client.gui.layout;

import java.util.ArrayList;
import java.util.List;

/** Overlays all children in the same bounds, in insertion/render order. */
public final class UIStackLayout implements UILayoutItem {
    private final List<UILayoutItem> items = new ArrayList<>();
    private UIInsets padding = UIInsets.all(0);
    public UIStackLayout add(UILayoutItem item) { items.add(item); return this; }
    public UIStackLayout padding(UIInsets padding) { this.padding = padding; return this; }
    @Override public void layout(UIBounds bounds) {
        UIBounds inner = bounds.inset(padding);
        items.forEach(item -> item.layout(inner));
    }
}
