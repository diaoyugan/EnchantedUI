package top.diaoyugan.enchanted_ui.api.client.gui;

import top.diaoyugan.enchanted_ui.client.gui.layout.VerticalLayout;

public final class UiVerticalLayout {
    private final VerticalLayout delegate;

    UiVerticalLayout(VerticalLayout delegate) {
        this.delegate = delegate;
    }

    VerticalLayout delegate() {
        return delegate;
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

    public UiVerticalLayout next(int height) {
        delegate.next(height);
        return this;
    }
}
