package top.diaoyugan.enchanted_ui.api.client.gui.layout;

/** Geometry regression checks runnable without a Minecraft client or a test framework. */
public final class UIWorkspaceLayoutTest {
    public static void main(String[] args) {
        UIWorkspaceLayout layout = UIWorkspaceLayout.builder().gap(4)
                .compactHeaderHeight(30).toolbarHeights(18, 20).build();
        UIWorkspaceLayout.Regions compact = layout.layout(new UIBounds(8, 38, 479, 200));
        require(compact.compact(), "Inspector must collapse below its breakpoint");
        require(compact.header().height() == 30 && compact.toolbar().height() == 18,
                "Compact header and toolbar heights");
        require(compact.body().y() == 90, "Compact content must follow header, toolbar and gap");
        UIWorkspaceLayout.Regions regular = layout.layout(new UIBounds(8, 38, 480, 200));
        require(!regular.compact() && regular.navigation().width() == 96 && regular.inspector().width() == 120,
                "Three columns at the inspector breakpoint");
        require(regular.header().height() == 0 && regular.body().y() == 62, "Inspector replaces compact header");
        UIWorkspaceLayout.Regions roomy = layout.layout(new UIBounds(8, 38, 700, 200));
        require(roomy.navigation().width() == 140 && roomy.inspector().width() == 180, "Roomy column widths");
        require(layout.layout(new UIBounds(8, 38, 699, 200)).inspector().width() == 120,
                "Regular widths below roomy breakpoint");
        UIWorkspaceLayout custom = UIWorkspaceLayout.builder().breakpoints(0, 0).gap(10)
                .compactHeaderHeight(100).toolbarHeights(50, 50)
                .navigationWidth(width -> 10000).inspectorWidth(width -> 10000).build();
        for (UIWorkspaceLayout value : new UIWorkspaceLayout[] {layout, custom}) {
            for (int width : new int[] {0, 1, 80, 120, 240, 479, 480, 699, 700, 1920}) {
                for (int height : new int[] {0, 1, 20, 36, 80, 200}) {
                    UIBounds page = new UIBounds(8, 38, width, height);
                    UIWorkspaceLayout.Regions regions = value.layout(page);
                    for (UIBounds region : new UIBounds[] {regions.navigation(), regions.content(), regions.inspector(),
                            regions.header(), regions.toolbar(), regions.body()}) {
                        require(region.width() >= 0 && region.height() >= 0, "Non-negative region dimensions");
                        require(region.x() >= page.x() && region.x() + region.width() <= page.x() + width,
                                "Region must stay inside horizontal page bounds");
                        require(region.y() >= page.y() && region.y() + region.height() <= page.y() + height,
                                "Region must stay inside vertical page bounds");
                    }
                }
            }
        }
        UIWorkspaceLayout.Regions plain = UIWorkspaceLayout.builder().navigation(false).inspector(false)
                .build().layout(new UIBounds(8, 38, 80, 20));
        require(plain.content().equals(plain.body()) && plain.content().width() == 80,
                "No columns or bars must leave all space to content");
        System.out.println("Workspace layout regression checks passed");
    }

    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
