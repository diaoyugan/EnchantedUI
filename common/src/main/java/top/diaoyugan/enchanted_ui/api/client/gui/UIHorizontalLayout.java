package top.diaoyugan.enchanted_ui.api.client.gui;

import top.diaoyugan.enchanted_ui.client.gui.layout.HorizontalLayout;

public final class UIHorizontalLayout {
    private final HorizontalLayout delegate;

    UIHorizontalLayout(HorizontalLayout delegate) {
        this.delegate = delegate;
    }

    public int x() {
        return delegate.x();
    }

    public int y() {
        return delegate.y();
    }

    public int gap() {
        return delegate.gap();
    }

    public UIHorizontalLayout next(int width) {
        delegate.next(width);
        return this;
    }
}
