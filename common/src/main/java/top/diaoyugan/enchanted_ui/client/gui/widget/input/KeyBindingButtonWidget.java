package top.diaoyugan.enchanted_ui.client.gui.widget.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeyBindingButtonWidget extends Button.Plain {
    private final Component label;

    private final Supplier<InputConstants.Key> getter;
    private final Consumer<InputConstants.Key> setter;

    private final UILocalization.KeyBindingMessages messages;
    private boolean listening = false;

    public KeyBindingButtonWidget(
            int x,
            int y,
            int width,
            int height,
            Component label,
            Supplier<InputConstants.Key> getter,
            Consumer<InputConstants.Key> setter
    ) {
        this(
                x,
                y,
                width,
                height,
                label,
                getter,
                setter,
                UILocalization.KeyBindingMessages.defaults()
        );
    }

    public KeyBindingButtonWidget(
            int x,
            int y,
            int width,
            int height,
            Component label,
            Supplier<InputConstants.Key> getter,
            Consumer<InputConstants.Key> setter,
            UILocalization.KeyBindingMessages messages
    ) {
        super(
                x,
                y,
                width,
                height,
                Component.empty(),
                b -> {},
                DEFAULT_NARRATION
        );

        this.label = label;
        this.getter = getter;
        this.setter = setter;
        this.messages = messages;

        refreshMessage();
    }

    public void tooltip(Component tooltip) {
        setTooltip(Tooltip.create(tooltip));
    }

    // -------------------------
    // input
    // -------------------------

    @Override
    public void onPress(InputWithModifiers input) {
        listening = true;
        refreshMessage();
    }

    public boolean keyPressed(KeyEvent event) {
        if (!listening) {
            return false;
        }

        InputConstants.Key key;

        if (event.key() == InputConstants.KEY_ESCAPE) {
            key = InputConstants.UNKNOWN;
        } else {
            key = InputConstants.getKey(event);
        }

        setter.accept(key);
        listening = false;
        refreshMessage();

        return true;
    }

    public InputConstants.Key currentKey() {
        return getter.get();
    }

    public void applyExternalKey(InputConstants.Key key) {
        setter.accept(key);
        listening = false;
        refreshMessage();
    }

    // -------------------------
    // UI
    // -------------------------

    public void refreshMessage() {
        if (listening) {
            setMessage(messages.listening(label));
            return;
        }

        InputConstants.Key key = getter.get();
        Component keyName = key == null || key.equals(InputConstants.UNKNOWN)
                ? Component.translatable("key.keyboard.unknown")
                : key.getDisplayName();

        setMessage(currentMessage(keyName));
    }

    private Component currentMessage(Component keyName) {
        return messages.current(label, keyName);
    }

    @Override
    public void updateWidgetNarration(
            NarrationElementOutput narrationElementOutput
    ) {
    }
}
