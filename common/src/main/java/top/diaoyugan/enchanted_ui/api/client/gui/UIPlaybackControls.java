package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** Playback adapter for the model-neutral {@link UITransportBar}. */
public final class UIPlaybackControls {
    public record Labels(Component previous, Component pause, Component play, Component next,
                         Function<Component, Component> mode, Component progress, Component volume,
                         Component elapsed, Component remaining) {
        public Labels {
            Objects.requireNonNull(previous);
            Objects.requireNonNull(pause);
            Objects.requireNonNull(play);
            Objects.requireNonNull(next);
            Objects.requireNonNull(mode);
            Objects.requireNonNull(progress);
            Objects.requireNonNull(volume);
            Objects.requireNonNull(elapsed);
            Objects.requireNonNull(remaining);
        }

        public static Labels defaults() {
            return new Labels(
                    UILocalization.frameworkText("playback.control.previous", "Previous"),
                    UILocalization.frameworkText("playback.control.pause", "Pause"),
                    UILocalization.frameworkText("playback.control.play", "Play"),
                    UILocalization.frameworkText("playback.control.next", "Next"),
                    mode -> UILocalization.frameworkText("playback.control.mode.current", "Playback mode: %s", mode),
                    UILocalization.frameworkText("playback.control.progress", "Playback progress"),
                    UILocalization.frameworkText("playback.control.volume", "Volume"),
                    UILocalization.frameworkText("playback.timer.elapsed", "Elapsed time"),
                    UILocalization.frameworkText("playback.timer.remaining", "Remaining time")
            );
        }
    }

    private final UIPlaybackModel model;
    private final Labels labels;
    private final UITheme theme;
    private final boolean previous;
    private final boolean playPause;
    private final boolean next;
    private final boolean mode;
    private final boolean progress;
    private final boolean volume;
    private final boolean elapsed;
    private final boolean remaining;
    private final int maxWidth;

    private UIPlaybackControls(Builder builder) {
        model = builder.model;
        labels = builder.labels;
        theme = builder.theme;
        previous = builder.previous;
        playPause = builder.playPause;
        next = builder.next;
        mode = builder.mode;
        progress = builder.progress;
        volume = builder.volume;
        elapsed = builder.elapsed;
        remaining = builder.remaining;
        maxWidth = builder.maxWidth;
    }

    public static Builder builder(UIPlaybackModel model) {
        return new Builder(model);
    }

    public UIPlaybackModel model() {
        return model;
    }

    public List<AbstractWidget> build(UIBounds region) {
        UITransportBar.Builder controls = UITransportBar.builder()
                .theme(theme)
                .maxWidth(maxWidth);

        if (previous) {
            controls.previous(
                    () -> Component.literal("|<"),
                    labels::previous,
                    model::previous
            );
        }
        if (playPause) {
            controls.primary(
                    () -> Component.literal(model.isPlaying() ? "||" : ">"),
                    () -> model.isPlaying() ? labels.pause() : labels.play(),
                    model::togglePlayback
            );
        }
        if (next) {
            controls.next(
                    () -> Component.literal(">|"),
                    labels::next,
                    model::next
            );
        }
        if (mode) {
            controls.mode(
                    () -> Component.literal(model.modeSymbol()),
                    () -> labels.mode().apply(model.modeLabel()),
                    model::cycleMode
            );
        }
        if (progress) {
            controls.progress(
                    labels.progress(),
                    () -> model.progress().get(),
                    model::seek
            );
        }
        if (volume) {
            controls.volume(
                    () -> Component.literal("V"),
                    labels.volume(),
                    () -> model.volume().get(),
                    model::volume
            );
        }
        if (elapsed || remaining) {
            controls.duration(() -> model.duration().get());
        }
        if (elapsed) {
            controls.elapsedTimer(labels.elapsed());
        }
        if (remaining) {
            controls.remainingTimer(labels.remaining());
        }
        return controls.build().build(region);
    }

    public static final class Builder {
        private final UIPlaybackModel model;
        private Labels labels = Labels.defaults();
        private UITheme theme = UIThemes.current();
        private boolean previous = true;
        private boolean playPause = true;
        private boolean next = true;
        private boolean mode;
        private boolean progress = true;
        private boolean volume = true;
        private boolean elapsed = true;
        private boolean remaining = true;
        private int maxWidth = 620;

        private Builder(UIPlaybackModel model) {
            this.model = Objects.requireNonNull(model);
        }

        public Builder labels(Labels value) {
            labels = Objects.requireNonNull(value);
            return this;
        }

        public Builder theme(UITheme value) {
            theme = Objects.requireNonNull(value);
            return this;
        }

        public Builder previous(boolean value) {
            previous = value;
            return this;
        }

        public Builder playPause(boolean value) {
            playPause = value;
            return this;
        }

        public Builder next(boolean value) {
            next = value;
            return this;
        }

        public Builder playbackMode(boolean value) {
            mode = value;
            return this;
        }

        public Builder progress(boolean value) {
            progress = value;
            return this;
        }

        public Builder volume(boolean value) {
            volume = value;
            return this;
        }

        public Builder elapsedTimer(boolean value) {
            elapsed = value;
            return this;
        }

        public Builder remainingTimer(boolean value) {
            remaining = value;
            return this;
        }

        public Builder maxWidth(int value) {
            maxWidth = Math.max(120, value);
            return this;
        }

        public UIPlaybackControls build() {
            return new UIPlaybackControls(this);
        }
    }
}

