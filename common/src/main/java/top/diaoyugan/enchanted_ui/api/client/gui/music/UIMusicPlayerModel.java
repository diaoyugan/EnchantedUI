package top.diaoyugan.enchanted_ui.api.client.gui.music;

import top.diaoyugan.enchanted_ui.api.client.gui.state.UIBinding;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/** State and transport contract consumed by the reusable music-player preset. */
public interface UIMusicPlayerModel {
    UIBinding<List<UIMusicTrack>> tracks();
    UIBinding<List<UIMusicPlaylist>> playlists();
    UIBinding<UIMusicTrack> selectedTrack();
    UIBinding<UIMusicTrack> currentTrack();
    UIBinding<UIPlaybackState> playbackState();
    UIBinding<Double> progress();
    UIBinding<Double> volume();
    default UIBinding<UIPlaybackMode> playbackMode() { return UIBinding.constant(UIPlaybackMode.ALL_LOOP); }
    default List<UIPlaybackMode> playbackModes() { return List.of(UIPlaybackMode.SEQUENTIAL,UIPlaybackMode.ALL_LOOP,UIPlaybackMode.SINGLE_LOOP,UIPlaybackMode.SHUFFLE); }
    default void playbackMode(UIPlaybackMode mode) {}
    default UIBinding<UIMusicSortOption> sort() { return UIBinding.constant(UIMusicSortOption.DEFAULT); }
    default List<UIMusicSortOption> sortOptions() { return List.of(UIMusicSortOption.DEFAULT,UIMusicSortOption.TITLE,UIMusicSortOption.ARTIST,UIMusicSortOption.ALBUM); }
    default void sort(UIMusicSortOption sort) {}
    default UIBinding<List<UIMusicRegistrationPath>> registrationPaths() { return UIBinding.constant(List.of()); }
    default void selectRegistrationPath(UIMusicRegistrationPath path) {}
    @Nullable default UIMusicPlaylistController playlistController() { return null; }
    void select(UIMusicTrack track);
    void play(UIMusicTrack track);
    void previous();
    void togglePlayback();
    void next();
    void seek(double progress);
    void volume(double volume);
    default void selectPlaylist(UIMusicPlaylist playlist) {}
}
