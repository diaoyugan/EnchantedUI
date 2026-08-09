package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;

import java.util.List;

/** A reusable composition that builds ordinary Minecraft widgets inside a region. */
@FunctionalInterface
public interface UIWidgetGroup {
    List<AbstractWidget> build(UIBounds bounds);
}
