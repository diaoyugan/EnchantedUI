package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;
import java.util.function.Supplier;

/** @deprecated Use {@link UIMarqueeLabel}; retained for source compatibility. */
@Deprecated(forRemoval = false)
public class UIScrollingLabelWidget extends UIMarqueeLabel {
    public UIScrollingLabelWidget(int x,int y,int width,int height,Supplier<Component> text,Supplier<Integer> color,UILabelWidget.Align align){
        super(x,y,width,height,text,color,align);
    }
}
