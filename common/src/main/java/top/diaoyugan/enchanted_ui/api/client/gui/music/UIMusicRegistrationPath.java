package top.diaoyugan.enchanted_ui.api.client.gui.music;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

/** Mod-defined navigation node such as "Vanilla scan / Volume Alpha". */
public record UIMusicRegistrationPath(String id, @Nullable String parentId, Component name, Kind kind,
                                      Predicate<UIMusicTrack> includes) {
    public enum Kind { ROOT, ALBUM, DIRECTORY, PROVIDER, CUSTOM }
    public UIMusicRegistrationPath {
        Objects.requireNonNull(id);Objects.requireNonNull(name);Objects.requireNonNull(kind);Objects.requireNonNull(includes);
    }
}
