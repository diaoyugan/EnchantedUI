package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.client.gui.components.AbstractWidget;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import top.diaoyugan.enchanted_ui.api.client.gui.UIPlaybackControls;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;

import java.util.List;
import java.util.Objects;

/** Music-model and label adapter for {@link UIPlaybackControls}. */
public final class UIMusicTransportControls {
    private final UIMusicPlayerModel model;
    private final UIPlaybackControls controls;

    private UIMusicTransportControls(Builder builder) {
        model = builder.model;
        controls = builder.controls.build();
    }

    public static Builder builder(UIMusicPlayerModel model) { return new Builder(model); }
    public UIMusicPlayerModel model() { return model; }
    public List<AbstractWidget> build(UIBounds region) { return controls.build(region); }

    public static final class Builder {
        private final UIMusicPlayerModel model;
        private final UIPlaybackControls.Builder controls;

        private Builder(UIMusicPlayerModel model) {
            this.model = Objects.requireNonNull(model);
            controls = UIPlaybackControls.builder(model).playbackMode(true)
                    .labels(new UIPlaybackControls.Labels(
                            UILocalization.frameworkText("music.control.previous", "Previous track"),
                            UILocalization.frameworkText("music.control.pause", "Pause"),
                            UILocalization.frameworkText("music.control.play", "Play"),
                            UILocalization.frameworkText("music.control.next", "Next track"),
                            mode -> UILocalization.frameworkText("music.control.mode.current", "Playback mode: %s", mode),
                            UILocalization.frameworkText("music.control.progress", "Playback progress"),
                            UILocalization.frameworkText("music.control.volume", "Volume"),
                            UILocalization.frameworkText("music.timer.elapsed", "Elapsed time"),
                            UILocalization.frameworkText("music.timer.remaining", "Remaining time")
                    ));
        }

        public Builder theme(UITheme value) { controls.theme(value); return this; }
        public Builder previous(boolean value) { controls.previous(value); return this; }
        public Builder playPause(boolean value) { controls.playPause(value); return this; }
        public Builder next(boolean value) { controls.next(value); return this; }
        public Builder playbackMode(boolean value) { controls.playbackMode(value); return this; }
        public Builder progress(boolean value) { controls.progress(value); return this; }
        public Builder volume(boolean value) { controls.volume(value); return this; }
        public Builder elapsedTimer(boolean value) { controls.elapsedTimer(value); return this; }
        public Builder remainingTimer(boolean value) { controls.remainingTimer(value); return this; }
        public Builder maxWidth(int value) { controls.maxWidth(value); return this; }

        public UIMusicTransportControls build() { return new UIMusicTransportControls(this); }
    }
}
