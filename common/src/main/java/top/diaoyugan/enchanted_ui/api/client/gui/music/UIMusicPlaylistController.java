package top.diaoyugan.enchanted_ui.api.client.gui.music;

import top.diaoyugan.enchanted_ui.api.client.gui.state.UIBinding;
import java.util.List;

/** Persistence-neutral user-playlist operations implemented by the integrating mod. */
public interface UIMusicPlaylistController {
    UIBinding<List<UIMusicPlaylist>> playlists();
    UIBinding<UIMusicPlaylist> selectedPlaylist();
    UIBinding<List<UIMusicTrack>> availableTracks();
    UIBinding<List<UIMusicTrack>> tracksInSelectedPlaylist();
    void select(UIMusicPlaylist playlist);
    void create(String name);
    void rename(UIMusicPlaylist playlist,String name);
    void delete(UIMusicPlaylist playlist);
    void setTracks(UIMusicPlaylist playlist,List<UIMusicTrack> tracks);
}
