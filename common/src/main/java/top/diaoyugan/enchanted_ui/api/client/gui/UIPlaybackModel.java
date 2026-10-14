package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.state.UIBinding;

import java.time.Duration;

/** Transport state and actions, independent of media metadata, collections and storage. */
public interface UIPlaybackModel {
    boolean isPlaying();
    UIBinding<Double> progress();
    UIBinding<Double> volume();
    default UIBinding<Duration> duration() { return UIBinding.constant(Duration.ZERO); }
    default Component modeLabel() { return Component.empty(); }
    default String modeSymbol() { return ""; }
    default void cycleMode() {}
    void previous();
    void togglePlayback();
    void next();
    void seek(double progress);
    void volume(double volume);
}
