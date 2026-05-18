package top.diaoyugan.enchanted_ui.client.gui.widget.option;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;

public class NumericSliderOptionWidget extends AbstractSliderButton {
    private final double min;
    private final double max;
    private final double step;
    private final DoubleSupplier getter;
    private final DoubleConsumer setter;
    private final Component label;
    private boolean percentage;
    private DoubleFunction<Component> valueFormatter;
    private String customValueKey;

    public NumericSliderOptionWidget(
            int x,
            int y,
            int width,
            int height,
            Component label,
            double min,
            double max,
            double step,
            DoubleSupplier getter,
            DoubleConsumer setter,
            boolean percentage
    ) {
        super(x, y, width, height, label, 0.0D);
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        this.min = min;
        this.max = max;
        this.step = step > 0.0D ? step : 1.0D;
        this.getter = Objects.requireNonNull(getter, "getter");
        this.setter = Objects.requireNonNull(setter, "setter");
        this.label = Objects.requireNonNull(label, "label");
        this.percentage = percentage;
        refreshFromGetter();
    }

    @Override
    protected void updateMessage() {
        double numericValue = getNumericValue();
        Component valueText;
        if (customValueKey != null) {
            valueText = Component.translatable(customValueKey, formatNumber(numericValue));
        } else if (valueFormatter != null) {
            valueText = valueFormatter.apply(numericValue);
        } else if (percentage) {
            double percent = (numericValue - min) * 100.0D / (max - min);
            valueText = Component.literal(formatNumber(percent) + "%");
        } else {
            valueText = Component.literal(formatNumber(numericValue));
        }
        setMessage(label.copy().append(Component.literal(": ")).append(valueText));
    }

    @Override
    protected void applyValue() {
        setter.accept(getNumericValue());
    }

    public NumericSliderOptionWidget setCustomValueKey(String key) {
        this.customValueKey = key;
        updateMessage();
        return this;
    }

    public NumericSliderOptionWidget setValueFormatter(DoubleFunction<Component> formatter) {
        this.valueFormatter = formatter;
        this.customValueKey = null;
        updateMessage();
        return this;
    }

    public NumericSliderOptionWidget percentage(boolean percentage) {
        this.percentage = percentage;
        updateMessage();
        return this;
    }

    public double numericValue() {
        return getNumericValue();
    }

    public void setNumericValue(double numericValue) {
        value = normalize(snap(numericValue));
        updateMessage();
    }

    public void refreshFromGetter() {
        setNumericValue(getter.getAsDouble());
    }

    private double getNumericValue() {
        return snap(min + value * (max - min));
    }

    private double snap(double candidate) {
        double clamped = Math.max(min, Math.min(max, candidate));
        double snappedSteps = Math.rint((clamped - min) / step);
        double snapped = min + snappedSteps * step;
        if (Math.abs(snapped - min) < 1.0E-9D) {
            return min;
        }
        if (Math.abs(snapped - max) < 1.0E-9D) {
            return max;
        }
        return Math.max(min, Math.min(max, snapped));
    }

    private double normalize(double numericValue) {
        return (numericValue - min) / (max - min);
    }

    private static String formatNumber(double value) {
        long rounded = Math.round(value);
        if (Math.abs(value - rounded) < 1.0E-9D) {
            return Long.toString(rounded);
        }
        String formatted = String.format(java.util.Locale.ROOT, "%.4f", value);
        int trimIndex = formatted.length();
        while (trimIndex > 0 && formatted.charAt(trimIndex - 1) == '0') {
            trimIndex--;
        }
        if (trimIndex > 0 && formatted.charAt(trimIndex - 1) == '.') {
            trimIndex--;
        }
        return formatted.substring(0, trimIndex);
    }
}
