package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.MultiLineEditBox;

/**
 * Public wrapper around a multi-line text area created by {@link UIForm}.
 * <p>
 * Use {@link #value()} to read the current text and {@link #value(String)} to
 * replace it. Common widget operations such as tooltips and visibility are
 * inherited from {@link UIWidget}.
 */
public final class UITextArea extends UISpecializedWidget<UITextArea> {
    private final MultiLineEditBox delegate;

    UITextArea(MultiLineEditBox delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    /**
     * Returns the current text in the area.
     */
    public String value() {
        return delegate.getValue();
    }

    /**
     * Replaces the current text.
     */
    public UITextArea value(String value) {
        delegate.setValue(value);
        return this;
    }

    @Override
    protected UITextArea self() {
        return this;
    }
}
