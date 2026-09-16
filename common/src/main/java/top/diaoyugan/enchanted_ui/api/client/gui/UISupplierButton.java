package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;
import java.util.function.Supplier;

/** @deprecated Use {@link UIDynamicButton}; retained for source compatibility. */
@Deprecated(forRemoval = false)
public class UISupplierButton extends UIDynamicButton {
    public UISupplierButton(int x,int y,int width,int height,Supplier<Component> label,Runnable onPress) {
        this(x,y,width,height,label,label,onPress);
    }
    public UISupplierButton(int x,int y,int width,int height,Supplier<Component> label,Supplier<Component> narration,Runnable onPress) {
        super(x,y,width,height,label,narration,onPress);
    }
}
