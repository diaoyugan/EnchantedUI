package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

/**
 * Text shown when a screen with unsaved form changes is closed.
 * <p>
 * The default prompt uses EnchantedUI translation keys. Use {@link #of(Component, Component)}
 * or {@link #of(Component, List, Component, Component)} when a screen needs
 * domain-specific wording.
 *
 * @param title dialog title
 * @param lines message lines shown in the dialog body
 * @param discardLabel label for the action that closes without saving
 * @param cancelLabel label for the action that keeps the screen open
 */
public record UIUnsavedChangesPrompt(
        Component title,
        List<Component> lines,
        Component discardLabel,
        Component cancelLabel
) {
    public UIUnsavedChangesPrompt {
        Objects.requireNonNull(title, "title");
        lines = List.copyOf(Objects.requireNonNull(lines, "lines"));
        Objects.requireNonNull(discardLabel, "discardLabel");
        Objects.requireNonNull(cancelLabel, "cancelLabel");
    }

    /**
     * Built-in localized prompt used by screens unless overridden.
     */
    public static UIUnsavedChangesPrompt defaults() {
        return new UIUnsavedChangesPrompt(
                Component.translatable("eui.dialog.unsaved_changes.title"),
                List.of(Component.translatable("eui.dialog.unsaved_changes.message")),
                Component.translatable("eui.dialog.unsaved_changes.discard"),
                Component.translatable("eui.dialog.unsaved_changes.cancel")
        );
    }

    /**
     * Creates a custom prompt that keeps the default localized action labels.
     */
    public static UIUnsavedChangesPrompt of(Component title, Component message) {
        return new UIUnsavedChangesPrompt(
                title,
                List.of(message),
                Component.translatable("eui.dialog.unsaved_changes.discard"),
                Component.translatable("eui.dialog.unsaved_changes.cancel")
        );
    }

    /**
     * Creates a fully custom prompt, including action labels.
     */
    public static UIUnsavedChangesPrompt of(
            Component title,
            List<Component> lines,
            Component discardLabel,
            Component cancelLabel
    ) {
        return new UIUnsavedChangesPrompt(title, lines, discardLabel, cancelLabel);
    }
}
