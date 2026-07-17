package top.diaoyugan.enchanted_ui.api.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.sdl.SDLMouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * An immutable, ordered combination of keyboard and mouse inputs.
 *
 * <p>The serialized representation uses Minecraft's stable input names, such
 * as {@code key.keyboard.v} and {@code key.mouse.4}, rather than backend key
 * codes.</p>
 */
public final class CombinationKeyBinding {
    public static final CombinationKeyBinding EMPTY = new CombinationKeyBinding(List.of());

    private final List<InputConstants.Key> keys;

    private CombinationKeyBinding(Collection<InputConstants.Key> keys) {
        LinkedHashSet<InputConstants.Key> uniqueKeys = new LinkedHashSet<>();
        for (InputConstants.Key key : keys) {
            InputConstants.Key nonNullKey = Objects.requireNonNull(key, "key");
            if (!nonNullKey.equals(InputConstants.UNKNOWN)) {
                uniqueKeys.add(nonNullKey);
            }
        }
        this.keys = List.copyOf(uniqueKeys);
    }

    public static CombinationKeyBinding of(Collection<InputConstants.Key> keys) {
        Objects.requireNonNull(keys, "keys");
        return keys.isEmpty() ? EMPTY : new CombinationKeyBinding(keys);
    }

    public static CombinationKeyBinding of(InputConstants.Key... keys) {
        return of(List.of(keys));
    }

    public static CombinationKeyBinding deserialize(Collection<String> serializedKeys) {
        Objects.requireNonNull(serializedKeys, "serializedKeys");
        List<InputConstants.Key> keys = new ArrayList<>(serializedKeys.size());
        for (String serializedKey : serializedKeys) {
            keys.add(InputConstants.getKey(Objects.requireNonNull(serializedKey, "serializedKey")));
        }
        return of(keys);
    }

    public List<InputConstants.Key> keys() {
        return keys;
    }

    public List<String> serialize() {
        return keys.stream().map(InputConstants.Key::getName).toList();
    }

    public Component displayName() {
        return Component.literal(keys.stream()
                .map(InputConstants.Key::getDisplayName)
                .map(Component::getString)
                .reduce((left, right) -> left + " + " + right)
                .orElse(""));
    }

    /** Returns whether every input in this non-empty combination is held. */
    public boolean isDown() {
        return !keys.isEmpty() && keys.stream().allMatch(CombinationKeyBinding::isKeyDown);
    }

    public boolean contains(InputConstants.Key key) {
        return keys.contains(key);
    }

    public boolean matches(KeyEvent event) {
        return contains(InputConstants.getKey(event));
    }

    public boolean matches(MouseButtonEvent event) {
        return contains(InputConstants.Type.MOUSE.getOrCreate(event.button()));
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }

    private static boolean isKeyDown(InputConstants.Key key) {
        if (key.getType() == InputConstants.Type.KEYBOARD) {
            return InputConstants.isKeyDown(key.getValue());
        }

        int button = key.getValue();
        return button > 0
                && button <= Integer.SIZE
                && (SDLMouse.SDL_GetMouseState(null, null) & (1 << (button - 1))) != 0;
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof CombinationKeyBinding other && keys.equals(other.keys);
    }

    @Override
    public int hashCode() {
        return keys.hashCode();
    }

    @Override
    public String toString() {
        return String.join(" + ", serialize());
    }
}
