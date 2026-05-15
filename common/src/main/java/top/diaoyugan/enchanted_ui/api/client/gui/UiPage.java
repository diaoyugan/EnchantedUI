package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.KeyEvent;

import java.util.List;

public interface UiPage {
    List<AbstractWidget> build(UiBuildContext ctx);

    default void onOpen() {
    }

    default void onClose() {
    }

    default void onShow() {
    }

    default void onHide() {
    }

    default void onPageChanged(int previousPage, int currentPage) {
    }

    default void onSave() {
    }

    default void tick() {
    }

    default boolean keyPressed(KeyEvent event) {
        return false;
    }
}
