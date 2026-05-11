package top.diaoyugan.enchanted_ui.client.gui.page;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

public interface Page {
    List<AbstractWidget> build(int centerX);
}