package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.network.chat.Component;
import java.util.Objects;
import java.util.function.Predicate;

public final class UIMusicPlaylist {
    private final String id;
    private final Component name;
    private final Predicate<UIMusicTrack> includes;
    private final boolean userCreated;
    public UIMusicPlaylist(String id,Component name,Predicate<UIMusicTrack> includes){this(id,name,includes,false);}
    public UIMusicPlaylist(String id,Component name,Predicate<UIMusicTrack> includes,boolean userCreated){
        this.id=Objects.requireNonNull(id);this.name=Objects.requireNonNull(name);this.includes=Objects.requireNonNull(includes);this.userCreated=userCreated;
    }
    public String id(){return id;}
    public Component name(){return name;}
    public Predicate<UIMusicTrack> includes(){return includes;}
    public boolean userCreated(){return userCreated;}
    @Override public boolean equals(Object value){return value instanceof UIMusicPlaylist playlist&&id.equals(playlist.id);}
    @Override public int hashCode(){return id.hashCode();}
}
