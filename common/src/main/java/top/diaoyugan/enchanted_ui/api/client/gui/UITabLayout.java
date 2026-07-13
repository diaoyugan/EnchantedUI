package top.diaoyugan.enchanted_ui.api.client.gui;

/**
 * Responsive tab-strip placement used by screen presets.
 * <p>
 * Tabs that do not fit are presented through a clipped, scrollable window with
 * navigation buttons. {@code endMargin} is measured from the bottom for a
 * vertical strip and from the right for a horizontal strip.
 *
 * @param orientation horizontal or vertical tab flow
 * @param startX left edge of the tab strip
 * @param startY top edge of the tab strip
 * @param endMargin bottom or right margin, depending on orientation
 * @param gap spacing between adjacent tab buttons
 * @param reserveContentSpace whether the content viewport should avoid the strip
 */
public record UITabLayout(
        Orientation orientation,
        int startX,
        int startY,
        int endMargin,
        int gap,
        boolean reserveContentSpace
) {
    public UITabLayout {
        if (orientation == null) throw new NullPointerException("orientation");
        if (startX < 0 || startY < 0 || endMargin < 0 || gap < 0) {
            throw new IllegalArgumentException("Tab layout values cannot be negative");
        }
    }

    public static UITabLayout sidebar() {
        return new UITabLayout(Orientation.VERTICAL, 10, 30, 40, 4, true);
    }

    public static UITabLayout top() {
        return new UITabLayout(Orientation.HORIZONTAL, 10, 28, 10, 4, true);
    }

    public enum Orientation {
        VERTICAL,
        HORIZONTAL
    }
}
