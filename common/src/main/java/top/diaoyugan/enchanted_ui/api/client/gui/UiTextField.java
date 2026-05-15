package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.ValidatedTextFieldWidget;

public final class UiTextField extends UiWidget {
    private final ValidatedTextFieldWidget delegate;

    UiTextField(ValidatedTextFieldWidget delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    public String value() {
        return delegate.getValue();
    }

    public UiTextField value(String value) {
        delegate.setValue(value);
        delegate.validateNow();
        return this;
    }

    public boolean valid() {
        return delegate.isValidValue();
    }

    @Nullable
    public Component error() {
        return delegate.validationError();
    }

    public boolean validateNow() {
        return delegate.validateNow();
    }
}
