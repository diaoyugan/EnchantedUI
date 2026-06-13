package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.WidgetConditions;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.ValidatedTextFieldWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.NumericSliderOptionWidget;

/**
 * Base wrapper for widgets returned by the public API.
 * <p>
 * It provides common operations such as tooltips, visibility, active state,
 * position, size, and message updates. Use {@link #vanilla()} only when you
 * need direct access to the underlying Minecraft widget.
 */
public class UIWidget {
    private final AbstractWidget delegate;

    UIWidget(AbstractWidget delegate) {
        this.delegate = delegate;
    }

    static UIWidget wrap(AbstractWidget widget) {
        if (widget instanceof NumericSliderOptionWidget slider) {
            return new UISlider(slider);
        }
        if (widget instanceof ValidatedTextFieldWidget textField) {
            return new UITextField(textField);
        }
        if (widget instanceof MultiLineEditBox textArea) {
            return new UITextArea(textArea);
        }
        return new UIWidget(widget);
    }

    /**
     * Returns the underlying Minecraft widget.
     */
    public final AbstractWidget vanilla() {
        return delegate;
    }

    protected final AbstractWidget delegate() {
        return delegate;
    }

    /**
     * Sets a simple text tooltip.
     */
    public UIWidget tooltip(Component tooltip) {
        WidgetConditions.setTooltip(delegate, Tooltip.create(tooltip));
        return this;
    }

    public UIWidget setTooltip(Component tooltip) {
        return tooltip(tooltip);
    }

    public UIWidget tooltip(Tooltip tooltip) {
        WidgetConditions.setTooltip(delegate, tooltip);
        return this;
    }

    public UIWidget setTooltip(Tooltip tooltip) {
        return tooltip(tooltip);
    }

    /**
     * Sets the tooltip shown while this widget is inactive.
     * The normal tooltip remains the fallback when no disabled tooltip is set.
     */
    public UIWidget disabledTooltip(Component tooltip) {
        WidgetConditions.setDisabledTooltip(delegate, Tooltip.create(tooltip));
        return this;
    }

    public UIWidget disabledTooltip(Tooltip tooltip) {
        WidgetConditions.setDisabledTooltip(delegate, tooltip);
        return this;
    }

    public UIWidget inactiveTooltip(Component tooltip) {
        return disabledTooltip(tooltip);
    }

    public UIWidget inactiveTooltip(Tooltip tooltip) {
        return disabledTooltip(tooltip);
    }

    /**
     * Shows or hides this widget immediately.
     */
    public UIWidget visible(boolean visible) {
        WidgetConditions.setVisibleState(delegate, visible);
        return this;
    }

    /**
     * Re-evaluates visibility from the supplied condition during screen ticks.
     */
    public UIWidget visibleIf(java.util.function.BooleanSupplier condition) {
        WidgetConditions.visibleIf(delegate, condition);
        return this;
    }

    /**
     * Enables or disables this widget immediately.
     */
    public UIWidget active(boolean active) {
        WidgetConditions.setActiveState(delegate, active);
        return this;
    }

    /**
     * Re-evaluates active state from the supplied condition during screen ticks.
     */
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

abstract class UISpecializedWidget<T extends UISpecializedWidget<T>> extends UIWidget {
    UISpecializedWidget(AbstractWidget delegate) {
        super(delegate);
    }

    protected abstract T self();

    @Override
    public T tooltip(Component tooltip) {
        super.tooltip(tooltip);
        return self();
    }

    @Override
    public T setTooltip(Component tooltip) {
        super.setTooltip(tooltip);
        return self();
    }

    @Override
    public T tooltip(Tooltip tooltip) {
        super.tooltip(tooltip);
        return self();
    }

    @Override
    public T setTooltip(Tooltip tooltip) {
        super.setTooltip(tooltip);
        return self();
    }

    @Override
    public T disabledTooltip(Component tooltip) {
        super.disabledTooltip(tooltip);
        return self();
    }

    @Override
    public T disabledTooltip(Tooltip tooltip) {
        super.disabledTooltip(tooltip);
        return self();
    }

    @Override
    public T inactiveTooltip(Component tooltip) {
        super.inactiveTooltip(tooltip);
        return self();
    }

    @Override
    public T inactiveTooltip(Tooltip tooltip) {
        super.inactiveTooltip(tooltip);
        return self();
    }

    @Override
    public T visible(boolean visible) {
        super.visible(visible);
        return self();
    }

    @Override
    public T visibleIf(java.util.function.BooleanSupplier condition) {
        super.visibleIf(condition);
        return self();
    }

    @Override
    public T active(boolean active) {
        super.active(active);
        return self();
    }

    @Override
    public T activeIf(java.util.function.BooleanSupplier condition) {
        super.activeIf(condition);
        return self();
    }

    @Override
    public T focused(boolean focused) {
        super.focused(focused);
        return self();
    }

    @Override
    public T message(Component message) {
        super.message(message);
        return self();
    }

    @Override
    public T position(int x, int y) {
        super.position(x, y);
        return self();
    }

    @Override
    public T size(int width, int height) {
        super.size(width, height);
        return self();
    }

    @Override
    public T bounds(int x, int y, int width, int height) {
        super.bounds(x, y, width, height);
        return self();
    }
}
