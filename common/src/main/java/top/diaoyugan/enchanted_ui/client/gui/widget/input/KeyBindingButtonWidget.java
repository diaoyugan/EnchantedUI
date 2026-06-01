package top.diaoyugan.enchanted_ui.client.gui.widget.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeyBindingButtonWidget extends Button.Plain {
    private final Component label;

    private final Supplier<InputConstants.Key> getter;
    private final Consumer<InputConstants.Key> setter;

    @Nullable
    private final Supplier<Component> displaySupplier;

    @Nullable
    private final KeyMapping vanillaKeyMapping;

    private final UILocalization.KeyBindingMessages messages;
    private boolean listening = false;
    private final boolean syncVanilla;

    public KeyBindingButtonWidget(
            int x,
            int y,
            int width,
            int height,
            Component label,
            Supplier<InputConstants.Key> getter,
            Consumer<InputConstants.Key> setter,
            @Nullable Supplier<Component> displaySupplier,
            @Nullable KeyMapping vanillaKeyMapping,
            boolean syncVanilla
    ) {
        this(
                x,
                y,
                width,
                height,
                label,
                getter,
                setter,
                displaySupplier,
                vanillaKeyMapping,
                syncVanilla,
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
            @Nullable Supplier<Component> displaySupplier,
            @Nullable KeyMapping vanillaKeyMapping,
            boolean syncVanilla,
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
        this.displaySupplier = displaySupplier;
        this.vanillaKeyMapping = vanillaKeyMapping;
        this.syncVanilla = syncVanilla;
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
            key = InputConstants.Type.KEYSYM.getOrCreate(event.key());
        }

        setter.accept(key);

        syncVanillaKey(key);

        listening = false;
        refreshMessage();

        return true;
    }

    public InputConstants.Key currentKey() {
        return getter.get();
    }

    public void applyExternalKey(InputConstants.Key key) {
        setter.accept(key);
        syncVanillaKey(key);
        listening = false;
        refreshMessage();
    }

    // -------------------------
    // sync vanilla
    // -------------------------

    private void syncVanillaKey(InputConstants.Key key) {
        if (!syncVanilla || vanillaKeyMapping == null) {
            return;
        }

        vanillaKeyMapping.setKey(key);

        KeyMapping.resetMapping();
    }

    // -------------------------
    // UI
    // -------------------------

    public void refreshMessage() {
        if (listening) {
            setMessage(Component.translatable(
                    messages.listeningKey(),
                    label
            ));
            return;
        }

        Component keyName;

        if (displaySupplier != null) {
            keyName = displaySupplier.get();
        } else if (vanillaKeyMapping != null) {
            keyName = vanillaKeyMapping.getTranslatedKeyMessage();
        } else {
            keyName = Component.translatable("key.keyboard.unknown");
        }

        setMessage(currentMessage(keyName));
    }

    private Component currentMessage(Component keyName) {
        return Component.translatable(
                messages.currentKey(),
                label,
                keyName
        );
    }

    @Override
    public void updateWidgetNarration(
            NarrationElementOutput narrationElementOutput
    ) {
    }
}
