package top.diaoyugan.enchanted_ui.client.gui.builder;

import net.minecraft.client.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Routes form-level input capture without coupling the form engine to widget classes. */
final class FormInteractionRegistry {
    private final List<Predicate<KeyEvent>> keyPressedHandlers = new ArrayList<>();
    private final List<Predicate<KeyEvent>> keyReleasedHandlers = new ArrayList<>();

    void onKeyPressed(Predicate<KeyEvent> handler) {
        keyPressedHandlers.add(handler);
    }

    void onKeyReleased(Predicate<KeyEvent> handler) {
        keyReleasedHandlers.add(handler);
    }

    boolean keyPressed(KeyEvent event) {
        return dispatch(keyPressedHandlers, event);
    }

    boolean keyReleased(KeyEvent event) {
        return dispatch(keyReleasedHandlers, event);
    }

    private static boolean dispatch(List<Predicate<KeyEvent>> handlers, KeyEvent event) {
        boolean handled = false;
        for (Predicate<KeyEvent> handler : handlers) {
            handled |= handler.test(event);
        }
        return handled;
    }
}
