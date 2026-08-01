package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/** Track metadata. Registration paths and storage remain owned by the integrating mod. */
public final class UIMusicTrack {
    private final String id;
    private final Component title;
    private final Component artist;
    private final Component album;
    private final Component source;
    @Nullable private final Identifier artwork;
    private final String registrationPathId;

    /** Backwards-compatible constructor; source is also used as the album label. */
    public UIMusicTrack(String id, Component title, Component artist, Component source, @Nullable Identifier artwork) {
        this(id, title, artist, source, source, artwork, "all");
    }

    public UIMusicTrack(String id, Component title, Component artist, Component album, Component source,
                        @Nullable Identifier artwork, String registrationPathId) {
        this.id=Objects.requireNonNull(id);this.title=Objects.requireNonNull(title);this.artist=Objects.requireNonNull(artist);
        this.album=Objects.requireNonNull(album);this.source=Objects.requireNonNull(source);this.artwork=artwork;
        this.registrationPathId=Objects.requireNonNull(registrationPathId);
    }

    public String id(){return id;}
    public Component title(){return title;}
    public Component artist(){return artist;}
    public Component album(){return album;}
    public Component source(){return source;}
    @Nullable public Identifier artwork(){return artwork;}
    public String registrationPathId(){return registrationPathId;}

    @Override public boolean equals(Object value){return value instanceof UIMusicTrack track&&id.equals(track.id);}
    @Override public int hashCode(){return id.hashCode();}
}
