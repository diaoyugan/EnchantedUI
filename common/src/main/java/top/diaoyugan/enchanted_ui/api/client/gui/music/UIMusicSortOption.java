package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.network.chat.Component;
import java.util.Comparator;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;

public record UIMusicSortOption(String id, Component label, Comparator<UIMusicTrack> comparator) {
    public static final UIMusicSortOption DEFAULT = new UIMusicSortOption("default",UILocalization.frameworkText("music.sort.default","Default"),(a,b)->0);
    public static final UIMusicSortOption TITLE = new UIMusicSortOption("title",UILocalization.frameworkText("music.sort.title","Title"),
            Comparator.comparing(track->track.title().getString(),String.CASE_INSENSITIVE_ORDER));
    public static final UIMusicSortOption ARTIST = new UIMusicSortOption("artist",UILocalization.frameworkText("music.sort.artist","Artist"),
            Comparator.comparing((UIMusicTrack track)->track.artist().getString(),String.CASE_INSENSITIVE_ORDER).thenComparing(track->track.title().getString(),String.CASE_INSENSITIVE_ORDER));
    public static final UIMusicSortOption ALBUM = new UIMusicSortOption("album",UILocalization.frameworkText("music.sort.album","Album"),
            Comparator.comparing((UIMusicTrack track)->track.album().getString(),String.CASE_INSENSITIVE_ORDER).thenComparing(track->track.title().getString(),String.CASE_INSENSITIVE_ORDER));
}
