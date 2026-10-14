package top.diaoyugan.enchanted_ui.api.client.gui.layout;

import java.util.Objects;
import java.util.function.IntUnaryOperator;

/** Responsive navigation, content and inspector layout with optional header and toolbar. */
public final class UIWorkspaceLayout {
    public record Regions(UIBounds navigation, UIBounds content, UIBounds inspector,
                          UIBounds header, UIBounds toolbar, UIBounds body) {
        public boolean compact() { return inspector.width() == 0; }
    }

    private final boolean navigation;
    private final boolean inspector;
    private final int inspectorAt;
    private final int roomyAt;
    private final int gap;
    private final int minimumContentWidth;
    private final int compactHeaderHeight;
    private final int compactToolbarHeight;
    private final int regularToolbarHeight;
    private final IntUnaryOperator navigationWidth;
    private final IntUnaryOperator inspectorWidth;

    private UIWorkspaceLayout(Builder builder) {
        navigation = builder.navigation;
        inspector = builder.inspector;
        inspectorAt = builder.inspectorAt;
        roomyAt = builder.roomyAt;
        gap = builder.gap;
        minimumContentWidth = builder.minimumContentWidth;
        compactHeaderHeight = builder.compactHeaderHeight;
        compactToolbarHeight = builder.compactToolbarHeight;
        regularToolbarHeight = builder.regularToolbarHeight;
        navigationWidth = builder.navigationWidth;
        inspectorWidth = builder.inspectorWidth;
    }

    public static Builder builder() { return new Builder(); }

    public Regions layout(UIBounds bounds) {
        int width = Math.max(0, bounds.width());
        int height = Math.max(0, bounds.height());
        boolean roomy = width >= roomyAt;
        boolean threeColumn = inspector && width >= inspectorAt;
        int sidebar = navigation ? Math.max(0, navigationWidth == null
                ? defaultNavigationWidth(width, roomy, threeColumn) : navigationWidth.applyAsInt(width)) : 0;
        int detail = threeColumn ? Math.max(0, inspectorWidth == null
                ? (roomy ? 180 : 120) : inspectorWidth.applyAsInt(width)) : 0;
        sidebar = Math.min(sidebar, Math.max(0, width - minimumContentWidth - (sidebar > 0 ? gap : 0)));
        detail = Math.min(detail, Math.max(0, width - sidebar - minimumContentWidth
                - (sidebar > 0 ? gap : 0) - (detail > 0 ? gap : 0)));
        int contentX = bounds.x() + sidebar + (sidebar > 0 ? gap : 0);
        int contentWidth = Math.max(0, width - sidebar - detail
                - (sidebar > 0 ? gap : 0) - (detail > 0 ? gap : 0));
        UIBounds content = new UIBounds(contentX, bounds.y(), contentWidth, height);
        int headerHeight = detail == 0 ? Math.min(height, compactHeaderHeight) : 0;
        int toolbarY = bounds.y() + headerHeight;
        int toolbarHeight = Math.min(height - headerHeight,
                detail == 0 ? compactToolbarHeight : regularToolbarHeight);
        int bodyY = toolbarY + toolbarHeight;
        if (toolbarHeight > 0) bodyY += Math.min(gap, Math.max(0, bounds.y() + height - bodyY));
        return new Regions(
                new UIBounds(bounds.x(), bounds.y(), sidebar, height), content,
                new UIBounds(contentX + contentWidth + (detail > 0 ? gap : 0), bounds.y(), detail, height),
                new UIBounds(contentX, bounds.y(), contentWidth, headerHeight),
                new UIBounds(contentX, toolbarY, contentWidth, toolbarHeight),
                new UIBounds(contentX, bodyY, contentWidth, Math.max(0, bounds.y() + height - bodyY))
        );
    }

    private static int defaultNavigationWidth(int width, boolean roomy, boolean threeColumn) {
        if (roomy) return 140;
        if (threeColumn) return 96;
        if (width >= 260) return Math.min(88, Math.max(72, width / 4));
        return Math.min(64, Math.max(0, width / 4));
    }

    public static final class Builder {
        private boolean navigation = true;
        private boolean inspector = true;
        private int inspectorAt = 480;
        private int roomyAt = 700;
        private int gap = 4;
        private int minimumContentWidth = 120;
        private int compactHeaderHeight;
        private int compactToolbarHeight;
        private int regularToolbarHeight;
        private IntUnaryOperator navigationWidth;
        private IntUnaryOperator inspectorWidth;

        private Builder() {}

        public Builder navigation(boolean value) { navigation = value; return this; }
        public Builder inspector(boolean value) { inspector = value; return this; }
        public Builder breakpoints(int inspectorAt, int roomyAt) {
            this.inspectorAt = Math.max(0, inspectorAt);
            this.roomyAt = Math.max(this.inspectorAt, roomyAt);
            return this;
        }
        public Builder gap(int value) { gap = Math.max(0, value); return this; }
        public Builder minimumContentWidth(int value) { minimumContentWidth = Math.max(0, value); return this; }
        public Builder compactHeaderHeight(int value) { compactHeaderHeight = Math.max(0, value); return this; }
        public Builder toolbarHeights(int compact, int regular) {
            compactToolbarHeight = Math.max(0, compact);
            regularToolbarHeight = Math.max(0, regular);
            return this;
        }
        public Builder navigationWidth(IntUnaryOperator value) { navigationWidth = Objects.requireNonNull(value); return this; }
        public Builder inspectorWidth(IntUnaryOperator value) { inspectorWidth = Objects.requireNonNull(value); return this; }
        public UIWorkspaceLayout build() { return new UIWorkspaceLayout(this); }
    }
}
