package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface UiTextValidator {
    @Nullable
    Component validate(String value);

    static UiTextValidator alwaysValid() {
        return value -> null;
    }
}
