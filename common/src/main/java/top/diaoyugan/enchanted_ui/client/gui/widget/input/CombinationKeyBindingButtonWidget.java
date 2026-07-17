package top.diaoyugan.enchanted_ui.client.gui.widget.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.input.CombinationKeyBinding;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CombinationKeyBindingButtonWidget extends Button.Plain {
    private final Component label;
    private final Supplier<CombinationKeyBinding> getter;
    private final Consumer<CombinationKeyBinding> setter;
    private final UILocalization.KeyBindingMessages messages;

    private boolean listening = false;
    private final Set<InputConstants.Key> pendingKeys = new LinkedHashSet<>();

    public CombinationKeyBindingButtonWidget(
            int x,
            int y,
            int width,
            int height,
            Component label,
            Supplier<CombinationKeyBinding> getter,
            Consumer<CombinationKeyBinding> setter
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

    public CombinationKeyBindingButtonWidget(
            int x,
            int y,
            int width,
            int height,
            Component label,
            Supplier<CombinationKeyBinding> getter,
            Consumer<CombinationKeyBinding> setter,
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
        pendingKeys.clear();
        refreshMessage();
    }

    public boolean keyPressed(KeyEvent event) {
        if (!listening) {
            return false;
        }

        InputConstants.Key key = InputConstants.getKey(event);
        InputConstants.Key escape = InputConstants.getKey("key.keyboard.escape");
        if (key.equals(escape)) {
            pendingKeys.clear();
            finishBinding();
            return true;
        }

        pendingKeys.add(key);
        refreshMessage();

        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!listening) {
            return super.mouseClicked(event, doubleClick);
        }

        pendingKeys.add(InputConstants.Type.MOUSE.getOrCreate(event.button()));
        refreshMessage();
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(event.button());
        if (listening && pendingKeys.contains(key)) {
            finishBinding();
            return true;
        }
        return super.mouseReleased(event);
    }

    public void tick() {
        if (!listening) {
            return;
        }

        for (InputConstants.Key key : pendingKeys) {
            if (key.getType() == InputConstants.Type.KEYBOARD && !InputConstants.isKeyDown(key.getValue())) {
                finishBinding();
                return;
            }
        }
    }

    // -------------------------
    // finish
    // -------------------------

    private void finishBinding() {
        setter.accept(CombinationKeyBinding.of(pendingKeys));
        listening = false;
        refreshMessage();
    }

    public void applyExternalBinding(CombinationKeyBinding binding) {
        setter.accept(binding);
        listening = false;
        pendingKeys.clear();
        refreshMessage();
    }

    // -------------------------
    // UI text
    // -------------------------

    public void refreshMessage() {
        setMessage(formatMessage());
    }

    private Component formatMessage() {
        CombinationKeyBinding binding = getter.get();

        if (listening) {
            return messages.listening(label);
        }

        if (binding.isEmpty()) {
            return messages.none(label);
        }

        String text = binding.keys().stream()
                .map(InputConstants.Key::getDisplayName)
                .map(Component::getString)
                .collect(Collectors.joining(" + "));

        return messages.current(label, text);
    }

    // -------------------------
    // render / narration
    // -------------------------

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // no-op
    }
}
