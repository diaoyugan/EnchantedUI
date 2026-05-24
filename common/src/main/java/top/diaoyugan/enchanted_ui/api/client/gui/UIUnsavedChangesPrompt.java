package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

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

    public static UIUnsavedChangesPrompt defaults() {
        return new UIUnsavedChangesPrompt(
                Component.translatable("eui.dialog.unsaved_changes.title"),
                List.of(Component.translatable("eui.dialog.unsaved_changes.message")),
                Component.translatable("eui.dialog.unsaved_changes.discard"),
                Component.translatable("eui.dialog.unsaved_changes.cancel")
        );
    }

    public static UIUnsavedChangesPrompt of(Component title, Component message) {
        return new UIUnsavedChangesPrompt(
                title,
                List.of(message),
                Component.translatable("eui.dialog.unsaved_changes.discard"),
                Component.translatable("eui.dialog.unsaved_changes.cancel")
        );
    }

    public static UIUnsavedChangesPrompt of(
            Component title,
            List<Component> lines,
            Component discardLabel,
            Component cancelLabel
    ) {
        return new UIUnsavedChangesPrompt(title, lines, discardLabel, cancelLabel);
    }
}
