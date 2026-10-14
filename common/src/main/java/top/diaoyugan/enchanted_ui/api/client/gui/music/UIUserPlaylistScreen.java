package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import top.diaoyugan.enchanted_ui.api.client.gui.UICollectionEditorScreen;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

/** Music labels and metadata adapter for the general collection editor. */
public final class UIUserPlaylistScreen extends UICollectionEditorScreen<UIMusicPlaylist, UIMusicTrack> {
    public UIUserPlaylistScreen(@Nullable Screen parent, Component title, UIMusicPlaylistController controller) {
        this(parent, title, controller, UIThemes.current());
    }

    public UIUserPlaylistScreen(@Nullable Screen parent, Component title, UIMusicPlaylistController controller, UITheme theme) {
        super(parent, title, UICollectionEditorScreen.builder(controller)
                .collectionKey(UIMusicPlaylist::id)
                .collectionText(UIMusicPlaylist::name)
                .itemKey(UIMusicTrack::id)
                .itemText(track -> Component.literal("").append(track.artist()).append(" - ").append(track.title()))
                .theme(theme)
                .labels(new Labels(
                        UILocalization.frameworkText("music.playlist.done", "Done"),
                        UILocalization.frameworkText("music.playlist.new", "New"),
                        UILocalization.frameworkText("music.playlist.default_name", "New playlist"),
                        UILocalization.frameworkText("music.playlist.name_hint", "Playlist name"),
                        UILocalization.frameworkText("music.playlist.save", "Save"),
                        UILocalization.frameworkText("music.playlist.delete", "Delete")
                )));
    }
}
