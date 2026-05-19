package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.WidgetConditions;
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
import top.diaoyugan.enchanted_ui.client.gui.widget.option.NumericSliderOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.TextWidget;

public class UIWidget {
    private final AbstractWidget delegate;

    UIWidget(AbstractWidget delegate) {
        this.delegate = delegate;
    }

    static UIWidget wrap(AbstractWidget widget) {
        if (widget instanceof NumericSliderOptionWidget slider) {
            return new UISlider(slider);
        }
        if (widget instanceof TextWidget text) {
            return new UIText(text);
        }
        if (widget instanceof ValidatedTextFieldWidget textField) {
            return new UITextField(textField);
        }
        if (widget instanceof BooleanOptionWidget toggle) {
            return new UIToggle(toggle);
        }
        if (widget instanceof KeyBindingButtonWidget keyBinding) {
            return new UIKeyBinding(keyBinding);
        }
        if (widget instanceof CombinationKeyBindingButtonWidget combo) {
            return new UICombinationKeyBinding(combo);
        }
        if (widget instanceof EditableDropdownListWidget editableDropdown) {
            return new UIEditableDropdownList(editableDropdown);
        }
        if (widget instanceof SelectDropdownWidget<?> selectDropdown) {
            return new UISelect(selectDropdown);
        }
        if (widget instanceof SearchableSelectDropdownWidget<?> searchableDropdown) {
            return new UISearchableSelect(searchableDropdown);
        }
        if (widget instanceof MultiSelectDropdownWidget<?> multiSelectDropdown) {
            return new UIMultiSelect(multiSelectDropdown);
        }
        if (widget instanceof DropdownListWidget dropdown) {
            return new UIDropdownList(dropdown);
        }
        if (widget instanceof ColorPreviewWidget preview) {
            return new UIColorPreview(preview);
        }
        if (widget instanceof Button button) {
            return new UIButton(button);
        }
        return new UIWidget(widget);
    }

    public final AbstractWidget vanilla() {
        return delegate;
    }

    protected final AbstractWidget delegate() {
        return delegate;
    }

    public UIWidget tooltip(Component tooltip) {
        delegate.setTooltip(Tooltip.create(tooltip));
        return this;
    }

    public UIWidget setTooltip(Component tooltip) {
        return tooltip(tooltip);
    }

    public UIWidget tooltip(Tooltip tooltip) {
        delegate.setTooltip(tooltip);
        return this;
    }

    public UIWidget setTooltip(Tooltip tooltip) {
        return tooltip(tooltip);
    }

    public UIWidget visible(boolean visible) {
        WidgetConditions.setVisibleState(delegate, visible);
        return this;
    }

    public UIWidget visibleIf(java.util.function.BooleanSupplier condition) {
        WidgetConditions.visibleIf(delegate, condition);
        return this;
    }

    public UIWidget active(boolean active) {
        WidgetConditions.setActiveState(delegate, active);
        return this;
    }

    public UIWidget activeIf(java.util.function.BooleanSupplier condition) {
        WidgetConditions.activeIf(delegate, condition);
        return this;
    }

    public UIWidget focused(boolean focused) {
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

    public UIWidget message(Component message) {
        delegate.setMessage(message);
        return this;
    }

    public UIWidget position(int x, int y) {
        delegate.setX(x);
        delegate.setY(y);
        return this;
    }

    public UIWidget size(int width, int height) {
        delegate.setWidth(width);
        delegate.setHeight(height);
        return this;
    }

    public UIWidget bounds(int x, int y, int width, int height) {
        return position(x, y).size(width, height);
    }
}
