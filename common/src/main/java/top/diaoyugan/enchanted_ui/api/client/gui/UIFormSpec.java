package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.input.KeyEvent;

public interface UIFormSpec {
    void build(UIForm form);

    default void onOpen(UIForm form) {
    }

    default void onClose(UIForm form) {
    }

    default void onShow(UIForm form) {
    }

    default void onHide(UIForm form) {
    }

    default void onPageChanged(UIForm form, int previousPage, int currentPage) {
    }

    default void onSave(UIForm form) {
    }

    default void tick(UIForm form) {
    }

    default boolean keyPressed(UIForm form, KeyEvent event) {
        return false;
    }

    default boolean keyReleased(UIForm form, KeyEvent event) {
        return false;
    }
}
