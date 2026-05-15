package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.CombinationKeyBindingButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.KeyBindingButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.ValidatedTextFieldWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.DropdownListWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.EditableDropdownListWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.MultiSelectDropdownWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.SearchableSelectDropdownWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.list.SelectDropdownWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.BooleanOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.ColorPreviewWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.IntSliderOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.TextWidget;

public class UiWidget {
    private final AbstractWidget delegate;

    UiWidget(AbstractWidget delegate) {
        this.delegate = delegate;
    }

    static UiWidget wrap(AbstractWidget widget) {
        if (widget instanceof IntSliderOptionWidget slider) {
            return new UiSlider(slider);
        }
        if (widget instanceof TextWidget text) {
            return new UiText(text);
        }
        if (widget instanceof ValidatedTextFieldWidget textField) {
            return new UiTextField(textField);
        }
        if (widget instanceof BooleanOptionWidget toggle) {
            return new UiToggle(toggle);
        }
        if (widget instanceof KeyBindingButtonWidget keyBinding) {
            return new UiKeyBinding(keyBinding);
        }
        if (widget instanceof CombinationKeyBindingButtonWidget combo) {
            return new UiCombinationKeyBinding(combo);
        }
        if (widget instanceof EditableDropdownListWidget editableDropdown) {
            return new UiEditableDropdownList(editableDropdown);
        }
        if (widget instanceof SelectDropdownWidget<?> selectDropdown) {
            return new UiDropdownList(selectDropdown);
        }
        if (widget instanceof SearchableSelectDropdownWidget<?> searchableDropdown) {
            return new UiDropdownList(searchableDropdown);
        }
        if (widget instanceof MultiSelectDropdownWidget<?> multiSelectDropdown) {
            return new UiDropdownList(multiSelectDropdown);
        }
        if (widget instanceof DropdownListWidget dropdown) {
            return new UiDropdownList(dropdown);
        }
        if (widget instanceof ColorPreviewWidget preview) {
            return new UiColorPreview(preview);
        }
        if (widget instanceof Button button) {
            return new UiButton(button);
        }
        return new UiWidget(widget);
    }

    public final AbstractWidget vanilla() {
        return delegate;
    }

    protected final AbstractWidget delegate() {
        return delegate;
    }

    public UiWidget tooltip(Component tooltip) {
        delegate.setTooltip(Tooltip.create(tooltip));
        return this;
    }

    public UiWidget setTooltip(Component tooltip) {
        return tooltip(tooltip);
    }

    public UiWidget tooltip(Tooltip tooltip) {
        delegate.setTooltip(tooltip);
        return this;
    }

    public UiWidget setTooltip(Tooltip tooltip) {
        return tooltip(tooltip);
    }

    public UiWidget visible(boolean visible) {
        delegate.visible = visible;
        return this;
    }

    public UiWidget active(boolean active) {
        delegate.active = active;
        return this;
    }

    public UiWidget focused(boolean focused) {
        delegate.setFocused(focused);
        return this;
    }

    public boolean focused() {
        return delegate.isFocused();
    }

    public boolean visible() {
        return delegate.visible;
    }

    public boolean active() {
        return delegate.active;
    }

    public int x() {
        return delegate.getX();
    }

    public int y() {
        return delegate.getY();
    }

    public int width() {
        return delegate.getWidth();
    }

    public int height() {
        return delegate.getHeight();
    }

    public Component message() {
        return delegate.getMessage();
    }

    public UiWidget message(Component message) {
        delegate.setMessage(message);
        return this;
    }

    public UiWidget position(int x, int y) {
        delegate.setX(x);
        delegate.setY(y);
        return this;
    }

    public UiWidget size(int width, int height) {
        delegate.setWidth(width);
        delegate.setHeight(height);
        return this;
    }

    public UiWidget bounds(int x, int y, int width, int height) {
        return position(x, y).size(width, height);
    }
}
