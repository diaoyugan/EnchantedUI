package top.diaoyugan.enchanted_ui.client.gui.layout;

public class HorizontalLayout {
    private int x;
    private final int y;
    private final int gap;

    public HorizontalLayout(int startX, int y, int gap) {
        this.x = startX;
        this.y = y;
        this.gap = gap;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int gap() {
        return gap;
    }

    public void next(int width) {
        x += width + gap;
    }
}
