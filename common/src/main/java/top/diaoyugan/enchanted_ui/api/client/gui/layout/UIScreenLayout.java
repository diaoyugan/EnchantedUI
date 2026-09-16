package top.diaoyugan.enchanted_ui.api.client.gui.layout;

/** Named screen regions with a fixed header and bottom dock. */
public final class UIScreenLayout implements UILayoutItem {
    private UILayoutItem header = b -> {}, sidebar = b -> {}, content = b -> {}, inspector = b -> {}, bottomDock = b -> {}, overlay = b -> {};
    private int headerHeight = 32, dockHeight = 36, sidebarWidth = 0, inspectorWidth = 0, gap = 4;
    private int collapseBelow = 520;
    public UIScreenLayout header(UILayoutItem item) { header = item; return this; }
    public UIScreenLayout sidebar(UILayoutItem item) { sidebar = item; return this; }
    public UIScreenLayout content(UILayoutItem item) { content = item; return this; }
    public UIScreenLayout inspector(UILayoutItem item) { inspector = item; return this; }
    public UIScreenLayout bottomDock(UILayoutItem item) { bottomDock = item; return this; }
    public UIScreenLayout overlay(UILayoutItem item) { overlay = item; return this; }
    public UIScreenLayout headerHeight(int value) { headerHeight = Math.max(0, value); return this; }
    public UIScreenLayout dockHeight(int value) { dockHeight = Math.max(0, value); return this; }
    public UIScreenLayout sidebarWidth(int value) { sidebarWidth = Math.max(0, value); return this; }
    public UIScreenLayout inspectorWidth(int value) { inspectorWidth = Math.max(0, value); return this; }
    public UIScreenLayout gap(int value) { gap = Math.max(0, value); return this; }
    public UIScreenLayout collapseBelow(int value) { collapseBelow = value; return this; }

    @Override public void layout(UIBounds b) {
        header.layout(new UIBounds(b.x(), b.y(), b.width(), Math.min(headerHeight, b.height())));
        bottomDock.layout(new UIBounds(b.x(), b.y() + Math.max(0, b.height() - dockHeight), b.width(), Math.min(dockHeight, b.height())));
        int top = b.y() + headerHeight + gap, height = Math.max(0, b.height() - headerHeight - dockHeight - gap * 2);
        boolean compact = b.width() < collapseBelow;
        int side = compact ? 0 : sidebarWidth;
        int inspect = compact ? 0 : inspectorWidth;
        sidebar.layout(new UIBounds(b.x(), top, side, height));
        inspector.layout(new UIBounds(b.x() + b.width() - inspect, top, inspect, height));
        int left = b.x() + side + (side > 0 ? gap : 0);
        int right = b.x() + b.width() - inspect - (inspect > 0 ? gap : 0);
        content.layout(new UIBounds(left, top, Math.max(0, right - left), height));
        overlay.layout(b);
    }
}
