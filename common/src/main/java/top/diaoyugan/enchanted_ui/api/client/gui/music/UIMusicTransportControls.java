package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import top.diaoyugan.enchanted_ui.api.client.gui.UITransportBar;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

import java.util.List;
import java.util.Objects;

/** Music-model adapter for the model-neutral {@link UITransportBar}. */
public final class UIMusicTransportControls {
    private final UIMusicPlayerModel model;
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

    private UIMusicTransportControls(Builder builder) {
        model = builder.model;
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

    public static Builder builder(UIMusicPlayerModel model) {
        return new Builder(model);
    }

    public UIMusicPlayerModel model() {
        return model;
    }

    public List<AbstractWidget> build(UIBounds region) {
        UITransportBar.Builder controls = UITransportBar.builder()
                .theme(theme)
                .maxWidth(maxWidth);

        if (previous) {
            controls.previous(
                    () -> Component.literal("|<"),
                    () -> UILocalization.frameworkText("music.control.previous", "Previous track"),
                    model::previous
            );
        }
        if (playPause) {
            controls.primary(
                    () -> Component.literal(model.playbackState().get() == UIPlaybackState.PLAYING ? "||" : ">"),
                    () -> model.playbackState().get() == UIPlaybackState.PLAYING
                            ? UILocalization.frameworkText("music.control.pause", "Pause")
                            : UILocalization.frameworkText("music.control.play", "Play"),
                    model::togglePlayback
            );
        }
        if (next) {
            controls.next(
                    () -> Component.literal(">|"),
                    () -> UILocalization.frameworkText("music.control.next", "Next track"),
                    model::next
            );
        }
        if (mode) {
            controls.mode(
                    () -> Component.literal(model.playbackMode().get().symbol()),
                    () -> UILocalization.frameworkText(
                            "music.control.mode.current",
                            "Playback mode: %s",
                            model.playbackMode().get().label()
                    ),
                    this::cycleMode
            );
        }
        if (progress) {
            controls.progress(
                    UILocalization.frameworkText("music.control.progress", "Playback progress"),
                    () -> model.progress().get(),
                    model::seek
            );
        }
        if (volume) {
            controls.volume(
                    () -> Component.literal("V"),
                    UILocalization.frameworkText("music.control.volume", "Volume"),
                    () -> model.volume().get(),
                    model::volume
            );
        }
        if (elapsed || remaining) {
            controls.duration(() -> model.duration().get());
        }
        if (elapsed) {
            controls.elapsedTimer(UILocalization.frameworkText("music.timer.elapsed", "Elapsed time"));
        }
        if (remaining) {
            controls.remainingTimer(UILocalization.frameworkText("music.timer.remaining", "Remaining time"));
        }
        return controls.build().build(region);
    }

    private void cycleMode() {
        List<UIPlaybackMode> values = model.playbackModes();
        if (values.isEmpty()) return;
        int index = values.indexOf(model.playbackMode().get());
        model.playbackMode(values.get((index + 1 + values.size()) % values.size()));
    }

    public static final class Builder {
        private final UIMusicPlayerModel model;
        private UITheme theme = UIThemes.current();
        private boolean previous = true;
        private boolean playPause = true;
        private boolean next = true;
        private boolean mode = true;
        private boolean progress = true;
        private boolean volume = true;
        private boolean elapsed = true;
        private boolean remaining = true;
        private int maxWidth = 620;

        private Builder(UIMusicPlayerModel model) {
            this.model = Objects.requireNonNull(model);
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

        public UIMusicTransportControls build() {
            return new UIMusicTransportControls(this);
        }
    }
}
