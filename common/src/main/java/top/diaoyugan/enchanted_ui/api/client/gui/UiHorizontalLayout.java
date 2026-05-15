package top.diaoyugan.enchanted_ui.api.client.gui;

import top.diaoyugan.enchanted_ui.client.gui.layout.HorizontalLayout;

public final class UiHorizontalLayout {
    private final HorizontalLayout delegate;

    UiHorizontalLayout(HorizontalLayout delegate) {
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

    public UiHorizontalLayout next(int width) {
        delegate.next(width);
        return this;
    }
}
