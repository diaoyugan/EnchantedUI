package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/** @deprecated Use {@link UINormalizedSlider}; retained for source compatibility. */
@Deprecated(forRemoval = false)
public class UIRangeSlider extends UINormalizedSlider {
    public UIRangeSlider(int x,int y,int width,int height,Component narration,DoubleSupplier value,DoubleConsumer setter){super(x,y,width,height,narration,value,setter);}
    public UIRangeSlider(int x,int y,int width,int height,Component narration,DoubleSupplier value,DoubleConsumer setter,UITheme theme){super(x,y,width,height,narration,value,setter,theme);}
}
