package top.diaoyugan.enchanted_ui.api.client.gui.layout;

import java.util.ArrayList;
import java.util.List;

/** Equal-column responsive grid. */
public final class UIGridLayout implements UILayoutItem {
    private final List<UILayoutItem> items = new ArrayList<>();
    private int columns = 2;
    private int minColumnWidth = 120;
    private int gap = 4;

    public UIGridLayout columns(int columns) { this.columns = Math.max(1, columns); return this; }
    public UIGridLayout minColumnWidth(int width) { minColumnWidth = Math.max(1, width); return this; }
    public UIGridLayout gap(int gap) { this.gap = Math.max(0, gap); return this; }
    public UIGridLayout add(UILayoutItem item) { items.add(item); return this; }

    @Override
    public void layout(UIBounds bounds) {
        int actualColumns = Math.max(1, Math.min(columns, (bounds.width() + gap) / (minColumnWidth + gap)));
        int rows = Math.max(1, (items.size() + actualColumns - 1) / actualColumns);
        int cellWidth = Math.max(0, (bounds.width() - (actualColumns - 1) * gap) / actualColumns);
        int cellHeight = Math.max(0, (bounds.height() - (rows - 1) * gap) / rows);
        for (int i = 0; i < items.size(); i++) {
            int col = i % actualColumns;
            int row = i / actualColumns;
            items.get(i).layout(new UIBounds(bounds.x() + col * (cellWidth + gap),
                    bounds.y() + row * (cellHeight + gap), cellWidth, cellHeight));
        }
    }
}
