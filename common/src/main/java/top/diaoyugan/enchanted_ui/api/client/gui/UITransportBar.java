package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;
import top.diaoyugan.enchanted_ui.api.client.gui.time.UITimerMode;
import top.diaoyugan.enchanted_ui.api.client.gui.time.UITimerWidget;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * Model-neutral transport controls for time-based content such as audio, video,
 * recordings, previews, or timelines.
 */
public final class UITransportBar implements UIWidgetGroup {
    private final Action previous;
    private final Action primary;
    private final Action next;
    private final Action mode;
    private final ValueControl progress;
    private final ValueControl volume;
    private final Supplier<Component> volumeLabel;
    private final Supplier<Duration> duration;
    private final Component elapsedTooltip;
    private final Component remainingTooltip;
    private final UITheme theme;
    private final int maxWidth;

    private UITransportBar(Builder builder) {
        previous = builder.previous;
        primary = builder.primary;
        next = builder.next;
        mode = builder.mode;
        progress = builder.progress;
        volume = builder.volume;
        volumeLabel = builder.volumeLabel;
        duration = builder.duration;
        elapsedTooltip = builder.elapsedTooltip;
        remainingTooltip = builder.remainingTooltip;
        theme = builder.theme;
        maxWidth = builder.maxWidth;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public List<AbstractWidget> build(UIBounds region) {
        List<AbstractWidget> widgets = new ArrayList<>();
        int width = Math.min(maxWidth, region.width());
        int left = region.x() + (region.width() - width) / 2;
        int y = region.y() + (region.height() - 20) / 2;
        int cursor = left;

        cursor = addAction(widgets, previous, cursor, y, 26, 4);
        cursor = addAction(widgets, primary, cursor, y, 30, 4);
        cursor = addAction(widgets, next, cursor, y, 26, 4);
        cursor = addAction(widgets, mode, cursor, y, 28, 4);

        int right = left + width;
        boolean showVolume = volume != null && width >= 240;
        if (showVolume) right -= 66;

        boolean showElapsed = elapsedTooltip != null && width >= 280;
        boolean showRemaining = remainingTooltip != null && width >= 280;
        int timerWidth = 34;
        int timerSpace = (showElapsed ? timerWidth + 4 : 0) + (showRemaining ? timerWidth + 4 : 0);
        int progressWidth = Math.max(12, right - cursor - timerSpace);

        if (showElapsed) {
            UITimerWidget timer = UITimerWidget.progress(
                    cursor,
                    y + 2,
                    timerWidth,
                    16,
                    UITimerMode.COUNT_UP,
                    this::safeDuration,
                    progress == null ? () -> 0.0D : progress.value()
            );
            timer.setTooltip(Tooltip.create(elapsedTooltip));
            widgets.add(timer);
            cursor += timerWidth + 4;
        }

        if (progress != null) {
            widgets.add(new UINormalizedSlider(
                    cursor,
                    y + 2,
                    progressWidth,
                    16,
                    progress.narration(),
                    progress.value(),
                    progress.setter(),
                    theme
            ));
            cursor += progressWidth;
        }

        if (showRemaining) {
            cursor += 4;
            UITimerWidget timer = UITimerWidget.progress(
                    cursor,
                    y + 2,
                    timerWidth,
                    16,
                    UITimerMode.COUNT_DOWN,
                    this::safeDuration,
                    progress == null ? () -> 0.0D : progress.value()
            );
            timer.setTooltip(Tooltip.create(remainingTooltip));
            widgets.add(timer);
        }

        if (showVolume) {
            widgets.add(new UILabelWidget(left + width - 62, y, 10, 20, volumeLabel));
            widgets.add(new UINormalizedSlider(
                    left + width - 50,
                    y + 2,
                    50,
                    16,
                    volume.narration(),
                    volume.value(),
                    volume.setter(),
                    theme
            ));
        }
        return widgets;
    }

    private int addAction(List<AbstractWidget> widgets, Action action, int x, int y, int width, int gap) {
        if (action == null) return x;
        UIDynamicButton button = new UIDynamicButton(
                x,
                y,
                width,
                20,
                action.label(),
                action.narration(),
                action.onPress()
        );
        button.setTooltip(Tooltip.create(action.narration().get()));
        widgets.add(button);
        return x + width + gap;
    }

    private Duration safeDuration() {
        Duration value = duration.get();
        return value == null || value.isNegative() ? Duration.ZERO : value;
    }

    private record Action(Supplier<Component> label, Supplier<Component> narration, Runnable onPress) {}

    private record ValueControl(
            Component narration,
            DoubleSupplier value,
            DoubleConsumer setter
    ) {}

    public static final class Builder {
        private Action previous;
        private Action primary;
        private Action next;
        private Action mode;
        private ValueControl progress;
        private ValueControl volume;
        private Supplier<Component> volumeLabel = () -> Component.literal("V");
        private Supplier<Duration> duration = () -> Duration.ZERO;
        private Component elapsedTooltip;
        private Component remainingTooltip;
        private UITheme theme = UIThemes.current();
        private int maxWidth = 620;

        private Builder() {}

        public Builder previous(Supplier<Component> label, Supplier<Component> narration, Runnable action) {
            previous = action(label, narration, action);
            return this;
        }

        public Builder primary(Supplier<Component> label, Supplier<Component> narration, Runnable action) {
            primary = action(label, narration, action);
            return this;
        }

        public Builder next(Supplier<Component> label, Supplier<Component> narration, Runnable action) {
            next = action(label, narration, action);
            return this;
        }

        public Builder mode(Supplier<Component> label, Supplier<Component> narration, Runnable action) {
            mode = action(label, narration, action);
            return this;
        }

        public Builder progress(Component narration, DoubleSupplier value, DoubleConsumer setter) {
            progress = valueControl(narration, value, setter);
            return this;
        }

        public Builder volume(
                Supplier<Component> label,
                Component narration,
                DoubleSupplier value,
                DoubleConsumer setter
        ) {
            volumeLabel = Objects.requireNonNull(label);
            volume = valueControl(narration, value, setter);
            return this;
        }

        public Builder duration(Supplier<Duration> value) {
            duration = Objects.requireNonNull(value);
            return this;
        }

        public Builder elapsedTimer(Component tooltip) {
            elapsedTooltip = Objects.requireNonNull(tooltip);
            return this;
        }

        public Builder remainingTimer(Component tooltip) {
            remainingTooltip = Objects.requireNonNull(tooltip);
            return this;
        }

        public Builder theme(UITheme value) {
            theme = Objects.requireNonNull(value);
            return this;
        }

        public Builder maxWidth(int value) {
            maxWidth = Math.max(120, value);
            return this;
        }

        public UITransportBar build() {
            return new UITransportBar(this);
        }

        private static Action action(
                Supplier<Component> label,
                Supplier<Component> narration,
                Runnable onPress
        ) {
            return new Action(
                    Objects.requireNonNull(label),
                    Objects.requireNonNull(narration),
                    Objects.requireNonNull(onPress)
            );
        }

        private static ValueControl valueControl(
                Component narration,
                DoubleSupplier value,
                DoubleConsumer setter
        ) {
            return new ValueControl(
                    Objects.requireNonNull(narration),
                    Objects.requireNonNull(value),
                    Objects.requireNonNull(setter)
            );
        }
    }
}
