package top.diaoyugan.enchanted_ui.api.client.gui.music;

import top.diaoyugan.enchanted_ui.api.client.gui.state.UIBinding;
import top.diaoyugan.enchanted_ui.api.client.gui.UICollectionController;
import java.util.List;

/** Persistence-neutral user-playlist operations implemented by the integrating mod. */
public interface UIMusicPlaylistController extends UICollectionController<UIMusicPlaylist, UIMusicTrack> {
    UIBinding<List<UIMusicPlaylist>> playlists();
    UIBinding<UIMusicPlaylist> selectedPlaylist();
    UIBinding<List<UIMusicTrack>> availableTracks();
    UIBinding<List<UIMusicTrack>> tracksInSelectedPlaylist();
    void select(UIMusicPlaylist playlist);
    void create(String name);
    void rename(UIMusicPlaylist playlist,String name);
    void delete(UIMusicPlaylist playlist);
    void setTracks(UIMusicPlaylist playlist,List<UIMusicTrack> tracks);
    @Override default UIBinding<List<UIMusicPlaylist>> collections() { return playlists(); }
    @Override default UIBinding<UIMusicPlaylist> selectedCollection() { return selectedPlaylist(); }
    @Override default UIBinding<List<UIMusicTrack>> availableItems() { return availableTracks(); }
    @Override default UIBinding<List<UIMusicTrack>> selectedItems() { return tracksInSelectedPlaylist(); }
    @Override default void setItems(UIMusicPlaylist playlist, List<UIMusicTrack> items) { setTracks(playlist, items); }
}
