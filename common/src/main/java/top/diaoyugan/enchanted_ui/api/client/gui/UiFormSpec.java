package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.input.KeyEvent;

public interface UiFormSpec {
    void build(UiForm form);

    default void onOpen(UiForm form) {
    }

    default void onClose(UiForm form) {
    }

    default void onShow(UiForm form) {
    }

    default void onHide(UiForm form) {
    }

    default void onPageChanged(UiForm form, int previousPage, int currentPage) {
    }

    default void onSave(UiForm form) {
    }

    default void tick(UiForm form) {
    }

    default boolean keyPressed(UiForm form, KeyEvent event) {
        return false;
    }
}
