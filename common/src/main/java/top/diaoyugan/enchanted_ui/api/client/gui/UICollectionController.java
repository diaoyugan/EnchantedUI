package top.diaoyugan.enchanted_ui.api.client.gui;

import top.diaoyugan.enchanted_ui.api.client.gui.state.UIBinding;

import java.util.List;

/** Storage-neutral operations for named collections and their members. */
public interface UICollectionController<C, T> {
    UIBinding<List<C>> collections();
    UIBinding<C> selectedCollection();
    UIBinding<List<T>> availableItems();
    UIBinding<List<T>> selectedItems();
    void select(C collection);
    void create(String name);
    void rename(C collection, String name);
    void delete(C collection);
    void setItems(C collection, List<T> items);
}
