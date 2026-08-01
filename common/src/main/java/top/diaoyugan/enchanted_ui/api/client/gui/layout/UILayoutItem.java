package top.diaoyugan.enchanted_ui.api.client.gui.layout;

import net.minecraft.client.gui.components.AbstractWidget;

@FunctionalInterface
public interface UILayoutItem {
    void layout(UIBounds bounds);

    static UILayoutItem widget(AbstractWidget widget) {
        return bounds -> {
            widget.setX(bounds.x());
            widget.setY(bounds.y());
            widget.setWidth(bounds.width());
            widget.setHeight(bounds.height());
        };
    }

    default UILayoutItem padding(UIInsets insets) { return bounds -> layout(bounds.inset(insets)); }

    default UILayoutItem constrained(UISizeConstraints constraints, UIAlignment horizontal, UIAlignment vertical) {
        return bounds -> layout(constraints.apply(bounds, horizontal, vertical));
    }
}
