package top.diaoyugan.enchanted_ui.api.client.gui.layout;

import net.minecraft.client.gui.components.AbstractWidget;
import java.util.Objects;

/** Responsive two-pane layout with minimum sizes and optional first-pane collapse. */
public final class UISplitLayout implements UILayoutItem {
    public enum Axis { HORIZONTAL, VERTICAL }
    private final Axis axis;
    private UILayoutItem first = bounds -> {};
    private UILayoutItem second = bounds -> {};
    private float ratio = 0.5F;
    private int minFirst = 0;
    private int minSecond = 0;
    private int gap = 0;
    private boolean collapsible;
    private int collapseBelow = 0;

    private UISplitLayout(Axis axis) { this.axis = axis; }
    public static UISplitLayout horizontal() { return new UISplitLayout(Axis.HORIZONTAL); }
    public static UISplitLayout vertical() { return new UISplitLayout(Axis.VERTICAL); }
    public UISplitLayout first(UILayoutItem item) { first = Objects.requireNonNull(item); return this; }
    public UISplitLayout first(AbstractWidget widget) { return first(UILayoutItem.widget(widget)); }
    public UISplitLayout second(UILayoutItem item) { second = Objects.requireNonNull(item); return this; }
    public UISplitLayout second(AbstractWidget widget) { return second(UILayoutItem.widget(widget)); }
    public UISplitLayout ratio(float ratio) { this.ratio = Math.max(0, Math.min(1, ratio)); return this; }
    public UISplitLayout minFirstWidth(int value) { minFirst = Math.max(0, value); return this; }
    public UISplitLayout minSecondWidth(int value) { minSecond = Math.max(0, value); return this; }
    public UISplitLayout gap(int value) { gap = Math.max(0, value); return this; }
    public UISplitLayout collapsible(boolean value) { collapsible = value; return this; }
    public UISplitLayout collapseBelow(int value) { collapseBelow = Math.max(0, value); return this; }

    @Override
    public void layout(UIBounds bounds) {
        int available = (axis == Axis.HORIZONTAL ? bounds.width() : bounds.height()) - gap;
        int threshold = collapseBelow > 0 ? collapseBelow : minFirst + minSecond + gap;
        if (collapsible && available + gap < threshold) {
            first.layout(new UIBounds(bounds.x(), bounds.y(), 0, 0));
            second.layout(bounds);
            return;
        }
        int firstSize = Math.max(minFirst, Math.min(available - minSecond, Math.round(available * ratio)));
        firstSize = Math.max(0, Math.min(available, firstSize));
        int secondSize = Math.max(0, available - firstSize);
        if (axis == Axis.HORIZONTAL) {
            first.layout(new UIBounds(bounds.x(), bounds.y(), firstSize, bounds.height()));
            second.layout(new UIBounds(bounds.x() + firstSize + gap, bounds.y(), secondSize, bounds.height()));
        } else {
            first.layout(new UIBounds(bounds.x(), bounds.y(), bounds.width(), firstSize));
            second.layout(new UIBounds(bounds.x(), bounds.y() + firstSize + gap, bounds.width(), secondSize));
        }
    }
}
