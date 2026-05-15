package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;

public record UiDialogAction(Component label, Runnable action, boolean closeAfterRun) {
}
